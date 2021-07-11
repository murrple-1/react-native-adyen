# React Native Adyen

A React Native module to handle interfacing with the [Adyen](https://www.adyen.com/) [iOS](https://github.com/Adyen/adyen-ios) and [Android](https://github.com/Adyen/adyen-android) Drop-In SDKs, written in Typescript, Swift and Kotlin.

## Description

Adyen is a platform for accepting payments, using a variety of methods (like Credit Cards, Google Pay, Apple Pay, etc.). They have provided SDKs for Android and iOS.

This library attempts to provide a simple API for React Native that presents the Drop-In component.

## Getting Started

### Dependencies

Android min Version: 28 `// TODO comfirm`

iOS min Version: 10.0 `// TODO comfirm`

### Installing

Add this project as a dependency to your React Native project

With Yarn

```bash
yarn add --dev @murrple_1/react-native-adyen
```

With NPM

```bash
npm install --save-dev @murrple_1/react-native-adyen
```

### Executing program

The library exports only 2 functions:

```typescript
async function startPayment(options: StartPaymentOptions): Promise<string>;
```

and

```typescript
async function _getPaymentMethodsJsonStr({
  adyenCheckoutHost,
  apiKey,
  merchantAccount,
  countryCode,
  amount,
  shopperReference,
}: _GetPaymentMethodsJsonStrOptions): Promise<string>;
```

## Help

- Making a note here (though I'm not sure if it is a common problem)...

Make sure your Android styles.xml has a theme like:

```xml
<style name="AppTheme" parent="Theme.MaterialComponents.Light.DarkActionBar">
```

- Currently, Adyen iOS SDK does not build on the `arm64` architecture (see https://github.com/Adyen/adyen-ios/issues/291).

## Authors

- [@RoadrunnerEX](https://twitter.com/RoadrunnerEX)

## Version History

`// TODO we haven't yet release a usable version, please consider any releases ALPHA for now`

## License

This project is licensed under the MIT License - see the `LICENSE` file for details

## Acknowledgments

Many inspirations from [mkharibalaji/react-native-adyen-payment](https://github.com/mkharibalaji/react-native-adyen-payment). Honestly, this project wouldn't have been started if that project a) supported Typescript, b) was more easily buildable for development in my environment
