# SumUp Android SDK

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-15%2B-orange.svg?style=flat-square)](http://developer.android.com/about/versions/android-4.0.3html)

**Preview build - All APIs and dependencies are subject to change.**

_NOTE: Please make sure to run 'gradle clean' after updating to version 1.53.+_

## I. Getting Started
* Create a SumUp account and get an affiliate key <a href="https://me.sumup.com/integration-tools" target="_blank">here</a>

## II. How to integrate the SumUp SDK with your app
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

##### 2. Add the dependency to a module 
```groovy
compile('com.sumup:merchant-sdk:1.55.0@aar') {
        transitive = true
    }
```

##### 3. Provide a callback activity
```xml
	<activity android:name="ResultActivity"  android:label="Payment Result">
	  <intent-filter>
	    <action android:name="com.example.ResultActivity"></action>
	    <category android:name="android.intent.category.DEFAULT"></category>
	    <category android:name="android.intent.category.BROWSABLE"></category>
	  </intent-filter>
	</activity>
```
	
##### 4. Initialize the SumUp components in your app
```java
	public class SampleApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		SumUpState.init(this);
	}

	@Override
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SumUpState.updateLocales();
	}
```

##### 5. Make a payment
```java
    SumUpPayment payment = SumUpPayment.builder()
            //mandatory parameters
            // Your affiliate key is bound to the applicationID entered in the SumUp dashboard at https://me.sumup.com/integration-tools
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

    SumUpAPI.openPaymentActivity(MainActivity.this, ResponseActivity.class, payment);
```

#III. Additional features

#####1. Include a transaction identifier

When setting up the SumUpPayment object, it is possible to pass an optional foreignTransactionID parameter. This identifier will be associated with the transaction and can be used to retrieve this transaction later. See [API documentation](https://sumup.com/integration#transactionReportingAPIs) for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.

#####2. Log out the currently logged in SumUp account
 ```java
 	SumUpAPI.logout();
 ```

#####3. Response Flags
Several response flags are available when the callback activity is called : 
* SumUpAPI.Response.RESULT_CODE
* SumUpAPI.Response.MESSAGE
* SumUpAPI.Response.TX_CODE
* SumUpAPI.Response.RECEIPT_SENT

#####4. Status code
A result code is provided within the Bundle that is passed back to the callback activity.

 ```java 
 	int resultCode = getIntent().getExtras()getInt(SumUpAPI.Response.RESULT_CODE);
 ```
 
Possible values are : 

* SumUpAPI.Response.ResultCode.TRANSACTION_SUCCESSFUL = 1
* SumUpAPI.Response.ResultCode.ERROR_TRANSACTION_FAILED = 2
* SumUpAPI.Response.ResultCode.ERROR_GEOLOCATION_REQUIRED = 3
* SumUpAPI.Response.ResultCode.ERROR_INVALID_PARAM = 4
* SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN = 5

#####5. Clear current Pin+ settings
 ```java
 	SumUpAPI.clearPinPlusSettings();
 ```

#####6. Enable ProGuard
```grovy
   buildTypes {
        release {
            //All ProGuard rules required by the SumUp SDK are packaged with the library
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
```
