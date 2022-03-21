package com.example.actualpepper


import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    private val TAG = "this is a fucking tag"
    private var qiContext: QiContext? = null
    private lateinit var mediaPlayer: MediaPlayer
    /*  override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Register the RobotLifecycleCallbacks to this Activity.
            QiSDK.register(this, this)
        }
      */
    ////////////////////////////
    // Application Life Cycle //
    ////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        QiSDK.register(this, this)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY)
        mediaPlayer = MediaPlayer()
        setContentView(R.layout.main_activity)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this, this)
        mediaPlayer.release()
    }


    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
        this.qiContext = qiContext
        Log.i(TAG, "Robot focus gained, running.")
        runPresentation()
    }

    ////////////////////////////
    //        helpers         //
    ////////////////////////////

    private fun makeSay(text : String) : Say {
        return SayBuilder.with(qiContext)
            .withText(text)
            .build()
    }

    ////////////////////////
    // Presentation logic //
    ////////////////////////

    private fun runPresentation() {

        // Part 1: "Making me talk..."

        setImage(R.drawable.scene1)
        makeSay("Okay, so ... making me talk is a first step a bit like ...").run()
        Thread.sleep(200)
        makeSay("a rolling rock ...").run()
        setImage(R.drawable.scene2)

        // Part 2: "Let me show you..."

        Thread.sleep(800)
        setImage(R.drawable.scene3)
        val sayMore = makeSay("But there's much more to do. Let me show you ! ...")
        val animateEnthusiast = makeAnimate(R.raw.nicereaction_a002)
        val animFuture = animateEnthusiast.async().run()
        animFuture.thenConsume {
            setImage(R.drawable.scene4)
        }
        Future.waitAll(sayMore.async().run(),
            animFuture).value

        // Part 3: "I can make sound"

        playMedia(R.raw.stone_breaks)
        setImage(R.drawable.scene5)
        makeSay("Like me, a rock can make sound.").run()
        setImage(R.drawable.scene6)
        Thread.sleep(1_000)
        setImage(R.drawable.scene7)
        makeSay("and light.").run()
        Thread.sleep(1_000)
        setImage(R.drawable.scene8)

        // Part 4: "And become beautiful"

        Timer("Diamond", false).schedule(2_000) {
            setImage(R.drawable.scene9)
            playMedia(R.raw.magic)
        }
        val sayBeautiful = makeSay("and become beautiful and precious! All this thanks to you")
        val animateYeah = makeAnimate(R.raw.yeah_b001)
        Future.waitAll(sayBeautiful.async().run(), animateYeah.async().run()).value
        setImage(R.drawable.scene10)
        makeSay("I can't wait for what we are gonna do !").run()
        Thread.sleep(1_000)
        clearImage()
    }

    private fun setImage(resource : Int) {
        runOnUiThread {
            splashImageView.setImageResource(resource)
            splashImageView.visibility = View.VISIBLE
        }
    }

    private fun clearImage() {
        runOnUiThread {
            splashImageView.visibility = View.GONE
        }
    }

    private fun makeAnimate(animResource: Int) : Animate {
        val animation = AnimationBuilder.with(qiContext)
            .withResources(animResource)
            .build()

        return AnimateBuilder.with(qiContext)
            .withAnimation(animation)
            .build()
    }

    private fun playMedia(mediaResource: Int) {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(applicationContext, mediaResource)
        mediaPlayer.start()
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
    }

    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }
}