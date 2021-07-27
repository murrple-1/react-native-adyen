import React, { useCallback, useState } from 'react';

import {
  Button,
  Alert,
  SafeAreaView,
  StyleSheet,
  View,
  Text,
  Platform,
} from 'react-native';

import { v4 as uuid4 } from 'uuid';

import {
  _getPaymentMethods,
  startPayment,
  ResultCode,
  Amount,
} from '@murrple_1/react-native-adyen';

import { environment } from './environment';

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  text: {
    marginBottom: 8,
  },
  separator: {
    flex: 1,
  },
});

const App = () => {
  const [isLoading, setIsLoading] = useState(false);

  const onStartPaymentPress = useCallback(() => {
    setIsLoading(true);

    const hostname = Platform.select({
      default: 'http://localhost:8000',
      android: 'http://10.0.2.2:8000',
    });

    const amount: Amount = {
      currency: 'CAD',
      value: 500,
    };

    _getPaymentMethods({
      requestDescriptor: {
        url: `${hostname}/paymentMethods`,
        headers: {},
      },
      countryCode: 'CA',
      amount,
      shopperReference: environment.shopperReference,
    })
      .then(
        async paymentMethodsJsonStr => {
          try {
            const checkoutResponse = await startPayment({
              paymentMethodsJsonStr,
              sendPaymentsRequestDescriptor: {
                url: `${hostname}/payments`,
                headers: {},
              },
              sendDetailsRequestDescriptor: {
                url: `${hostname}/payments/details`,
                headers: {},
              },
              reference: uuid4(),
              returnUrl: {
                ios: 'adyen-example://',
                android: null,
              },
              clientKey: environment.clientKey,
              environment: 'test',
              countryCode: 'CA',
              amount,
              locale: 'en',
              cardOptions: {
                shopperReference: environment.shopperReference,
              },
              googlePayOptions: {},
              applePayOptions: {
                summaryItems: [
                  {
                    label: 'Gumball x2',
                    amount: 2.25,
                    type: 'final',
                  },
                  {
                    label: 'Mars Bar x1',
                    amount: 2.75,
                    type: 'final',
                  },
                ],
                merchantIdentifier: "Gary's Corner Store",
              },
            });
            let alertStr: string;
            switch (checkoutResponse.resultCode) {
              case ResultCode.Authorised: {
                alertStr = 'Authorised';
                break;
              }
              case ResultCode.Error: {
                alertStr = `Error: ${checkoutResponse.refusalReason}`;
                break;
              }
              case ResultCode.Pending: {
                alertStr = 'Pending';
                break;
              }
              case ResultCode.Received: {
                alertStr = 'Received';
                break;
              }
              case ResultCode.Refused: {
                alertStr = 'Refused';
                break;
              }
            }

            Alert.alert('Response', alertStr);
          } catch (reason: unknown) {
            console.error(reason);
          }
        },
        (reason: unknown) => {
          console.error(reason);
        },
      )
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.separator} />
      <Text style={styles.text}>Make Payment of $5.00 CAD</Text>
      <Button
        title="Initiate Payment"
        disabled={isLoading}
        onPress={onStartPaymentPress}
      >
        Initiate Payment
      </Button>
      <View style={styles.separator} />
    </SafeAreaView>
  );
};

export default App;
