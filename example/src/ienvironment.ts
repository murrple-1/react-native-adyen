import { Amount } from 'react-native-adyen';

export interface IEnvironment {
  adyenHost: string;
  clientKey: string;
  merchantAccount: string;
  countryCode: string | undefined;
  amount: Amount;
  shopperReference: string | undefined;

  apiKey: string;
}
