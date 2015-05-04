# Android-MerchantSDK

This is a sample application for the SumUp Android SDK.

**Note: Alpha version - All APIs and dependencies are subject to change**

##How to use the SDK


+ Get a developer Key: https://sumup.co.uk/api


+ Add the repository to your gradle dependencies 
	```groovy
	allprojects {
	    repositories {
	        maven { url 'https://maven.sumup.com/releases' }
	    }
	}
	```

	Add the dependency to a module
	```groovy
	compile 'com.sumup:merchant-sdk:1.52.0@aar'
	```

+ Provide a callback activity
	```xml
	<activity android:name="ResultActivity"  android:label="Payment Result">
	  <intent-filter>
	    <action android:name="com.example.ResultActivity"></action>
	    <category android:name="android.intent.category.DEFAULT"></category>
	    <category android:name="android.intent.category.BROWSABLE"></category>
	  </intent-filter>
	</activity>
	```


+ Make a payment
	```java
	// create SumUpPayment with mandatory parameters
	String affiliateKey = "abcd1234wxyz";
	double productAmount = 12.34;
	String currency = "EUR";

	// reference YOUR OWN activity here that should receive the payment result
	String resultActivity = "com.example.ResultActivity";

	SumUpPayment sumUpPayment = new SumUpPayment(affiliateKey, productAmount, currency, resultActivity, this);

	// optional: add details
	sumUpPayment.setProductTitle("Taxi Ride");
	sumUpPayment.setReceiptEmail("customer@mail.com");
	sumUpPayment.setReceiptSMS("+3531234567890");

	// optional: Add additional metadata
	HashMap<String, String> additionalInfo = new HashMap(3);
	additionalInfo.put("AccountId","taxi0334");
	additionalInfo.put("From","Here");
	additionalInfo.put("To","There");
	sumUpPayment.setAdditionalInfo(additionalInfo);

	// start the payment process
	sumUpPayment.openPaymentActivity(this);
	```




