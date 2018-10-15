package com.example.whoclicksfaster

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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


    var clickNum = 0

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


        ClientAPIHelper.initCelerClient(keyStoreString, passwordStr, profileStr)


        // Deposit some token from faucet
        FaucetHelper().getTokenFromFaucet(this, walletAddress = joinAddr, faucetCallBack = object : FaucetHelper.FaucetCallBack {
            override fun onSuccess() {

                Log.d("whoclicksfaster", "\n getTokenSucceed ")

//                ClientAPIHelper.joinCeler(clientSideDepositAmount, serverSideDepositAmount)
//
//                GroupAPIHelper.onNewGroupClient(keyStoreString, passwordStr, MainActivity.this)


            }

            override fun onFailure() {

                Log.d("whoclicksfaster", "\n getToken Error ")

            }

        })

        ClientAPIHelper.joinCeler(clientSideDepositAmount, serverSideDepositAmount)

        GroupAPIHelper.onNewGroupClient(keyStoreString, passwordStr, this)


    }


    override fun onRecvGroup(gresp: GroupResp?, err: String?) {

        Log.e("whoclicksfaster", "OnRecvGroup--------------------:")
        Log.e("whoclicksfaster", gresp?.toString())
        Log.e("whoclicksfaster", err)
        gresp?.let {

            handler.post {
                var code = it.g.code.toString()
                join_code.text = "join code is: " + code
            }


            if (it.g.users.split(",").size == 2) {
                Log.e("whoclicksfaster", "matched")

                ClientAPIHelper.initSession(this, gresp, callback)


                handler.post {
                    opponentScoreBar.progress = 0
                    yourScoreBar.progress = 0
                    clickNum = 0
                    lock = false
                    join_code.text = "matched"
                    opponentScoreBar.max = MAX
                    yourScoreBar.max = MAX
                    clickButton.visibility = View.VISIBLE
                    clickButton.isEnabled = true
                    clickButton.text = "Click as fast as you can"
                    Log.d("whoclicksfaster", "session id: " + ClientAPIHelper.sessionId)
                    Log.d("whoclicksfaster", "round id: " + gresp.round.id)
                    Toast.makeText(this, ClientAPIHelper.sessionId, Toast.LENGTH_LONG).show()
                }

            }
        }

    }


    fun onCreatePrivate(v: View) {
        if (GroupAPIHelper.gc == null) {
            Toast.makeText(applicationContext, "GroupAPIHelper.onNewGroupClient failure. Try again later.", Toast.LENGTH_LONG).show()
            GroupAPIHelper.onNewGroupClient(keyStoreString, passwordStr, this)
        } else {
            GroupAPIHelper.onCreatePrivate(joinAddr)
        }

    }

    fun onJoinPrivate(v: View) {
        var code = etJoinCode.text.toString().toLong()
        GroupAPIHelper.onJoinPrivate(joinAddr, code)
    }

    fun clickMe(v: View) {
        var state = ByteArray(1)
        clickNum++
        state[0] = clickNum.toByte()

        ClientAPIHelper.sendState(state)

        handler.post {
            clickButton.text = clickNum.toString()
            yourScoreBar.progress = clickNum
            if (!lock && clickNum >= MAX) {
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


}
