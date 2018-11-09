package com.example.payment

import android.util.Log
import com.example.whoclicksfaster.KeyStoreData
import com.google.gson.Gson
import network.celer.celersdk.*

object CelerClientAPIHelper {

    private val TAG = "who clicks faster"
    private var client: Client? = null

    lateinit var joinAddr: String

    fun initCelerClient(keyStoreString: String, passwordStr: String, profileStr: String): String {
        // Init Celer Client

        var keyStoreJson = Gson().fromJson(keyStoreString, KeyStoreData::class.java)

        Log.d(TAG, "address in keyStoreJson: ${keyStoreJson.address}")

        joinAddr = "0x" + keyStoreJson.address

        try {
            client = Celersdk.newClient(keyStoreString, passwordStr, profileStr)
            Log.d(TAG, "Celer client created")
            return "Celer client created"
        } catch (e: Exception) {
            Log.d(TAG, e.localizedMessage)
            return e.localizedMessage
        }
    }

    fun joinCeler(clientSideDepositAmount: String, serverSideDepositAmount: String): String {
        // Join Celer Network
        try {
            client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
            return "joinCeler: successful"
        } catch (e: Exception) {
            Log.d(TAG, "Join Celer Network Error: ${e.localizedMessage}")
            return "Join Celer Network Error: ${e.localizedMessage}"
        }

    }

    fun checkBalance(): String {
        // check has joined Celer
        try {
            Log.d(TAG, "current Balance: ${client?.getBalance(1L)}")
            return "${client?.getBalance(1L)?.available} wei"
        } catch (e: Exception) {
            Log.d(TAG, "Check Balance Error: ${e.localizedMessage}")
            return "Check Balance Error: ${e.localizedMessage}"
        }

    }

    fun sendPayment(receiverAddress: String, transferAmount: String): String {
        try {
            client?.sendPay(receiverAddress, transferAmount)
            return "Send payment: successful"
        } catch (e: Exception) {
            Log.d(TAG, "send PaymentError: ${e.localizedMessage}")
            return "Send payment Error: ${e.localizedMessage}"
        }

    }


}