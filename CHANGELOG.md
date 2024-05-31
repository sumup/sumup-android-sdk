# Changelog

For more information, see the [README](https://github.com/sumup/Android-MerchantSDK/blob/master/README.md)
## Version 5.0.1
* [FIXED] "duplicate class" issue (due to R8 obfuscation) possibly happening when integrating with other external libraries

## Version 5.0.0
* [ADDED] Support for the Solo Lite card reader (beta phase)
* [ADDED] Support for Australian Dollar and Mexican Pesos currencies - `SumUpPayment.Currency.AUD`, `SumUpPayment.Currency.MXN`
* [ADDED] New Troubleshooting flow for all our readers
* [ADDED] Introduction of a unified Black and White theme that represents "One Design Language" across all SumUp products
* [FIXED] A crash that was happening due to the play services location version incompatibility.
* [IMPROVEMENT] Updates about the technical stack:
    * Minimum supported targetSDK is 31
    * Minimum Kotlin is upgraded to 1.7.21
    * Minimum Kotlin Coroutine is upgraded to 1.6.4
    * Minimum AndroidX Activity is upgraded to version 1.5.1
    * Minimum AndroidX Fragment is upgraded to version 1.5.7
    * Minimum AppCompat is upgraded to 1.4.2
    * Minimum Material Components is upgraded to 1.6.1
    * Minimum Moshi is upgraded to 1.14.0
    * Minimum Hilt is upgraded to 2.44.2
    * Minimum AGP is upgraded to 7.3.0
    * Building with AGP 7.3.0
    * Updated Play services location to 21.0.1

## Version 4.3.0
* [REMOVED] Dropping support for Android 7 (API 25) and below 
* [ADDED] Solo USB under Beta: the Solo card reader now supports connecting and transacting over USB cable. The minimum required Solo software version that supports the USB mode is 3.3.17.2.
  * If this version (3.3.17.2) is not yet available on your Solo when connected via Bluetooth, you can switch it to standalone mode (Menu-> Connection -> WiFi), login directly on the Solo and update it to the latest version. 
  * Solo USB is under beta phase which means that the feature still requires some optimization to ensure full stability.
  * Known limitation: currently, if the USB cable is unplugged, the USB permission on the mobile device will need to be regranted.
  * As of now, Solo is not able to wake up automatically from sleep mode. A connection needs to be reestablished from the Card Reader page.

## Version 4.2.0
* [ADDED] Support for Peruvian Sol currency – `SumUpPayment.Currency.PEN`
* [IMPROVEMENT] Enhanced tablet view UI in card reader setup when multiple card readers are discovered

## Version 4.1.1
* [FIXED] A misbehavior that could lead to isolated cases of rescanning for a card reader before a checkout
* [FIXED] A crash that can occur when finalizing a firmware update on an Air card reader

## Version 4.1.0
* [ADDED] Introduction of the tip on card reader feature, which allows the customer to add a tip directly on the card reader instead of the Android device with `tipOnCardReader` (see dedicated section in README [here](https://github.com/sumup/sumup-android-sdk#tip-on-card-reader) for more)
* [IMPROVEMENT] Retain BLE connection at the end of a transaction is now default - `SumUpExperimentalAPI.prepareForCheckout(boolean retainBLEConnection)` is deprecated accordingly in favor of `SumUpAPI.prepareForCheckout()`
* [IMPROVEMENT] Enhanced reader UI during checkout
* [IMPROVEMENT] Miscellaneous bug fixes and enhancements

## Version 4.0.3
* [FIXED] "duplicate class" issue (due to R8 obfuscation) possibly happening when integrating with other external libraries

## Version 4.0.2
* Internal release with no public purpose (advertising only for consistency)

## Version 4.0.1
* [FIXED] A crash after successful transaction if one of the `receiptEmail` or `mobilePhone` is missing from the `SumUpPayment.builder()`

## Version 4.0.0
* [ADDED] Starting with firmware version 1.0.1.84, Air card readers with serial numbers starting with 108, 109 or later require SDK version 4.0.0 and later. Please update to the latest SDK version if you need to support these readers
* [ADDED] Introduction of the Card reader page which centralises all the options related to the card reader in a single activity
* [ADDED] Introduction of the support for SumUp 'Solo' card reader (beta phase)
* [ADDED - Experimental] Retain BLE connection at the end of a transaction (see dedicated section in README [here](https://github.com/sumup/sumup-android-sdk#13-retain-ble-connection-experimental) for more)
* [REMOVED] Dropping support for Android 5 (API 22) and below – Dedicated error code introduced for handling unsupported API levels - SumUpAPI.Response.ResultCode.ERROR_API_LEVEL_TOO_LOW
* [API CHANGE] SumUpApi.openPaymentSettingsActivity(Activity activity, int requestCode) is deprecated in favor of SumUpApi.openCardReaderPage(Activity activity, int requestCode)
* [IMPROVEMENT] New tablet layout for the login screen
* [IMPROVEMENT] Refreshed SumUp Brand Design Language for failed, successful and receipt screens
* [IMPROVEMENT] Updates about the technical stack:
   * Building with AGP 7.0.0 and Java 11
  * Raises targetSdkVersion to API 31 - it is still possible to target API 30 with some handling in your AndroidManifest.xml (see dedicated section in README [here](https://github.com/sumup/sumup-android-sdk#11-targetsdk-lower-than-31) for more)
  * Minimum Kotlin is upgraded to 1.5.21
  * Minimum Kotlin Coroutine is upgraded to 1.5.1
  * Minimum AGP is upgraded to 4.0.2
  * Drops support for Android 5 (API 22) and below
  * Raises recommended Play Services Location to version 19.0.1
  * Raises OkHttp to version 4.9.0
  * Minimum Material component is upgraded to 1.3.0
  * Minimum AndroidX Annotation is upgraded to version 1.2.0
  * Minimum AndroidX Fragment is upgraded to version 1.3.6
  * Minimum Moshi is upgraded to 1.12.0
  * Introduces Hilt - minimum supported version 2.40.5
  * Introduces Retrofit - minimum supported version 2.9.0
* [REMOVED] Contact Picker functionality from the Transaction Success screen

## Version 3.4.0
* [ADDED] Ability to skip the transaction failed screen by calling `skipFailedScreen()`
* [ADDED] Support for Colombian Peso and Croatian Kuna currencies - `SumUpPayment.Currency.COP`, `SumUpPayment.Currency.HRK`
* [ADDED] Spanish translations for US Merchants `es-US`
* [ADDED] Dedicated error code for handling invalid decimal values for currencies - `SumUpAPI.Response.ResultCode.ERROR_INVALID_AMOUNT_DECIMALS`
* [FIXED] Unable to select a customer contact on Successful transaction on the devices from Android 10 (API 29)
* [IMPROVEMENT] Updates about technical stack
    * Minimum Material component is upgraded to 1.2.0
    * Minimum AppCompat is upgraded to 1.2.0
* [IMPROVEMENT] Miscellaneous bug fixes and enhancements
* [UPCOMING] Dropping the support for Android 5 (API 22) and below.
* [UPCOMING] Next SDK version will require Java 8

## Version 3.3.2
* [IMPROVEMENT] Refreshed SumUp Brand Design Language for the buttons and landscape mode
* [FIXED] A crash that occurred when the integrator App was killed by Android System while being in the background
* [FIXED] A misbehavior causing the SumUp SDK to remain in a loading state when it is brought back from the background during payment processing.
* [IMPROVEMENT] Updates about technical stack:
    * Introduces Constraint layout - version 1.1.0 and above is supported
    * Introduces Moshi - version 1.6.0 and above supported
    * Introduces Coroutines - version 1.3.3 and above supported
    * Upgraded to Proguard 6.2.2
* [IMPROVEMENT] Miscellaneous bug fixes and enhancements

## Version 3.3.1
* [ADDED] Support for Brazilian Pin+ Cless Card Reader
* [ADDED] On top of establishing the Bluetooth connection via the “Connect” button, it is now also possible to do it by tapping on the card reader image 
* [IMPROVEMENT] Refreshed SumUp Brand Design Language for color and typography
* [FIXED] “Forgot password” button was broken in the previous release and is now clickable
* [FIXED] A crash that can occur when updating the firmware of the Card Reader
* [IMPROVEMENT] Update technical stack:
   * Raises targetSdkVersion to API 29 - it is still possible to target API 28
   * Updates the Android Gradle Plugin to version 4.4.1
   * Introduces Kotlin - Version 1.3.10 and above is needed
* [IMPROVEMENT] Miscellaneous bug fixes and enhancements

## Version 3.3.0
* [CHANGED] The SumUp SDK is migrated to AndroidX. Please make sure to [migrate your project to AndroidX](https://developer.android.com/jetpack/androidx/migrate) before including SumUp SDK 3.3.0 and future SDK versions.
* [CHANGED] Package requires minor edit.
 `com.sumup.merchant` becomes `com.sumup.merchant.reader` - e.g. 
   * `com.sumup.merchant.api.SumUpAPI` > `com.sumup.merchant.reader.api.SumUpAPI`
   * `com.sumup.merchant.models.TransactionInfo` > `com.sumup.merchant.reader.models.TransactionInfo`

   Please note, this applies to ALL REFERENCES in the package.
* [CHANGED] SumUp ‘Payments Link’ (formerly Mobile Payments/SMS) product has been removed from the SDK.  Should you wish to still use Payments Links in your integration, please get in touch [here](https://developer.sumup.com/help/#tech-question)
* [ADDED] Introduction of in-app Bluetooth troubleshooting guide for Air Card Reader
* [IMPROVEMENT] Enhanced UI for reader selection during selection & setup
* [IMPROVEMENT] The SDK is now 15% lighter with footprint of approximately 4M
* [FIXED] Fixed screen rotation issue in landscape mode for Tablets running Android 7.0 and above
* [FIXED] Remove permissions which became obsolete (e.g. `GET_ACCOUNTS` , `READ_GSERVICES`, `RECORD_AUDIO`, `NFC`)
* [FIXED] Add `sumup_` prefixes to resources ([issue #96](https://github.com/sumup/sumup-android-sdk/issues/96))

## Version 3.2.2
* [IMPROVEMENT] Security improvements

## Version 3.2.1
* [IMPROVEMENT] Security improvements

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
