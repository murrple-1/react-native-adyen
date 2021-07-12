package com.reactnativeadyen

import android.app.Activity
import android.content.Intent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.Amount
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.adyen.checkout.dropin.DropInResult
import com.adyen.checkout.googlepay.GooglePayConfiguration
import com.facebook.react.bridge.*
import java.util.Locale
import org.json.JSONObject

class RNAdyenModule(private var reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {
    data class RequestDescriptor(val url: String, val headers: Map<String, String>)

    init {
        reactContext.addActivityEventListener(this)
    }

    override fun getName() = "RNAdyenModule"

    object Context {
        var promise: Promise? = null
        var sendPaymentsRequestDescriptor: RequestDescriptor? = null
        var sendDetailsRequestDescriptor: RequestDescriptor? = null
    }

    @ReactMethod
    fun startPayment(options: ReadableMap, promise: Promise) {
        val activity = currentActivity
        if (activity != null) {
            var configSendPaymentsRequestDescriptor: RequestDescriptor
            (options.getMap("sendPaymentsRequestDescriptor") as ReadableMap).run {
                val url = this.getString("url") as String
                val headers = this.getMap("headers") as ReadableMap
                val configHeaders = mutableMapOf<String, String>()
                for ((headerKey, headerValue) in headers.entryIterator) {
                    configHeaders[headerKey] = headerValue as String
                }
                configSendPaymentsRequestDescriptor = RequestDescriptor(url, configHeaders)
            }

            var configSendDetailsRequestDescriptor: RequestDescriptor
            (options.getMap("sendDetailsRequestDescriptor") as ReadableMap).run {
                val url = this.getString("url") as String
                val headers = this.getMap("headers") as ReadableMap
                val configHeaders = mutableMapOf<String, String>()
                for ((headerKey, headerValue) in headers.entryIterator) {
                    configHeaders[headerKey] = headerValue as String
                }
                configSendDetailsRequestDescriptor = RequestDescriptor(url, configHeaders)
            }

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

            val configAmount = Amount().apply {
                this.currency = amount.getString("currency") as String
                this.value = amount.getInt("value")
            }
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
                var shopperReference: String? = null
                if (cardOptions.hasKey("shopperReference")) {
                    shopperReference = cardOptions.getString("shopperReference") as String
                }

                val cardConfigurationBuilder = CardConfiguration.Builder(reactContext, clientKey)

                if (configLocale != null) {
                    cardConfigurationBuilder.setShopperLocale(configLocale)
                }

                if (shopperReference != null) {
                    cardConfigurationBuilder.setShopperReference(shopperReference)
                }

                dropInConfigurationBuilder.addCardConfiguration(cardConfigurationBuilder.build())
            }

            if (options.hasKey("googlePayOptions")) {
                val googlePayConfigurationBuilder = GooglePayConfiguration.Builder(reactContext, clientKey).setAmount(configAmount)

                if (configLocale != null) {
                    googlePayConfigurationBuilder.setShopperLocale(configLocale)
                }

                dropInConfigurationBuilder.addGooglePayConfiguration(googlePayConfigurationBuilder.build())
            }

            Context.promise = promise
            Context.sendPaymentsRequestDescriptor = configSendPaymentsRequestDescriptor
            Context.sendDetailsRequestDescriptor = configSendDetailsRequestDescriptor

            DropIn.startPayment(activity, paymentMethodsApiResponse, dropInConfigurationBuilder.build())
        }
    }

    override fun onActivityResult(
        activity: Activity?,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        try {
            val dropInResult = DropIn.handleActivityResult(requestCode, resultCode, data) ?: return
            when (dropInResult) {
                is DropInResult.Finished -> {
                    Context.promise?.resolve(dropInResult.result)
                }
                is DropInResult.Error -> {
                    Context.promise?.reject("DropInResultError", dropInResult.reason)
                }
                is DropInResult.CancelledByUser -> {
                    Context.promise?.reject("DropInResultCancelledByUser", "Cancelled by User")
                }
            }
        } finally {
            Context.promise = null
            Context.sendPaymentsRequestDescriptor = null
            Context.sendDetailsRequestDescriptor = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        TODO("Not yet implemented")
    }
}
