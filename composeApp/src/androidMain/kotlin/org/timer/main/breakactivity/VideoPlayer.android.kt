package org.timer.main.breakactivity

import android.app.*
import android.content.pm.*
import android.os.*
import android.view.*
import android.view.WindowInsets
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.*

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@Composable
actual fun VideoPlayer(modifier: Modifier, url: String) {
    YoutubeVideoPlayer(modifier = modifier, youtubeURL = url, autoPlay = true, showControls = true)
}

@Composable
fun YoutubeVideoPlayer(
    modifier: Modifier = Modifier,
    youtubeURL: String,
    isPlaying: (Boolean) -> Unit = {},
    isLoading: (Boolean) -> Unit = {},
    onVideoEnded: () -> Unit = {},
    autoPlay: Boolean,
    showControls: Boolean
) {
    val context = LocalContext.current
    var activity by remember { mutableStateOf<Activity?>(null) }

    LaunchedEffect(context) {
        activity = context as? Activity
    }
    val videoId = remember(youtubeURL) {
        youtubeURL.substringAfter("v=").substringBefore("&")
            .ifEmpty { youtubeURL.substringAfterLast("/") }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var player: YouTubePlayer? = null
    val playerView = YouTubePlayerView(context)
    var isFullScreen by remember { mutableStateOf(false) }
    var isLoadingState by remember { mutableStateOf(true) }
    var thumbnailLoaded by remember { mutableStateOf(false) }

    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

    var fullscreenView: View? by remember { mutableStateOf(null) }

    val fullScreenListener = object : FullscreenListener {
        override fun onEnterFullscreen(view: View, exitFullscreen: () -> Unit) {
            isFullScreen = true
            fullscreenView = view
            playerView.visibility = View.GONE

            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            Handler(Looper.getMainLooper()).post {
                (activity?.window?.decorView as ViewGroup).addView(view)
                activity?.let { configureFullScreen(it, true) }

            }
            player?.play()
        }

        override fun onExitFullscreen() {
            isFullScreen = false
            playerView.visibility = View.VISIBLE
            fullscreenView?.let { view ->
                (activity?.window?.decorView as ViewGroup).removeView(view)
                fullscreenView = null
            }

            Handler(Looper.getMainLooper()).post {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                activity?.let {
                    configureFullScreen(it, false)
                }
            }
            player?.play()
        }
    }

    val playerStateListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            player = youTubePlayer
            youTubePlayer.loadVideo(videoId, 0F)
            isLoadingState = false
            isLoading(false)
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState,
        ) {
            when (state) {
                PlayerConstants.PlayerState.BUFFERING -> {
                    isLoadingState = true
                    isLoading(true)
                    isPlaying(false)
                }

                PlayerConstants.PlayerState.PLAYING -> {
                    isLoadingState = false
                    isLoading(false)
                    isPlaying(true)
                    thumbnailLoaded = true
                }

                PlayerConstants.PlayerState.ENDED -> {
                    isPlaying(false)
                    isLoading(false)
                    onVideoEnded()
                }

                else -> {}
            }
        }
    }

    val playerBuilder = IFramePlayerOptions.Builder().apply {
        controls(if (showControls) 1 else 0)
        fullscreen(1)
        autoplay(if (autoPlay) 1 else 0)
        modestBranding(1)
        rel(0)
        ivLoadPolicy(3)
        ccLoadPolicy(1)
    }

    Box(modifier = modifier.background(Color.Black)) {
//        if (!thumbnailLoaded) {
//            Image(
//                painter = rememberAsyncImagePainter(thumbnailUrl),
//                contentDescription = "Video Thumbnail",
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black)
//            )
//        }

        if (isLoadingState) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.White
            )
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .alpha(if (isLoadingState) 0f else 1f),
            factory = {
                playerView.apply {
                    enableAutomaticInitialization = false
                    initialize(playerStateListener, playerBuilder.build())
                    addFullscreenListener(fullScreenListener)
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            playerView.removeYouTubePlayerListener(playerStateListener)
            playerView.removeFullscreenListener(fullScreenListener)
            playerView.release()
            player = null
        }
    }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> player?.play()
                Lifecycle.Event.ON_PAUSE -> player?.pause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}

fun configureFullScreen(activity: Activity, enable: Boolean) {
    if (enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.setDecorFitsSystemWindows(false)
            activity.window.insetsController?.apply {
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.setDecorFitsSystemWindows(true)
            activity.window.insetsController?.show(WindowInsets.Type.systemBars())
        } else {
            @Suppress("DEPRECATION")
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}