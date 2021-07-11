import React, { useCallback, useState } from 'react';

import { Button, Alert, SafeAreaView } from 'react-native';

import { _getPaymentMethodsJsonStr, startPayment } from 'react-native-adyen';

import { environment } from './environment';

const App = () => {
  const [isLoading, setIsLoading] = useState(false);

  const onStartPaymentPress = useCallback(() => {
    setIsLoading(true);

    _getPaymentMethodsJsonStr({
      adyenCheckoutHost: environment.adyenCheckoutHost,
      apiKey: environment.apiKey,
      merchantAccount: environment.merchantAccount,
      countryCode: environment.countryCode,
      amount: environment.amount,
      shopperReference: environment.shopperReference,
    })
      .then(async paymentMethodsJsonStr => {
        try {
          const checkoutResponse = await startPayment({
            adyenCheckoutHost: environment.adyenCheckoutHost,
            paymentMethodsJsonStr,
            clientKey: environment.clientKey,
            environment: 'test',
            countryCode: environment.countryCode,
            amount: environment.amount,
            cardOptions: {
              shopperReference: environment.shopperReference,
            },
            googlePayOptions: {},
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
    <SafeAreaView>
      <Button
        title="Initiate Payment"
        disabled={isLoading}
        onPress={onStartPaymentPress}
      >
        Initial Payment
      </Button>
    </SafeAreaView>
  );
};

export default App;
