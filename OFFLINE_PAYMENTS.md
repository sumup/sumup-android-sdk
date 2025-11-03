# Offline payments

Offline payments enable merchants to continue accepting card-present transactions when the app is unable to reach all required SumUp backend services (e.g., network loss or backend outage) or when there is no internet connection. Transactions performed while offline are securely stored on the Android device under encryption and uploaded as a batch when connectivity and services are available again.
Offline acceptance is session‑based and limited in time and volume.
* Uploading cached transactions promptly after coming back online reduces acquirer decline risk.
* The SDK also auto‑uploads pending offline batches before the next online transaction.
* foreignTransactionId is a mandatory field for offline payments

## Supported hardware & firmware
* Card reader: Solo Lite only
* Minimum firmware: 2.2.1.25.
* Attempting offline transactions with other reader models or older firmware is not supported.

## Eligibility & opting in
There are two layers of enablement:
* Integrator enablement — The partner is onboarded via SumUp Partnerships with an executed contract extension covering offline terms.
* Merchant enablement — Specific merchant IDs (MIDs) are remotely enabled by the Partnerships team after the integrator supplies the list of MIDs.

###  SDK‑side opt‑in:
* Use the payment builder’s optInOfflinePayments() to request offline usage.
* Note: Calling optInOfflinePayments() does not auto‑enable offline; the MID must be approved server‑side.
  Precondition (first‑time):  The merchant must complete at least one online card‑present transaction using the updated reader and paired device before being allowed to go offline.

## Security patch validity
* Before beginning an offline session, validate the device’s offline security configuration:
fetchOfflineSecurityPatchValidity(callback: SecurityPatchValidityCallback)
This callback returns either
* Valid(val remainingTimeMillis: Long): SecurityPatchValidityResult ➜ you may begin/continue an offline session.
* NotValid: SecurityPatchValidityResult ➜ re‑fetch to refresh the security patch. Patches expire over time; revalidation may be required.
E.g.:
```kotlin
when (result) {
    is SecurityPatchValidityResult.Valid -> { /* valid */ }
    is SecurityPatchValidityResult.NotValid -> { /* not valid */ }
}
```



## Starting & ending an offline session
### What counts as “offline”?
* The device can’t reach all required SumUp backend services, either because:
* No usable/turned off internet connection (Wi‑Fi and/or cellular), or
* SumUp payment services are temporarily unavailable.
### Session lifecycle
* Begins with the first offline transaction attempt.
* Ends when the app is back online and the batch is uploaded (automatically or programmatically).
* Only one active offline session is permitted at a time, and it must be bound to one reader device.*
  
## Uploading offline transactions
### Manual upload
* uploadOfflineTransactions(listener: UploadOfflineTransactionsStatusListener)
* Triggers upload when:
  - Internet connectivity is available, and
  - SumUp backend services are reachable.
* Must be invoked while logged in as the same MID that accepted the offline transactions.
```kotlin
SumUpAPI.uploadOfflineTransactions(new UploadOfflineTransactionsStatusListener() {
  @Override
  public void onUploadSuccess() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(
          MainActivity.this,
          "Offline transactions uploaded successfully",
          Toast.LENGTH_LONG
        ).show();
      }
    });
  }

  @Override
  public void onUploadFailure(@NonNull OfflineUploadFailureReasons offlineUploadFailureReasons) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(
          MainActivity.this,
          "Failed to upload offline transactions: " + offlineUploadFailureReasons.getMessage(),
          Toast.LENGTH_LONG
        ).show();
      }
    });
  }
});
}
});

```
  

### Automatic upload
* The SDK automatically attempts to upload intrinsically, including before each online transaction.
* If offline transactions exist, online transactions are blocked until the batch uploads successfully.

### Session renewal
After a successful upload, the current offline session is considered complete; a new session will begin with the next offline transaction (subject to constraints).
  
## Querying session status
* Use the session inquiry API anytime during an active session using fetchCurrentOfflineSession(callback: OfflineSessionCallback)
* If active, the callback provides:
  - remainingTimeMillis: Long — Time left within the 24‑hour session window.
  - approvedTransactionCount: Int — Locally approved transactions.
  - failedTransactionCount: Int — Locally failed transactions.
  - totalApprovedAmount: BigDecimal — Sum of locally approved amounts (limits apply to approved totals).


```kotlin
SumUpAPI.fetchCurrentOfflineSession(new OfflineSessionCallback() {
  @Override
  public void onSessionInfoReceived(@NonNull OfflineSessionResult offlineSessionResult) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        mOfflineSessionInfo.setText("Offline Session: " + offlineSessionResult);
      }
    });
  }
});


```
    

## Limits & constraints
### Time
* Max offline duration: 24 hours from the first offline transaction. After 24h, new offline transactions are prevented until upload occurs.

### Transaction policy
* Max transactions per session: 75
* Max amount per transaction: €50
  
## Device/session
* One active session at a time.
* One card reader per session (mixing readers risks batch declines). - Upload must use the same MID used to capture the offline batch.

## Card schemes supported while offline
* Visa
* Mastercard

## Risk note
The longer a batch remains offline, the higher the chance of later decline by the acquirer. Encourage prompt uploads.

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
