# Changelog

For more information, see the [README](https://github.com/sumup/Android-MerchantSDK/blob/master/README.md)

## Version 3.2.0
* [IMPROVEMENT] New styles for SumUp UI screens
* [IMPROVEMENT] Adds Google Location Service library support. See [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#9-use-google-location-services) for required changes 
* [IMPROVEMENT] Minor bug fixes and enhancements on the Bluetooth scanning and connection
* [IMPROVEMENT] Adjust back button behavior during ongoing transaction [issue 67](https://github.com/sumup/sumup-android-sdk/issues/67)
* [ADDED] Adds support for the SumUp 3G reader
* [FIXED] Fix support for 64 bits to align with the Play Store requirements [issue 80](https://github.com/sumup/sumup-android-sdk/issues/80) [issue 81](https://github.com/sumup/sumup-android-sdk/issues/81)
* [FIXED] Missing index parameter to string [issue 61](https://github.com/sumup/sumup-android-sdk/issues/61)
* [UPCOMING] SDK 3.3.0 will migrate to AndroidX. As a consequence of this migration, *minimum* `targetSdkVersion` will be raised to 28. See [README](https://github.com/sumup/sumup-android-sdk/blob/master/README.md#prerequisites) for more info

## Version 3.1.2
* [FIXED] A crash that can occur when pressing "Back" button in the success screen

## Version 3.1.1
* [FIXED] Minimum required targetSdkVersion - set to API 24 from 3.0.0

## Version 3.1.0
* [Added] Adds `getCurrentMerchant` method to retrieve the data of the current logged in merchant account
* [Added] Adds `isLoggedIn` method to retrieve if an account is currently logged in
* [IMPROVEMENT] Switch from otto to greenrobot EventBus
* [IMPROVEMENT] Avoid dead thread warning when closing bluetooth connection
* [FIXED] Handles "NOT_ALLOWED" error code correctly (issue highlighted in [issue 57](https://github.com/sumup/sumup-android-sdk/issues/57))
* [FIXED] A crash that can occur when back button is pressed while connecting to the card reader
* [FIXED] A crash that can occur when calling Payment Settings while connection to the card reader is ongoing
* [FIXED] A crash that can occur when the host app for the SDK is brought to the background while a transaction is ongoing

## Version 3.0.0
* [IMPROVEMENT] Raises targetSdkVersion to API 27 - it is still possible to target API 24 and above with a Gradle ResolutionStrategy (see example in [build.gradle](https://github.com/sumup/Android-SDKSampleApp/blob/master/app/build.gradle))
* [IMPROVEMENT] Removes unnecessary `READ_PHONE_STATE` permission 
* [FIXED] A compilation problem highlighted in [issue 32](https://github.com/sumup/sumup-android-sdk/issues/32) - Workaround described there is no longer necessary
* [FIXED] A crash that can occur when establishing a Bluetooth connection with the card reader 
* [API CHANGE] Removes fields and methods deprecated in previous versions

## Version 2.5.2
* [API CHANGE] Deprecates passing affiliateKey in SumUpPayment. Now it's only required to pass it to SumUpLogin
* [ADDED] Adds support for new european country - `et`
* [FIXED] A crash that can occur while scanning for a device to setup
* [FIXED] A freeze than can occur after a successful setup, and before starting a checkout
* [FIXED] A crash that can occur when cancelling a checkout

## Version 2.5.1
* [ADDED] Enforces TLS 1.2 for Android devices running between Android 4.4.4 and Android 4.1 - Mandatory update by May 2018 if supporting Android 4.4.4 and below

## Version 2.5.0
* [IMPROVEMENT] Decoupled login from checkout - A SumUp account can be logged in independently of performing a checkout 
* [ADDED] Adds support for Bulgarian lev, Chilean Peso, and Danish Krone - `Currency.BGN`, `Currency.CLP`, and `Currency.DKK`
* [ADDED] Adds support for new european countries - `bg`, `da`, `sk`
* [API CHANGE] `SumUpApi.openPaymentActivity(Activity activity, SumUpPayment payment, int requestCode)` is deprecated in favor of `SumUpApi.checkout(Activity activity, SumUpPayment payment, int requestCode)`
* [API CHANGE] `SumUpPayment.productAmount(double) and SumUpPayment.tipAmount(double)` are deprecated in favor of `SumUpPayment.total(BigDecimal) and SumUpPayment.tip(BigDecimal)`
* [API CHANGE] `SumUpPayment.productTitle(String)` is deprecated in favor of `SumUpPayment.title(String)`
* [IMPROVEMENT] Various bug fixes and enhancements around the Bluetooth scanning and connection

## Version 2.4.1
* [ADDED] Adds support for Czech koruna and Hungarian forint - `Currency.NOK`, `Currency.HUF`
* [ADDED] Adds support for new european countries - `cs`, `hu`, `lv`

## Version 2.4.0
* [FIXED] Fixes conflict with Dagger 2 - Switched from Dagger 1 to Toothpick
* [IMPROVEMENT] Adds error code for duplicate foreign transaction id - `SumUpAPI.Response.ResultCode.ERROR_DUPLICATE_FOREIGN_TX_ID`
* [ADDED] Adds support for Norwegian krone - `Currency.NOK`
* [ADDED] Adds support for new european countries - `el`, `fi`, `lt`, `nb` and `sl`
* [IMPROVEMENT] Improves bluetooth reconnection to the card terminals
* [ADDED] Adds Bluetooth troubleshooting screen during setup
* [IMPROVEMENT] Improves UX in the receipt screen

## Version 2.3.0
* [ADDED] Adds `skipSuccessScreen` to optionally skip successful result screens
* [ADDED] Supports adding a tip before a checkout with `tipAmount`
* [ADDED] Adds `prepareForCheckout` method to wake up your SumUp Card terminal in advance
* [API CHANGE] `SumUpApi.openPaymentActivity(Activity activity, Class callbackActivity, SumUpPayment payment)` is deprecated. We encourage to switch to `SumUpApi.openPaymentActivity(Activity activity, SumUpPayment payment, int requestCode)` introduced in SDK 1.55.1

## Version 2.2.0
* [ADDED] Supports Miura card terminals for payleven partners
* [IMPROVEMENT] Various bugfixes and enhancements

## Version 2.1.0
* [ADDED] Makes Payment Settings accessible
* [API CHANGE] Renames result code flag with value 1. The old name is deprecated - `TRANSACTION_SUCCESSFUL` > `SUCCESSFUL`
* [IMPROVEMENT] Improves bluetooth discovery and connection stability

## Version 2.0.1
* [ADDED] Includes the upcoming SSL certificate - Certificate included in any previous SDK version will expire Friday, 24th Feb 2017
* [FIXED] Fixes misbehaviour in the rotation handling on the signature screen

## Version 2.0.0
* [ADDED] Support for Air and Air light terminals
* [API CHANGE] `clearPinPlusSettings` is now deprecated and replaced by `clearCardTerminalSettings`
* [FIXED] Makes `TransactiondInfo.Card` public
* [IMPROVEMENT] Refresh of the login screen UI

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
