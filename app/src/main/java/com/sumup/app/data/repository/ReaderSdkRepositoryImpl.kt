package com.sumup.app.data.repository

import com.sumup.app.util.CoroutinesDispatcherProvider
import com.sumup.app.data.mapper.ConnectedReaderMapper
import com.sumup.app.data.mapper.MerchantInfoMapper
import com.sumup.app.data.mapper.OfflineSessionMapper
import com.sumup.app.domain.model.ConnectedReader
import com.sumup.app.domain.model.MerchantInfo
import com.sumup.app.domain.model.OfflineSdkError
import com.sumup.app.domain.model.OfflineSession
import com.sumup.app.domain.repository.ReaderSdkRepository
import com.sumup.contract.offline.OfflineSessionState
import com.sumup.contract.offline.OfflineUploadFailureReasons
import com.sumup.merchant.reader.api.SumUpAPI
import com.sumup.merchant.reader.offline.callbacks.OfflineSessionCallback
import com.sumup.merchant.reader.offline.callbacks.SecurityPatchUpdateCallback
import com.sumup.merchant.reader.offline.callbacks.UploadOfflineTransactionsStatusListener
import com.sumup.merchant.reader.offline.session.domain.contracts.StartOfflineSessionCallback
import com.sumup.merchant.reader.offline.session.domain.contracts.StopOfflineSessionCallback
import com.sumup.merchant.reader.offline.session.domain.model.OfflineOperationResult.StartOfflineSessionResult
import com.sumup.merchant.reader.offline.session.domain.model.OfflineOperationResult.StopOfflineSessionResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.milliseconds

internal class ReaderSdkRepositoryImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val offlineSessionMapper: OfflineSessionMapper,
    private val connectedReaderMapper: ConnectedReaderMapper,
    private val merchantInfoMapper: MerchantInfoMapper,
) : ReaderSdkRepository {

    override suspend fun isLoggedIn(): Boolean = onIo { SumUpAPI.isLoggedIn() }

    override suspend fun getCurrentMerchant(): MerchantInfo? = onIo {
        merchantInfoMapper.map(SumUpAPI.getCurrentMerchant())
    }

    override suspend fun logout() = onMain { SumUpAPI.logout() }

    override suspend fun prepareForCheckout() = onMain {
        // SumUpAPI.prepareForCheckout() constructs an android.os.Handler internally,
        // which requires a thread with a Looper. Force the Main dispatcher so it
        // does not crash when invoked from a background coroutine (e.g. Dispatchers.IO).
        SumUpAPI.prepareForCheckout()
    }

    override suspend fun isTipOnCardReaderAvailable(): Boolean = onIo {
        SumUpAPI.isTipOnCardReaderAvailable()
    }

    override fun connectedReaderFlow(intervalMs: Long): Flow<ConnectedReader?> = flow {
        emit(snapshotConnectedReader())
        while (true) {
            delay(intervalMs.milliseconds)
            emit(snapshotConnectedReader())
        }
    }
        .distinctUntilChanged()
        .flowOn(dispatcherProvider.io)

    private fun snapshotConnectedReader(): ConnectedReader? = runCatching {
        if (!SumUpAPI.isCardReaderConnected()) return@runCatching null
        connectedReaderMapper.map(SumUpAPI.getSavedCardReaderDetails())
    }.getOrNull()

    override suspend fun startOfflineSession() = onIo {
        awaitCallback { continuation ->
            SumUpAPI.startOfflineSession(object : StartOfflineSessionCallback {
                override fun onResult(result: StartOfflineSessionResult) {
                    when (result) {
                        is StartOfflineSessionResult.Success,
                        is StartOfflineSessionResult.SessionInProgress,
                        -> continuation.safeResume(Unit)
                        else -> continuation.safeResumeWithException(
                            OfflineSdkError.StartSessionFailed(
                                resultName = result::class.simpleName.orEmpty(),
                            ),
                        )
                    }
                }
            })
        }
    }

    override suspend fun stopOfflineSession() = onIo {
        awaitCallback { continuation ->
            SumUpAPI.stopOfflineSession(object : StopOfflineSessionCallback {
                override fun onResult(result: StopOfflineSessionResult) {
                    when (result) {
                        is StopOfflineSessionResult.Success,
                        is StopOfflineSessionResult.NoActiveSession,
                        -> continuation.safeResume(Unit)
                        else -> continuation.safeResumeWithException(
                            OfflineSdkError.StopSessionFailed(
                                resultName = result::class.simpleName.orEmpty(),
                            ),
                        )
                    }
                }
            })
        }
    }

    override suspend fun updateOfflineSecurityPatch() = onIo {
        awaitCallback { continuation ->
            SumUpAPI.updateOfflineSecurityPatch(object : SecurityPatchUpdateCallback {
                override fun onSuccess() {
                    continuation.safeResume(Unit)
                }

                override fun onFailure() {
                    continuation.safeResumeWithException(OfflineSdkError.SecurityPatchUpdateFailed)
                }
            })
        }
    }

    override suspend fun uploadOfflineTransactions() = onIo {
        awaitCallback { continuation ->
            SumUpAPI.uploadOfflineTransactions(object : UploadOfflineTransactionsStatusListener {
                override fun onUploadSuccess() {
                    continuation.safeResume(Unit)
                }

                override fun onUploadFailure(error: OfflineUploadFailureReasons) {
                    continuation.safeResumeWithException(
                        OfflineSdkError.UploadFailed(reason = error.message),
                    )
                }
            })
        }
    }

    override suspend fun getCurrentOfflineSession(): OfflineSession = onIo {
        awaitCallback { continuation ->
            SumUpAPI.fetchCurrentOfflineSession(object : OfflineSessionCallback {
                override fun onSessionInfoReceived(offlineSessionState: OfflineSessionState) {
                    continuation.safeResume(offlineSessionMapper.map(offlineSessionState))
                }
            })
        }
    }

    private suspend inline fun <T> onIo(crossinline block: suspend () -> T): T =
        withContext(dispatcherProvider.io) { block() }

    private suspend inline fun <T> onMain(crossinline block: suspend () -> T): T =
        withContext(dispatcherProvider.main) { block() }

    private suspend inline fun <T> awaitCallback(
        crossinline register: (CancellableContinuation<T>) -> Unit,
    ): T = suspendCancellableCoroutine { continuation -> register(continuation) }

    private fun <T> CancellableContinuation<T>.safeResume(value: T) {
        if (isActive) resume(value)
    }

    private fun <T> CancellableContinuation<T>.safeResumeWithException(exception: Throwable) {
        if (isActive) resumeWithException(exception)
    }
}
