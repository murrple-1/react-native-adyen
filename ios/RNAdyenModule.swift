import Foundation

import Adyen

@objc(RNAdyenModule)
class RNAdyenModule: NSObject, DropInComponentDelegate {
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }

    static var dropInComponent: DropInComponent?
    static var resolve: RCTPromiseResolveBlock?
    static var reject: RCTPromiseRejectBlock?

    @objc(startPayment:resolve:reject:) func startPayment(_ options: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        if RNAdyenModule.dropInComponent != nil {
            reject("Already Running Error", "DropInComponent already visible", nil)
            return
        }

        do {
            if let presentedViewController = RCTPresentedViewController() {
                guard let paymentMethodsJsonStr = options["paymentMethodsJsonStr"] as? String else {
                    reject("Options Error", "'paymentMethodsJsonStr' missing", nil)
                    return
                }
                guard let clientKey = options["clientKey"] as? String else {
                    reject("Options Error", "'clientKey' missing", nil)
                    return
                }
                guard let environment = options["environment"] as? String else {
                    reject("Options Error", "'environment' missing", nil)
                    return
                }
                guard let amount = options["amount"] as? [String: AnyObject] else {
                    reject("Options Error", "'amount' missing", nil)
                    return
                }

                guard let paymentMethodsJson = paymentMethodsJsonStr.data(using: String.Encoding.utf8) else {
                    reject("Options Error", "'paymentMethodsJsonStr' malformed", nil)
                    return
                }

                let paymentMethods = try JSONDecoder().decode(PaymentMethods.self, from: paymentMethodsJson)

                var configEnvironment: Environment
                switch environment {
                case "test":
                    configEnvironment = Environment.test
                case "europe":
                    configEnvironment = Environment.liveEurope
                case "united_states":
                    configEnvironment = Environment.liveUnitedStates
                case "australia":
                    configEnvironment = Environment.liveAustralia
                default:
                    reject("Options Error", "'environment' malformed", nil)
                    return
                }

                var configLocale: String?
                if let locale = options["locale"] as? String {
                    configLocale = locale
                }

                let apiContext = APIContext(environment: configEnvironment, clientKey: clientKey)

                let dropInConfiguration = DropInComponent.Configuration(apiContext: apiContext)

                guard let amountValue = amount["value"] as? Int else {
                    reject("Options Error", "'value' missing", nil)
                    return
                }
                guard let amountCurrency = amount["currency"] as? String else {
                    reject("Options Error", "'currency' missing", nil)
                    return
                }
                dropInConfiguration.payment = Payment(amount: Amount(value: amountValue, currencyCode: amountCurrency), countryCode: "US")
                dropInConfiguration.localizationParameters = LocalizationParameters(bundle: nil, tableName: nil, keySeparator: nil, locale: configLocale)

                let dropInComponent = DropInComponent(paymentMethods: paymentMethods, configuration: dropInConfiguration)
                dropInComponent.delegate = self

                RNAdyenModule.dropInComponent = dropInComponent
                RNAdyenModule.resolve = resolve
                RNAdyenModule.reject = reject

                DispatchQueue.main.async {
                    presentedViewController.present(dropInComponent.viewController, animated: true)
                }
            } else {
                reject("View Controller Error", "View Controller is nil", nil)
            }
        } catch let error {
            reject("Unknown Error", error.localizedDescription, error)
        }
    }

    func didSubmit(_ data: PaymentComponentData, for paymentMethod: PaymentMethod, from component: DropInComponent) {
        // TODO implement
        print("didSubmit")
    }

    func didProvide(_ data: ActionComponentData, from component: DropInComponent) {
        // TODO implement
        print("didProvide")
    }

    func didComplete(from component: DropInComponent) {
        component.viewController.dismiss(animated: true) {
            RNAdyenModule.dropInComponent = nil
            RNAdyenModule.resolve = nil
            RNAdyenModule.reject = nil
        }
    }

    func didFail(with error: Error, from component: DropInComponent) {
        if let reject = RNAdyenModule.reject {
            reject("Unknown Error", error.localizedDescription, error)
        }
        component.viewController.dismiss(animated: true) {
            RNAdyenModule.dropInComponent = nil
            RNAdyenModule.resolve = nil
            RNAdyenModule.reject = nil
        }
    }

    func didCancel(component: PaymentComponent, from dropInComponent: DropInComponent) {
        // do nothing
        print("didCancel")
    }

    func didOpenExternalApplication(_ component: DropInComponent) {
        // do nothing
        print("didOpenExternalApplication")
    }
}
