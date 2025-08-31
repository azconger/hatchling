package lol.conger.hatchling.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.MessageDigest

@Component
class DigestAuthenticationFilter(
    private val apiKeyService: ApiKeyService,
    private val digestEntryPoint: DigestAuthenticationEntryPoint
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader != null && authHeader.lowercase().startsWith("digest ")) {
            try {
                val digestHeader = parseDigestHeader(authHeader.substring(7))
                val username = digestHeader["username"]
                
                if (username != null && apiKeyService.isValidUsername(username)) {
                    val password = apiKeyService.getPassword(username)
                    if (password != null && validateDigestResponse(request, digestHeader, password)) {
                        val authorities = listOf(SimpleGrantedAuthority("ROLE_API_USER"))
                        val authentication = UsernamePasswordAuthenticationToken(username, null, authorities)
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            } catch (e: Exception) {
                // Invalid digest, let it fall through to 401
            }
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun parseDigestHeader(header: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val parts = header.split(",")
        
        for (part in parts) {
            val trimmedPart = part.trim()
            val equalIndex = trimmedPart.indexOf('=')
            if (equalIndex > 0) {
                val key = trimmedPart.substring(0, equalIndex).trim()
                var value = trimmedPart.substring(equalIndex + 1).trim()
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length - 1)
                }
                result[key] = value
            }
        }
        return result
    }
    
    private fun validateDigestResponse(
        request: HttpServletRequest, 
        digestHeader: Map<String, String>,
        password: String
    ): Boolean {
        val username = digestHeader["username"] ?: return false
        val realm = digestHeader["realm"] ?: return false
        val nonce = digestHeader["nonce"] ?: return false
        val uri = digestHeader["uri"] ?: return false
        val response = digestHeader["response"] ?: return false
        val qop = digestHeader["qop"]
        val nc = digestHeader["nc"]
        val cnonce = digestHeader["cnonce"]
        
        // Calculate HA1 = MD5(username:realm:password)
        val ha1 = md5("$username:$realm:$password")
        
        // Calculate HA2 = MD5(method:uri)
        val ha2 = md5("${request.method}:$uri")
        
        // Calculate expected response
        val expectedResponse = if (qop == "auth" && nc != null && cnonce != null) {
            md5("$ha1:$nonce:$nc:$cnonce:$qop:$ha2")
        } else {
            md5("$ha1:$nonce:$ha2")
        }
        
        return expectedResponse == response
    }
    
    private fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}