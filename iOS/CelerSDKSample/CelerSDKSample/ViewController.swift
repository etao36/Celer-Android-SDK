//
//  ViewController.swift
//  CelerSDKSample
//
//  Created by Jinyao Li on 10/5/18.
//  Copyright © 2018 CelerNetwork. All rights reserved.
//

import UIKit
import Mobile

class ViewController: UIViewController {

  // TODO: Add your own keystore and its password here. Put your receiver addr
  private var keyStoreString = ""
  private var password = ""
  private var receiverAddr = ""
  
  let datadir = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
  
  private var clientSideAmount: String = "500000000000000000" // 0.5 cETH
  private var serverSideAmount: String = "1500000000000000000" // 1.5 cETH
  private var transferAmount: String = "30000000000000000" // 0.03 ETH
  
  override func viewDidLoad() {
    super.viewDidLoad()
    // Do any additional setup after loading the view, typically from a nib.
    
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    
    newClient()
  }

  private func newClient() {
    
    // To generate a new usable keystore for creating new client, use KeyStoreHelper and
    keyStoreString = KeyStoreHelper.shared.getKeyStoreString()

//    print(keyStoreString)

    password = KeyStoreHelper.shared.getPassword()
    
    let config = "{\"ETHInstance\": \"wss://ropsten.infura.io/ws\", \"SvrRPC\": \"osp1-hack-ropsten.celer.app:10000\", \"StoreDir\": \"\(datadir)\", \"SvrETHAddr\": \"f805979adde8d63d08490c7c965ee5c1df0aaae2\", \"ChanAddr\": \"011b1fa33797be5fcf7f7e6bf436cf99683c186d\", \"ResolverAddr\": \"cf8938ae21a21a7ffb2d47a69742ef5ce7a669cc\", \"DepositPoolAddr\": \"658333a4ea7dd461b56592ed62839afc18d54a42\", \"HTLRegistryAddr\": \"a41bf533110e0b778f6757e04cf7c6d2a8e294b1\"}"
    
    let client = MobileClient(keyStoreString, pass: password, cfg: config)
  
//    do {
//      try client?.joinCeler("0x0", amtWei: clientSideAmount, peerAmtWei: serverSideAmount)
//      print(client?.getBalance(1)?.available())
//    } catch {
//      print("Join Celer failed: \(error.localizedDescription)")
//    }
    
//    do {
//      receiverAddr = "0x2718aaa01fc6fa27dd4d6d06cc569c4a0f34d399"
//      try client?.sendPay(receiverAddr, amtWei: transferAmount)
//      print(client?.getBalance(1)?.pending())
//      print(client?.getBalance(1)?.total())
//      print(client?.getBalance(1)?.available())
//    } catch {
//      print("Send Pay Error: \(error.localizedDescription)")
//    }
  }
}

