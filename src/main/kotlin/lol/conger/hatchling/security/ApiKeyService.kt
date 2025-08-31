package lol.conger.hatchling.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "digest")
data class DigestAuthProperties(
    val credentials: Map<String, String> = emptyMap()
)

@Service
@EnableConfigurationProperties(DigestAuthProperties::class)
class ApiKeyService(private val digestAuthProperties: DigestAuthProperties) {
    
    fun validateCredentials(username: String, password: String): Boolean {
        return digestAuthProperties.credentials[username] == password
    }
    
    fun getPassword(username: String): String? {
        return digestAuthProperties.credentials[username]
    }
    
    fun getAllCredentials(): Map<String, String> {
        return digestAuthProperties.credentials
    }
    
    fun isValidUsername(username: String): Boolean {
        return digestAuthProperties.credentials.containsKey(username)
    }
}