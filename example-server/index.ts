import express from 'express';

import { addAsync } from '@awaitjs/express';

import morgan from 'morgan';

import yargs from 'yargs/yargs';
import { hideBin } from 'yargs/helpers';

import {
  paymentMethods,
  payments,
  paymentsDetails,
  _setVerbose,
} from './src/routes';

const args = yargs(hideBin(process.argv))
  .option('verbose', {
    alias: 'v',
    type: 'boolean',
    describe: 'enable verbose logging',
    default: false,
  })
  .option('hostname', {
    alias: 'H',
    type: 'string',
    describe: 'hostname to bind on',
    default: 'localhost',
  })
  .option('port', {
    alias: 'p',
    type: 'number',
    describe: 'port to bind on',
    default: 8000,
  })
  .parseSync();

_setVerbose(args.verbose);

const express_ = express();
const app = addAsync(express_);

app.use(morgan('dev'));
app.use(express.json());

app.postAsync('/paymentMethods', paymentMethods);
app.postAsync('/payments', payments);
app.postAsync('/payments/details', paymentsDetails);

app.listen(args.port, args.hostname, () => {
  console.log(
    `⚡️[server]: Server is running at http://${args.hostname}:${args.port}`,
  );
});
