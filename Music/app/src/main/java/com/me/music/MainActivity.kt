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
import kotlinx.serialization.Serializable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import android.content.Context

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            enableEdgeToEdge()
            setContent {
                MusicTheme {
                    App(this)
                }
            }
        }catch(e:Exception){
            val exc = e
        }
    }
}

// https://developer.android.com/guide/navigation/design
@Serializable
data class PlaybackDto(
    val playback: _iPlaybackService,
    val darkTheme: Boolean
)

@Serializable
data class SongSelectDto(
    val context: Context?,
    val darkTheme: Boolean
)

@Composable
fun App(
    context: Context,
    darkTheme: Boolean = isSystemInDarkTheme()
){
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = SongSelectDto(context = context, darkTheme = darkTheme)
    ) {
        composable<PlaybackDto> { backStackEntry ->
            val playback: PlaybackDto = backStackEntry.toRoute()
            PlaybackView(
                dto = playback,
                onNavigateToSongSelect = {
                    navController.navigate(
                        route = SongSelectDto(context = context, darkTheme = darkTheme)
                    )
                }
            )
        }
        composable<SongSelectDto> {
            SongSelectView(
                dto = SongSelectDto(context = context, darkTheme = darkTheme),
                onNavigateToPlayback = { playback ->
                    navController.navigate(
                        route = PlaybackDto(playback = playback, darkTheme = darkTheme)
                    )
                }
            )
        }
    }
}

@Composable
fun SongSelectView(
    dto: SongSelectDto,
    onNavigateToPlayback: (_iPlaybackService) -> Unit
){
    TextButton(
        onClick = {
            // Create MediaPlayer instance with a raw audio resource
            val mediaPlayer = MediaPlayer.create(dto.context, R.raw.sound)
            val playback = PlaybackService(mediaPlayer)
            onNavigateToPlayback(playback)
        }
    ) {
        Text(text = "Play Song")
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PlaybackView(
    dto: PlaybackDto,
    onNavigateToSongSelect: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        TextButton(
            onClick = onNavigateToSongSelect
        ) {
            Image(
                painter = painterResource(id = R.drawable.stop),
                contentDescription = null,
                modifier = Modifier.height(30.dp)
            )
        }
        Column (
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val imageId = if (dto.darkTheme) {
                R.drawable.music_icon_dark
            } else {
                R.drawable.music_icon
            }
            var sliderPosition by remember { mutableFloatStateOf(0f) }
            var positionText by remember { mutableStateOf("0:00") }
            var playPause by remember { mutableStateOf(false) }

            dto.playback.setProgressFunction { progress, text ->
                sliderPosition = progress
                positionText = text
            }

            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(height = 300.dp, width = 300.dp)
            )

            Column(

            ) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { it ->
                        sliderPosition = it
                        dto.playback.seekTo(it)
                    },
                    modifier = Modifier
                        .width(300.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(300.dp)
                ) {
                    Text(text = positionText)
                    Text(text = dto.playback.durationText())
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(300.dp)
            ) {
                TextButton(
                    onClick = {
                        dto.playback.playPause(playPause)
                        playPause = !playPause
                    }
                ) {
                    val playPauseImage = if (playPause) {
                        R.drawable.pause
                    } else {
                        R.drawable.play
                    }
                    Image(
                        painter = painterResource(id = playPauseImage),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(70.dp)
                    )
                }
                TextButton(
                    onClick = { dto.playback.seekTo(0f) }
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
}


@Preview(showBackground = true)
@Composable
fun PlaybackViewPreview() {
    MusicTheme (darkTheme = true) {
        PlaybackView(
            dto = PlaybackDto(
                playback = _mPlaybackService(),
                darkTheme = true
            ),
            onNavigateToSongSelect = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongSelectViewPreview() {
    MusicTheme (darkTheme = true) {
        SongSelectView(
            dto = SongSelectDto(
                context = null,
                darkTheme = true
            ),
            onNavigateToPlayback = {}
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


