import { Amount, CountryCode } from 'react-native-adyen';

export interface IEnvironment {
  adyenHost: string;
  clientKey: string;
  merchantAccount: string;
  countryCode: CountryCode;
  amount: Amount;
  shopperReference: string | undefined;

  apiKey: string;
}
