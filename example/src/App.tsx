import React, { useCallback, useState } from 'react'

import { View, Button, Alert } from 'react-native'

import { _getPaymentMethods } from 'react-native-adyen'

import { environment } from './environment'

const App = () => {
  const [isLoading, setIsLoading] = useState(false)

  const onStartPaymentPress = useCallback(() => {
    setIsLoading(true)

    _getPaymentMethods(
      environment.adyenHost,
      environment.apiKey,
      environment.merchantAccount,
      environment.countryCode,
      environment.amount,
      environment.shopperReference
    )
      .then((response) => {
        Alert.alert('Response', JSON.stringify(response))
      })
      .finally(() => {
        setIsLoading(false)
      })
  }, [])

  return (
    <View>
      <Button
        title='Initiate Payment'
        disabled={isLoading}
        onPress={onStartPaymentPress}
      >
        Initial Payment
      </Button>
    </View>
  )
}

export default App
