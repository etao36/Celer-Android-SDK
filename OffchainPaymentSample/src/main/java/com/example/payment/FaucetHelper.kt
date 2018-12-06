package com.celer.joincelersample

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class FaucetHelper {

    interface FaucetCallBack {

        fun onSuccess()

        fun onFailure(toString: String)

    }

    // var faucetURL = "http://54.188.217.246:3008/donate/"
    fun getTokenFromPrivateNetFaucet(context: Context, faucetURL: String, walletAddress: String, faucetCallBack: FaucetCallBack) {

        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Request.Method.GET, "$faucetURL$walletAddress",
                Response.Listener {

                    response ->
                    Log.i("VOLLEY", response)

                    if (response == "200") {
                        faucetCallBack.onSuccess()
                    } else {
                        faucetCallBack.onFailure(response)
                    }
                },
                Response.ErrorListener {

                    error ->
                    Log.e("VOLLEY", error.toString())

                    faucetCallBack.onFailure(error.toString())
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Connection"] = "close"

                return params
            }

            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }
        }

        requestQueue.add(stringRequest)
    }

}