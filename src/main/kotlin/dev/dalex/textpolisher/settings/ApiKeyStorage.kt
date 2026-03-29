package dev.dalex.textpolisher.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

object ApiKeyStorage {

    private fun attributesFor(provider: String) = CredentialAttributes(
        generateServiceName("AITextPolisher", provider)
    )

    fun get(provider: String): String? =
        PasswordSafe.instance.getPassword(attributesFor(provider))

    fun set(provider: String, apiKey: String) =
        PasswordSafe.instance.set(attributesFor(provider), Credentials(provider, apiKey))
}
