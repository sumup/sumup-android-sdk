# SumUp mPOS SDK - Android

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat-square)](https://developer.android.com/about/versions/android-4.1.html)

This repository provides a step by step documentation for SumUp's native Android SDK, that enables you to integrate our proprietary card terminal(s) and its payment platform to accept credit and debit card payments (incl. VISA, MasterCard, American Express and more). The SDK communicates transparently to the card terminal(s) via Bluetooth (BLE 4.0). Upon initiating a checkout, the SDK guides your user using appropriate screens through each step of the payment process. As part of the process, SumUp provides also the card terminal setup screen, along with the cardholder signature verification screen. The checkout result is returned with the relevant data for your records.

No sensitive card data is ever passed through to or stored on the merchant’s phone. All data is encrypted by the card terminal, which has been fully certified to the highest industry standards (PCI, EMV I & II, Visa, MasterCard & Amex).

For more information about SumUp developer products, please refer to our <a href="http://docs.sumup.com/" target="_blank"> SumUp API documentation</a>.


## Prerequisites
1. Registered for a merchant account via SumUp's [country websites](https://sumup.com/) (or received a test account).
2. Received SumUp card terminal: Air, Air Lite or PIN+ Terminal
3. Requested an Affiliate (Access) Key via [SumUp Dashboard](https://me.sumup.com/developers) for Developers.
4. `minSdkVersion` 16 or later
5. `targetSdkVersion` 24 or later (together with Support Library 24.2.0 or later)
> SDK 3.3.0 will raise `targetSdkVersion` to 28. (as a consequence of the migration to AndroidX) (see point below)
6. Following Google's best practices SDK 3.3.0 will migrate to AndroidX. For more information about AndroidX and how to migrate see [Google AndroidX Documentation](https://developer.android.com/jetpack/androidx)

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
compile 'com.sumup:merchant-sdk:3.2.2'
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

### 3. Login
Before calling any features of the SumUp SDK, a registered SumUp merchant account needs to be logged in. Please go to https://me.sumup.com/developers to retrieve your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)

```java
SumUpLogin sumupLogin = SumUpLogin.builder(mAffiliateKey).build();
SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, 1);
```

> Note: It is also possible to login an account with a token, without the user entering their SumUp login credentials in the SDK. Please refer to section [Transaction Authentication](#5-transparent-authentication)

### 4. Make a payment
Once logged in, you can start using the SumUp SDK to accept card payments. If no account is logged in, `SumUpAPI.Response.ResultCode.ERROR_NOT_LOGGED_IN` will be returned.

```java
    SumUpPayment payment = SumUpPayment.builder()
            // mandatory parameters
            .total(new BigDecimal("1.12")) // minimum 1.00
            .currency(SumUpPayment.Currency.EUR)
	    // optional: include a tip amount in addition to the total
	    .tip(new BigDecimal("0.10"))
            // optional: add details
            .title("Taxi Ride")
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

    SumUpAPI.checkout(MainActivity.this, payment, 2);
```

### 5. Handle payment result
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
    * SumUpAPI.Response.ResultCode.ERROR_DUPLICATE_FOREIGN_TX_ID = 9
    * SumUpAPI.Response.ResultCode.ERROR_INVALID_AFFILIATE_KEY = 10
    * SumUpAPI.Response.ResultCode.ERROR_ALREADY_LOGGED_IN = 11
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

When a merchant is logged in, you can enable them to change their payment method settings, e.g. select and pair their preferred card terminal. The preferences available to a merchant depend on the individual merchant settings.

```java
 	SumUpAPI.openPaymentSettingsActivity(MainActivity.this, 3);
 ```

### 3. Prepare the SumUp Card terminal in advance
To prepare a SumUp card terminal for checkout, a registered SumUp merchant account needs to be logged in and the card terminal will have been already setup. 
Calling `prepareForCheckout()` before instancing a checkout will speed up the checkout time.


### 4. Additional checkout parameters
When setting up the `SumUpPayment` object, the following optional parameters can be included:

#### Tip amount
A tip amount can be processed in addition to the `total` using the `tip` parameter. The tip amount will then be shown during the checkout process and be included in the response. Please note that a tip amount cannot be changed during/after the checkout.

#### Transaction identifier
The `foreignTransactionID` identifier will be associated with the transaction and can be used to retrieve details related to the transaction. See [API documentation](https://developer.sumup.com/rest-api/#tag/Transactions) for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.
The foreignTransactionID is available when the callback activity is called: `SumUpAPI.Param.FOREIGN_TRANSACTION_ID`

#### Skip success screen
To skip the screen shown at the end of a successful transaction, the `skipSuccessScreen` parameter can be used. When using the parameter  your application is responsible for displaying the transaction result to the customer. In combination with the Receipts API your application can also send your own receipts, see [API documentation](https://developer.sumup.com/rest-api/#tag/Receipts) for details. Please note success screens will still be shown when using the SumUp Air Lite readers.

### 5. Transparent authentication

To authenticate an account without the user typing in their SumUp credentials each time, you can generate an access token using OAuth2.0 and use it to transparently login to the SumUp SDK.

```java
SumUpLogin sumupLogin = SumUpLogin.builder(mAffiliateKey).accessToken("MY_ACCESS_TOKEN").build();
SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, 1);
```

For information about how to obtain a token, please see the [API Documentation](https://developer.sumup.com/docs/authorization).

If the token is invalid, `SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN` will be returned.

### 6. Retrieve data of the current merchant account

If a merchant account is currently logged in, it is possible to retrieve the data for this account.

```java
	if (!SumUpAPI.isLoggedIn()) {
		// no merchant account currently logged in
	} else {
		Merchant currentMerchant = SumUpAPI.getCurrentMerchant();
	}
```

### 7. Log out SumUp account
 ```java
 	SumUpAPI.logout();
 ```


### 8. Enable ProGuard
```groovy
   buildTypes {
        release {
            // All ProGuard rules required by the SumUp SDK are packaged with the library
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
```


### 9. Use Google Location Services
       
The SDK supports Google Location Services, to improve location accuracy and reduce power consumption.

In order to use it you need to add the dependency in build.gradle file 
```groovy
implementation "com.google.android.gms:play-services-location:16.0.0"
```
       
If the GLS dependency is not added to the project or Google Play Services are not installed on the device the SDK will determine the location with the default Location Service.
       
> IMPORTANT: Google Location Services can only be used with Support Library 25.1.0 or above

## Out of Scope
The following functions are handled by the [SumUp APIs](https://developer.sumup.com/docs/authorization#authorization-scopes):
* [Refunds](https://developer.sumup.com/docs/authorization#restricted-scopes)
* [Transaction history](https://developer.sumup.com/docs/authorization#optional-scopes)
* [Receipts](https://developer.sumup.com/docs/authorization#optional-scopes)
* [Account management](https://developer.sumup.com/docs/authorization#optional-scopes)
* [Payments](https://developer.sumup.com/docs/authorization#restricted-scopes)

## Community
- **Questions?** Get in contact with our integration team by sending an email to
<a href="mailto:integration@sumup.com">integration@sumup.com</a>.
- **Found a bug?** [Open an issue](https://github.com/sumup/sumup-android-sdk/issues/new).
Please provide as much information as possible.

## Changelog
 [SumUp Android SDK Changelog](https://github.com/sumup/Android-MerchantSDK/blob/master/CHANGELOG.md)

## License
[SumUp Android SDK License](https://github.com/sumup/Android-MerchantSDK/blob/master/LICENSE.md)
