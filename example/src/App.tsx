import React, { useEffect } from 'react'
import RNAdyenModule, { Counter } from 'react-native-adyen'

const App = () => {
  useEffect(() => {
    console.log(RNAdyenModule)
  })

  return <Counter />
}

export default App
