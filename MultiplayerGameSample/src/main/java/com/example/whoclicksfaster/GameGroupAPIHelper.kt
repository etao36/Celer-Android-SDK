package com.example.whoclicksfaster

import android.util.Log
import network.celer.mobile.*

object GameGroupAPIHelper {


    private val TAG = "who clicks fast"
    var gc: GroupClient? = null
    var gresp: GroupResp? = null

    fun createNewGroupClient(keyStoreString: String, passwordStr: String, callback: GroupCallback): String {
        try {
            gc = Mobile.newGroupClient("group-test-priv.celer.app:10001", keyStoreString, passwordStr, callback)
            Log.e("whoclicksfaster ", "Connected to Group Server")
            return "Connected to Group Server Success"
        } catch (e: Exception) {
            Log.e("whoclicksfaster ", e.toString())
            return e.toString()
        }
    }


    fun createGame(joinAddr: String): String {
        leave(joinAddr)
        var g = Group()
        g.myId = joinAddr
        g.size = 2
        g.stake = "1000000000000000"
        Log.e("whoclicksfaster ", "Create: " + g.toString())
        try {
            gc?.createPrivate(g)
            return "Success"
        } catch (e: Exception) {
            Log.e("whoclicksfaster ", e.toString())
            return e.toString()
        }
    }


    fun joinGame(joinAddr: String, code: Long, stake:String) {
        leave(joinAddr)
        var g = Group()
        g.myId = joinAddr
        g.code = code
        g.stake = stake

        try {
            gc?.joinPrivate(g)
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