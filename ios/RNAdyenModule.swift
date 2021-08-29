import PassKit

import Adyen

@objc(RNAdyenModule)
class RNAdyenModule: RCTEventEmitter {
    struct Context {
        var dropInComponent: DropInComponent
        var resolve: RCTPromiseResolveBlock
        var reject: RCTPromiseRejectBlock
        var amount: Amount
        var reference: String
        var returnUrl: String
    }

    static let jsonEncoder = JSONEncoder()
    static let jsonDecoder = JSONDecoder()

    static var context: Context?

    var hasListener = false

    override func startObserving() {
        hasListener = true
    }

    override func stopObserving() {
        hasListener = false
    }

    override func supportedEvents() -> [String]! {
        return ["PaymentEvent", "PaymentDetailsEvent"]
    }

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc(startPayment:resolve:reject:) func startPayment(_ options: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            if RNAdyenModule.context != nil {
                reject("Already Running Error", "Context already in use", nil)
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
                    guard let countryCode = options["countryCode"] as? String else {
                        reject("Options Error", "'countryCode' missing", nil)
                        return
                    }
                    guard let reference = options["reference"] as? String else {
                        reject("Options Error", "'reference' missing", nil)
                        return
                    }

                    var returnUrl: String
                    if let returnUrlMap = options["returnUrl"] as? [String: AnyObject] {
                        if let returnUrl_ = returnUrlMap["ios"] as? String {
                            returnUrl = returnUrl_
                        } else {
                            reject("Options Error", "'ios' missing", nil)
                            return
                        }
                    } else {
                        reject("Options Error", "'returnUrl' missing", nil)
                        return
                    }

                    guard let paymentMethodsJson = paymentMethodsJsonStr.data(using: String.Encoding.utf8) else {
                        reject("Options Error", "'paymentMethodsJsonStr' malformed", nil)
                        return
                    }

                    let paymentMethods = try RNAdyenModule.jsonDecoder.decode(PaymentMethods.self, from: paymentMethodsJson)

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
                        var cardConfiguration = CardComponent.Configuration()

                        if let allowedCardTypes = cardOptions["allowedCardTypes"] as? [String] {
                            var configAllowedCardTypes: [CardType] = []
                            for allowedCardType in allowedCardTypes {
                                switch allowedCardType {
                                case "accel":
                                    configAllowedCardTypes.append(CardType.accel)
                                case "alphaBankBonusMasterCard":
                                    configAllowedCardTypes.append(CardType.alphaBankBonusMasterCard)
                                case "alphaBankBonusVISA":
                                    configAllowedCardTypes.append(CardType.alphaBankBonusVISA)
                                case "argencard":
                                    configAllowedCardTypes.append(CardType.argencard)
                                case "americanExpress":
                                    configAllowedCardTypes.append(CardType.americanExpress)
                                case "bcmc":
                                    configAllowedCardTypes.append(CardType.bcmc)
                                case "bijenkorfCard":
                                    configAllowedCardTypes.append(CardType.bijenkorfCard)
                                case "cabal":
                                    configAllowedCardTypes.append(CardType.cabal)
                                case "carteBancaire":
                                    configAllowedCardTypes.append(CardType.carteBancaire)
                                case "cencosud":
                                    configAllowedCardTypes.append(CardType.cencosud)
                                case "chequeDejeneur":
                                    configAllowedCardTypes.append(CardType.chequeDejeneur)
                                case "chinaUnionPay":
                                    configAllowedCardTypes.append(CardType.chinaUnionPay)
                                case "codensa":
                                    configAllowedCardTypes.append(CardType.codensa)
                                case "creditUnion24":
                                    configAllowedCardTypes.append(CardType.creditUnion24)
                                case "dankort":
                                    configAllowedCardTypes.append(CardType.dankort)
                                case "dankortVISA":
                                    configAllowedCardTypes.append(CardType.dankortVISA)
                                case "diners":
                                    configAllowedCardTypes.append(CardType.diners)
                                case "discover":
                                    configAllowedCardTypes.append(CardType.discover)
                                case "elo":
                                    configAllowedCardTypes.append(CardType.elo)
                                case "forbrugsforeningen":
                                    configAllowedCardTypes.append(CardType.forbrugsforeningen)
                                case "hiper":
                                    configAllowedCardTypes.append(CardType.hiper)
                                case "hipercard":
                                    configAllowedCardTypes.append(CardType.hipercard)
                                case "jcb":
                                    configAllowedCardTypes.append(CardType.jcb)
                                case "karenMillen":
                                    configAllowedCardTypes.append(CardType.karenMillen)
                                case "kcp":
                                    configAllowedCardTypes.append(CardType.kcp)
                                case "laser":
                                    configAllowedCardTypes.append(CardType.laser)
                                case "maestro":
                                    configAllowedCardTypes.append(CardType.maestro)
                                case "maestroUK":
                                    configAllowedCardTypes.append(CardType.maestroUK)
                                case "masterCard":
                                    configAllowedCardTypes.append(CardType.masterCard)
                                case "mir":
                                    configAllowedCardTypes.append(CardType.mir)
                                case "naranja":
                                    configAllowedCardTypes.append(CardType.naranja)
                                case "netplus":
                                    configAllowedCardTypes.append(CardType.netplus)
                                case "nyce":
                                    configAllowedCardTypes.append(CardType.nyce)
                                case "oasis":
                                    configAllowedCardTypes.append(CardType.oasis)
                                case "pulse":
                                    configAllowedCardTypes.append(CardType.pulse)
                                case "shopping":
                                    configAllowedCardTypes.append(CardType.shopping)
                                case "solo":
                                    configAllowedCardTypes.append(CardType.solo)
                                case "star":
                                    configAllowedCardTypes.append(CardType.star)
                                case "troy":
                                    configAllowedCardTypes.append(CardType.troy)
                                case "uatp":
                                    configAllowedCardTypes.append(CardType.uatp)
                                case "visa":
                                    configAllowedCardTypes.append(CardType.visa)
                                case "warehouse":
                                    configAllowedCardTypes.append(CardType.warehouse)
                                case "cup":
                                    // do nothing
                                    break
                                default:
                                    reject("Options Error", "'allowedCardTypes' element malformed", nil)
                                    return
                                }
                            }

                            cardConfiguration.allowedCardTypes = configAllowedCardTypes
                        }

                        if let billingAddressMode = cardOptions["billingAddressMode"] as? String {
                            var configBillingAddressMode: CardComponent.AddressFormType
                            switch billingAddressMode {
                            case "full":
                                configBillingAddressMode = CardComponent.AddressFormType.full
                            case "none":
                                configBillingAddressMode = CardComponent.AddressFormType.none
                            case "postalCode":
                                configBillingAddressMode = CardComponent.AddressFormType.postalCode
                            default:
                                reject("Options Error", "'billingAddressMode' malformed", nil)
                                return
                            }

                            cardConfiguration.billingAddressMode = configBillingAddressMode
                        }

                        if let showsHolderNameField = cardOptions["showsHolderNameField"] as? Bool {
                            cardConfiguration.showsHolderNameField = showsHolderNameField
                        }

                        if let showsSecurityCodeField = cardOptions["showsSecurityCodeField"] as? Bool {
                            cardConfiguration.showsSecurityCodeField = showsSecurityCodeField
                        }

                        if let showsStorePaymentMethodField = cardOptions["showsStorePaymentMethodField"] as? Bool {
                            cardConfiguration.showsStorePaymentMethodField = showsStorePaymentMethodField
                        }

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

                        var applePayConfiguration = ApplePayComponent.Configuration(summaryItems: configSummaryItems, merchantIdentifier: merchantIdentifier)

                        if let requiredBillingContactFields = applePayOptions["requiredBillingContactFields"] as? [String] {
                            var configRequiredBillingContactFields = Set<PKContactField>()
                            for requiredBillingContactField in requiredBillingContactFields {
                                switch requiredBillingContactField {
                                case "emailAddress":
                                    configRequiredBillingContactFields.insert(PKContactField.emailAddress)
                                case "name":
                                    configRequiredBillingContactFields.insert(PKContactField.name)
                                case "phoneNumber":
                                    configRequiredBillingContactFields.insert(PKContactField.phoneNumber)
                                case "phoneticName":
                                    configRequiredBillingContactFields.insert(PKContactField.phoneticName)
                                case "postalAddress":
                                    configRequiredBillingContactFields.insert(PKContactField.postalAddress)
                                default:
                                    reject("Options Error", "'requiredBillingContactFields' element malformed", nil)
                                    return
                                }
                            }

                            applePayConfiguration.requiredBillingContactFields = configRequiredBillingContactFields // ignored in iOS 10.*
                        }

                        if let requiredShippingContactFields = applePayOptions["requiredShippingContactFields"] as? [String] {
                            var configRequiredShippingContactFields = Set<PKContactField>()
                            for requiredShippingContactField in requiredShippingContactFields {
                                switch requiredShippingContactField {
                                case "emailAddress":
                                    configRequiredShippingContactFields.insert(PKContactField.emailAddress)
                                case "name":
                                    configRequiredShippingContactFields.insert(PKContactField.name)
                                case "phoneNumber":
                                    configRequiredShippingContactFields.insert(PKContactField.phoneNumber)
                                case "phoneticName":
                                    configRequiredShippingContactFields.insert(PKContactField.phoneticName)
                                case "postalAddress":
                                    configRequiredShippingContactFields.insert(PKContactField.postalAddress)
                                default:
                                    reject("Options Error", "'requiredBillingContactFields' element malformed", nil)
                                    return
                                }
                            }

                            applePayConfiguration.requiredShippingContactFields = configRequiredShippingContactFields // ignored in iOS 10.*
                        }

                        // TODO implement applePayConfiguration.billingContact

                        dropInConfiguration.applePay = applePayConfiguration
                    }

                    let dropInComponent = DropInComponent(paymentMethods: paymentMethods, configuration: dropInConfiguration)
                    dropInComponent.delegate = self

                    RNAdyenModule.context = RNAdyenModule.Context(dropInComponent: dropInComponent, resolve: resolve, reject: reject, amount: Amount(value: amountValue, currencyCode: amountCurrency), reference: reference, returnUrl: returnUrl)

                    presentedViewController.present(dropInComponent.viewController, animated: true)

                } else {
                    reject("View Controller Error", "View Controller is nil", nil)
                }
            } catch let error {
                reject("Unknown Error", error.localizedDescription, error)
            }
        }
    }

    @objc(passPaymentResponse:) func passPaymentResponse(_ response: NSDictionary) {
        if let context = RNAdyenModule.context {
            self.handlePaymentsDetailsResponse(component: context.dropInComponent, context: context, response: response)
        }
    }

    @objc(passPaymentDetailsResponse:) func passPaymentDetailsResponse(_ response: NSDictionary) {
        if let context = RNAdyenModule.context {
            self.handlePaymentsDetailsResponse(component: context.dropInComponent, context: context, response: response)
        }
    }

    @objc(passError:) func passError(_ reason: NSString) {
        if let context = RNAdyenModule.context {
            self.handleError(component: context.dropInComponent, reject: context.reject, code: "JS Error", message: reason as String, error: nil)
        }
    }

    private func handlePaymentsDetailsResponse(component: DropInComponent, context: Context, response: NSDictionary) {
        do {
            let data = try JSONSerialization.data(withJSONObject: response, options: JSONSerialization.WritingOptions())
            let response = try RNAdyenModule.jsonDecoder.decode(PaymentsDetailsResponseBody.self, from: data)
            if let action = response.action {
                DispatchQueue.main.async {
                    component.handle(action)
                }
            } else {
                var resolveArray = [response.resultCode]
                if let refusalReason = response.refusalReason {
                    resolveArray.append(refusalReason)
                }

                DispatchQueue.main.async {
                    context.resolve(resolveArray)

                    self.dismissDropInComponent(dropInComponent: component, animated: true) {
                        RNAdyenModule.context = nil
                    }
                }
            }
        } catch let error {
            DispatchQueue.main.async {
                self.handleError(component: component, reject: context.reject, code: "Network Error", message: "Unknown error", error: error)
            }
        }
    }
}

extension RNAdyenModule: DropInComponentDelegate {
    internal struct PaymentsRequestBody: Encodable {
        var amount: Amount
        var paymentMethod: AnyEncodable
        var reference: String
        var returnUrl: String

        enum CodingKeys: String, CodingKey {
            case amount = "amount"
            case paymentMethod = "paymentMethod"
            case reference = "reference"
            case returnUrl = "returnUrl"
        }

        func encode(to encoder: Encoder) throws {
            var container = encoder.container(keyedBy: CodingKeys.self)
            try container.encode(paymentMethod, forKey: .paymentMethod)
            try container.encode(amount, forKey: .amount)
            try container.encode(reference, forKey: .reference)
            try container.encode(returnUrl, forKey: .returnUrl)
        }
    }

    internal struct PaymentsDetailsRequestBody: Encodable {
        var paymentData: String?
        var details: AnyEncodable

        enum CodingKeys: String, CodingKey {
            case paymentData = "paymentData"
            case details = "details"
        }

        func encode(to encoder: Encoder) throws {
            var container = encoder.container(keyedBy: CodingKeys.self)
            try container.encode(paymentData, forKey: .paymentData)
            try container.encode(details, forKey: .details)
        }
    }

    internal struct PaymentsDetailsResponseBody: Decodable {
        var resultCode: String
        var action: Action?
        var refusalReason: String?

        enum CodingKeys: String, CodingKey {
            case resultCode = "resultCode"
            case action = "action"
            case refusalReason = "refusalReason"
        }

        init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            self.resultCode = try container.decode(String.self, forKey: .resultCode)
            self.action = try container.decodeIfPresent(Action.self, forKey: .action)
            self.refusalReason = try container.decodeIfPresent(String.self, forKey: .refusalReason)
        }
    }

    func didSubmit(_ data: PaymentComponentData, for paymentMethod: PaymentMethod, from component: DropInComponent) {
        if hasListener {
            if let context = RNAdyenModule.context {
                do {
                    var amount: Amount
                    if let dataAmount = data.amount {
                        amount = dataAmount
                    } else {
                        amount = context.amount
                    }

                    let paymentsBody = PaymentsRequestBody(amount: amount, paymentMethod: data.paymentMethod.encodable, reference: context.reference, returnUrl: context.returnUrl)
                    let json = try RNAdyenModule.jsonEncoder.encode(paymentsBody)
                    if let obj = try JSONSerialization.jsonObject(with: json, options: JSONSerialization.ReadingOptions()) as? NSDictionary {
                        self.sendEvent(withName: "PaymentEvent", body: obj)
                    } else {
                        self.handleError(component: component, reject: nil, code: "Native Event Error", message: "Failed to send native event", error: nil)
                    }
                } catch let error {
                    self.handleError(component: component, reject: nil, code: "Native Event Error", message: "Failed to send native event", error: error)
                }
            } else {
                self.handleError(component: component, reject: nil, code: "Context Error", message: "Context not set", error: nil)
            }
        }
    }

    func didProvide(_ data: ActionComponentData, from component: DropInComponent) {
        if hasListener {
            do {
                let detailsBody = PaymentsDetailsRequestBody(paymentData: data.paymentData, details: data.details.encodable)
                let json = try RNAdyenModule.jsonEncoder.encode(detailsBody)
                if let obj = try JSONSerialization.jsonObject(with: json, options: JSONSerialization.ReadingOptions()) as? NSDictionary {
                    self.sendEvent(withName: "PaymentDetailsEvent", body: obj)
                } else {
                    self.handleError(component: component, reject: nil, code: "Native Event Error", message: "Failed to send native event", error: nil)
                }
            } catch let error {
                self.handleError(component: component, reject: nil, code: "Native Event Error", message: "Failed to send native event", error: error)
            }
        }
    }

    private func handleError(component: DropInComponent, reject: RCTPromiseRejectBlock?, code: String, message: String, error: Error?) {
        if let reject = reject {
            reject(code, message, error)
        }

        self.dismissDropInComponent(dropInComponent: component, animated: true) {
            RNAdyenModule.context = nil
        }
    }

    func didComplete(from component: DropInComponent) {
        self.dismissDropInComponent(dropInComponent: component, animated: true) {
            RNAdyenModule.context = nil
        }
    }

    func didFail(with error: Error, from component: DropInComponent) {
        if let context = RNAdyenModule.context {
            context.reject("Unknown Error", error.localizedDescription, error)
        }

        self.dismissDropInComponent(dropInComponent: component, animated: true) {
            RNAdyenModule.context = nil
        }
    }

    func didCancel(component: PaymentComponent, from dropInComponent: DropInComponent) {
        // do nothing
    }

    func didOpenExternalApplication(_ component: DropInComponent) {
        // do nothing
    }

    private func dismissDropInComponent(dropInComponent: DropInComponent, animated: Bool, completion: (() -> Void)? = nil) {
        let viewController = dropInComponent.viewController

        if let presentedViewController = viewController.presentedViewController {
            presentedViewController.dismiss(animated: animated) {
                viewController.dismiss(animated: animated, completion: completion)
            }
        } else {
            viewController.dismiss(animated: animated, completion: completion)
        }
    }
}
