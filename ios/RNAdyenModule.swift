//
//  RNAdyenModule.swift
//  RNAdyenModule
//
//  Copyright © 2021 Murray Christopherson. All rights reserved.
//

import Foundation

@objc(RNAdyenModule)
class RNAdyenModule: NSObject {
  @objc
  func constantsToExport() -> [AnyHashable : Any]! {
    return ["count": 1]
  }

  @objc
  static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
