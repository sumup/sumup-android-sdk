# Offline Payments V2

- Offline payments enable merchants to accept card-present transactions whenever they want irrespective of connectivity conditions. Transactions performed while offline are securely stored on the Android device under encryption and uploaded as a batch when connectivity and services are available again.
  Offline acceptance is session‑based and limited in time and volume.
* We strongly suggest uploading stored transactions immediately after returning online to reduce the risk of acquirer decline, for which the merchant will bear full financial liability.
* The SDK also auto‑uploads pending offline batches before the next online transaction.
* **foreignTransactionId** is a mandatory field for offline payments
---

## Eligibility & opting in
There are two layers of enablement:
* Integrator enablement — The partner is onboarded via SumUp Partnerships with an executed contract extension covering offline terms.
* Merchant enablement — Specific merchant IDs (MIDs) are remotely enabled by the Partnerships team after the integrator supplies the list of MIDs.

## Summary of changes (V1 to V2 — What Changed)
- This section describes **changes** in the offline transaction feature compared to the V1 behaviour. 
- This section can be ignored, when integrating offline transactions for the first time.

| What changed | Before                                                                                        | After                                                                                                                                                                    |
|--------------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Offline session** | Session started **implicitly** with the first offline transaction; no explicit start or stop. | Session uses **explicit start and stop APIs**: `startOfflineSession()` and `stopOfflineSession()`. You must start before taking offline transactions and stop when done. |
| **SDK opt-in** | `optInOfflinePayments()` on `SumUpPayment` was used to enable offline for a checkout.         | **Removed.** No per-payment opt-in. One still needs to take **one online transaction** before using offline.                                                             |
| **Supported readers** | **Solo Lite** only (firmware ≥ 2.2.1.19).                                                     | **Solo (≥ 3.3.31.0) and Solo Lite (≥ 2.2.1.19)**.                                                                                                                        |
| **Security patch validity** | API to check if the current security patch is still valid.                                    | **Removed** (Handled internally)                                                                                                                                         |
| **Automatic upload** | Upload was triggered automatically when back online).                                         | Upload **Now also runs when you call the stop session API** (every session stop triggers upload).                                                                        |
| **Session renewal** | A new session begins with next offline transaction                                            | Now the offline session start with the startOfflineSession()                                                                                                             |
| **Session time window** | Described as a fixed 24-hour session window.                                                  | **No longer static 24 hours.** The session window is whatever is set by the SumUp backend for the merchant category.                                                     |


---

## Contents

- [1. Offline session: explicit start and stop](#1-offline-session-explicit-start-and-stop)
- [2. Reader support: Solo is now supported](#2-reader-support-solo-is-now-supported)
- [Starting & ending an offline session](#starting--ending-an-offline-session)
- [Automatic upload](#automatic-upload)
- [How to call the offline APIs](#how-to-call-the-offline-apis)
- [Fetch current offline session & remaining time](#fetch-current-offline-session--remaining-time)
- [Summary](#summary)

---

## Offline session: explicit start and stop

The offline session is controlled by **explicit start and stop APIs**:

- **Start** — Call `SumUpAPI.startOfflineSession(StartOfflineSessionCallback callback)` before taking offline transactions. The callback receives a result via the **sealed class** that `StartOfflineSessionCallback` expects (StartOfflineSessionResult).
- **Stop** — Call `SumUpAPI.stopOfflineSession(StopOfflineSessionCallback callback)` when done. The callback receives a result via the **sealed class** that `StopOfflineSessionCallback` expects. Calling stop also **triggers upload** of stored offline transactions (see [Automatic upload](#automatic-upload)).

---

## Supported hardware & firmware
* Card readers: Solo Lite (≥ 2.2.1.25) and Solo (≥ 3.3.31.0)
* Attempting offline transactions with other reader models or older firmware is not supported.

---

## Starting & ending an offline session

1. **Before going offline:** Call `SumUpAPI.startOfflineSession(StartOfflineSessionCallback callback)`. Implement the callback to handle the **sealed result type**. Only after a successful start should you take offline transactions.
2. **When done with offline:** Call `SumUpAPI.stopOfflineSession(StopOfflineSessionCallback callback)`. Implement the callback to handle the **sealed result type** returned by the stop API. The stop call also triggers upload of any stored offline transactions (see [Automatic upload](#automatic-upload)).

**Note:** One **online transaction** is still required (e.g. before starting an offline session); the previous SDK-side opt-in (`optInOfflinePayments()`) has been removed.

---

## Automatic upload

Upload runs when you call the stop session API and before attempting an online transaction. Every time you call `SumUpAPI.stopOfflineSession(...)`, the SDK uploads stored offline transactions as part of stopping the session. You can still call `SumUpAPI.uploadOfflineTransactions(callback)` when needed (e.g. when back online without calling stop).

---

## Offline APIs and their purposes

| API | Purpose                                                                                                                                                             |
|-----|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `SumUpAPI.startOfflineSession(StartOfflineSessionCallback callback)` | Start an offline session before taking offline transactions. Callback receives the **sealed class** result                                                          |
| `SumUpAPI.stopOfflineSession(StopOfflineSessionCallback callback)` | Stop the offline session. Callback receives the **sealed class** result. Also triggers upload of stored offline transactions and reset the limits for next session. |
| `SumUpAPI.uploadOfflineTransactions(UploadOfflineTransactionsStatusListener callback)` | Manually upload stored offline transactions when back online.                                                         |
| `SumUpAPI.fetchCurrentOfflineSession(OfflineSessionCallback callback)` | Get current session info (remaining time, transaction counts, total approved amount etc).                                                                           |
| `SumUpAPI.updateOfflineSecurityPatch(SecurityPatchUpdateCallback callback)` | Download and apply latest offline limits/security patch.                                                                                                            |

**Removed:** `optInOfflinePayments()` on `SumUpPayment`; `SumUpAPI.fetchOfflineSecurityPatchValidity(...)` (security patch validity).

---

### 1. Start offline session

- Call before taking offline transactions. The callback receives a **sealed class** result (implement the callback to handle all variants).
- All following transactions will be attempted as offline transactions until the offline session is ended, regardless of the client connectivity status.

```java
SumUpAPI.startOfflineSession(new StartOfflineSessionCallback() {
    @Override
    public void onResult(@NonNull StartOfflineSessionResult startOfflineSessionResult) {
        final String result;
        if (startOfflineSessionResult instanceof StartOfflineSessionResult.NotEligible) {
            result = "Not eligible";
        }
        else if (startOfflineSessionResult instanceof StartOfflineSessionResult.SessionInProgress) {
            result = "Session in progress";
        }
        else if (startOfflineSessionResult instanceof StartOfflineSessionResult.OfflineLimitsAbsent) {
            result = "Offline limits absent";
        }
        else if (startOfflineSessionResult instanceof StartOfflineSessionResult.FeatureNotEnabled) {
            result = "Feature not enabled";
        }
        else if (startOfflineSessionResult instanceof StartOfflineSessionResult.TransactionsPresent) {
            result = "Transactions present";
        }
        else if (startOfflineSessionResult instanceof StartOfflineSessionResult.UserLoggedOut){
            result = "User logged out";
        }
        else if (startOfflineSessionResult instanceof StartOfflineSessionResult.Success) {
            result = "Offline session started";
        }
        else {
            result = "Failed to start offline session";
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

**Callback:** `StartOfflineSessionCallback`  
**Sealed class the callback expects:** `StartOfflineSessionResult`.

---

### 2. Stop offline session

Call when done with the offline period. The callback receives a **sealed class** result. This call **also uploads** any stored offline transactions.


**Callback:** `StopOfflineSessionCallback`  
**Sealed class the callback expects:** `StopOfflineSessionResult`.

```java
SumUpAPI.stopOfflineSession(new StopOfflineSessionCallback() {
    @Override
    public void onResult(@NonNull StopOfflineSessionResult stopOfflineSessionResult) {
        final String result;
        if (stopOfflineSessionResult instanceof StopOfflineSessionResult.UserLoggedOut) {
            result = "User logged out";
        } else if (stopOfflineSessionResult instanceof StopOfflineSessionResult.NoActiveSession) {
            result = "No active session";
        } else if (stopOfflineSessionResult instanceof StopOfflineSessionResult.Success) {
            result = "Offline session stopped";
        } else {
            result = "Failed to stop offline session";
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```
---

### 3. Upload offline transactions

- Use when you want to upload stored transactions (e.g. when back online). Upload also runs automatically when you call `stopOfflineSession`.
- This also ends the offline session with a successful upload.

```java
SumUpAPI.uploadOfflineTransactions(new UploadOfflineTransactionsStatusListener() {
    @Override
    public void onUploadFailure(@NonNull OfflineUploadFailureReasons error) {
        Log.i("Upload failed: " + error);
        new Handler(Looper.getMainLooper()).post(() -> {
            if (MainActivity.this != null && !MainActivity.this.isFinishing()) {
                Toast.makeText(MainActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onUploadSuccess() {
        Log.i("Upload success");
        new Handler(Looper.getMainLooper()).post(() -> {
            if (MainActivity.this != null && !MainActivity.this.isFinishing()) {
                Toast.makeText(MainActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
            }
        });
    }
});
```
---

### 4. Update offline security patch (Offline limits)

Download and apply the latest offline limits/security patch when the feature is enabled. Call when back online to refresh limits.

```java
SumUpAPI.updateOfflineSecurityPatch(new SecurityPatchUpdateCallback() {
    @Override
    public void onSuccess() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Security patch update successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Security patch update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
});
```
---

### 5. Fetch current offline session & remaining time

- Use `SumUpAPI.fetchCurrentOfflineSession(OfflineSessionCallback callback)` to get the current session info.
- Return:  remaining session time in millis, approved transactions, failed transactions, total approved amount.


```java
SumUpAPI.fetchCurrentOfflineSession(new OfflineSessionCallback() {
    @Override
    public void onSessionInfoReceived(OfflineSessionState offlineSessionState) {
        if (offlineSessionState instanceof OfflineSessionState.NoActiveSession) {
            // No offline session in progress.
        } else if (OfflineSessionState instanceof OfflineSessionState.ActiveSession) {
            OfflineSessionState.ActiveSession session =
                (OfflineSessionState.ActiveSession) offlineSessionState;
            long remainingMs = session.getRemainingTimeMillis(); 
            int approvedCount = session.getApprovedTransactionCount();
            int failedCount = session.getFailedTransactionCount();
            BigDecimal totalApproved = session.getTotalApprovedAmount();
            // Update UI or logic accordingly.
        }
    }
});
```

**Imports:** `OfflineSessionCallback`, `OfflineSessionState` from `com.sumup.merchant.reader.offline.*`

---

### Automatic upload
* The SDK automatically attempts to upload intrinsically, including before each **online transaction** and after every **stopOfflineSession()** API. 
* If offline transactions exist, online transactions are **blocked** until the batch uploads successfully.

## Device/session
* One active session at a time.
* One card reader per session (mixing readers risks batch declines). - Upload must use the same MID used to capture the offline batch.

## Card schemes supported while offline
* Visa
* Mastercard

## Risk note
The longer a batch remains offline, the higher the chance of later decline by the acquirer. We encourage prompt uploads.

## Disclaimers
* SumUp does not assume responsibility for offline transactions that are not uploaded or are later declined by the acquirer.
* Risk factors include (but are not limited to):
    - App storage/cache deletion or app uninstall before upload
    - Exceeding the 24‑hour window without upload
    - Device loss, theft, or damage before upload
    - Reader/session or MID mismatches
* Fields returned for offline transactions follow the online contract; some fields (e.g., payment type/status) may be absent in specific offline scenarios.

## Support
* Partnerships & enablement: integrations@sumup.com
* General: Your SumUp technical contact

