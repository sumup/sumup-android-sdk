# SumUp Android SDK

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-15%2B-orange.svg?style=flat-square)](http://developer.android.com/about/versions/android-4.0.3html)

_NOTE: Please make sure to run 'gradle clean' after updating to version 1.53.+_

## I. Getting Started
* Create a SumUp account and get an affiliate key <a href="https://me.sumup.com/integration-tools" target="_blank">here</a>

## II. Integrate the SumUp SDK with your app
* You can use the sample app provided in this repository as a reference
* [Changelog](https://github.com/sumup/Android-MerchantSDK/blob/master/CHANGELOG.md)
* <a href="https://sumup.com/integration" target="_blank">Full SumUp API Documentation</a>

##### 1. Add the repository to your gradle dependencies
```groovy
allprojects {
   repositories {
      maven { url 'https://maven.sumup.com/releases' }
   }
}
```

##### 2. Set up the SDK

Add the dependency to a module
```groovy
compile('com.sumup:merchant-sdk:1.60.0@aar') {
        transitive = true
    }
```

Gradle Plugin 1.4+: Enable vector drawable support

```groovy
android {

    [...]

    defaultConfig {
       [...]

        // Allows for vector drawables in SumUp SDK
        generatedDensities = []
    }
```
	
Initialize the SumUp components in your app
```java
	public class SampleApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		SumUpState.init(this);
	}
```

##### 3. Make a payment
```java
    SumUpPayment payment = SumUpPayment.builder()
            //mandatory parameters
            // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
            .affiliateKey("YOUR_AFFILIATE_KEY")
            .productAmount(1.23)
            .currency(SumUpPayment.Currency.EUR)
            // optional: add details
            .productTitle("Taxi Ride")
            .receiptEmail("customer@mail.com")
            .receiptSMS("+3531234567890")
            // optional: Add metadata
            .addAdditionalInfo("AccountId", "taxi0334")
            .addAdditionalInfo("From", "Paris")
            .addAdditionalInfo("To", "Berlin")
            //optional: foreign transaction ID, must be unique!
            .foreignTransactionId(UUID.randomUUID().toString())  // can not exceed 128 chars
            .build();

    SumUpAPI.openPaymentActivity(MainActivity.this, payment, 1);
```

##### 4. Handle the result
```java
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == 1 && data != null) {
         // Handle the response here
      }
   }
```

##### Legacy: Handle result in new Activity

If you still prefer to handle the result of a transaction in a new Activity, provide a callback Activity in your manifest: 

```xml
	<activity android:name="ResponseActivity"  android:label="Payment Result">
	  <intent-filter>
	    <action android:name="com.example.ResponseActivity"></action>
	    <category android:name="android.intent.category.DEFAULT"></category>
	    <category android:name="android.intent.category.BROWSABLE"></category>
	  </intent-filter>
	</activity>
```

Then, open the PaymentActivity with:
 
```java
   SumUpAPI.openPaymentActivity(MainActivity.this, ResponseActivity.class, payment);
```



#III. Additional features

#####1. Include a transaction identifier

When setting up the SumUpPayment object, it is possible to pass an optional foreignTransactionID parameter. This identifier will be associated with the transaction and can be used to retrieve this transaction later. See [API documentation](https://sumup.com/integration#transactionReportingAPIs) for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.

#####2. Log out the currently logged in SumUp account
 ```java
 	SumUpAPI.logout();
 ```

#####3. Response fields
Several response fields are available when the callback activity is called : 
* SumUpAPI.Response.RESULT_CODE
  * Type : int
  * Possible Values : 
    * SumUpAPI.Response.ResultCode.TRANSACTION_SUCCESSFUL = 1
    * SumUpAPI.Response.ResultCode.ERROR_TRANSACTION_FAILED = 2
    * SumUpAPI.Response.ResultCode.ERROR_GEOLOCATION_REQUIRED = 3
    * SumUpAPI.Response.ResultCode.ERROR_INVALID_PARAM = 4
    * SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN = 5
    * SumUpAPI.Response.ResultCode.ERROR_NO_CONNECTIVITY = 6
* SumUpAPI.Response.MESSAGE
  * Type : String
  * Description : A human readable message describing the result of the payment
* SumUpAPI.Response.TX_CODE
  * Type : String
  * Description : The transaction code associated with the payment
* SumUpAPI.Response.TX_INFO
  * Type : Parcelable of type com.sumup.merchant.Models.TransactionInfo
  * Description : Transaction info object containing information about this transaction. As of SDK version 1.60.0, it contains the following information:
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
  * Type : boolean
  * Description : true if a receipt was issued to the customer, false otherwise

The response flags are provided within the Bundle that is passed back to the callback activity.

```java 
 	int resultCode = getIntent().getExtras()getInt(SumUpAPI.Response.RESULT_CODE);
 ```

#####4. Clear current PIN+ settings
 ```java
 	SumUpAPI.clearPinPlusSettings();
 ```

#####5. Enable ProGuard
```grovy
   buildTypes {
        release {
            //All ProGuard rules required by the SumUp SDK are packaged with the library
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
```

#####6. Transparent authentication

If users should be authenticated without typing in their user credentials (or know the credentials), but instead authenticate to the app transaparently without user input, you can aquire a token from our backend and pass it to the SDK when starting a payment.

To pass the access token, call `SumUpPayment.builder().accessToken("MY_ACCESS_TOKEN")`. For information about how to obtain a token, please see the [API Documentation](https://sumup.co.uk/integration#APIAuth).

If the token is invalid, `SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN` will be returned.

#####7. Runtime permissions for API Level 23

If you target 23, please ask the user to grant the following runtime permissions:

* `ACCESS_COARSE_LOCATION`
* `ACCESS_FINE_LOCATION`

If you want to use audio readers, please also ask for

* `RECORD_AUDIO`


