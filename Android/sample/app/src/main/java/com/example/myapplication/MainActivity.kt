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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generateFilePath()
        val profile = getString(R.string.cprofile, datadir)

        try {
            val client = Mobile.newClient(keyStoreString, password, profile)
            client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
            print("Balance: ${client?.getBalance(1)?.available}")
            val hasJoin = client?.hasJoinedCeler(receiverAddr)
            print(hasJoin)
            client?.sendPay(receiverAddr, transferAmount)
        } catch(e: Exception) {
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
