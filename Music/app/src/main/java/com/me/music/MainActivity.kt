package com.me.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.me.music.ui.theme.MusicTheme


import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.material3.Slider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    // Declare MediaPlayer for audio playback
    private var mediaPlayer: MediaPlayer? = null

    // Declare UI elements
    var seekBar: SeekBar? = null
    var textCurrentTime: TextView? = null
    var textTotalTime: TextView? = null
    var buttonPlay: ImageView? = null
    var buttonPause: ImageView? = null
    var buttonStop: ImageView? = null

    // Handler to update SeekBar and current time text every second
    private val handler = Handler(Looper.getMainLooper())

    // Runnable task that updates SeekBar and current playback time
    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                // Update SeekBar progress and current time text
                seekBar!!.progress = mediaPlayer!!.currentPosition
                textCurrentTime!!.text = formatTime(mediaPlayer!!.currentPosition)

                // Repeat this task every 1 second
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }


        // Create MediaPlayer instance with a raw audio resource
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)

        // Set listener to configure SeekBar and total time after MediaPlayer is ready
        mediaPlayer!!.setOnPreparedListener { mp: MediaPlayer ->
            seekBar!!.setMax(mp.duration)
            textTotalTime!!.setText(formatTime(mp.duration))
        }

        // Play button starts the audio and begins updating UI
        buttonPlay!!.setOnClickListener(View.OnClickListener { v: View? ->
            mediaPlayer!!.start()
            handler.post(updateSeekBar)
        })

        // Pause button pauses the audio playback
        buttonPause!!.setOnClickListener(View.OnClickListener { v: View? -> mediaPlayer!!.pause() })

        // Stop button stops playback and resets UI and MediaPlayer
        buttonStop!!.setOnClickListener(View.OnClickListener { v: View? ->
            mediaPlayer!!.stop()
            mediaPlayer = MediaPlayer.create(this, R.raw.sound)
            seekBar!!.setProgress(0)
            textCurrentTime!!.setText("0:00")
            textTotalTime!!.setText(formatTime(mediaPlayer!!.duration))
        })

        // Listen for SeekBar user interaction
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            // Called when progress is changed
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer != null) {
                    // Seek MediaPlayer to new position and update current time
                    mediaPlayer!!.seekTo(progress)
                    textCurrentTime!!.setText(formatTime(progress))
                }
            }

            // Not used, but required to override
            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            // Not used, but required to override
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    // Format milliseconds into minutes:seconds format (e.g., 1:05)
    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    // Clean up MediaPlayer and handler when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicTheme {
        MainView()
    }
}

@Composable
fun MainView(modifier: Modifier = Modifier) {
    var sliderPosition by remember { mutableStateOf(0f) }
    Image(painter = painterResource(id = R.drawable.music_icon), contentDescription = null)
    Text(text = sliderPosition.toString(), modifier = modifier)
    Slider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it }
    )
}

//TODO need to pull layout from other project
//TODO then, update all the logic to do what it is supposed to


