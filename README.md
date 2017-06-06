# SumUp Android SDK with Miura integration

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-15%2B-orange.svg?style=flat-square)](http://developer.android.com/about/versions/android-4.0.3html)

NOTE:

* We strongly advise against having the payleven SDK and the SumUp SDK integrated simultaneously in one application due to highly probable conflicts between the underlying Adyen instances.
* This particular integration is ONLY to be used by existing payleven integration partners.
* Upon migration of payleven merchant accounts to the SumUp systems, accounts and card readers will be fully operational.
* Integrating and running the SumUP SDK does not require the SumUp Android app to be installed on your mobile device.


## Prerequisites
1. Received a test account.
2. Requested an Affiliate (Access) Key via [SumUp Dashboard](https://me.sumup.com/developers) for Developers.
3. Android API 15 or later
4. `targetSdkVersion` 24 or later (hard requirement with upcoming SDK 3.0.0)

## I. Implementation notes for migrating to the SumUp SDK


While migrating from the payleven SDK to the SumUp SDK, please pay particular attention to the main differences outlined below:

* As opposed to the payleven SDK, where the Miura card reader needed to be paired in the Bluetooth settings of the mobile device, the SumUp SDK manages the Bluetooth pairing AND bonding of the card reader as part of the first payment attempt or can be conducted separately within the payment settings.
* For security purposes the user's current location is required to accept payments. While for the payleven SDK implementation, geolocation (lattitude, longitude) was required to be provided during the payment request, the SumUp SDK retrieves the location on it’s own.

## II. Integrate the SumUp SDK Snapshot for the Miura integration
  * You can use the sample app provided in this repository as a reference

### 1. Dependency
Add the repository to your gradle dependencies:

```groovy
allprojects {
   repositories {
      maven { url 'https://maven.sumup.com/releases' }
   }
}
```

Add the dependency to a module:

```groovy
compile('com.sumup:merchant-sdk:2.3.0@aar') {
        transitive = true
    }
```


### 2. Initialization


Initialize the SumUp components in your app:

```java
	public class SampleApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		SumUpState.init(this);
	}
```

Add Adyen related service and receiver in your Manifest

```xml
<!-- Required for bluetooth communication with an Adyen terminal -->
     <receiver android:name="com.adyen.adyenpos.receiver.BluetoothState">

         <intent-filter>
             <action android:name="android.bluetooth.adapter.action.STATE_CHANGED"/>
             <action android:name="android.bluetooth.device.action.UUID"/>
         </intent-filter>
     </receiver>

     <!-- Required for bluetooth communication with an Adyen terminal (when enableEagerConnection is used)-->
     <service android:name="com.adyen.adyenpos.service.TerminalConnectIntentService"/>
```

##### 3. Make a payment
```java
    SumUpPayment payment = SumUpPayment.builder()
            // mandatory parameters
            // Please go to https://me.sumup.com/developers to retrieve your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
            .affiliateKey("YOUR_AFFILIATE_KEY")
            .productAmount(1.12)
            .currency(SumUpPayment.Currency.EUR)
            // optional: add details
            .productTitle("Taxi Ride")
            .receiptEmail("customer@mail.com")
            .receiptSMS("+3531234567890")
            // optional: Add metadata
            .addAdditionalInfo("AccountId", "taxi0334")
            .addAdditionalInfo("From", "Paris")
            .addAdditionalInfo("To", "Berlin")
            // optional: foreign transaction ID, must be unique!
            .foreignTransactionId(UUID.randomUUID().toString())  // can not exceed 128 chars
            .build();

    SumUpAPI.openPaymentActivity(MainActivity.this, payment, 1);
```

### 4. Handle payment result
```java
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == 2 && data != null) {
         // Handle the response here
      }
   }
```


## III. Additional features

### 1. Response fields
Several response fields are available when the callback activity is called:
* SumUpAPI.Response.RESULT_CODE
  * Type: int
  * Possible Values:
    * SumUpAPI.Response.ResultCode.SUCCESSFUL = 1
    * SumUpAPI.Response.ResultCode.ERROR_TRANSACTION_FAILED = 2
    * SumUpAPI.Response.ResultCode.ERROR_GEOLOCATION_REQUIRED = 3
    * SumUpAPI.Response.ResultCode.ERROR_INVALID_PARAM = 4
    * SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN = 5
    * SumUpAPI.Response.ResultCode.ERROR_NO_CONNECTIVITY = 6
    * SumUpAPI.Response.ResultCode.ERROR_PERMISSION_DENIED = 7
    * SumUpAPI.Response.ResultCode.ERROR_NOT_LOGGED_IN = 8
* SumUpAPI.Response.MESSAGE
  * Type: String
  * Description: A human readable message describing the result of the payment
* SumUpAPI.Response.TX_CODE
  * Type: String
  * Description: The transaction code associated with the payment
* SumUpAPI.Response.TX_INFO
  * Type: Parcelable of type com.sumup.merchant.Models.TransactionInfo
  * Description: Transaction info object containing information about this transaction. As of SDK version 1.60.0, it contains the following information:
    - Transaction Code
    - Merchant Code
    - Amount
    - VAT
    - Currency (e.g. EUR)
    - Payment Status (PENDING | SUCCESSFUL | CANCELLED | FAILED)
    - Payment Type (CASH | POS | ECOM | UNKNOWN | RECURRING | BITCOIN | BALANCE)
    - Entry Mode (e.g. CHIP)
    - Number of Installments
    - Card Type (e.g. MASTERCARD)
    - Last four digits of the card
    - Product information
* SumUpAPI.Response.RECEIPT_SENT
  * Type: boolean
  * Description: true if a receipt was issued to the customer, false otherwise

The response flags are provided within the Bundle that is passed back to the callback activity:

```java 
 	int resultCode = getIntent().getExtras().getInt(SumUpAPI.Response.RESULT_CODE);
 ```

### 2. Payment settings

```java
 	SumUpAPI.openPaymentSettingsActivity(MainActivity.this, 3);
 ```

To access the payment settings, it is required to be logged in:
 ```java
 	SumUpLogin sumUplogin = SumUpLogin.builder(mAffiliateKey).build();
 	SumUpAPI.openLoginActivity(MainActivity.this, sumUplogin, 1);
 ```

### 3. Transaction identifier

When setting up the `SumUpPayment` object, it is possible to pass an optional `foreignTransactionID` parameter. This identifier will be associated with the transaction and can be used to retrieve details related to the transaction. See [API documentation](https://sumup.com/docs/rest-api/transactions-api) for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.


### 4. Transparent authentication

If users should be authenticated without typing in their user credentials (or knowing the credentials), but instead authenticate to the app transaparently without user input, you can aquire a token from our backend and pass it to the SDK when starting a payment.

To pass the access token, call `SumUpPayment.builder().accessToken("MY_ACCESS_TOKEN")`. For information about how to obtain a token, please see the [API Documentation](https://sumup.com/docs/oauth/).

If the token is invalid, `SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN` will be returned.


### 5. Log out SumUp account
 ```java
 	SumUpAPI.logout();
 ```


### 6. Enable ProGuard
```grovy
   buildTypes {
        release {
            // All ProGuard rules required by the SumUp SDK are packaged with the library
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
```

## Out of Scope
The following functions are handled by the [SumUp APIs](https://sumup.com/docs/oauth/):
* [Refunds](http://docs.sumup.com/rest-api/transactions-api/#merchant-refunds)
* [Transaction history](http://docs.sumup.com/rest-api/transactions-api/#merchant-transactions)
* [Receipts](http://docs.sumup.com/rest-api/transactions-api/#receipts)
* [Account management](http://docs.sumup.com/rest-api/accounts-api/)
