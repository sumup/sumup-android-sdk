package com.sumup.app.domain.model

internal data class ConnectedReader(
    val readerType: ReaderType,
    val serialNumber: String,
    val lastKnownBatteryPercentage: Int?,
)
