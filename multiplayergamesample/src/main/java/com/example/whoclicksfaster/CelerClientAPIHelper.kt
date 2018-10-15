package com.example.whoclicksfaster

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import network.celer.mobile.*
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import java.io.InputStream
import java.util.*

object CelerClientAPIHelper {
    private var client: Client? = null

    lateinit var joinAddr: String

    private var opponentIndex = -1
    private var myIndex = -1
    private var myAddress: String? = null
    private var opponentAddress: String? = null

    var sessionId: String? = null

    val cApp = CApp()

//    var callback = object : CAppCallback {
//        override fun onStatusChanged(status: Long) {
//            Log.e("whoclicksfaster", "createNewCAppSession onStatusChanged is: $status")
//        }
//
//        override fun onReceiveState(state: ByteArray?): Boolean {
//            Log.e("whoclicksfaster", "createNewCAppSession onReceiveState : $state")
//
//
//            return true
//        }
//    }

    fun initCelerClient(keyStoreString: String, passwordStr: String, profileStr: String) {
        // Init Celer Client

        var keyStoreJson = Gson().fromJson(keyStoreString, KeyStoreData::class.java)
        joinAddr = "0x" + keyStoreJson.address



        try {
            client = Mobile.newClient(keyStoreString, passwordStr, profileStr)
        } catch (e: Exception) {
            Log.d("whoclicksfaster ", e.localizedMessage)
        }
    }

    fun joinCeler(clientSideDepositAmount: String, serverSideDepositAmount: String) {
        // Join Celer Network
        try {
            client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
            Log.d("whoclicksfaster ", "Balance: ${client?.getBalance(1)?.available}")
        } catch (e: Exception) {
            Log.d("whoclicksfaster ", "Join Celer Network Error: ${e.localizedMessage}")

        }

    }


    fun initSession(context: Context, gresp: GroupResp?, callback: CAppCallback) {
        gresp?.let {
            it?.g.let {

                val playerAddresses = it.users.split(",")

                if (playerAddresses.size == 2) {

                    if (playerAddresses[0].toLowerCase() == joinAddr!!.toLowerCase()) {
                        myAddress = playerAddresses[0]
                        opponentAddress = playerAddresses[1]
                        myIndex = 1
                        opponentIndex = 2
                    } else {
                        myAddress = playerAddresses[1]
                        opponentAddress = playerAddresses[0]
                        opponentIndex = 1
                        myIndex = 2
                    }

                    var gomokuABI: String? = null
                    var gomokuBIN: String? = null

                    //read ABI
                    try {
                        val inputStream: InputStream = context.assets.open("Gomoku.abi")
                        gomokuABI = inputStream.bufferedReader().use { it.readText() }
//                        Log.e("whoclicksfaster", "createNewCAppSession gomokuABI is: $gomokuABI")
                    } catch (e: Exception) {
                        Log.e("whoclicksfaster", "createNewCAppSession gomokuABI reading exception: ${e.message}")
                    }

                    //read bin
                    try {
                        val inputStream: InputStream = context.assets.open("Gomoku.bin")
                        gomokuBIN = inputStream.bufferedReader().use { it.readText() }
//                        Log.e("whoclicksfaster", "createNewCAppSession gomokuBIN is: $gomokuBIN")
                    } catch (e: Exception) {
                        Log.e("whoclicksfaster", "createNewCAppSession gomokuBIN reading exception:  ${e.message}")
                    }


//                    cApp.contractAbi = gomokuABI
//                    cApp.contractBin = gomokuBIN

                    cApp.callback = callback

                    val constructor = FunctionEncoder.encodeConstructor(Arrays.asList(
                            Address(playerAddresses[0]),
                            Address(playerAddresses[1]),
                            Uint256(3),
                            Uint256(3),
                            Uint8(5),
                            Uint8(3)))

                    try {
                        sessionId = client?.newCAppSession(cApp, constructor, gresp.round.id)
                    } catch (e: Exception) {
                        Log.e("whoclicksfaster ", "newCAppSession Error: ${e.localizedMessage}")

                    }

                    Log.e("whoclicksfaster", "myAddress : $myAddress")
                    Log.e("whoclicksfaster", "opponentAddress : $opponentAddress")
                    Log.e("whoclicksfaster", "sessionId : $sessionId")
                    Log.e("whoclicksfaster", "gresp.round.id : ${gresp.round.id}")


//                    val delay = if (myIndex == 1) 2000L else 0L
//                    var stake = it.stake.split(".")[0]
//
//                    sendPayWithConditions(stake, opponentIndex)

                }


            }

        }


    }

    fun sendPayWithConditions(amount: String, indexOpponent: Int) {
        val booleanCondition = BooleanCondition()
        booleanCondition.timeout = 500
        booleanCondition.sessionID = sessionId
        val argsForQueryResult = byteArrayOf(1)
        argsForQueryResult[0] = indexOpponent.toByte()
        booleanCondition.argsForQueryResult = argsForQueryResult

        Log.e("whoclicksfaster", "sendPayWithCondtions: argsForQueryResult[0]: ${argsForQueryResult[0]}")

        try {
            client?.sendPayWithConditions(opponentAddress, amount, booleanCondition)
            Log.e("whoclicksfaster", "sendPay: sent")
        } catch (e: Exception) {
            Log.e("whoclicksfaster ", "sendPayWithConditions Error: ${e.localizedMessage}")

        }


    }

    fun sendState(state: ByteArray) {
        Log.e("whoclicksfaster", "sessionId : $sessionId")
        Log.e("whoclicksfaster", "myAddress : $myAddress")
        Log.e("whoclicksfaster", "opponentAddress : $opponentAddress")
        client?.sendCAppState(sessionId, opponentAddress, state)

    }
}