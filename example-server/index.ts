import express from 'express';

import { addAsync } from '@awaitjs/express';

import { json as jsonBodyParser_ } from 'body-parser';

import yargs from 'yargs/yargs';
import { hideBin } from 'yargs/helpers';

import { paymentMethods, payments, paymentsDetails } from './src/routes';

const args = yargs(hideBin(process.argv))
  .option('hostname', {
    alias: 'H',
    type: 'string',
    description: 'hostname to bind on',
    default: 'localhost',
  })
  .option('port', {
    alias: 'p',
    type: 'number',
    describe: 'port to bind on',
    default: 8000,
  })
  .parseSync();

const express_ = express();
const app = addAsync(express_);

const jsonBodyParser = jsonBodyParser_();

app.postAsync('/paymentMethods', jsonBodyParser, paymentMethods);
app.postAsync('/payments', jsonBodyParser, payments);
app.postAsync('/payments/details', jsonBodyParser, paymentsDetails);

app.listen(args.port, args.hostname, () => {
  console.log(
    `⚡️[server]: Server is running at http://${args.hostname}:${args.port}`,
  );
});
