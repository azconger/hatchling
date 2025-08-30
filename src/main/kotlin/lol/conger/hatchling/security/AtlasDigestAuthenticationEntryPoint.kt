package lol.conger.hatchling.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class AtlasDigestAuthenticationEntryPoint : AuthenticationEntryPoint {
    
    private val realm = "MongoDB Atlas API"
    private val nonceValiditySeconds = 300
    private val key = "hatchling-atlas-api"
    
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.addHeader("WWW-Authenticate", createDigestHeader())
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Digest Authentication Required")
    }
    
    private fun createDigestHeader(): String {
        val expiryTime = System.currentTimeMillis() + (nonceValiditySeconds * 1000L)
        val signatureValue = "$expiryTime:$key"
        val nonceValue = Base64.getEncoder().encodeToString(signatureValue.toByteArray())
        
        return "Digest realm=\"$realm\", " +
                "qop=\"auth\", " +
                "nonce=\"$nonceValue\", " +
                "algorithm=MD5"
    }
}