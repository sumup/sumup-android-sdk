#Changelog

For more information, see the [README](https://github.com/sumup/Android-MerchantSDK/blob/master/README.md)

## Version 1.55.1
* [INCUBATING] It is now possible to call the SDK through a startActivityForResult pattern by calling `SumUpApi.openPaymentActivity(Activity activity, SumUpPayment payment, int requestCode)`
* [IMPROVEMENT] Various bugfixes and enhancements

## Version 1.55.0
* [IMPROVEMENT] Encapsulated response flags in a static `SumUpAPI.Response` class - `SumUpAPI.EXTRA_RESULT_CODE` > `SumUpAPI.Response.RESULT_CODE` (see [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#3-response-flags) for more info)
* [IMPROVEMENT] Encapsulated response code flags in a static `SumUpAPI.Response.ResultCode` class - `SumUpAPI.TRANSACTION_SUCCESSFUL` > `SumUpAPI.Response.ResultCode.TRANSACTION_SUCCESSFUL`
* [IMPROVEMENT] Changed currency enum from name of the currency to the ISO 4217 currency code - `.currency(SumUpPayment.Currency.EURO)` > `.currency(SumUpPayment.Currency.EUR)`
* [IMPROVEMENT] Renamed package of SumUpAPI and SumUpPayment classes - `import com.kaching.merchant.SumUpAPI` > `import com.sumup.merchant.api.SumUpAPI`, `import com.kaching.merchant.SumUpPayment
` > `import com.sumup.merchant.api.SumUpPayment`, and `com.kaching.merchant.SumUpState` > `com.sumup.merchant.api.SumUpState`
* [IMPROVEMENT] Various bugfixes

## Version 1.54.1
* [FIXED] Fixed a bug that may lead to a crash in landscape mode

## Version 1.54.0
* [ADDED] Added method to clear the current Pin+ settings - `SumUpAPI.clearPinPlusSettings()`
* [IMPROVEMENT] Currency is now passed as an Enum - `.currency("EUR")` > `.currency(SumUpPayment.Currency.EURO)`
* [IMPROVEMENT] Updated design to align with material design guidelines
* [FIXED] Fixed internal Proguard rules that may lead to a rare crash if Proguard is not enabled in the host app

## Version 1.53.1
* [HOTFIX] Solved an issue that may lead to a crash when not applying Proguard to the project

## Version 1.53.0
* [UPDATE] Raised minSdkVersion to 15
* [ADDED] Result Code for the transaction
* [ADDED] Support for landscape mode on tablets
* [IMPROVEMENT] Simplified call to SumUpState object - SumUpState.Instance().updateLocales() > SumUpState.updateLocales()


## Version 1.52.2
* Change API call from SumUpState.Instance().updateLocales() to SumUpState.updateLocales()

## Version 1.52.1

* Changelog added
* ProGuard support
* Logout function added
* Added foreign transaction ID to sample app: Pass a unique identifier that will be associated with this transaction in the SumUp backend
* Reduced SDK footprint by 30%
