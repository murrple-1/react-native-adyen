import { NativeModules } from 'react-native';

const { RNAdyenModule } = NativeModules;

// from https://docs.adyen.com/account/supported-currencies
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

export interface Amount {
  currency: CurrencyType;
  value: number;
}

export type Environment = 'test' | 'europe' | 'united_states' | 'australia';

export interface _GetPaymentMethodsJsonStrOptions {
  adyenCheckoutHost: string;
  apiKey: string;
  merchantAccount: string;
  countryCode?: string;
  amount?: Amount;
  shopperReference?: string;
}

export async function _getPaymentMethodsJsonStr({
  adyenCheckoutHost,
  apiKey,
  merchantAccount,
  countryCode,
  amount,
  shopperReference,
}: _GetPaymentMethodsJsonStrOptions) {
  const response = await fetch(`${adyenCheckoutHost}/v67/paymentMethods`, {
    headers: {
      'Content-Type': 'application/json',
      'X-API-Key': apiKey,
    },
    body: JSON.stringify({
      merchantAccount,
      countryCode,
      amount,
      shopperReference,
    }),
    method: 'POST',
  });
  return await response.text();
}

export interface StartPaymentOptions {
  paymentMethodsJsonStr: string;
  clientKey: string;
  environment: Environment;
  amount: Amount;
  locale?: string;
  cardOptions?: {
    shopperReference?: string;
  };
  googlePayOptions?: {};
}

export async function startPayment(options: StartPaymentOptions) {
  const checkoutResponse = (await RNAdyenModule.startPayment(
    options,
  )) as string;
  return checkoutResponse;
}
