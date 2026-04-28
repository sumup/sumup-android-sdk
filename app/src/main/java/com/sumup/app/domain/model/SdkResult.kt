package com.sumup.app.domain.model

/**
 * Result of an activity returned by the SumUp SDK.
 *
 * Different request codes carry different payloads, so we model them as distinct
 * variants instead of a single "everything optional" bag:
 *  - [Status] is produced by login and card-reader-page results. These only carry a
 *    status code and a message.
 *  - [Payment] is produced by checkout results. In addition to the status code and
 *    message it contains transaction-specific fields.
 */
internal sealed interface SdkResult {
    val resultCode: Int
    val message: String

    /** Result of a login or card-reader-page activity. */
    data class Status(
        override val resultCode: Int,
        override val message: String,
    ) : SdkResult

    /** Result of a checkout/payment activity. */
    data class Payment(
        override val resultCode: Int,
        override val message: String,
        val transactionCode: String?,
        val receiptSent: Boolean?,
        val transactionInfo: String?,
    ) : SdkResult
}
