//
//  RNAdyenModule.m
//  RNAdyenModule
//
//  Copyright © 2021 Murray Christopherson. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNAdyenModule, NSObject)
RCT_EXTERN_METHOD(startPayment:(NSDictionary *)paymentMethodsJson clientKey:(NSString *)clientKey)
@end
