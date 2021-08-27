package com.reactnativeadyen

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.card.data.CardType
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.Amount
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.adyen.checkout.dropin.DropInResult
import com.adyen.checkout.dropin.service.DropInServiceResult
import com.adyen.checkout.googlepay.GooglePayConfiguration
import com.adyen.checkout.redirect.RedirectComponent
import com.facebook.react.bridge.*
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale
import org.json.JSONObject

class RNAdyenModule(private var reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {
    init {
        reactContext.addActivityEventListener(this)
    }

    data class Context(val reactContext: ReactApplicationContext, val promise: Promise, val amount: Amount, val reference: String, val returnUrl: String)

    companion object {
        var context: Context? = null
        var sendResultFn: ((result: DropInServiceResult) -> Unit)? = null

        private const val TAG = "RNAdyenModule"

        fun convertJsonToMap(jsonObject: JSONObject): WritableMap {
            val map = Arguments.createMap()
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                when (val value = jsonObject[key]) {
                    is JSONObject -> {
                        map.putMap(key, convertJsonToMap(value))
                    }
                    is JSONArray -> {
                        map.putArray(key, convertJsonToArray(value))
                    }
                    is Boolean -> {
                        map.putBoolean(key, value)
                    }
                    is Int -> {
                        map.putInt(key, value)
                    }
                    is Double -> {
                        map.putDouble(key, value)
                    }
                    is String -> {
                        map.putString(key, value)
                    }
                    else -> {
                        map.putString(key, value.toString())
                    }
                }
            }
            return map
        }

        private fun convertJsonToArray(jsonArray: JSONArray): WritableArray {
            val array = Arguments.createArray()
            for (i in 0 until jsonArray.length()) {
                when (val value = jsonArray[i]) {
                    is JSONObject -> {
                        array.pushMap(convertJsonToMap(value))
                    }
                    is JSONArray -> {
                        array.pushArray(convertJsonToArray(value))
                    }
                    is Boolean -> {
                        array.pushBoolean(value)
                    }
                    is Int -> {
                        array.pushInt(value)
                    }
                    is Double -> {
                        array.pushDouble(value)
                    }
                    is String -> {
                        array.pushString(value)
                    }
                    else -> {
                        array.pushString(value.toString())
                    }
                }
            }
            return array
        }

        fun convertMapToJson(readableMap: ReadableMap): JSONObject {
            val obj = JSONObject()
            val iterator = readableMap.keySetIterator()
            while (iterator.hasNextKey()) {
                val key = iterator.nextKey()
                when (readableMap.getType(key)) {
                    ReadableType.Null -> obj.put(key, JSONObject.NULL)
                    ReadableType.Boolean -> obj.put(key, readableMap.getBoolean(key))
                    ReadableType.Number -> obj.put(key, readableMap.getDouble(key))
                    ReadableType.String -> obj.put(key, readableMap.getString(key))
                    ReadableType.Map -> obj.put(key, convertMapToJson(readableMap.getMap(key) as ReadableMap))
                    ReadableType.Array -> obj.put(
                        key,
                        convertArrayToJson(readableMap.getArray(key) as ReadableArray)
                    )
                }
            }
            return obj
        }

        private fun convertArrayToJson(readableArray: ReadableArray): JSONArray {
            val array = JSONArray()
            for (i in 0 until readableArray.size()) {
                when (readableArray.getType(i)) {
                    ReadableType.Null -> {
                    }
                    ReadableType.Boolean -> array.put(readableArray.getBoolean(i))
                    ReadableType.Number -> array.put(readableArray.getDouble(i))
                    ReadableType.String -> array.put(readableArray.getString(i))
                    ReadableType.Map -> array.put(convertMapToJson(readableArray.getMap(i)))
                    ReadableType.Array -> array.put(convertArrayToJson(readableArray.getArray(i)))
                }
            }
            return array
        }
    }

    override fun getName() = "RNAdyenModule"

    @ReactMethod
    fun startPayment(options: ReadableMap, promise: Promise) {
        try {
            val activity = currentActivity
            if (activity != null) {
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

                    if (cardOptions.hasKey("showsSecurityCodeField")) {
                        cardConfigurationBuilder.setHideCvc(!cardOptions.getBoolean("showsSecurityCodeField"))
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

                context = Context(reactContext, promise, configAmount, reference, returnUrl ?: RedirectComponent.getReturnUrl(reactContext))

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

    @ReactMethod
    fun passPaymentResponse(response: ReadableMap) {
        val dropInResult = try {
            jsResponseToDropInServiceResult(response)
        } catch (e: Throwable) {
            Log.wtf(TAG, e)
            DropInServiceResult.Error()
        }

        try {
            sendResultFn?.invoke(dropInResult)
        } finally {
            sendResultFn = null
        }
    }

    @ReactMethod
    fun passPaymentDetailsResponse(response: ReadableMap) {
        val dropInResult = try {
            jsResponseToDropInServiceResult(response)
        } catch (e: Throwable) {
            Log.wtf(TAG, e)
            DropInServiceResult.Error()
        }

        try {
            sendResultFn?.invoke(dropInResult)
        } finally {
            sendResultFn = null
        }
    }

    @ReactMethod
    fun passError(reason: String) {
        try {
            Log.e(TAG, "Error: $reason")
            sendResultFn?.invoke(DropInServiceResult.Error())
        } finally {
            sendResultFn = null
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
                    resolveArray.pushString(dropInResult.result)
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

    private fun jsResponseToDropInServiceResult(response: ReadableMap): DropInServiceResult {
        val jsonResponse = convertMapToJson(response)
        return try {
            val action = jsonResponse.getJSONObject("action")
            DropInServiceResult.Action(action.toString())
        } catch (e: JSONException) {
            try {
                val refusalReason = jsonResponse.getString("refusalReason")
                DropInServiceResult.Error(refusalReason)
            } catch (e: JSONException) {
                val resultCode = jsonResponse.getString("resultCode")
                DropInServiceResult.Finished(resultCode)
            }
        }
    }
}
