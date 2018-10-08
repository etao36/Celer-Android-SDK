//
//  KeyStoreManager.swift
//  CPaySample
//
//  To get a keyStoreString, call KeyStoreManager.shared.getKeyStoreString()
//  Then you can print this keyStoreString to find address.
//
//  Created by Jinyao Li on 10/5/18.
//  Copyright © 2018 chainscale. All rights reserved.
//

import Foundation
import Mobile

class KeyStoreHelper {

  static let shared = KeyStoreHelper()
  
  private let gethKeyStore: GethKeyStore
  private var gethAccount: GethAccount? = nil
  private var gethKeyStoreString: String = ""
  
  private let createPassword: String = "CelerNetwork"
  private let exportPassword: String = "CelerNetwork"
  
  let datadir = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
  
  
  private init () {
    gethKeyStore = GethNewKeyStore("\(datadir)/gethKeyStore", GethLightScryptN, GethLightScryptP)
    
    do {
      if let size = gethKeyStore.getAccounts()?.size(), size > 0 {
        gethAccount = try gethKeyStore.getAccounts()?.get(0)
      } else {
        gethAccount = try gethKeyStore.newAccount(createPassword)
      }
    } catch {
      print(error.localizedDescription)
    }
  }
  
 
  func getKeyStoreString() -> String {
    if gethKeyStoreString.isEmpty {
      generateKeyStoreString()
    }
    
    return gethKeyStoreString
  }
  
  func getPassword() -> String {
    return exportPassword
  }
  
  private func generateKeyStoreString() {
    guard let gethAccount = gethAccount else {
      fatalError("Empty gethAccount")
    }
    
    do {
      let jsonKeyData = try gethKeyStore.exportKey(gethAccount, passphrase: createPassword, newPassphrase: exportPassword)
      if let jsonKeyString = String(data: jsonKeyData, encoding: .utf8) {
        gethKeyStoreString = jsonKeyString
      }
    } catch {
        print(error.localizedDescription)
    }
  }
}
