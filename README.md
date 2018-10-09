# MobileSample
# Java doc
https://celer-network.github.io/AndroidApp/index.html
# Hackathon doc
https://docs.google.com/document/d/17xTCVwyPqIiSNYr5dR7n3k9wNzGhXzvzwmdSDpUVbtk/edit?usp=sharing

# Add Payment funtionality to your app 

## API Overview

### Start the app and connect to Celer
In this step, Celer does the following things for you: 
* Prepare ETH account
* Create Celer Client
* Join Celer with deposit

Implement the following code when you start your app.
* client = Mobile.createNewCelerClient()
* client.joinCeler()

### Display off-chain balance
Implement the following code when want to display users current balance in UI:
* client.getAvailableBalance()

### Send Payment
Implement the following code on your "send" button click event or UI swipe event.
* client.sendPay(destinationAddress)


## Get started

### Step 1. GetkeyStoreString with password of  an ethereum wallet. 

```kotlin
keyStoreString = KeyStoreHelper().getKeyStoreString(this)
password = KeyStoreHelper().getPassword()
```

KeyStoreHelper will create a new ethereum account(wallet). This account will be used to create Celer Client.

### Step 2. Get Celer profile

To connect to an offc-hain service provider, we need a server configuration file which is provided by this off-chain provider. You can use the hard-coded profile inside the sample application directly. We have prepared everything you need. To use Celer SDK in application, we will generate files inside devices. “StoreDir” means location of generated files. 

```kotlin
val profile = getString(R.string.cprofile, datadir)
```

From the code ,you can see the profile is a json string for create celer client

```json
{"ETHInstance": "wss://ropsten.infura.io/ws",
 "SvrRPC": "osp1-hack-ropsten.celer.app:10000",
 "StoreDir": "%1$s",
 "SvrETHAddr":  "f805979adde8d63d08490c7c965ee5c1df0aaae2", 
 "ChanAddr": "011b1fa33797be5fcf7f7e6bf436cf99683c186d", 
"ResolverAddr": "cf8938ae21a21a7ffb2d47a69742ef5ce7a669cc",
 "DepositPoolAddr": "658333a4ea7dd461b56592ed62839afc18d54a42",
 "HTLRegistryAddr": "a41bf533110e0b778f6757e04cf7c6d2a8e294b1"}
```

### Step 3. Create Celer mobile client

From Step 1 and step 2, we got three params: keyStoreString, password, profile String.

Then we can create a Celer mobile client like this:

```kotlin
client = Mobile.newClient(keyStoreString, passwordStr, profileStr)
```

Celer mobile client has all the methods you need in Celer SDK. 

### Step 4. Join Celer Network

Joining Celer means entering the off-chain world. To join celer, you need to deposit a certain amount of tokens from your on-chain wallet to Celer's state channel, to make sure that you have some off-chain balance to send to others. Meanwhile, server should also deposit certain amount of tokens to the same channel.  

Remember that we have already generated new account in the first step, this account does not have any on-chain balance yet.
You can transfer some money from your existing account if you already have some tokens. If you want to get some free testnet tokens, here is a quick tutorial to get free ethers on Ropsten:

* Get some free ETH:
https://apitester.com/

* Check your balance on Ropsten:
https://ropsten.etherscan.io/address/0xd3e03fdd15d3860da8e897779388412a7f7125e8

Once you have enough balance, you are good to go with this API call:

```kotlin
// Join Celer Network
try {
   client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
   addLog("Balance: ${client?.getBalance(1)?.available}")
} catch (e: Exception) {
   addLog("Join Celer Network Error: ${e.localizedMessage}")
}
```

Here, “0x0” represents the Ether token. 

If this process is successful, you will see the balance in the log. A general failure in this process is “Insufficient fund to join celer”, it means that you need to make sure the wallet has enough on-chain balance before joining Celer.

Joining Celer takes some time because it involves some on-chain transactions. The joinCeler function returns a channel id. If you see this channel id, that means your channel is ready to use.  

### Step 5. Send transaction

Now that you have opened the channel, and you are in the off-chain world. You are able to send some off-chain Ether to someone who has also joined Celer.

How do you know that an address has already joined Celer like youself?

```kotlin
// check if an address has joined Celer Network
try {
   receiverAddr = "0x2718aaa01fc6fa27dd4d6d06cc569c4a0f34d399"
   val hasJoined = client?.hasJoinedCeler(receiverAddress)
   addLog("hasJoined: $hasJoined")
} catch (e: Exception) {
   addLog("check Error: ${e.localizedMessage}")
}
```

The name of “hasJoinedCeler” has not been refactored. It doesn’t explicitly mean whether this address has joined celer (like a boolean value). It is a String representing how much you can send to that address. If it is zero, it means this address has not joined Celer.

If the receiverAddress has joined, you can send some tokens to it

```kotlin
// send cETH to an address
try {
   client?.sendPay(receiverAddress, transferAmount)
} catch (e: Exception) {
   addLog("send cETH Error: ${e.localizedMessage}")
}
```

# Multiplayer Mobile Game 

## API Overview

### Step 1. Start the app and connect to Celer
In this step, Celer does the following things: 
* Prepare ETH account
* Create Celer Client
* Join Celer with deposit

Implement the following code when you start your app.
* client = Mobile.createNewCelerClient()
* client.joinCeler()

### Step 2. Start game session, conditionally pay stake to the other parties
In this step, Celer does the following things for you: 
* Create cApp session
* Send payment with conditions

Implement the following code when your game session has just started:
* client.newCAppSession
* client.sendPaymentWithConditions()

### Step 3. When playing the game, send and receive game state
In this step, Celer does the following things for you: 
* Send state to other players
* Receive state from others

Implement the following code when the user is playing the game.
* client.sendCAppState()
* onReceiveState(byte[] state) 

