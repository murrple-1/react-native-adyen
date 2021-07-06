package com.murraychristopherson.reactnativeadyen

import android.content.Intent
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import org.json.JSONObject

class RNAdyenModule(private var reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName() = "RNAdyenModule"

    @ReactMethod
    fun startPayment(paymentMethodsJson: JSONObject, clientKey: String, promise: Promise) {
        val activity = super.getCurrentActivity()
        if (activity != null) {
            val paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(paymentMethodsJson)

            val resultIntent = Intent(reactContext, activity::class.java)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            val dropInConfiguration = DropInConfiguration.Builder(reactContext, DropInServiceImpl::class.java, clientKey).build()

            DropIn.startPayment(activity, paymentMethodsApiResponse, dropInConfiguration, resultIntent)
        }
    }
}
