package org.timer.main.video

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.viewinterop.*
import kotlinx.coroutines.*
import platform.UIKit.*
import platform.WebKit.*

@Composable
actual fun VideoPlayer(modifier: Modifier, url: String) {
    YouTubeIFramePlayer(
        url = url,
        modifier,
    )
}

@Composable
fun YouTubeIFramePlayer(
    url: String,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    val videoId = remember(url) {
        url.substringAfter("v=").substringBefore("&").ifEmpty { url.substringAfterLast("/") }
    }

    val htmlContent = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
            <style>
                body, html {
                    margin: 0;
                    padding: 0;
                    height: 100%;
                    overflow: hidden;
                    background-color: white;
                }
                .video-container {
                    position: relative;
                    width: 100%;
                    height: 100%;
                    background-color: white;
                }
                .video-container iframe{
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    border: none;
                   background-color: white;
                }
            </style>
          
            <script src="https://www.youtube.com/iframe_api"></script>
        </head>
        <body>
            <div class="video-container">
                <iframe 
                    id="youtubePlayer"
                    src="https://www.youtube.com/embed/$videoId?autoplay=1&mute=0&modestbranding=1&rel=0&showinfo=0" 
                    allow="autoplay; encrypted-media;"
                    frameborder="0">
                </iframe>
            </div>
        </body>
        </html>
    """.trimIndent()

    UIKitView(
        factory = {
            val webView = WKWebView().apply {
                scrollView.scrollEnabled = false
                backgroundColor = UIColor.whiteColor
                configuration.allowsInlineMediaPlayback = true
                configuration.mediaTypesRequiringUserActionForPlayback =
                    WKAudiovisualMediaTypes.MAX_VALUE
                loadHTMLString(htmlContent, baseURL = null)
            }
            webView
        },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        update = { view ->
            view.configuration.allowsInlineMediaPlayback = true
            scope.launch {
                view.reload()
            }
        }
    )
}