import Foundation

import PassKit

import Adyen

@objc(RNAdyenModule)
class RNAdyenModule: NSObject, DropInComponentDelegate {
    class RequestDescriptor {
        var url: String
        var headers: [String: String]

        init(url: String, headers: [String: String]) {
            self.url = url
            self.headers = headers
        }
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }

    static var dropInComponent: DropInComponent?
    static var resolve: RCTPromiseResolveBlock?
    static var reject: RCTPromiseRejectBlock?
    static var sendPaymentsRequestDescriptor: RequestDescriptor?
    static var sendDetailsRequestDescriptor: RequestDescriptor?

    @objc(startPayment:resolve:reject:) func startPayment(_ options: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        if RNAdyenModule.dropInComponent != nil {
            reject("Already Running Error", "DropInComponent already visible", nil)
            return
        }

        do {
            if let presentedViewController = RCTPresentedViewController() {
                var configSendPaymentsRequestDescriptor: RequestDescriptor
                if let sendPaymentsRequestDescriptor = options["sendPaymentsRequestDescriptor"] as? [String: AnyObject] {
                    guard let url = sendPaymentsRequestDescriptor["url"] as? String else {
                        reject("Options Error", "'url' missing", nil)
                        return
                    }

                    guard let headers = sendPaymentsRequestDescriptor["headers"] as? [String: String] else {
                        reject("Options Error", "'headers' missing", nil)
                        return
                    }

                    configSendPaymentsRequestDescriptor = RequestDescriptor(url: url, headers: headers)
                } else {
                    reject("Options Error", "'sendPaymentsRequestDescriptor' missing", nil)
                    return
                }

                var configSendDetailsRequestDescriptor: RequestDescriptor
                if let sendDetailsRequestDescriptor = options["sendDetailsRequestDescriptor"] as? [String: AnyObject] {
                    guard let url = sendDetailsRequestDescriptor["url"] as? String else {
                        reject("Options Error", "'url' missing", nil)
                        return
                    }

                    guard let headers = sendDetailsRequestDescriptor["headers"] as? [String: String] else {
                        reject("Options Error", "'headers' missing", nil)
                        return
                    }

                    configSendDetailsRequestDescriptor = RequestDescriptor(url: url, headers: headers)
                } else {
                    reject("Options Error", "'sendPaymentsRequestDescriptor' missing", nil)
                    return
                }

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
                guard let countryCode = options["countryCode"] as? String else {
                    reject("Options Error", "'countryCode' missing", nil)
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
                dropInConfiguration.payment = Payment(amount: Amount(value: amountValue, currencyCode: amountCurrency), countryCode: countryCode)
                dropInConfiguration.localizationParameters = LocalizationParameters(bundle: nil, tableName: nil, keySeparator: nil, locale: configLocale)

                if let cardOptions = options["cardOptions"] as? [String: AnyObject] {
                    let cardConfiguration = CardComponent.Configuration()

                    // TODO more options

                    dropInConfiguration.card = cardConfiguration
                }

                if let applePayOptions = options["applePayOptions"] as? [String: AnyObject] {
                    guard let summaryItems = applePayOptions["summaryItems"] as? [[String: AnyObject]] else {
                        reject("Options Error", "'summaryItems' missing", nil)
                        return
                    }

                    var configSummaryItems: [PKPaymentSummaryItem] = []
                    for summaryItem in summaryItems {
                        guard let label = summaryItem["label"] as? String else {
                            reject("Options Error", "'label' missing", nil)
                            return
                        }

                        guard let amount = summaryItem["amount"] as? NSNumber else {
                            reject("Options Error", "'amount' missing", nil)
                            return
                        }

                        guard let paymentSummaryItemType = summaryItem["type"] as? String else {
                            reject("Options Error", "'type' missing", nil)
                            return
                        }

                        var configPaymentSummaryItemType: PKPaymentSummaryItemType
                        switch paymentSummaryItemType {
                        case "final":
                            configPaymentSummaryItemType = PKPaymentSummaryItemType.final
                        case "pending":
                            configPaymentSummaryItemType = PKPaymentSummaryItemType.pending
                        default:
                            reject("Options Error", "'type' malformed", nil)
                            return
                        }

                        configSummaryItems.append(PKPaymentSummaryItem(label: label, amount: NSDecimalNumber(decimal: amount.decimalValue), type: configPaymentSummaryItemType))
                    }

                    guard let merchantIdentifier = applePayOptions["merchantIdentifier"] as? String else {
                        reject("Options Error", "'merchantIdentifier' missing", nil)
                        return
                    }

                    let applePayConfiguration = ApplePayComponent.Configuration(summaryItems: configSummaryItems, merchantIdentifier: merchantIdentifier)

                    // TODO more options
                    dropInConfiguration.applePay = applePayConfiguration
                }

                let dropInComponent = DropInComponent(paymentMethods: paymentMethods, configuration: dropInConfiguration)
                dropInComponent.delegate = self

                RNAdyenModule.dropInComponent = dropInComponent
                RNAdyenModule.resolve = resolve
                RNAdyenModule.reject = reject
                RNAdyenModule.sendPaymentsRequestDescriptor = configSendPaymentsRequestDescriptor
                RNAdyenModule.sendDetailsRequestDescriptor = configSendDetailsRequestDescriptor

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
        do {
            if let sendPaymentsRequestDescriptor = RNAdyenModule.sendPaymentsRequestDescriptor {
                let json = try JSONEncoder().encode(data.paymentMethod.encodable)

                if let url = URL(string: sendPaymentsRequestDescriptor.url) {
                    var request = URLRequest(url: url)
                    for (headerKey, headerValue) in sendPaymentsRequestDescriptor.headers {
                        request.setValue(headerValue, forHTTPHeaderField: headerKey)
                    }
                    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                    request.httpMethod = "POST"
                    request.httpBody = json

                    let task = URLSession.shared.dataTask(with: request) { data, response, error in
                        guard let data = data, let response = response as? HTTPURLResponse, error == nil else {
                            // TODO do nothing?
                            return
                        }

                        guard (200 ... 299) ~= response.statusCode else {
                            // TODO do nothing?
                            return
                        }

                        // let responseString = String(data: data, encoding: .utf8)
                        // TODO do nothing?
                    }

                    task.resume()
                } else {
                    // TODO do nothing?
                }
            }
        } catch {
            // TODO do nothing?
        }
    }

    func didProvide(_ data: ActionComponentData, from component: DropInComponent) {
        do {
            if let sendDetailsRequestDescriptor = RNAdyenModule.sendDetailsRequestDescriptor {
                let json = try JSONEncoder().encode(data.details.encodable)

                if let url = URL(string: sendDetailsRequestDescriptor.url) {
                    var request = URLRequest(url: url)
                    for (headerKey, headerValue) in sendDetailsRequestDescriptor.headers {
                        request.setValue(headerValue, forHTTPHeaderField: headerKey)
                    }
                    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                    request.httpMethod = "POST"
                    request.httpBody = json

                    let task = URLSession.shared.dataTask(with: request) { data, response, error in
                        guard let data = data, let response = response as? HTTPURLResponse, error == nil else {
                            // TODO do nothing?
                            return
                        }

                        guard (200 ... 299) ~= response.statusCode else {
                            // TODO do nothing?
                            return
                        }

                        // let responseString = String(data: data, encoding: .utf8)
                        // TODO do nothing?
                    }

                    task.resume()
                } else {
                    // TODO do nothing?
                }
            }
        } catch {
            // TODO do nothing?
        }
    }

    func didComplete(from component: DropInComponent) {
        component.viewController.dismiss(animated: true) {
            RNAdyenModule.dropInComponent = nil
            RNAdyenModule.resolve = nil
            RNAdyenModule.reject = nil
            RNAdyenModule.sendPaymentsRequestDescriptor = nil
            RNAdyenModule.sendDetailsRequestDescriptor = nil
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
            RNAdyenModule.sendPaymentsRequestDescriptor = nil
            RNAdyenModule.sendDetailsRequestDescriptor = nil
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
