import { CountryCode } from '@murrple_1/react-native-adyen';

export interface IEnvironment {
  adyenCheckoutHost: string;
  clientKey: string;
  merchantAccount: string;
  shopperReference: string | undefined;

  apiKey: string;
}
