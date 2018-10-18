package com.example.whoclicksfaster

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_fast_click_game.*
import network.celer.celersdk.CAppCallback
import org.spongycastle.util.encoders.Hex

class FastClickGameActivity : AppCompatActivity() {
    private val TAG = "who clicks faster"

    val MAX = 50

    var myScore = 0

    var opponentScore = 0

    var lock = false

    var handler: Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fast_click_game)

        myScoreBar?.max = MAX
        opponentScoreBar?.max = MAX

        CelerClientAPIHelper.initSession(this, GameGroupAPIHelper.groupResponse, callback)
    }


    fun clickMe(v: View) {
        var state = ByteArray(3)
        myScore++
        state[0] = 0x0

        if (CelerClientAPIHelper.myIndex == 1) {
            state[1] = myScore.toByte()
            state[2] = opponentScore.toByte()
        } else {
            state[1] = opponentScore.toByte()
            state[2] = myScore.toByte()
        }

        CelerClientAPIHelper.sendState(state)

        handler.post {
            clickButton.text = myScore.toString()
            myScoreBar.progress = myScore
            if (!lock && myScore >= MAX) {
                clickButton.text = "You win!"
                clickButton.isEnabled = false
                lock = true
            }
        }

    }

    private var callback = object : CAppCallback {
        override fun onStatusChanged(status: Long) {
            Log.d(TAG, "createNewCAppSession onStatusChanged : $status")
        }

        override fun onReceiveState(state: ByteArray?): Boolean {
            Log.d(TAG, "createNewCAppSession onReceiveState : ${Hex.toHexString(state)}")

            Log.d(TAG, "CelerClientAPIHelper.myIndex : ${CelerClientAPIHelper.myIndex}")
            Log.d(TAG, "CelerClientAPIHelper.opponentIndex : ${CelerClientAPIHelper.opponentIndex}")
            Log.d(TAG, "Player 1 score: ${state!![1].toInt()}")
            Log.d(TAG, "Player 2 score : ${state!![2].toInt()}")

            if (CelerClientAPIHelper.myIndex == 1) {
                opponentScore = state!![2].toInt()
            } else {
                opponentScore = state!![1].toInt()
            }

            Log.d(TAG, "opponent score : $opponentScore")


            handler.post {

                state?.let {
                    if (!lock) {
                        opponentScoreBar.progress = opponentScore
                    }

                    if (!lock && opponentScore >= MAX) {
                        clickButton.text = "You lost!"
                        clickButton.isEnabled = false
                        lock = true
                    }
                }


            }
            return true
        }
    }
}
