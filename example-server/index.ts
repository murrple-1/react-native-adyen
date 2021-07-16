import express from 'express';

import yargs from 'yargs/yargs';
import { hideBin } from 'yargs/helpers';

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

const app = express();

app.get('/', (req, res) => res.send('Express + TypeScript Server'));

app.listen(args.port, args.hostname, () => {
  console.log(
    `⚡️[server]: Server is running at http://${args.hostname}:${args.port}`,
  );
});
