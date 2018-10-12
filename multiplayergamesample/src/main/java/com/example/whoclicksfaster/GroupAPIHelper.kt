package com.example.whoclicksfaster

import android.util.Log
import network.celer.mobile.Group
import network.celer.mobile.GroupCallback
import network.celer.mobile.GroupClient
import network.celer.mobile.Mobile

object GroupAPIHelper {
    private lateinit var gc: GroupClient

    fun onNewGroupClient(keyStoreString: String, passwordStr: String, callback: GroupCallback) {
        try {
            gc = Mobile.newGroupClient("group-prod-ropsten.celer.app:10001", keyStoreString, passwordStr, callback)
            Log.e("whoclicksfaster ", "Connected to Group Server")
        } catch (e: Exception) {
            Log.e("whoclicksfaster ", e.toString())
        }
    }


    fun onCreatePrivate(joinAddr: String) {
        leave(joinAddr)
        var g = Group()
        g.myId = joinAddr
        g.size = 2
        g.stake = "1000000000000000"
        Log.e("whoclicksfaster ", "Create: " + g.toString())
        try {
            gc.createPrivate(g)
        } catch (e: Exception) {
            Log.e("whoclicksfaster ", e.toString())
        }
    }


    fun onJoinPrivate(joinAddr: String, code: Long) {
        leave(joinAddr)
        var g = Group()
        g.myId = joinAddr
        g.code = code
        g.stake = "10"

        try {
            gc.joinPrivate(g)
        } catch (e: Exception) {
            Log.e("whoclicksfaster ", e.toString())
        }
    }


    fun leave(joinAddr: String) {
        gc?.let {
            Log.e("whoclicksfaster", "leave previous group")
            var g = Group()
            g.myId = joinAddr
            it.leave(g)
        }
    }


}