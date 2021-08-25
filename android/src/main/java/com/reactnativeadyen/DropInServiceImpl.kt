package com.reactnativeadyen

import com.adyen.checkout.components.ActionComponentData
import com.adyen.checkout.components.PaymentComponentState
import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.dropin.service.DropInServiceResult
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.json.JSONObject

class DropInServiceImpl : DropInService() {
    override fun onPaymentsCallRequested(
        paymentComponentState: PaymentComponentState<*>,
        paymentComponentJson: JSONObject
    ) {
        try {
            val context = RNAdyenModule.context as RNAdyenModule.Context
            val params = Arguments.createMap().apply {
                putMap(
                    "amount",
                    if (paymentComponentJson.has("amount")) RNAdyenModule.convertJsonToMap(paymentComponentJson.getJSONObject("amount")) else
                        Arguments.createMap().apply {
                            val amount = context.amount
                            putString("currency", amount.currency)
                            putInt("value", amount.value)
                        }
                )
                putMap("paymentMethod", RNAdyenModule.convertJsonToMap(paymentComponentJson.getJSONObject("paymentMethod")))
                putString("reference", context.reference)
                putString("returnUrl", context.returnUrl)
            }

            RNAdyenModule.sendResultFn = { response ->
                RNAdyenModule.sendResultFn = null
                sendResult(handleResponse(response))
            }
            context.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit("PaymentEvent", params)
        } catch (e: Throwable) {
            RNAdyenModule.context?.promise?.reject(e)
            throw e
        }
    }

    override fun onDetailsCallRequested(
        actionComponentData: ActionComponentData,
        actionComponentJson: JSONObject
    ) {
        try {
            val context = RNAdyenModule.context as RNAdyenModule.Context

            val params = RNAdyenModule.convertJsonToMap(actionComponentJson)

            RNAdyenModule.sendResultFn = { response ->
                RNAdyenModule.sendResultFn = null
                sendResult(handleResponse(response))
            }

            context.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit("PaymentDetailsEvent", params)
        } catch (e: Throwable) {
            RNAdyenModule.context?.promise?.reject(e)
            throw e
        }
    }

    private fun handleResponse(response: JSONObject): DropInServiceResult {
        return if (response.has("action")) {
            val action = response.getJSONObject("action")
            DropInServiceResult.Action(action.toString())
        } else {
            if (response.has("refusalReason")) {
                val refusalReason = response.getString("refusalReason")
                DropInServiceResult.Error(refusalReason, "Refusal")
            } else {
                val resultCode = response.getString("resultCode")
                DropInServiceResult.Finished(resultCode)
            }
        }
    }
}
