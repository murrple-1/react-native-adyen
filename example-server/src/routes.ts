import { RequestHandler } from 'express';

import axios from 'axios';

import { v4 as uuid4 } from 'uuid';

import { environment } from './environment';

let verbose = false;

export function _setVerbose(verbose_: boolean) {
  verbose = verbose_;
}

export const paymentMethods: RequestHandler = async (req, res) => {
  if (verbose) {
    console.log('JSON Request Body:', JSON.stringify(req.body, null, 2));
  }

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
  if (verbose) {
    console.log('JSON Request Body:', JSON.stringify(req.body, null, 2));
  }

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
  if (verbose) {
    console.log('JSON Request Body:', JSON.stringify(req.body, null, 2));
  }

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
