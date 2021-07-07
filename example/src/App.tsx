import React, { useCallback, useState } from 'react';

import { Button, Alert, SafeAreaView } from 'react-native';

import { _getPaymentMethodsJsonStr, startPayment } from 'react-native-adyen';

import { environment } from './environment';

const App = () => {
  const [isLoading, setIsLoading] = useState(false);

  const onStartPaymentPress = useCallback(() => {
    setIsLoading(true);

    _getPaymentMethodsJsonStr({
      adyenCheckoutHost: environment.adyenHost,
      apiKey: environment.apiKey,
      merchantAccount: environment.merchantAccount,
      countryCode: environment.countryCode,
      amount: environment.amount,
      shopperReference: environment.shopperReference,
    })
      .then(async paymentMethodsJsonStr => {
        const checkoutResponse = await startPayment({
          paymentMethodsJsonStr,
          clientKey: environment.clientKey,
          environment: 'test',
          amount: environment.amount,
          cardOptions: {},
        });
        Alert.alert('Response', JSON.stringify(checkoutResponse));
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
