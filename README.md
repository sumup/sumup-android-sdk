# SumUp mPOS SDK - Android

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-15%2B-orange.svg?style=flat-square)](https://developer.android.com/about/versions/android-4.0.3.html)

This repository provides a step by step documentation for SumUp's native Android SDK, that enables you to integrate our proprietary card terminal(s) and its payment platform to accept credit and debit card payments (incl. VISA, MasterCard, American Express and more). The SDK communicates transparently to the card terminal(s) via Bluetooth (BLE 4.0). Upon initiating a checkout, the SDK guides your user using appropriate screens through each step of the payment process. As part of the process, SumUp provides also the card terminal setup screen, along with the cardholder signature verification screen. The checkout result is returned with the relevant data for your records.

No sensitive card data is ever passed through to or stored on the merchant’s phone. All data is encrypted by the card terminal, which has been fully certified to the highest industry standards (PCI, EMV I & II, Visa, MasterCard & Amex).

For more information about SumUp developer products, please refer to our <a href="https://sumup.com/docs" target="_blank"> SumUp API documentation</a>.


## Prerequisites
1. Registered for a merchant account via SumUp's [country websites](https://sumup.com/) (or received a test account).
2. Received SumUp card terminal: Air, Air Lite or PIN+ Terminal
3. Requested an Affiliate (Access) Key via [SumUp Dashboard](https://me.sumup.com/developers) for Developers.
4. Android API 15 or later
5. `targetSdkVersion` 24 or later (hard requirement with upcoming SDK 3.0.0)

## I. Integrate the SumUp SDK

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
compile('com.sumup:merchant-sdk:2.4.0@aar') {
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

### 3. Make a payment
```java
    SumUpPayment payment = SumUpPayment.builder()
            // mandatory parameters
            // Please go to https://me.sumup.com/developers to retrieve your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
            .affiliateKey("YOUR_AFFILIATE_KEY")
            .productAmount(1.12)
            .currency(SumUpPayment.Currency.EUR)
	    // optional: include a tip amount in addition to the productAmount
	    .tipAmount(0.10)
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
	    // optional: skip the success screen
	    .skipSuccessScreen()
            .build();

    SumUpAPI.openPaymentActivity(MainActivity.this, payment, 2);
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


## II. Additional features

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
  * Description: Transaction info object containing information about this transaction. It contains the following information:
    - Transaction Code
    - Merchant Code
    - Amount (including tip amount and VAT)
    - Tip amount
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

To access the payment settings, a registered SumUp merchant account needs to be logged in:
 ```java
 	SumUpLogin sumUplogin = SumUpLogin.builder(mAffiliateKey).build();
 	SumUpAPI.openLoginActivity(MainActivity.this, sumUplogin, 1);
 ```

### 3. Prepare the SumUp Card terminal in advance
To prepare a SumUp card terminal for checkout, a registered SumUp merchant account needs to be logged in and the card terminal will have been already setup. 
Calling `prepareForCheckout()` before instancing a checkout will speed up the checkout time.


### 4. Additional checkout parameters
When setting up the `SumUpPayment` object, the following optional parameters can be included:

#### Tip amount
A tip amount can be processed in addition to the `productAmount` using the `tipAmount` parameter. The tip amount will then be shown during the checkout process and be included in the response. Please note that a tip amount cannot be changed during/after the checkout.

#### Transaction identifier
The `foreignTransactionID` identifier will be associated with the transaction and can be used to retrieve details related to the transaction. See [API documentation](https://sumup.com/docs/rest-api/transactions-api) for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.
The foreignTransactionID is available when the callback activity is called: `SumUpAPI.Param.FOREIGN_TRANSACTION_ID`

#### Skip success screen
To skip the screen shown at the end of a successful transaction, the `skipSuccessScreen` parameter can be used. When using the parameter  your application is responsible for displaying the transaction result to the customer. In combination with the Receipts API your application can also send your own receipts, see [API documentation](https://sumup.com/docs/rest-api/transactions-api) for details. Please note success screens will still be shown when using the SumUp Air Lite readers.

### 5. Transparent authentication

If users should be authenticated without typing in their user credentials (or knowing the credentials), but instead authenticate to the app transparently without user input, you can aquire a token from our backend and pass it to the SDK when starting a payment.

To pass the access token, call `SumUpPayment.builder().accessToken("MY_ACCESS_TOKEN")`. For information about how to obtain a token, please see the [API Documentation](https://sumup.com/docs/oauth/).

If the token is invalid, `SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN` will be returned.


### 6. Log out SumUp account
 ```java
 	SumUpAPI.logout();
 ```


### 7. Enable ProGuard
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

## Community
- **Questions?** Get in contact with our integration team by sending an email to
<a href="mailto:integration@sumup.com">integration@sumup.com</a>.
- **Found a bug?** [Open an issue](https://github.com/sumup/sumup-android-sdk/issues/new).
Please provide as much information as possible.

## Changelog
 [SumUp Android SDK Changelog](https://github.com/sumup/Android-MerchantSDK/blob/master/CHANGELOG.md)

## License
[SumUp Android SDK License](https://github.com/sumup/Android-MerchantSDK/blob/master/LICENSE.md)
