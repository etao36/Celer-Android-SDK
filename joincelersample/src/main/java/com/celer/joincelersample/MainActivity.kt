package com.celer.joincelersample

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.celer.celersdk.Celer
import kotlinx.android.synthetic.main.activity_main.*
import network.celer.celersdk.Client

class MainActivity : AppCompatActivity() {

    private val TAG = "joincelersample"

    var handler: Handler = Handler()

    var profileStr = ""

    var faucetURL = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    fun joinCeler(v: View) {

        progressBar.visibility = View.VISIBLE

        createWallet()

        getTokenFromFaucet()

    }

    private fun createWallet() {

        KeyStoreHelper.generateAccount(this)

        showTips("keyStoreString" + KeyStoreHelper.getKeyStoreString())
        showTips("joinAddr: " + KeyStoreHelper.getAddress())

        faucetURL = "http://54.188.217.246:3008/donate/"
        profileStr = getString(R.string.cprofile, KeyStoreHelper.generateFilePath(this))


//        faucetURL = "https://osp1-test-priv.celer.app/donate/"
//        profileStr = getString(R.string.cprofile_osp1_test_priv)
    }


    private fun getTokenFromFaucet() {

        // Get some token from faucet
        FaucetHelper().getTokenFromPrivateNetFaucet(context = this,
                faucetURL = faucetURL,
                walletAddress = KeyStoreHelper.getAddress(), faucetCallBack = object : FaucetHelper.FaucetCallBack {
            override fun onSuccess() {
                showTips("getTokenFromFaucet success,wait transcation complete")
                createAndJoinCeler()
            }

            override fun onFailure() {
                showTips("getTokenFromFaucet error ")

                handler.post {
                    progressBar.visibility = View.GONE
                }
            }

        })

    }

    fun createAndJoinCeler() {

        var listener: Celer.Listener = object : Celer.Listener {

            override fun onError(code: Int, desc: String) {

                showTips("Join Celer Error: $code -- $desc")

                handler.post {
                    progressBar.visibility = View.GONE
                }


            }

            override fun onReady(celerClient: Client) {


                showTips("Celer Ready ")

                handler.post {
                    button.text = "Reset"

                    progressBar.visibility = View.GONE
                }
            }

        }


        Celer.Builder().keyStoreString(KeyStoreHelper.getKeyStoreString())
                .passwordStr(KeyStoreHelper.getPassword())
                .ospPlanUrl(profileStr)
                .listener(listener)
                .build()
                .createAndJoinCeler()


    }



    private fun showTips(str: String) {
        Log.e(TAG, str)
        handler.post {
            tips.append("\n" + str)
        }

    }


}
