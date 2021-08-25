#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RNAdyenModule, RCTEventEmitter)
RCT_EXTERN_METHOD(startPayment:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(passPaymentResponse:(NSDictionary *)response)
RCT_EXTERN_METHOD(passPaymentDetailsResponse:(NSDictionary *)response)
RCT_EXTERN_METHOD(passError:(NSString *)reason)
@end
