package com.example.whoclicksfaster

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.payment.KeyStoreHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import network.celer.mobile.*
import java.io.File

class MainActivity : AppCompatActivity(), GroupCallback {


    private var keyStoreString = ""
    private var passwordStr = ""
    private var receiverAddr = ""

    private var datadir = ""

    private val clientSideDepositAmount = "500000000000000000" // 0.5 ETH
    private val serverSideDepositAmount = "1500000000000000000" // 1.5 ETH
    private var transferAmount: String = "30000000000000000" // 0.03 ETH

    private var client: Client? = null
    private lateinit var gc: GroupClient
    lateinit var joinAddr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generateFilePath()

        // Get keyStroeString and passwordStr
        keyStoreString = KeyStoreHelper().getKeyStoreString(this@MainActivity)
        passwordStr = KeyStoreHelper().getPassword()

        var keyStoreJson = Gson().fromJson(keyStoreString, KeyStoreData::class.java)
        joinAddr = "0x" + keyStoreJson.address

        addLog("keyStoreString: ${keyStoreString}")
        Log.d("MainActivity", keyStoreString)
        addLog("passwordStr: ${passwordStr}")

        val profileStr = getString(R.string.cprofile, datadir)

        // Init Celer Client
        try {
            client = Mobile.newClient(keyStoreString, passwordStr, profileStr)
        } catch(e: Exception) {
            addLog("Init Celer Client Error: ${e.localizedMessage}")
        }


        // Deposit some token from faucet
        FaucetHelper().getTokenFromFaucet(this, walletAddress = joinAddr, faucetCallBack = object : FaucetHelper.FaucetCallBack {
            override fun onSuccess() {

                logtext.append("\n getTokenSucceed ")

                // Join Celer Network
                try {
                    client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
                    addLog("Balance: ${client?.getBalance(1)?.available}")
                } catch (e: Exception) {
                    addLog("Join Celer Network Error: ${e.localizedMessage}")

                }

                onNewGroupClient()



            }

            override fun onFailure() {

                logtext.append("\n getToken Error ")

            }

        })


    }


    fun onNewGroupClient() {
        try {
            gc = Mobile.newGroupClient("group-prod-ropsten.celer.app:10001", keyStoreString, passwordStr, this)
            addLog("Connected to Group Server")
        } catch (e: Exception) {
            addLog("NewGroupClient failed: " + e.toString())
            Log.e("NewGroupClient failed: ", e.toString())
        }
    }

    override fun onRecvGroup(gresp: GroupResp?, err: String?) {
        addLog("OnRecvGroup:")
        addLog(gresp.toString())
        addLog(err)
    }

    fun onCreatePrivate(v: View) {
        var g = Group()
        g.myId = keyStoreString
        g.code = 111111
        g.stake = "1000000000000000000"
        addLog("Bot: " + g.toString())
        try {
            gc.createPrivate(g)
        } catch (e: Exception) {
            addLog(e.toString())
        }
    }

    fun onJoinPrivate(v: View) {
        var g = Group()
        g.myId = keyStoreString
        g.code = 111111
        g.stake = "10"
        addLog("Join: " + g.toString())

        try {
            gc.joinPrivate(g)
        } catch (e: Exception) {
            addLog(e.toString())
        }
    }


    private fun generateFilePath() {
        val generaFile = File(this.filesDir.path, "celer")
        if (!generaFile.exists()) {
            generaFile.mkdir()
        }
        datadir = generaFile.path
    }


    fun addLog(text: String?) {
        Log.d("Celer Off-chain Payment", text)
        logtext.append("\n" + text)
    }

}
