package com.sumup.app.data.mapper

import com.sumup.app.domain.model.ConnectedReader
import com.sumup.app.domain.model.ReaderType
import com.sumup.merchant.reader.models.SavedCardReaderDetailsResult
import com.sumup.merchant.reader.models.ReaderType as SdkReaderType

internal class ConnectedReaderMapper {

    fun map(result: SavedCardReaderDetailsResult): ConnectedReader? = when (result) {
        is SavedCardReaderDetailsResult.SavedCardReaderDetails -> ConnectedReader(
            readerType = result.readerType.toDomain(),
            serialNumber = result.serialNumber.orEmpty(),
            lastKnownBatteryPercentage = result.lastKnownBatteryPercentage,
        )
        is SavedCardReaderDetailsResult.NoSavedReader -> null
    }

    private fun SdkReaderType?.toDomain(): ReaderType = when (this) {
        SdkReaderType.SOLO -> ReaderType.SOLO
        SdkReaderType.SOLO_LITE -> ReaderType.SOLO_LITE
        SdkReaderType.AIR -> ReaderType.AIR
        SdkReaderType.THREE_G -> ReaderType.THREE_G
        SdkReaderType.PIN_PLUS -> ReaderType.PIN_PLUS
        SdkReaderType.UNKNOWN -> ReaderType.UNKNOWN
        null -> ReaderType.UNKNOWN
    }
}
