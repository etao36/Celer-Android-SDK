package com.celer.joincelersample

import android.content.Context
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.IOException


class FaucetHelper {

    interface FaucetCallBack {

        fun onSuccess()

        fun onFailure(toString: String)

    }


    // var faucetURL = "http://54.188.217.246:3008/donate/"
    fun getTokenFromPrivateNetFaucet(context: Context, faucetURL: String, walletAddress: String, faucetCallBack: FaucetCallBack) {


        var okHttpClient = OkHttpClient()
        var request = okhttp3.Request.Builder().url("$faucetURL$walletAddress").method("GET", null).build()

        var call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                faucetCallBack.onFailure(e.toString())
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.code() == 200) {
                    faucetCallBack.onSuccess()
                } else {
                    faucetCallBack.onFailure(response.message())
                }

            }

        })


    }


}