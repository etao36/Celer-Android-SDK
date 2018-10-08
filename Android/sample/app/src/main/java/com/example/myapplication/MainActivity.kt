package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import network.celer.mobile.*
import java.io.File

class MainActivity : AppCompatActivity() {

    // TODO: Add your own keystore and its password here. Put your receiver addr
    private var keyStoreString = ""
    private var password = ""
    private var receiverAddr = ""

    private var datadir = ""

    private val clientSideDepositAmount = "500000000000000000" // 0.5 cETH
    private val serverSideDepositAmount = "1500000000000000000" // 1.5 cETH
    private var transferAmount: String = "30000000000000000" // 0.03 ETH

    private var client: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generateFilePath()

        keyStoreString = KeyStoreHelper().getKeyStoreString(this@MainActivity)
        password = KeyStoreHelper().getPassword()

        val profile = getString(R.string.cprofile, datadir)

        try {
            client = Mobile.newClient(keyStoreString, password, profile)
        } catch(e: Exception) {
            Log.e("GoLog", "Error: ${e.localizedMessage}")
        }

        try {
            client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
            print("Balance: ${client?.getBalance(1)?.available}")
        } catch (e: Exception) {
            Log.e("GoLog", "Error: ${e.localizedMessage}")

        }

        try {
            receiverAddr = "0x2718aaa01fc6fa27dd4d6d06cc569c4a0f34d399"
            val hasJoin = client?.hasJoinedCeler(receiverAddr)
            print(hasJoin)
        } catch (e: Exception) {
            Log.e("GoLog", "Error: ${e.localizedMessage}")

        }

        try {
            client?.sendPay(receiverAddr, transferAmount)
        } catch (e: Exception) {
            Log.e("GoLog", "Error: ${e.localizedMessage}")

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
