import {
  EmitterSubscription,
  NativeEventEmitter,
  NativeModules,
  Platform,
} from 'react-native';

const { RNAdyenModule } = NativeModules;

/**
 * Currencies supported. Taken from https://docs.adyen.com/account/supported-currencies
 */
export type CurrencyType =
  | 'AUD'
  | 'USD'
  | 'BRL'
  | 'CAD'
  | 'CHF'
  | 'CZK'
  | 'DKK'
  | 'EUR'
  | 'GBP'
  | 'HKD'
  | 'HRK'
  | 'HUF'
  | 'ILS'
  | 'JPY'
  | 'MXN'
  | 'NOK'
  | 'NZD'
  | 'PLN'
  | 'RON'
  | 'RUB'
  | 'SEK'
  | 'SGD'
  | 'THB'
  | 'TRY'
  | 'ZAR'
  | 'INR'
  | 'MYR';

/**
 * Country Codes. At this time, unsure which are supported by Adyen, but this is the complete list in the requisite format. Taken from https://datahub.io/core/country-list
 */
export type CountryCode =
  | 'AF'
  | 'AX'
  | 'AL'
  | 'DZ'
  | 'AS'
  | 'AD'
  | 'AO'
  | 'AI'
  | 'AQ'
  | 'AG'
  | 'AR'
  | 'AM'
  | 'AW'
  | 'AU'
  | 'AT'
  | 'AZ'
  | 'BS'
  | 'BH'
  | 'BD'
  | 'BB'
  | 'BY'
  | 'BE'
  | 'BZ'
  | 'BJ'
  | 'BM'
  | 'BT'
  | 'BO'
  | 'BQ'
  | 'BA'
  | 'BW'
  | 'BV'
  | 'BR'
  | 'IO'
  | 'BN'
  | 'BG'
  | 'BF'
  | 'BI'
  | 'KH'
  | 'CM'
  | 'CA'
  | 'CV'
  | 'KY'
  | 'CF'
  | 'TD'
  | 'CL'
  | 'CN'
  | 'CX'
  | 'CC'
  | 'CO'
  | 'KM'
  | 'CG'
  | 'CD'
  | 'CK'
  | 'CR'
  | 'CI'
  | 'HR'
  | 'CU'
  | 'CW'
  | 'CY'
  | 'CZ'
  | 'DK'
  | 'DJ'
  | 'DM'
  | 'DO'
  | 'EC'
  | 'EG'
  | 'SV'
  | 'GQ'
  | 'ER'
  | 'EE'
  | 'ET'
  | 'FK'
  | 'FO'
  | 'FJ'
  | 'FI'
  | 'FR'
  | 'GF'
  | 'PF'
  | 'TF'
  | 'GA'
  | 'GM'
  | 'GE'
  | 'DE'
  | 'GH'
  | 'GI'
  | 'GR'
  | 'GL'
  | 'GD'
  | 'GP'
  | 'GU'
  | 'GT'
  | 'GG'
  | 'GN'
  | 'GW'
  | 'GY'
  | 'HT'
  | 'HM'
  | 'VA'
  | 'HN'
  | 'HK'
  | 'HU'
  | 'IS'
  | 'IN'
  | 'ID'
  | 'IR'
  | 'IQ'
  | 'IE'
  | 'IM'
  | 'IL'
  | 'IT'
  | 'JM'
  | 'JP'
  | 'JE'
  | 'JO'
  | 'KZ'
  | 'KE'
  | 'KI'
  | 'KP'
  | 'KR'
  | 'KW'
  | 'KG'
  | 'LA'
  | 'LV'
  | 'LB'
  | 'LS'
  | 'LR'
  | 'LY'
  | 'LI'
  | 'LT'
  | 'LU'
  | 'MO'
  | 'MK'
  | 'MG'
  | 'MW'
  | 'MY'
  | 'MV'
  | 'ML'
  | 'MT'
  | 'MH'
  | 'MQ'
  | 'MR'
  | 'MU'
  | 'YT'
  | 'MX'
  | 'FM'
  | 'MD'
  | 'MC'
  | 'MN'
  | 'ME'
  | 'MS'
  | 'MA'
  | 'MZ'
  | 'MM'
  | 'NA'
  | 'NR'
  | 'NP'
  | 'NL'
  | 'NC'
  | 'NZ'
  | 'NI'
  | 'NE'
  | 'NG'
  | 'NU'
  | 'NF'
  | 'MP'
  | 'NO'
  | 'OM'
  | 'PK'
  | 'PW'
  | 'PS'
  | 'PA'
  | 'PG'
  | 'PY'
  | 'PE'
  | 'PH'
  | 'PN'
  | 'PL'
  | 'PT'
  | 'PR'
  | 'QA'
  | 'RE'
  | 'RO'
  | 'RU'
  | 'RW'
  | 'BL'
  | 'SH'
  | 'KN'
  | 'LC'
  | 'MF'
  | 'PM'
  | 'VC'
  | 'WS'
  | 'SM'
  | 'ST'
  | 'SA'
  | 'SN'
  | 'RS'
  | 'SC'
  | 'SL'
  | 'SG'
  | 'SX'
  | 'SK'
  | 'SI'
  | 'SB'
  | 'SO'
  | 'ZA'
  | 'GS'
  | 'SS'
  | 'ES'
  | 'LK'
  | 'SD'
  | 'SR'
  | 'SJ'
  | 'SZ'
  | 'SE'
  | 'CH'
  | 'SY'
  | 'TW'
  | 'TJ'
  | 'TZ'
  | 'TH'
  | 'TL'
  | 'TG'
  | 'TK'
  | 'TO'
  | 'TT'
  | 'TN'
  | 'TR'
  | 'TM'
  | 'TC'
  | 'TV'
  | 'UG'
  | 'UA'
  | 'AE'
  | 'GB'
  | 'US'
  | 'UM'
  | 'UY'
  | 'UZ'
  | 'VU'
  | 'VE'
  | 'VN'
  | 'VG'
  | 'VI'
  | 'WF'
  | 'EH'
  | 'YE'
  | 'ZM'
  | 'ZW';

/**
 * Description of how to connect to your server.
 * At a few points in the process, `POST` requests will be sent with JSON payloads. These must be passed along to the relevant Adyen endpoints.
 */
export interface RequestDescriptor {
  /**
   * Complete endpoint URL which will be POST'ed to.
   */
  url: string;

  /**
   * Additional headers to be sent to the endpoint. Likely useful if your server uses `X-API-Key` or some-such headers.
   */
  headers: Record<string, string>;
}

/**
 * The currency type and value (in minor units) of the payment.
 */
export interface Amount {
  /**
   * The currency type of the payment.
   */
  currency: CurrencyType;
  /**
   * The value of the payment (in minor units).
   */
  value: number;
}

/**
 * Adyen's supported test and production environments.
 */
export type Environment = 'test' | 'europe' | 'united_states' | 'australia';

export interface _GetPaymentMethodsJsonStrOptions {
  requestDescriptor: RequestDescriptor;
  countryCode?: CountryCode;
  amount?: Amount;
  shopperReference?: string;
}

/**
 * This method is not strictly recommended for use in your app, but should give you a starting point on how to get the `/paymentMethods` JSON string, which is necessary for `startPayment()`.
 */
export async function _getPaymentMethods({
  requestDescriptor,
  countryCode,
  amount,
  shopperReference,
}: _GetPaymentMethodsJsonStrOptions) {
  const response = await fetch(requestDescriptor.url, {
    headers: {
      ...requestDescriptor.headers,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      countryCode,
      amount,
      shopperReference,
      channel: Platform.select({
        android: 'Android',
        ios: 'iOS',
      }),
    }),
    method: 'POST',
  });
  return await response.text();
}

export interface _SendPaymentOptions {
  requestDescriptor: RequestDescriptor;
  amount: {
    currency: CurrencyType;
    value: number;
  };
  paymentMethod: Record<string, unknown>;
  reference: string;
  returnUrl: string;
}

/**
 * This method is not strictly recommended for use in your app, but should give you a starting point on how to send the `/payment` payload, which is necessary for `startPayment()`.
 */
export async function _sendPayment({
  requestDescriptor,
  amount,
  paymentMethod,
  reference,
  returnUrl,
}: _SendPaymentOptions) {
  const response = await fetch(requestDescriptor.url, {
    headers: {
      ...requestDescriptor.headers,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      amount,
      paymentMethod,
      reference,
      returnUrl,
    }),
    method: 'POST',
  });
  return (await response.json()) as Record<string, unknown>;
}

export interface _SendPaymentDetailsOptions {
  requestDescriptor: RequestDescriptor;
  requestObj: Record<string, unknown>;
}

/**
 * This method is not strictly recommended for use in your app, but should give you a starting point on how to send the `/payment/details` payload, which is necessary for `startPayment()`.
 */
export async function _sendPaymentDetails({
  requestDescriptor,
  requestObj,
}: _SendPaymentDetailsOptions) {
  const response = await fetch(requestDescriptor.url, {
    headers: {
      ...requestDescriptor.headers,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestObj),
    method: 'POST',
  });
  return (await response.json()) as Record<string, unknown>;
}

/**
 * Card types supported by Adyen, eg. Mastercard, Visa, Diner's Club, etc.
 *
 * Ignored by Android: `accel`, `cencosud`, `chequeDejeneur`, `chinaUnionPay`, `creditUnion24`, `kcp`, `netplus`, `nyce`, `pulse`, `star`
 *
 * Ignored by iOS: `cup`
 */
export type CardType =
  | 'accel' // Accel
  | 'alphaBankBonusMasterCard' // Alpha Bank Bonus MasterCard
  | 'alphaBankBonusVISA' // Alpha Bank Bonus VISA
  | 'argencard' // Argencard
  | 'americanExpress' // American Express
  | 'bcmc' // BCMC
  | 'bijenkorfCard' // de Bijenkorf Card
  | 'cabal' // Cabal
  | 'carteBancaire' // Carte Bancaire
  | 'cencosud' // Cencosud
  | 'chequeDejeneur' // Chèque Déjeuner
  | 'chinaUnionPay' // China UnionPay
  | 'codensa' // Codensa
  | 'creditUnion24' // Credit Union 24
  | 'cup' // CUP
  | 'dankort' // Dankort
  | 'dankortVISA' // Dankort VISA
  | 'diners' // Diners Club
  | 'discover' // Discover
  | 'elo' // Elo
  | 'forbrugsforeningen' // Forbrugsforeningen
  | 'hiper' // Hiper
  | 'hipercard' // Hipercard
  | 'jcb' // JCB
  | 'karenMillen' // KarenMillen
  | 'kcp' // Korea Cyber Payment
  | 'laser' // Laser (Discontinued in 2014)
  | 'maestro' // Maestro
  | 'maestroUK' // Maestro UK
  | 'masterCard' // MasterCard
  | 'mir' // Mir
  | 'naranja' // Naranja
  | 'netplus' // Net+
  | 'nyce' // NYCE
  | 'oasis' // Oasis
  | 'pulse' // Pulse
  | 'shopping' // Shopping
  | 'solo' // Solo
  | 'star' // STAR
  | 'troy' // Troy
  | 'uatp' // Universal Air Travel Plan
  | 'visa' // VISA
  | 'warehouse'; // The Warehouse

export type ApplePayContactField =
  | 'emailAddress'
  | 'name'
  | 'phoneNumber'
  | 'phoneticName'
  | 'postalAddress';

export interface StartPaymentOptions {
  /**
   * The complete, unabridged response from Adyen's `/paymentMethods` endpoint. This is to be treated as a blackbox, as Adyen internally decodes it into something useful.
   */
  paymentMethodsJsonStr: string;
  /**
   * Reference to be used to connect your server's order with the Adyen order.
   */
  reference: string;
  /**
   * If a payment method requires you to go to a web browser, this is the 'URL' to return to.
   *
   * Note: for our purposes, they are not web page URLs, but custom app URL schemes.
   */
  returnUrl: {
    /**
     * iOS custom app URI scheme is required. For more information on setting a custom URL scheme for your app, read the [Apple Developer documentation](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app).
     */
    ios: string;
    /**
     * Android custom app URI scheme is not required. You can either set it yourself, or it defaults to the result of [`RedirectComponent.getReturnUrl(context)`](https://github.com/Adyen/adyen-android/blob/develop/redirect/src/main/java/com/adyen/checkout/redirect/RedirectComponent.java).
     */
    android: string | null;
  };
  /**
   * Adyen-provided Client Key.
   */
  clientKey: string;
  /**
   * The Adyen environment you are working in.
   */
  environment: Environment;
  /**
   * The amount of the payment.
   */
  amount: Amount;
  /**
   * The country code of the country in which payment is received.
   */
  countryCode: CountryCode;
  /**
   * The locale to display/translate the Adyen component for. If not provided, Adyen will choose a default locale to display (likely the locale of the app's device).
   */
  locale?: string;
  /**
   * Options to customize the credit card payment component.
   */
  cardOptions?: {
    /**
     * Used by Android.
     */
    shopperReference?: string;
    /**
     * Card types you are allowing to be used.
     */
    allowedCardTypes?: CardType[];
    /**
     * Used by iOS.
     */
    billingAddressMode?: 'full' | 'none' | 'postalCode';
    /**
     * Used by iOS.
     */
    showsHolderNameField?: boolean;
    /**
     * TODO fill in
     */
    showsSecurityCodeField?: boolean;
    /**
     * Used by iOS.
     */
    showsStorePaymentMethodField?: boolean;
  };
  /**
   * Options to customize the Google Pay component. Used by Android.
   */
  googlePayOptions?: Record<string, never>;
  /**
   * Options to customize the Apple Pay component. Used by iOS.
   */
  applePayOptions?: {
    /**
     * Receipt-like entries describing what is being purchased, and how much each item costs.
     */
    summaryItems: {
      /**
       * Label of the entry.
       * @example "12oz Premium Shampoo x 2"
       */
      label: string;
      /**
       * Cost of the entry.
       * @example 3.99
       */
      amount: number;
      /**
       * Entry type. Is the entry final, or an estimated price?
       */
      type: 'final' | 'pending';
    }[];
    /**
     * Name of your "shop".
     * @example "My Board Game Shop"
     */
    merchantIdentifier: string;
    /**
     * List of fields required in the Billing address section. Ignored in iOS 10.*, according to Adyen docs.
     */
    requiredBillingContactFields?: ApplePayContactField[];
    /**
     * List of fields required in the Shipping address section. Ignored in iOS 10.*, according to Adyen docs.
     */
    requiredShippingContactFields?: ApplePayContactField[];
  };
}

/**
 * TODO
 */
export type SendPaymentFn = (obj: {
  amount: {
    currency: CurrencyType;
    value: number;
  };
  paymentMethod: Record<string, unknown>;
  reference: string;
  returnUrl: string;
}) => Promise<Record<string, unknown>>;

/**
 * TODO
 */
export type SendPaymentDetailsFn = (
  obj: Record<string, unknown>,
) => Promise<Record<string, unknown>>;

const _eventEmitter = new NativeEventEmitter(RNAdyenModule);

/**
 * This is the singular function you must call to display the Drop-In component atop your app.
 *
 * It returns a tuple of the `resultCode` (see https://docs.adyen.com/api-explorer/#/CheckoutService/v67/post/payments__resParam_resultCode) and `refusalReason`, if exists.
 */
export async function startPayment(
  options: StartPaymentOptions,
  sendPaymentsFn: SendPaymentFn,
  sendPaymentDetailsFn: SendPaymentDetailsFn,
) {
  let paymentEventListener: EmitterSubscription | null = null;
  let paymentDetailsEventListener: EmitterSubscription | null = null;

  try {
    paymentEventListener = _eventEmitter.addListener(
      'PaymentEvent',
      paymentObj => {
        sendPaymentsFn(paymentObj).then(
          response => {
            RNAdyenModule.passPaymentResponse(response);
          },
          (reason: unknown) => {
            RNAdyenModule.passError(String(reason));
          },
        );
      },
    );
    paymentDetailsEventListener = _eventEmitter.addListener(
      'PaymentDetailsEvent',
      paymentDetailsObj => {
        sendPaymentDetailsFn(paymentDetailsObj).then(
          response => {
            RNAdyenModule.passPaymentDetailsResponse(response);
          },
          (reason: unknown) => {
            RNAdyenModule.passError(String(reason));
          },
        );
      },
    );

    return (await RNAdyenModule.startPayment(options)) as
      | [string]
      | [string, string];
  } finally {
    if (paymentEventListener !== null) {
      paymentEventListener.remove();
    }

    if (paymentDetailsEventListener !== null) {
      paymentDetailsEventListener.remove();
    }
  }
}
