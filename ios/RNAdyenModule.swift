//
//  RNAdyenModule.swift
//  RNAdyenModule
//
//  Copyright © 2021 Murray Christopherson. All rights reserved.
//

import Foundation
import UIKit

import Adyen

@objc(RNAdyenModule)
class RNAdyenModule: NSObject, DropInComponentDelegate {
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc
    func startPayment(
        _ options: NSDictionary,
        resolver resolve: RCTPromiseResolveBlock,
        rejecter reject: RCTPromiseRejectBlock
    ) throws {
        do {
            let paymentMethodsJsonStr = options["paymentMethodsJsonStr"] as! String
            let clientKey = options["clientKey"] as! String
            let environment = options["environment"] as! String
            let amount = options["amount"] as! [String: AnyObject]

            let paymentMethods = try JSONDecoder().decode(PaymentMethods.self, from: paymentMethodsJsonStr.data(using: String.Encoding.utf8)!)
            let apiContext = APIContext(environment: Environment.test, clientKey: clientKey as String)
            let configuration = DropInComponent.Configuration(apiContext: apiContext)

            let dropInComponent = DropInComponent(paymentMethods: paymentMethods, configuration: configuration)
            dropInComponent.delegate = self

            RCTPresentedViewController()?.present(dropInComponent.viewController, animated: true, completion: nil)
        } catch let error {
            throw error
        }
    }

    func didSubmit(_ data: PaymentComponentData, for paymentMethod: PaymentMethod, from component: DropInComponent) {
        // TODO implement
    }

    func didProvide(_ data: ActionComponentData, from component: DropInComponent) {
        // TODO implement
    }

    func didComplete(from component: DropInComponent) {
        // TODO implement
    }

    func didFail(with error: Error, from component: DropInComponent) {
        // TODO implement
    }

    func didCancel(component: PaymentComponent, from dropInComponent: DropInComponent) {
        // TODO implement
    }

    func didOpenExternalApplication(_ component: DropInComponent) {
        // TODO implement
    }
}
