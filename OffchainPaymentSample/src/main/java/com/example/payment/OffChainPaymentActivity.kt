package com.example.payment

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.text.method.ScrollingMovementMethod


class OffChainPaymentActivity : AppCompatActivity() {

    private val TAG = "OffChainPaymentActivity"
    private val clientSideDepositAmount = "5" // 5 WEI
    private val serverSideDepositAmount = "15" // 15 WEI
    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActions()
    }

    private fun showLog(str: String) {
        Log.d(TAG, str)
        handler.post {
            logTextView?.movementMethod = ScrollingMovementMethod()
            logTextView?.append("\n" + str)
        }
    }

    private fun initActions() {
        //step 1: create wallet
        createWalletButton?.setOnClickListener {
            KeyStoreHelper.generateAccount(this)
            showLog("Step 1: createWallet success : ${KeyStoreHelper.getAddress()}")
        }

        //step 2: get token from faucet
        getTokenFromFaucetButton?.setOnClickListener {
            FaucetHelper().getTokenFromPrivateNetFaucet(context = this,
                    faucetURL = "http://54.188.217.246:3008/donate/",
                    walletAddress = KeyStoreHelper.getAddress(),
                    faucetCallBack = object : FaucetHelper.FaucetCallBack {
                        override fun onSuccess() {
                            showLog("Step 2: getTokenFromFaucet success, wait for transaction to complete")
                        }

                        override fun onFailure() {
                            showLog("getTokenFromFaucet error")
                        }
                    })
        }

        //step 3: create Celer Client
        createCelerClientButton?.setOnClickListener {
            val profile = getString(R.string.cprofile, KeyStoreHelper.generateFilePath(this))
            val result = CelerClientAPIHelper.initCelerClient(
                    KeyStoreHelper.getKeyStoreString(),
                    KeyStoreHelper.getPassword(), profile)
            showLog("Step 3: $result")
        }

        //step 4: Join Celer
        joinCelerButton?.setOnClickListener {
            val result = CelerClientAPIHelper.joinCeler(clientSideDepositAmount, serverSideDepositAmount)
            showLog("Step 4: $result")
            if (result.contains("successful")) {
                sendPaymentButton?.visibility = View.VISIBLE
            } else {
                sendPaymentButton?.visibility = View.GONE
            }
        }

        //step 5: check balance
        checkBalanceButton?.setOnClickListener {
            val result = CelerClientAPIHelper.checkBalance()
            showLog("Current balance: $result")
        }

        //step 6: send payment
        sendPaymentButton?.setOnClickListener {
            val result = CelerClientAPIHelper.sendPayment(
                    "0x200082086aa9f3341678927e7fc441196a222ac1",
                    "1")
            showLog(result)
        }
    }

}
