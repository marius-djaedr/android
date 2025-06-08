package com.me.music


import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.me.music.service.PlaybackService
import com.me.music.service._iPlaybackService
import com.me.music.service._mPlaybackService
import com.me.music.ui.theme.MusicTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create MediaPlayer instance with a raw audio resource
        val mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        val playback = PlaybackService(mediaPlayer)
        enableEdgeToEdge()
        setContent {
            MusicTheme {
                MainView(playback)
            }
        }
    }
}

@Composable
fun MainView(
    playback: _iPlaybackService,
    darkTheme: Boolean = isSystemInDarkTheme()
){
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        PlaybackView(
            playback,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            darkTheme
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PlaybackView(
    playback: _iPlaybackService,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    Column (
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        val imageId = if(darkTheme){R.drawable.music_icon_dark}else{R.drawable.music_icon}
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        var positionText by remember {mutableStateOf("0:00")}
        var playPause by remember {mutableStateOf(false)}

        playback.setProgressFunction { progress, text ->
            sliderPosition = progress
            positionText = text
        }

        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier.size(height = 300.dp, width = 300.dp)
        )

        Column(

        ){
            Slider(
                value = sliderPosition,
                onValueChange = {it ->
                    sliderPosition = it
                    playback.seekTo(it)
                },
                modifier = Modifier
                    .width(300.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(300.dp)
            ){
                Text(text = positionText)
                Text(text = playback.durationText())
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(300.dp)
        ){
            TextButton(
                onClick = {
                    playback.playPause(playPause)
                    playPause = !playPause
                }
            ) {
                val playPauseImage = if(playPause){R.drawable.pause}else{R.drawable.play}
                Image(
                    painter = painterResource(id = playPauseImage),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(70.dp)
                )
            }
            TextButton(
                onClick = { playback.seekTo(0f) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.refresh),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(50.dp)
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MusicTheme (darkTheme = true) {
        MainView(
            playback = _mPlaybackService(),
            darkTheme = true
        )
    }
}

//TODO desired features
//- scan music from library
//- play queue
//- shuffle all
//- sort and select music by author and album
  //- on author and album pages, button to shuffle and button to play all, simply add to play queue
//- display current playing song, artist, album
//- autoplay next in queue
//- skip song buttons, forward and back
//- bluetooth
//- smart playlist
//- cast
//- integrate with google maps


