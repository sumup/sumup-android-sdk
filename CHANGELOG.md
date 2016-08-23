#Changelog

For more information, see the [README](https://github.com/sumup/Android-MerchantSDK/blob/master/README.md)

## Version 1.61.0
* [UPDATE] Raises targetSdkVersion to 23 - SDK will now request runtime permissions on API 23 level and up
* [UPDATE] Uses Vector support library - It is no longer required to set `generatedDensities = []`
* [API CHANGE] ResultCode.ERROR_PERMISSION_DENIED is returned if a runtime permission is denied by the user on API level 23+
* [ADDED] Adds support for swiss francs - `Currency.CHF`
* [IMPROVEMENT] Complete redesign of the PIN+ reader’s setup flow
* [IMPROVEMENT] Improved bluetooth discovery and connection stability
* [IMPROVEMENT] Removes `materialish-progress` dependency
* [UPDATE] Replace `frozen` dependency with `icepick`

## Version 1.60.0
* [API CHANGE] Removes SumUpState.updateLocales() - SDK users no longer have to call this
* [API CHANGE] Adds use of vector drawables to reduce the size of the SDK - See [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#2-set-up-the-sdk) for required changes
* [ADDED] Adds user transparent background authentication - See [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#6-transparent-authentication) to get started
* [ADDED] Adds a contact picker to the receipt screen
* [IMPROVEMENT] Starting a payment with startActivityForResult is now the recommended way to start a payment - See [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#4-handle-the-result) for more info
* [IMPROVEMENT] Adds TX Info object to response
* [IMPROVEMENT] No longer depends on NineOldAndroids and ListViewAnimations - Check your excludes
* [IMPROVEMENT] Support for API Level 23 - See [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#7-runtime-permissions-for-api-level-23) for required runtime permissions
* [IMPROVEMENT] Adds error code for network issues - `SumUpAPI.Response.ResultCode.ERROR_NO_CONNECTIVITY`

## Version 1.55.1
* [INCUBATING] It is now possible to call the SDK through a startActivityForResult pattern by calling `SumUpApi.openPaymentActivity(Activity activity, SumUpPayment payment, int requestCode)`
* [IMPROVEMENT] Various bugfixes and enhancements

## Version 1.55.0
* [IMPROVEMENT] Encapsulates response flags in a static `SumUpAPI.Response` class - `SumUpAPI.EXTRA_RESULT_CODE` > `SumUpAPI.Response.RESULT_CODE` (see [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#3-response-fields) for more info)
* [IMPROVEMENT] Encapsulates response code flags in a static `SumUpAPI.Response.ResultCode` class - `SumUpAPI.TRANSACTION_SUCCESSFUL` > `SumUpAPI.Response.ResultCode.TRANSACTION_SUCCESSFUL`
* [IMPROVEMENT] Changes currency enum from name of the currency to the ISO 4217 currency code - `.currency(SumUpPayment.Currency.EURO)` > `.currency(SumUpPayment.Currency.EUR)`
* [IMPROVEMENT] Renames package of SumUpAPI and SumUpPayment classes - `import com.kaching.merchant.SumUpAPI` > `import com.sumup.merchant.api.SumUpAPI`, `import com.kaching.merchant.SumUpPayment
` > `import com.sumup.merchant.api.SumUpPayment`, and `com.kaching.merchant.SumUpState` > `com.sumup.merchant.api.SumUpState`
* [IMPROVEMENT] Various bugfixes

## Version 1.54.1
* [FIXED] Fixed a bug that may lead to a crash in landscape mode

## Version 1.54.0
* [ADDED] Adds method to clear the current Pin+ settings - `SumUpAPI.clearPinPlusSettings()`
* [IMPROVEMENT] Currency is now passed as an Enum - `.currency("EUR")` > `.currency(SumUpPayment.Currency.EURO)`
* [IMPROVEMENT] Updates design to align with material design guidelines
* [FIXED] Fixes internal Proguard rules that may lead to a rare crash if Proguard is not enabled in the host app

## Version 1.53.1
* [HOTFIX] Solves an issue that may lead to a crash when not applying Proguard to the project

## Version 1.53.0
* [UPDATE] Raises minSdkVersion to 15
* [ADDED] Result Code for the transaction
* [ADDED] Support for landscape mode on tablets
* [IMPROVEMENT] Simplifies call to SumUpState object - SumUpState.Instance().updateLocales() > SumUpState.updateLocales()


## Version 1.52.2
* Changes API call from SumUpState.Instance().updateLocales() to SumUpState.updateLocales()

## Version 1.52.1

* Adds changelog 
* ProGuard support
* Adds logout function
* Adds foreign transaction ID to sample app: Pass a unique identifier that will be associated with this transaction in the SumUp backend
* Reduces SDK footprint by 30%
