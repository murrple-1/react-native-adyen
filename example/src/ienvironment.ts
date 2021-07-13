import { CountryCode } from '@murrple_1/react-native-adyen';

export interface IEnvironment {
  clientKey: string;
  merchantAccount: string;
  shopperReference: string | undefined;

  adyenCheckoutHost: string;
  apiKey: string;
}
