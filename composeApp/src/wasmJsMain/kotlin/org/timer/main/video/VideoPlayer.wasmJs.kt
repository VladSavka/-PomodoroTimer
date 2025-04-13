package org.timer.main.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import org.timer.main.HtmlView
import org.w3c.dom.HTMLIFrameElement

@Composable
actual fun VideoPlayer(modifier: Modifier, url: String) {
    val videoId = extractVideoId(url)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HtmlView(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            factory = {
                val iframe = document.createElement("iframe") as HTMLIFrameElement
                iframe.apply {
                    width = "100%"
                    height = "100%"
                    src =
                        "https://www.youtube.com/embed/$videoId?autoplay=1&mute=0&modestbranding=1&rel=0&showinfo=0"
                    setAttribute("frameborder", "0")
                    setAttribute(
                        "allow",
                        "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    )
                    setAttribute("allowfullscreen", "true")
                    setAttribute("referrerpolicy", "no-referrer-when-downgrade")
                }
            }
        )
    }
}

private fun extractVideoId(url: String): String {
    val videoIdRegex =
        Regex("""(?:youtube\.com\/(?:[^\/]+\/.+\/|(?:v|e(?:mbed)?)\/|.*[?&]v=)|youtu\.be\/)([^"&?\/\s]{11})""")
    return videoIdRegex.find(url)?.groupValues?.get(1) ?: "default_video_id"
}