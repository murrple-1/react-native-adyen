export interface IEnvironment {
  adyenHost: string;
  clientKey: string;
  merchantAccount: string;
  countryCode: string | undefined;
  amount:
    | {
        currency: string;
        value: number;
      }
    | undefined;
  shopperReference: string | undefined;

  apiKey: string;
}
