package com.bitwarden.authenticatorbridge.manager.util

import com.bitwarden.authenticatorbridge.manager.model.AuthenticatorBridgeConnectionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthenticatorBridgeConnectionTypeExtensionsTest {

    @Test
    fun `toPackageName RELEASE should map to correct release package`() {
        assertEquals(
            "com.rjpw.android",
            AuthenticatorBridgeConnectionType.RELEASE.toPackageName(),
        )
    }

    @Test
    fun `toPackageName DEV should map to correct dev package`() {
        assertEquals(
            "com.rjpw.android.dev",
            AuthenticatorBridgeConnectionType.DEV.toPackageName(),
        )
    }
}
