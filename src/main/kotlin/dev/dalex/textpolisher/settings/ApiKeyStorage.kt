package dev.dalex.textpolisher.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

object ApiKeyStorage {

    private val credentialAttributes = CredentialAttributes(
        generateServiceName("AITextPolisher", "apiKey")
    )

    fun get(): String? {
        return PasswordSafe.instance.getPassword(credentialAttributes)
    }

    fun set(apiKey: String) {
        PasswordSafe.instance.set(credentialAttributes, Credentials("apiKey", apiKey))
    }
}
