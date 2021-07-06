import { NativeModules } from 'react-native'

const { RNAdyenModule } = NativeModules

export type PaymentMethods = Record<string, unknown>
export interface Amount {
  currency: string
  value: number
}

export async function _getPaymentMethods(
  adyenCheckoutHost: string,
  apiKey: string,
  merchantAccount: string,
  countryCode: string | undefined,
  amount: Amount | undefined,
  shopperReference: string | undefined
) {
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
  })
  const json = await response.json()
  if (json) {
    return json as PaymentMethods
  } else {
    throw new Error('bad response')
  }
}

export async function startPayment(
  paymentMethods: PaymentMethods,
  clientKey: string
) {
  return await RNAdyenModule.startPayment(paymentMethods, clientKey)
}
