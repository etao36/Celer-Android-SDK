# MobileSample
# Java doc
https://celer-network.github.io/AndroidApp/index.html
# Hackathon doc
https://docs.google.com/document/d/17xTCVwyPqIiSNYr5dR7n3k9wNzGhXzvzwmdSDpUVbtk/edit?usp=sharing

# Add Payment funtionality to your app 

## Step 1. Start the app and connect to Celer
In this step, Celer does the following things for you: 
* Prepare ETH account
* Create Celer Client
* Join Celer with deposit

Implement the following code when you start your app.
* client = Mobile.createNewCelerClient()
* client.joinCeler()

## Step 2. Display off-chain balance
Implement the following code when want to display users current balance in UI:
* client.getAvailableBalance()

## Step 3. Send Payment
Implement the following code on your "send" button click event or UI swipe event.
* client.sendPay(destinationAddress)


# Multiplayer Mobile Game 

## Step 1. Start the app and connect to Celer
In this step, Celer does the following things: 
* Prepare ETH account
* Create Celer Client
* Join Celer with deposit

Implement the following code when you start your app.
* client = Mobile.createNewCelerClient()
* client.joinCeler()

## Step 2. Start game session, conditionally pay stake to the other parties
In this step, Celer does the following things for you: 
* Create cApp session
* Send payment with conditions

Implement the following code when your game session has just started:
* client.newCAppSession
* client.sendPaymentWithConditions()

## Step 3. When playing the game, send and receive game state
In this step, Celer does the following things for you: 
* Send state to other players
* Receive state from others

Implement the following code when the user is playing the game.
* client.sendCAppState()
* onReceiveState(byte[] state) 


