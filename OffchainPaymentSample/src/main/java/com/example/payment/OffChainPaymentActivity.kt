package com.example.payment

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.whoclicksfaster.KeyStoreData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class OffChainPaymentActivity : AppCompatActivity() {

    private val TAG = "JoinCelerActivity"
    private var keyStoreString = ""
    private var passwordStr = ""
    private var senderAddress = ""

    private val clientSideDepositAmount = "5" // 5 WEI
    private val serverSideDepositAmount = "15" // 15 WEI

    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createWallet(v: View) {

        // Get keyStoreString and passwordStr
        keyStoreString = KeyStoreHelper().getKeyStoreString(this@OffChainPaymentActivity)
        passwordStr = KeyStoreHelper().getPassword()

        var keyStoreJson = Gson().fromJson(keyStoreString, KeyStoreData::class.java)
        senderAddress = "0x" + keyStoreJson.address

        Log.d(TAG, "keyStoreString: $keyStoreString")
        Log.d(TAG, "senderAddress: $senderAddress")

        showTips("createWallet Success : $senderAddress")

    }

    fun getTokenFromFaucet(v: View) {

        // Get some token from faucet
        FaucetHelper().getTokenFromPrivateNetFaucet(context = this, faucetURL = "http://54.188.217.246:3008/donate/", walletAddress = senderAddress, faucetCallBack = object : FaucetHelper.FaucetCallBack {
            override fun onSuccess() {
                Log.d(TAG, "\n faucet success")
                showTips("getTokenFromFaucet success,wait for transaction to complete")
            }

            override fun onFailure() {
                Log.d(TAG, "\n faucet error ")
                showTips("getTokenFromFaucet error ")
            }

        })

    }

    fun createCelerClient(v: View) {

        val profile = getString(R.string.cprofile, KeyStoreHelper().generateFilePath(this))
        var result = CelerClientAPIHelper.initCelerClient(keyStoreString, passwordStr, profile)

        showTips(result)
    }

    fun joinCeler(v: View) {
        var result = CelerClientAPIHelper.joinCeler(clientSideDepositAmount, serverSideDepositAmount)
        showTips(result)
        if (result.contains("successful")) {
            sendPayment.visibility = View.VISIBLE
        } else {
            sendPayment.visibility = View.GONE
        }
    }

    fun checkBalance(v: View) {
        var result = CelerClientAPIHelper.checkBalance()
        showTips(result)
    }


    fun sendPayment(v: View) {
        var result = CelerClientAPIHelper.sendPayment("0x200082086aa9f3341678927e7fc441196a222ac1", "1")
        showTips(result)
    }


    private fun showTips(str: String) {

        handler.post {
            tips.append("\n" + str)
        }

    }


}
