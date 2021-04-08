package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Context
import android.media.MediaPlayer
import com.github.HumanLearning2021.HumanLearningApp.R
import javax.inject.Inject

class LearningAudioFeedback (val applicationContext: Context) {
    private lateinit var correctMp: MediaPlayer
    private lateinit var incorrectMp: MediaPlayer

    fun initMediaPlayers(){
        correctMp = MediaPlayer.create(applicationContext, R.raw.bravo)
        incorrectMp = MediaPlayer.create(applicationContext, R.raw.dommage)
    }

    fun releaseMediaPlayers(){
        // we release when the user pauses the app so that we don't eat up resources uselessly
        correctMp.release()
        incorrectMp.release()
    }


    fun stopAndPrepareMediaPlayers(){
        stopAndPrepare(correctMp)
        stopAndPrepare(incorrectMp)
    }

    private fun stopAndPrepare(mp : MediaPlayer) {
        if (mp.isPlaying) {
            mp.stop()
            // from https://developer.android.com/reference/android/media/MediaPlayer#prepare()
            // "For files, it is OK to call prepare(), which blocks until MediaPlayer
            // is ready for playback."
            mp.prepare()
        }
    }

    fun startCorrectFeedback(){
        correctMp.start()
    }

    fun startIncorrectFeedback(){
        incorrectMp.start()
    }

    /**
     * Gets the MediaPlayer that plays the 'correct' feedback (eg. 'bravo!')
     * ONLY USE THIS FOR UNIT TESTING
     */
    fun __testing_getCorrectMP(): MediaPlayer = correctMp
    /**
     * Gets the MediaPlayer that plays the 'incorrect' feedback (eg. 'dommage!')
     * ONLY USE THIS FOR UNIT TESTING
     */
    fun __testing_getIncorrectMP(): MediaPlayer = incorrectMp
}