import React, { useCallback, useState } from 'react';

import {
  Button,
  Alert,
  SafeAreaView,
  StyleSheet,
  View,
  Text,
} from 'react-native';

import {
  _getPaymentMethods,
  startPayment,
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

    _getPaymentMethods({
      adyenCheckoutHost: environment.adyenCheckoutHost,
      apiKey: environment.apiKey,
      merchantAccount: environment.merchantAccount,
      countryCode: 'US',
      amount: {
        currency: 'USD',
        value: 100,
      },
      shopperReference: environment.shopperReference,
    })
      .then(async paymentMethodsJsonStr => {
        try {
          const checkoutResponse = await startPayment({
            paymentMethodsJsonStr,
            sendPaymentsRequestDescriptor: {
              url: `${environment.adyenCheckoutHost}/v67/payments`,
              headers: {
                'X-API-Key': environment.apiKey,
              },
            },
            sendDetailsRequestDescriptor: {
              url: `${environment.adyenCheckoutHost}/v67/payments/details`,
              headers: {
                'X-API-Key': environment.apiKey,
              },
            },
            clientKey: environment.clientKey,
            environment: 'test',
            countryCode: 'US',
            amount: {
              currency: 'USD',
              value: 100,
            },
            locale: 'en',
            cardOptions: {
              shopperReference: environment.shopperReference,
            },
            googlePayOptions: {},
            applePayOptions: {
              summaryItems: [
                {
                  label: 'Gumball x2',
                  amount: 0.5,
                  type: 'final',
                },
                {
                  label: 'Mars Bar x1',
                  amount: 0.5,
                  type: 'final',
                },
              ],
              merchantIdentifier: "Gary's Corner Store",
            },
          });
          Alert.alert('Response', JSON.stringify(checkoutResponse));
        } catch (reason: unknown) {
          console.error(reason);
        }
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.separator} />
      <Text style={styles.text}>Make Payment of $1.00 USD</Text>
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
