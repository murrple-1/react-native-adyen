package com.murraychristopherson.reactnativeadyen

import org.json.JSONObject

import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.dropin.service.DropInServiceResult

class DropInServiceImpl: DropInService() {
    override fun makePaymentsCall(paymentComponentJson: JSONObject): DropInServiceResult {
        // TODO make /payments call with the component data
        return DropInServiceResult.Action("action JSON object")
    }

    override fun makeDetailsCall(actionComponentJson: JSONObject): DropInServiceResult {
        // TODO make /payments/details call with the component data
        return DropInServiceResult.Finished("Success")
    }
}
