package lol.conger.hatchling.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "atlas")
data class AtlasProperties(
    val apiKeys: Map<String, String> = emptyMap()
)

@Service
@EnableConfigurationProperties(AtlasProperties::class)
class ApiKeyService(private val atlasProperties: AtlasProperties) {
    
    fun validateCredentials(publicKey: String, privateKey: String): Boolean {
        return atlasProperties.apiKeys[publicKey] == privateKey
    }
    
    fun getPrivateKey(publicKey: String): String? {
        return atlasProperties.apiKeys[publicKey]
    }
    
    fun getAllApiKeys(): Map<String, String> {
        return atlasProperties.apiKeys
    }
    
    fun isValidPublicKey(publicKey: String): Boolean {
        return atlasProperties.apiKeys.containsKey(publicKey)
    }
}