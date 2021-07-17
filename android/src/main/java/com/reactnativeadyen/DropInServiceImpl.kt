package com.reactnativeadyen

import com.adyen.checkout.components.model.payments.Amount
import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.dropin.service.DropInServiceResult
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.facebook.react.common.StandardCharsets
import java.util.concurrent.ExecutionException
import org.json.JSONObject

class DropInServiceImpl : DropInService() {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext)
    }

    private class MyJsonObjectRequest(
        method: Int,
        url: String?,
        jsonRequest: JSONObject?,
        listener: Response.Listener<JSONObject?>?,
        errorListener: Response.ErrorListener?,
        private val headers: Map<String, String>
    ) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
        override fun getHeaders(): Map<String, String> {
            return mutableMapOf<String, String>().apply {
                putAll(super.getHeaders())
                putAll(this@MyJsonObjectRequest.headers)
                this["Content-Type"] = "application/json"
            }
        }
    }

    override fun makePaymentsCall(paymentComponentJson: JSONObject): DropInServiceResult {
        val sendPaymentsRequestDescriptor = RNAdyenModule.Context.sendPaymentsRequestDescriptor as RNAdyenModule.RequestDescriptor

        val jsonObject = JSONObject().apply {
            put(
                "amount",
                if (paymentComponentJson.has("amount")) paymentComponentJson.getJSONObject("amount") else
                    JSONObject().apply {
                        val amount = RNAdyenModule.Context.amount as Amount
                        put("currency", amount.currency)
                        put("value", amount.value)
                    }
            )

            put("paymentMethod", paymentComponentJson.getJSONObject("paymentMethod"))
        }

        val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val request = MyJsonObjectRequest(Request.Method.POST, sendPaymentsRequestDescriptor.url, jsonObject, future, future, sendPaymentsRequestDescriptor.headers)
        requestQueue.add(request)

        return this.handlePaymentsDetailsResponse(future)
    }

    override fun makeDetailsCall(actionComponentJson: JSONObject): DropInServiceResult {
        val sendDetailsRequestDescriptor = RNAdyenModule.Context.sendDetailsRequestDescriptor as RNAdyenModule.RequestDescriptor

        val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val request = MyJsonObjectRequest(Request.Method.POST, sendDetailsRequestDescriptor.url, actionComponentJson, future, future, sendDetailsRequestDescriptor.headers)
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
            val cause = e.cause
            return if (cause is VolleyError) {
                val responseBody = String(cause.networkResponse.data, StandardCharsets.UTF_8)
                DropInServiceResult.Error(responseBody, "VolleyError")
            } else {
                val message = cause?.message
                DropInServiceResult.Error(message ?: "Unknown Error", "ExecutionException")
            }
        }
    }
}
