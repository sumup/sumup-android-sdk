package com.sumup.app.domain.model

internal enum class ReaderType(val displayName: String) {
    SOLO("Solo"),
    SOLO_LITE("Solo Lite"),
    AIR("Air"),
    THREE_G("3G"),
    PIN_PLUS("PIN+"),
    UNKNOWN("Reader"),
}
