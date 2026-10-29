package com.bitwarden.authenticatorbridge.manager.util

import com.bitwarden.authenticatorbridge.manager.model.AuthenticatorBridgeConnectionType

/**
 * Convert a [AuthenticatorBridgeConnectionType] to raw package name for connection.
 */
internal fun AuthenticatorBridgeConnectionType.toPackageName() =
    when (this) {
        AuthenticatorBridgeConnectionType.RELEASE -> "com.rjpw.android"
        AuthenticatorBridgeConnectionType.DEV -> "com.rjpw.android.dev"
    }
