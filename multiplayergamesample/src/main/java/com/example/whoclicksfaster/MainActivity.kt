package com.example.whoclicksfaster

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.payment.KeyStoreHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import network.celer.mobile.*
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), GroupCallback {


    private var keyStoreString = ""
    private var passwordStr = ""
    private var receiverAddr = ""

    private var datadir = ""

    private val clientSideDepositAmount = "500000000000000000" // 0.5 ETH
    private val serverSideDepositAmount = "1500000000000000000" // 1.5 ETH
    private var transferAmount: String = "30000000000000000" // 0.03 ETH

    private var client: Client? = null
    var sessionId: String? = null

    private lateinit var gc: GroupClient
    lateinit var joinAddr: String


    private var opponentIndex = -1
    private var myIndex = -1
    private var myAddress: String? = null
    private var opponentAddress: String? = null

    var handler: Handler = Handler()

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
        Log.d("keyStoreString", keyStoreString)
        Log.d("joinAddr: ", joinAddr)
        addLog("passwordStr: ${passwordStr}")

        val profileStr = getString(R.string.cprofile, datadir)

        // Init Celer Client
        try {
            client = Mobile.newClient(keyStoreString, passwordStr, profileStr)
        } catch(e: Exception) {
            addLog("Init Celer Client Error: ${e.localizedMessage}")
            Log.d("InitClient Error: ", e.localizedMessage)
        }


        // Deposit some token from faucet
        FaucetHelper().getTokenFromFaucet(this, walletAddress = joinAddr, faucetCallBack = object : FaucetHelper.FaucetCallBack {
            override fun onSuccess() {

                logtext.append("\n getTokenSucceed ")

//                // Join Celer Network
//                try {
//                    client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
//                    addLog("Balance: ${client?.getBalance(1)?.available}")
//                } catch (e: Exception) {
//                    addLog("Join Celer Network Error: ${e.localizedMessage}")
//
//                }
//
//                onNewGroupClient()


            }

            override fun onFailure() {

                logtext.append("\n getToken Error ")

            }

        })

        // Join Celer Network
        try {
            client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
            addLog("Balance: ${client?.getBalance(1)?.available}")
        } catch (e: Exception) {
            addLog("Join Celer Network Error: ${e.localizedMessage}")

        }

        onNewGroupClient()


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
                join_code.text = "matched"
                initSession(gresp)
            }
        }

    }

    fun leave() {
        gc?.let {
            Log.e("whoclicksfaster", "leave previous group")
            var g = Group()
            g.myId = joinAddr
            it.leave(g)
        }
    }

    private fun initSession(gresp: GroupResp?) {
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


                    //TODO CREATE SESSION

                    val cApp = CApp()

                    cApp.callback = object : CAppCallback {
                        override fun onStatusChanged(status: Long) {
//                        Timber.d("createNewCAppSession onStatusChanged is: %s", status)
                        }

                        override fun onReceiveState(state: ByteArray?): Boolean {
//                        Timber.d("createNewCAppSession onReceiveState")
                            state?.let {
                                //                            currentCAppStateLive.postValue(state)
                            }
                            return true
                        }
                    }

                    val constructor = FunctionEncoder.encodeConstructor(Arrays.asList(
                            Address(myAddress),
                            Address(opponentAddress),
                            Uint256(3),
                            Uint256(3),
                            Uint8(5),
                            Uint8(3)))


                    sessionId = client?.newCAppSession(cApp, constructor, gresp.round.id)


                }


            }

        }


    }

    fun onCreatePrivate(v: View) {
        leave()
        var g = Group()
        g.myId = joinAddr
        g.size = 2
        g.stake = "1000000000000000000"
        addLog("Create: " + g.toString())
        try {
            gc.createPrivate(g)
        } catch (e: Exception) {
            addLog(e.toString())
        }
    }

    fun onJoinPrivate(v: View) {
        leave()
        var g = Group()
        g.myId = joinAddr
        g.code = etJoinCode.text.toString().toLong()
        g.stake = "10"

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
