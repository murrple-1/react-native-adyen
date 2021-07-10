package com.reactnativeadyen

import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.dropin.service.DropInServiceResult
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import java.util.concurrent.ExecutionException
import org.json.JSONObject

class DropInServiceImpl : DropInService() {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext)
    }

    override fun makePaymentsCall(paymentComponentJson: JSONObject): DropInServiceResult {
        val adyenHost = RNAdyenModule.Context.adyenHost as String

        val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val request = JsonObjectRequest(Method.POST, "$adyenHost/payments", paymentComponentJson, future, future)
        requestQueue.add(request)

        return try {
            val response: JSONObject = future.get()
            DropInServiceResult.Action(response.toString())
        } catch (e: InterruptedException) {
            DropInServiceResult.Error(e.message, "InterruptedException")
        } catch (e: ExecutionException) {
            DropInServiceResult.Error(e.message, "ExecutionException")
        }
    }

    override fun makeDetailsCall(actionComponentJson: JSONObject): DropInServiceResult {
        val adyenHost = RNAdyenModule.Context.adyenHost as String

        val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val request = JsonObjectRequest(Method.POST, "$adyenHost/payments/details", actionComponentJson, future, future)
        requestQueue.add(request)

        return try {
            future.get()
            DropInServiceResult.Finished("Success")
        } catch (e: InterruptedException) {
            DropInServiceResult.Error(e.message, "InterruptedException")
        } catch (e: ExecutionException) {
            DropInServiceResult.Error(e.message, "ExecutionException")
        }
    }
}
