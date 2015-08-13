#Changelog

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

For more information, see the [README](https://github.com/sumup/Android-MerchantSDK/blob/master/README.md)
