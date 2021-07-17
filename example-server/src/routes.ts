import { RequestHandler } from 'express';

import axios from 'axios';

import { v4 as uuid4 } from 'uuid';

import { environment } from './environment';

export const paymentMethods: RequestHandler = async (req, res) => {
  const { countryCode, amount, shopperReference, channel } = req.body;

  const response = await axios({
    url: `${environment.adyenCheckoutHost}/v67/paymentMethods`,
    headers: {
      'X-API-Key': environment.apiKey,
      'Content-Type': 'application/json',
    },
    data: {
      merchantAccount: environment.merchantAccount,

      countryCode,
      amount,
      shopperReference,
      channel,
    },
    method: 'POST',
  });

  res.set('Content-Type', 'application/json');
  res.send(JSON.stringify(response.data));
};

export const payments: RequestHandler = async (req, res) => {
  const { amount, paymentMethod } = req.body;

  const response = await axios({
    url: `${environment.adyenCheckoutHost}/v67/payments`,
    headers: {
      'X-API-Key': environment.apiKey,
      'Content-Type': 'application/json',
    },
    data: {
      merchantAccount: environment.merchantAccount,
      amount,
      paymentMethod,
      reference: uuid4(),
    },
    method: 'POST',
  });

  res.set('Content-Type', 'application/json');
  res.send(JSON.stringify(response.data));
};

export const paymentsDetails: RequestHandler = async (req, res) => {
  const response = await axios({
    url: `${environment.adyenCheckoutHost}/v67/payments/details`,
    headers: {
      'X-API-Key': environment.apiKey,
      'Content-Type': 'application/json',
    },
    data: req.body,
    method: 'POST',
  });

  res.set('Content-Type', 'application/json');
  res.send(JSON.stringify(response.data));
};
