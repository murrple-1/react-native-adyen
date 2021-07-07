package com.reactnativeadyen

import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.Amount
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.facebook.react.bridge.*
import java.util.Locale
import org.json.JSONObject

class RNAdyenModule(private var reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName() = "RNAdyenModule"

    object PromiseWrapper {
        var promise: Promise? = null
    }

    @ReactMethod
    fun startPayment(options: ReadableMap, promise: Promise) {
        val activity = super.getCurrentActivity()
        if (activity != null) {
            val paymentMethodsJsonStr = options.getString("paymentMethodsJsonStr") as String
            val clientKey = options.getString("clientKey") as String
            val environment = options.getString("environment") as String
            val amount = options.getMap("amount") as ReadableMap

            var configLocale: Locale? = null
            if (options.hasKey("locale")) {
                val locale = options.getString("locale") as String
                configLocale = Locale.forLanguageTag(locale)
            }

            val paymentMethodsJson = JSONObject(paymentMethodsJsonStr)
            val paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(paymentMethodsJson)

            val dropInConfigurationBuilder = DropInConfiguration.Builder(reactContext, DropInServiceImpl::class.java, clientKey)

            val currency = amount.getString("currency") as String
            val value = amount.getInt("value")
            val configAmount = Amount()
            configAmount.currency = currency
            configAmount.value = value
            dropInConfigurationBuilder.setAmount(configAmount)

            if (configLocale != null) {
                dropInConfigurationBuilder.setShopperLocale(configLocale)
            }

            val configEnvironment: Environment
            when (environment) {
                "test" -> {
                    configEnvironment = Environment.TEST
                }
                "europe" -> {
                    configEnvironment = Environment.EUROPE
                }
                "united_states" -> {
                    configEnvironment = Environment.UNITED_STATES
                }
                "australia" -> {
                    configEnvironment = Environment.AUSTRALIA
                }
                else -> {
                    promise.reject(IllegalArgumentException("environment malformed"))
                    return
                }
            }

            dropInConfigurationBuilder.setEnvironment(configEnvironment)

            if (options.hasKey("cardOptions")) {
                val cardOptions = options.getMap("cardOptions") as ReadableMap
                val publicKey = cardOptions.getString("publicKey") as String

                val cardConfigurationBuilder = CardConfiguration.Builder(reactContext, clientKey)

                if (configLocale != null) {
                    cardConfigurationBuilder.setShopperLocale(configLocale)
                }

                dropInConfigurationBuilder.addCardConfiguration(cardConfigurationBuilder.build())
            }

            val dropInConfiguration = dropInConfigurationBuilder.build()

            PromiseWrapper.promise = promise

            DropIn.startPayment(activity, paymentMethodsApiResponse, dropInConfiguration)
        }
    }
}
