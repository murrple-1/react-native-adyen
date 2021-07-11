import { IEnvironment } from './ienvironment';

export const environment: IEnvironment = {
  adyenCheckoutHost: '<ADYEN_CHECKOUT_HOST>', // like 'https://checkout-test.adyen.com'
  clientKey: '<CLIENT_KEY>',
  merchantAccount: '<MERCHANT_ACCOUNT>',
  countryCode: 'US',
  amount: {
    currency: 'USD',
    value: 100,
  },
  shopperReference: undefined,

  apiKey: '<API_KEY>',
};
