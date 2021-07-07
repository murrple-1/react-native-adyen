package com.reactnativeadyen

import android.app.Activity
import android.os.Bundle
import com.adyen.checkout.dropin.DropIn

class DropInHandleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val promise = RNAdyenModule.PromiseWrapper.promise
        if (promise != null) {
            val result = intent.getStringExtra(DropIn.RESULT_KEY)

            if (result != null) {
                promise.resolve(result)
            } else {
                promise.reject("No Result", "No Result was returned")
            }
        }

        RNAdyenModule.PromiseWrapper.promise = null

        finish()
    }
}
