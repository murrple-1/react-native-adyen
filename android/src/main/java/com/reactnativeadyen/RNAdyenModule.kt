package com.reactnativeadyen

import android.app.Activity
import android.content.Intent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.card.data.CardType
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.Amount
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.adyen.checkout.dropin.DropInResult
import com.adyen.checkout.googlepay.GooglePayConfiguration
import com.adyen.checkout.redirect.RedirectComponent
import com.facebook.react.bridge.*
import java.util.Locale
import org.json.JSONObject

class RNAdyenModule(private var reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {
    init {
        reactContext.addActivityEventListener(this)
    }

    data class RequestDescriptor(val url: String, val headers: Map<String, String>)

    data class Context(val promise: Promise, val sendPaymentsRequestDescriptor: RequestDescriptor, val sendDetailsRequestDescriptor: RequestDescriptor, val amount: Amount, val reference: String, val returnUrl: String)

    companion object {
        var context: Context? = null
    }

    override fun getName() = "RNAdyenModule"

    @ReactMethod
    fun startPayment(options: ReadableMap, promise: Promise) {
        try {
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
                val reference = options.getString("reference") as String

                val returnUrlMap = options.getMap("returnUrl") as ReadableMap
                val returnUrl = returnUrlMap.getString("android")

                var configLocale: Locale? = null
                if (options.hasKey("locale")) {
                    val locale = options.getString("locale") as String
                    configLocale = Locale.forLanguageTag(locale)
                }

                val paymentMethodsJson = JSONObject(paymentMethodsJsonStr)
                val paymentMethodsApiResponse =
                    PaymentMethodsApiResponse.SERIALIZER.deserialize(paymentMethodsJson)

                val dropInConfigurationBuilder = DropInConfiguration.Builder(
                    reactContext,
                    DropInServiceImpl::class.java,
                    clientKey
                )

                val amountCurrency = amount.getString("currency") as String
                val amountValue = amount.getInt("value")
                val configAmount = Amount().apply {
                    this.currency = amountCurrency
                    this.value = amountValue
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

                    val cardConfigurationBuilder =
                        CardConfiguration.Builder(reactContext, clientKey)

                    if (configLocale != null) {
                        cardConfigurationBuilder.setShopperLocale(configLocale)
                    }

                    if (cardOptions.hasKey("shopperReference")) {
                        cardConfigurationBuilder.setShopperReference(cardOptions.getString("shopperReference") as String)
                    }

                    if (cardOptions.hasKey("allowedCardTypes")) {
                        val allowedCardTypes = cardOptions.getArray("allowedCardTypes") as ReadableArray
                        val configAllowedCardTypes = mutableSetOf<CardType>()
                        for (i in 0..allowedCardTypes.size()) {
                            when (allowedCardTypes.getString(i)) {
                                "alphaBankBonusMasterCard" -> {
                                    configAllowedCardTypes.add(CardType.MCALPHABANKBONUS)
                                }
                                "alphaBankBonusVISA" -> {
                                    configAllowedCardTypes.add(CardType.VISAALPHABANKBONUS)
                                }
                                "argencard" -> {
                                    configAllowedCardTypes.add(CardType.ARGENCARD)
                                }
                                "americanExpress" -> {
                                    configAllowedCardTypes.add(CardType.AMERICAN_EXPRESS)
                                }
                                "bcmc" -> {
                                    configAllowedCardTypes.add(CardType.BCMC)
                                }
                                "bijenkorfCard" -> {
                                    configAllowedCardTypes.add(CardType.BIJENKORF_CARD)
                                }
                                "cabal" -> {
                                    configAllowedCardTypes.add(CardType.CABAL)
                                }
                                "carteBancaire" -> {
                                    configAllowedCardTypes.add(CardType.CARTEBANCAIRE)
                                }
                                "codensa" -> {
                                    configAllowedCardTypes.add(CardType.CODENSA)
                                }
                                "dankort" -> {
                                    configAllowedCardTypes.add(CardType.DANKORT)
                                }
                                "dankortVISA" -> {
                                    configAllowedCardTypes.add(CardType.VISADANKORT)
                                }
                                "diners" -> {
                                    configAllowedCardTypes.add(CardType.DINERS)
                                }
                                "discover" -> {
                                    configAllowedCardTypes.add(CardType.DISCOVER)
                                }
                                "elo" -> {
                                    configAllowedCardTypes.add(CardType.ELO)
                                }
                                "forbrugsforeningen" -> {
                                    configAllowedCardTypes.add(CardType.FORBRUGSFORENINGEN)
                                }
                                "hiper" -> {
                                    configAllowedCardTypes.add(CardType.HIPER)
                                }
                                "hipercard" -> {
                                    configAllowedCardTypes.add(CardType.HIPERCARD)
                                }
                                "jcb" -> {
                                    configAllowedCardTypes.add(CardType.JCB)
                                }
                                "karenMillen" -> {
                                    configAllowedCardTypes.add(CardType.KARENMILLER)
                                }
                                "laser" -> {
                                    configAllowedCardTypes.add(CardType.LASER)
                                }
                                "maestro" -> {
                                    configAllowedCardTypes.add(CardType.MAESTRO)
                                }
                                "maestroUK" -> {
                                    configAllowedCardTypes.add(CardType.MAESTRO_UK)
                                }
                                "masterCard" -> {
                                    configAllowedCardTypes.add(CardType.MASTERCARD)
                                }
                                "mir" -> {
                                    configAllowedCardTypes.add(CardType.MIR)
                                }
                                "naranja" -> {
                                    configAllowedCardTypes.add(CardType.NARANJA)
                                }
                                "oasis" -> {
                                    configAllowedCardTypes.add(CardType.OASIS)
                                }
                                "shopping" -> {
                                    configAllowedCardTypes.add(CardType.SHOPPING)
                                }
                                "solo" -> {
                                    configAllowedCardTypes.add(CardType.SOLO)
                                }
                                "troy" -> {
                                    configAllowedCardTypes.add(CardType.TROY)
                                }
                                "uatp" -> {
                                    configAllowedCardTypes.add(CardType.UATP)
                                }
                                "visa" -> {
                                    configAllowedCardTypes.add(CardType.VISA)
                                }
                                "warehouse" -> {
                                    configAllowedCardTypes.add(CardType.WAREHOUSE)
                                }
                                "accel", "cencosud", "chequeDejeneur", "chinaUnionPay", "creditUnion24", "kcp", "netplus", "nyce", "pulse", "star" -> {
                                    // do nothing
                                }
                                else -> {
                                    promise.reject(IllegalArgumentException("'allowedCardTypes' entry malformed"))
                                    return
                                }
                            }
                        }

                        cardConfigurationBuilder.setSupportedCardTypes(*configAllowedCardTypes.toTypedArray())
                    }

                    if (cardOptions.hasKey("showsCvc")) {
                        cardConfigurationBuilder.setHideCvc(!cardOptions.getBoolean("showsCvc"))
                    }

                    // TODO more options

                    dropInConfigurationBuilder.addCardConfiguration(cardConfigurationBuilder.build())
                }

                if (options.hasKey("googlePayOptions")) {
                    val googlePayConfigurationBuilder =
                        GooglePayConfiguration.Builder(reactContext, clientKey)
                            .setAmount(configAmount)

                    if (configLocale != null) {
                        googlePayConfigurationBuilder.setShopperLocale(configLocale)
                    }

                    // TODO more options

                    dropInConfigurationBuilder.addGooglePayConfiguration(
                        googlePayConfigurationBuilder.build()
                    )
                }

                context = Context(promise, configSendPaymentsRequestDescriptor, configSendDetailsRequestDescriptor, configAmount, reference, returnUrl ?: RedirectComponent.getReturnUrl(reactContext))

                DropIn.startPayment(
                    activity,
                    paymentMethodsApiResponse,
                    dropInConfigurationBuilder.build()
                )
            }
        } catch (e: Throwable) {
            promise.reject(e)
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
                    val resolveArray = Arguments.createArray()
                    if (dropInResult.result == "Error") {
                        resolveArray.pushString("Error")
                        resolveArray.pushString("")
                    } else {
                        resolveArray.pushString(dropInResult.result)
                    }
                    context?.promise?.resolve(resolveArray)
                }
                is DropInResult.Error -> {
                    val resolveArray = Arguments.createArray()
                    resolveArray.pushString("Error")
                    resolveArray.pushString(dropInResult.reason)
                    context?.promise?.resolve(resolveArray)
                }
                is DropInResult.CancelledByUser -> {
                    context?.promise?.reject("DropInResultCancelledByUser", "Cancelled by User")
                }
            }
        } catch (e: Throwable) {
            context?.promise?.reject(e)
        } finally {
            context = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        TODO("Not yet implemented")
    }
}
