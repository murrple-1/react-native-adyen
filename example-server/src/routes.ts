import { RequestHandler } from 'express';

import axios, { AxiosError } from 'axios';

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

  try {
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
      timeout: 30000,
    });

    if (verbose) {
      console.log('JSON Response Body:', response.data);
    }

    res.set('Content-Type', 'application/json');
    res.send(JSON.stringify(response.data));
  } catch (e: unknown) {
    const axiosError = e as AxiosError;
    if (axiosError.response) {
      res.statusCode = axiosError.response.status;
      res.send(axiosError.response.data);
    } else {
      console.error(e);

      res.statusCode = 500;
      res.send('unknown error occurred');
    }
  }
};

export const payments: RequestHandler = async (req, res) => {
  if (verbose) {
    console.log('JSON Request Body:', JSON.stringify(req.body, null, 2));
  }

  const { amount, paymentMethod, reference, returnUrl } = req.body;

  try {
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
        reference,
        returnUrl,
      },
      method: 'POST',
      timeout: 30000,
    });

    if (verbose) {
      console.log('JSON Response Body:', response.data);
    }

    res.set('Content-Type', 'application/json');
    res.send(JSON.stringify(response.data));
  } catch (e: unknown) {
    const axiosError = e as AxiosError;
    if (axiosError.response) {
      res.statusCode = axiosError.response.status;
      res.send(axiosError.response.data);
    } else {
      console.error(e);

      res.statusCode = 500;
      res.send('unknown error occurred');
    }
  }
};

export const paymentsDetails: RequestHandler = async (req, res) => {
  if (verbose) {
    console.log('JSON Request Body:', JSON.stringify(req.body, null, 2));
  }

  try {
    const response = await axios({
      url: `${environment.adyenCheckoutHost}/v67/payments/details`,
      headers: {
        'X-API-Key': environment.apiKey,
        'Content-Type': 'application/json',
      },
      data: req.body,
      method: 'POST',
      timeout: 30000,
    });

    if (verbose) {
      console.log('JSON Response Body:', response.data);
    }

    res.set('Content-Type', 'application/json');
    res.send(JSON.stringify(response.data));
  } catch (e: unknown) {
    const axiosError = e as AxiosError;
    if (axiosError.response) {
      res.statusCode = axiosError.response.status;
      res.send(axiosError.response.data);
    } else {
      console.error(e);

      res.statusCode = 500;
      res.send('unknown error occurred');
    }
  }
};
