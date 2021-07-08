#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNAdyenModule, NSObject)
RCT_EXTERN_METHOD(startPayment:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
@end
