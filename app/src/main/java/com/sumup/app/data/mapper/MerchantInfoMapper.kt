package com.sumup.app.data.mapper

import com.sumup.app.domain.model.MerchantInfo
import com.sumup.merchant.reader.models.Merchant

internal class MerchantInfoMapper {

    fun map(merchant: Merchant?): MerchantInfo? {
        if (merchant == null) return null
        return MerchantInfo(
            merchantCode = merchant.merchantCode,
            currencyCode = merchant.currency.isoCode.orEmpty(),
        )
    }
}
