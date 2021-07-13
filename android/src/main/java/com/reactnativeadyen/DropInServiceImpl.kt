package com.reactnativeadyen

import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.dropin.service.DropInServiceResult
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.reactnativeadyen.RNAdyenModule.Amount
import java.util.concurrent.ExecutionException
import org.json.JSONObject

class DropInServiceImpl : DropInService() {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext)
    }

    override fun makePaymentsCall(paymentComponentJson: JSONObject): DropInServiceResult {
        val sendPaymentsRequestDescriptor = RNAdyenModule.Context.sendPaymentsRequestDescriptor as RNAdyenModule.RequestDescriptor

        val jsonObject = JSONObject().apply {
            put("merchantAccount", RNAdyenModule.Context.merchantAccount as String)
            put(
                "amount",
                JSONObject().apply {
                    val amount = RNAdyenModule.Context.amount as Amount
                    put("currency", amount.currency)
                    put("value", amount.value)
                }
            )
            put("reference", RNAdyenModule.Context.reference as String)
            put("paymentMethod", paymentComponentJson)
        }

        val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val request = JsonObjectRequest(Method.POST, sendPaymentsRequestDescriptor.url, jsonObject, future, future)
        request.headers.putAll(sendPaymentsRequestDescriptor.headers)
        request.headers["Content-Type"] = "application/json"
        requestQueue.add(request)

        return this.handlePaymentsDetailsResponse(future)
    }

    override fun makeDetailsCall(actionComponentJson: JSONObject): DropInServiceResult {
        val sendDetailsRequestDescriptor = RNAdyenModule.Context.sendDetailsRequestDescriptor as RNAdyenModule.RequestDescriptor

        val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val request = JsonObjectRequest(Method.POST, sendDetailsRequestDescriptor.url, actionComponentJson, future, future)
        request.headers.putAll(sendDetailsRequestDescriptor.headers)
        request.headers["Content-Type"] = "application/json"
        requestQueue.add(request)

        return this.handlePaymentsDetailsResponse(future)
    }

    private fun handlePaymentsDetailsResponse(future: RequestFuture<JSONObject>): DropInServiceResult {
        try {
            val response = future.get()

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
        } catch (e: InterruptedException) {
            return DropInServiceResult.Error(e.message, "InterruptedException")
        } catch (e: ExecutionException) {
            return DropInServiceResult.Error(e.message, "ExecutionException")
        }
    }
}
