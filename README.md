# SumUp mPOS SDK - Android

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat-square)](https://developer.android.com/about/versions/android-4.1.html)

This repository provides a step by step documentation for SumUp's native Android SDK, that enables you to integrate our proprietary card terminal(s) and its payment platform to accept credit and debit card payments (incl. VISA, MasterCard, American Express and more). The SDK communicates transparently to the card terminal(s) via Bluetooth (BLE 4.0). Upon initiating a checkout, the SDK guides your user using appropriate screens through each step of the payment process. As part of the process, SumUp provides also the card terminal setup screen, along with the cardholder signature verification screen. The checkout result is returned with the relevant data for your records.

No sensitive card data is ever passed through to or stored on the merchant’s phone. All data is encrypted by the card terminal, which has been fully certified to the highest industry standards (PCI, EMV I & II, Visa, MasterCard & Amex).

For more information about SumUp developer products, please refer to our <a href="http://docs.sumup.com/" target="_blank"> SumUp API documentation</a>.


## Prerequisites
1. Registered for a merchant account via SumUp's [country websites](https://sumup.com/) (or received a test account)
2. Received SumUp card terminal: Solo, Air, Air Lite or PIN+ Terminal
3. Requested an Affiliate (Access) Key via [SumUp Dashboard](https://me.sumup.com/developers) for Developers
4. SumUp SDK requires `minSdkVersion` 26 or later
6. SumUp SDK ensures support for
   - `targetSDK` 31 or later
   - AGP 7.3.0 or later
   - Kotlin version 1.7.21 or later
   - Java 11 and later 

## Compatibility

* Starting with firmware version 1.0.1.84, Air card readers with serial numbers starting with 108, 109 or later require SDK version 4.0.0 and later. Please update to the latest SDK version if you need to support these readers.

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
implementation 'com.sumup:merchant-sdk:5.0.1'
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
}
```

### 3. Login
Before calling any features of the SumUp SDK, a registered SumUp merchant account needs to be logged in. Please go to https://me.sumup.com/developers to retrieve your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)

```java
SumUpLogin sumupLogin = SumUpLogin.builder(mAffiliateKey).build();
SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, 1);
```

> Note: It is also possible to login an account with a token, without the user entering their SumUp login credentials in the SDK. Please refer to section [Transparent Authentication](#5-transparent-authentication)

### 4. Make a payment
Once logged in, you can start using the SumUp SDK to accept card payments. If no account is logged in, `SumUpAPI.Response.ResultCode.ERROR_NOT_LOGGED_IN` will be returned.

```java
    SumUpPayment payment = SumUpPayment.builder()
            // mandatory parameters
            .total(new BigDecimal("1.12")) // minimum 1.00
            .currency(SumUpPayment.Currency.EUR)
            // optional: to be used only if the card reader supports the feature, what can be checked with `SumUpApi.isTipOnCardReaderAvailable()`
            .tipOnCardReader()
	    // optional: include a tip amount in addition to the total, ignored if `tipOnCardReader()` is present
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
	    // optional: skip the failed screen
            .skipFailedScreen()
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
    * SumUpAPI.Response.ResultCode.ERROR_INVALID_AMOUNT_DECIMALS = 12
    * SumUpAPI.Response.ResultCode.ERROR_API_LEVEL_TOO_LOW = 13
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

### 2. Card reader page

When a merchant is logged in, you can open this activity to access all the settings and options related to the card reader.
* This activity mainly offers
    * To be able to connect to a new card reader.
    * To be able to see the reader attributes when previously connected i.e. Battery percentage, Serial number, Last three digits of serial number, Software version.
    * Connect to the last saved reader if it is inactive.
    * Update firmware of the reader if available.
    * Visual illustration of the saved reader with it's current connectivity status and name.

```java
 	SumUpAPI.openCardReaderPage(MainActivity.this, 4);
 ```

### 3. Prepare the SumUp Card Terminal ahead of a checkout for faster experience

`prepareForCheckout()` offers the possibility to connect the card reader ahead of initiating the checkout which speeds up the overall checkout time.

To call this method, user needs to be logged in with a SumUp account and their card reader should already be setup. Next, call `prepareForCheckout()` before initiating a checkout.

> Note: Air and Solo card readers remain connected via BLE after each transaction while `prepareForCheckout()` is used when the card reader becomes disconnected (e.g. the reader is out of range, the host app looses focus, or the reader is turned off). 


### 4. Additional checkout parameters
When setting up the `SumUpPayment` object, the following optional parameters can be included:

#### Tip amount
A tip amount can be processed in addition to the `total` using the `tip` parameter. The tip amount will then be shown during the checkout process and be included in the response. Please note that a tip amount cannot be changed during/after the checkout.

##### Tip on card reader
This allows the customer to add a tip directly on the card reader, rather than prompting for a tip amount on the Android device.

A tip amount can be prompted directly in the card reader by using `tipOnCardReader` parameter, if the card reader supports tipping. See example [here](https://github.com/sumup/sumup-android-sdk#4-make-a-payment) for the field `tipOnCardReader`.

> Note: Not all card readers support this feature. To find out if the feature is supported for the last-saved card reader, you should always check `SumUpApi.isTipOnCardReaderAvailable()`. You must handle this case yourself in order to avoid no tip from being prompted.
Please also note that if both `tip` and `tipOnCardReader` are called then only `tipOnCardReader` amount will be considered during checkout if available.


#### Transaction identifier
The `foreignTransactionID` identifier will be associated with the transaction and can be used to retrieve details related to the transaction. See [API documentation](https://developer.sumup.com/rest-api/#tag/Transactions) for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.
The foreignTransactionID is available when the callback activity is called: `SumUpAPI.Param.FOREIGN_TRANSACTION_ID`

#### Skip success screen
To skip the success screen shown at the end of a successful transaction, the `skipSuccessScreen` parameter can be used. When using this parameter  your application is responsible for displaying the transaction result to the customer. In combination with the Receipts API your application can also send your own receipts, see [API documentation](https://developer.sumup.com/rest-api/#tag/Receipts) for details. Please note success screens will still be shown when using the SumUp Air Lite readers.

#### Skip failed screen
To skip the failed screen shown at the end of a failed transaction, the `skipFailedScreen` parameter can be used. When using this parameter  your application is responsible for displaying the transaction result to the customer. Please note failed screens will still be shown when using the SumUp Air Lite readers.

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
implementation "com.google.android.gms:play-services-location:19.0.1"
```
       
If the GLS dependency is not added to the project or Google Play Services are not installed on the mobile device the SumUp SDK will determine the location with the default Location Service provided by Android.
       
> NOTE: Using GLS version 19.0.1 is recommended.

## Out of Scope
The following functions are handled by the [SumUp APIs](https://developer.sumup.com/docs/api/sum-up-rest-api/):
* [Refunds](https://developer.sumup.com/docs/api/refunds/)
* [Transaction history](https://developer.sumup.com/docs/api/list-transactions/)
* [Receipts](https://developer.sumup.com/docs/api/receipts/)
* [Account management](https://developer.sumup.com/docs/api/account-details/)
* [Online Payments](https://developer.sumup.com/docs/online-payments/introduction/getting-started/)

## Community
- **Questions?** Get in contact with our integration team by sending an email to
<a href="mailto:integration@sumup.com">integration@sumup.com</a>.
- **Found a bug?** [Open an issue](https://github.com/sumup/sumup-android-sdk/issues/new).
Please provide as much information as possible.

## Changelog
 [SumUp Android SDK Changelog](https://github.com/sumup/Android-MerchantSDK/blob/master/CHANGELOG.md)

## License
[SumUp Android SDK License](https://github.com/sumup/Android-MerchantSDK/blob/master/LICENSE.md)
