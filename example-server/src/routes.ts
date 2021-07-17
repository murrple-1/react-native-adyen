import { RequestHandler } from 'express';

import { environment } from './environment';

export const paymentMethods: RequestHandler = async (req, res) => {
  const { countryCode, amount, shopperReference, channel } = req.body;

  const response = await fetch(
    `${environment.adyenCheckoutHost}/v67/paymentMethods`,
    {
      headers: {
        'X-API-Key': environment.apiKey,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        merchantAccount: environment.merchantAccount,

        countryCode,
        amount,
        shopperReference,
        channel,
      }),
      method: 'POST',
    },
  );

  const responseJson = await response.json();

  res.set('Content-Type', 'application/json');
  res.send(JSON.stringify(responseJson));
};

export const payments: RequestHandler = async (req, res) => {
  const { amount, paymentMethod } = req.body;

  const response = await fetch(
    `${environment.adyenCheckoutHost}/v67/payments`,
    {
      headers: {
        'X-API-Key': environment.apiKey,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        merchantAccount: environment.merchantAccount,
        amount,
        paymentMethod,
        reference: '', // TODO reference
      }),
      method: 'POST',
    },
  );

  const responseJson = await response.json();

  res.set('Content-Type', 'application/json');
  res.send(JSON.stringify(responseJson));
};

export const paymentsDetails: RequestHandler = async (req, res) => {
  const response = await fetch(
    `${environment.adyenCheckoutHost}/v67/payments/details`,
    {
      headers: {
        'X-API-Key': environment.apiKey,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(req.body),
      method: 'POST',
    },
  );

  const responseJson = await response.json();

  res.set('Content-Type', 'application/json');
  res.send(JSON.stringify(responseJson));
};
