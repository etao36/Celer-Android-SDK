package com.example.whoclicksfaster

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.payment.KeyStoreHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import network.celer.mobile.CAppCallback
import network.celer.mobile.GroupCallback
import network.celer.mobile.GroupResp
import java.io.File

class MainActivity : AppCompatActivity(), GroupCallback {


    private var keyStoreString = ""
    private var passwordStr = ""
    private var receiverAddr = ""

    private var datadir = ""

    private val clientSideDepositAmount = "500000000000000000" // 0.5 ETH
    private val serverSideDepositAmount = "1500000000000000000" // 1.5 ETH
    private var transferAmount: String = "30000000000000000" // 0.03 ETH


    lateinit var joinAddr: String

    val MAX = 50


    var numberOfClicks = 0

    var handler: Handler = Handler()

    var lock = false


    var callback = object : CAppCallback {
        override fun onStatusChanged(status: Long) {
            Log.e("whoclicksfaster", "createNewCAppSession onStatusChanged is: $status")
        }

        override fun onReceiveState(state: ByteArray?): Boolean {
            Log.e("whoclicksfaster", "createNewCAppSession onReceiveState : $state")


            handler.post {

                state?.let {
                    if (!lock) {
                        opponentScoreBar.progress = state[0].toInt()
                    }

                    if (!lock && state[0].toInt() >= MAX) {
                        clickButton.text = "You lost!"
                        clickButton.isEnabled = false
                        lock = true
                    }
                }


            }
            return true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generateFilePath()

        // Get keyStroeString and passwordStr
        keyStoreString = KeyStoreHelper().getKeyStoreString(this@MainActivity)
        passwordStr = KeyStoreHelper().getPassword()

        var keyStoreJson = Gson().fromJson(keyStoreString, KeyStoreData::class.java)
        joinAddr = "0x" + keyStoreJson.address

//        addLog("keyStoreString: ${keyStoreString}")
        Log.d("keyStoreString", keyStoreString)
        Log.d("joinAddr: ", joinAddr)

        val profileStr = getString(R.string.cprofile, datadir)


        CelerClientAPIHelper.initCelerClient(keyStoreString, passwordStr, profileStr)


        // Deposit some token from faucet
        FaucetHelper().getTokenFromFaucet(this, walletAddress = joinAddr, faucetCallBack = object : FaucetHelper.FaucetCallBack {
            override fun onSuccess() {

                Log.d("whoclicksfaster", "\n getTokenSucceed ")

//                CelerClientAPIHelper.joinCeler(clientSideDepositAmount, serverSideDepositAmount)
//
//                GameGroupAPIHelper.onNewGroupClient(keyStoreString, passwordStr, MainActivity.this)


            }

            override fun onFailure() {

                Log.d("whoclicksfaster", "\n getToken Error ")

            }

        })

        CelerClientAPIHelper.joinCeler(clientSideDepositAmount, serverSideDepositAmount)

        GameGroupAPIHelper.onNewGroupClient(keyStoreString, passwordStr, this)


    }


    override fun onRecvGroup(gresp: GroupResp?, err: String?) {

        Log.e("whoclicksfaster", "OnRecvGroup--------------------:")
        Log.e("whoclicksfaster", gresp?.toString())
        Log.e("whoclicksfaster", err)
        gresp?.let {

            handler.post {
                var code = it.g.code.toString()
                join_code.text = "Game code is: $code"
            }


            if (it.g.users.split(",").size == 2) {
                Log.d("whoclicksfaster", "Matched with a player!")

                CelerClientAPIHelper.initSession(this, gresp, callback)


                handler.post {
                    hideSoftKeyboard()
                    opponentScoreBar.progress = 0
                    yourScoreBar.progress = 0
                    numberOfClicks = 0
                    lock = false
                    join_code.text = "matched"
                    opponentScoreBar.max = MAX
                    yourScoreBar.max = MAX
                    clickButton.visibility = View.VISIBLE
                    clickButton.isEnabled = true
                    clickButton.text = "Click as fast as you can"
                    Log.d("whoclicksfaster", "session id: " + CelerClientAPIHelper.sessionId)
                    Log.d("whoclicksfaster", "round id: " + gresp.round.id)
                    Toast.makeText(this, CelerClientAPIHelper.sessionId, Toast.LENGTH_LONG).show()
                }

            }
        }

    }


    fun onCreateGame(v: View) {
        if (GameGroupAPIHelper.gc == null) {
            Toast.makeText(applicationContext, "GameGroupAPIHelper.onNewGroupClient failure. Try again later.", Toast.LENGTH_LONG).show()
            GameGroupAPIHelper.onNewGroupClient(keyStoreString, passwordStr, this)
        } else {
            GameGroupAPIHelper.createGame(joinAddr)
        }

    }

    fun onJoinGame(v: View) {
        var code = etJoinCode.text.toString().toLong()
        GameGroupAPIHelper.joinGame(joinAddr, code)
    }

    fun clickMe(v: View) {
        var state = ByteArray(1)
        numberOfClicks++
        state[0] = numberOfClicks.toByte()

        CelerClientAPIHelper.sendState(state)

        handler.post {
            clickButton.text = numberOfClicks.toString()
            yourScoreBar.progress = numberOfClicks
            if (!lock && numberOfClicks >= MAX) {
                clickButton.text = "You win!"
                clickButton.isEnabled = false
                lock = true
            }
        }

    }


    private fun generateFilePath() {
        val generaFile = File(this.filesDir.path, "celer")
        if (!generaFile.exists()) {
            generaFile.mkdir()
        }
        datadir = generaFile.path
    }


    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager!!.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

}
