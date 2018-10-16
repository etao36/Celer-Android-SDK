package com.example.whoclicksfaster

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_join_celer.*
import kotlinx.android.synthetic.main.activity_main.*

class CreateOrJoinGroupActivity : AppCompatActivity() {

    private val TAG = "CreateOrJoinGroup"

    private var keyStoreString = ""
    private var passwordStr = ""
    private var joinAddr = ""

    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_join_group)


        keyStoreString = intent.getStringExtra("keyStoreString")
        passwordStr = intent.getStringExtra("passwordStr")
        joinAddr = intent.getStringExtra("joinAddr")
    }


    fun createGame(v: View) {

        var result = GameGroupAPIHelper.createNewGroupClient(keyStoreString, passwordStr)

        showTips("createNewGroupClient : $result")

        if (GameGroupAPIHelper.gc == null || !result.contains("Success")) {
            Toast.makeText(applicationContext, "GameGroupAPIHelper.createNewGroupClient failure. Try again later.", Toast.LENGTH_LONG).show()
        } else {
            var result = GameGroupAPIHelper.createGame(joinAddr)

            showTips("createGame : $result")
        }


    }


    fun joinGame(v: View) {
        var code = etJoinCode.text.toString().toLong()
        var result = GameGroupAPIHelper.joinGame(joinAddr, code)

        showTips("joinGame : $result")

    }


    private fun showTips(str: String) {

        handler.post {
            tips.append("\n" + str)
        }

    }
}
