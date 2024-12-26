package com.nikhilbiju67.compose_chat_ui.video

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import models.VideoMessage
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLayer
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun VideoPlayerView(modifier: Modifier, videoMessage: VideoMessage) {
    val resourcePath = videoMessage.url
    val url = resourcePath?.let { NSURL.URLWithString(it) }
    val playerItem = url?.let { AVPlayerItem.playerItemWithURL(it) }
    val player = remember { AVPlayer.playerWithPlayerItem(playerItem) }
    val playerLayer = remember { AVPlayerLayer() }
    val avPlayerViewController = remember { AVPlayerViewController() }
    avPlayerViewController.player = player
    avPlayerViewController.showsPlaybackControls = true



    avPlayerViewController.videoGravity = AVLayerVideoGravityResizeAspectFill

    playerLayer.player = player
    playerLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
    { view: UIView, rect: CValue<CGRect> ->
        CATransaction.begin()
        CATransaction.setValue(true, kCATransactionDisableActions)
        view.layer.setFrame(rect)
        playerLayer.setFrame(rect)
        avPlayerViewController.view.layer.frame = rect
        CATransaction.commit()
    }
    Column {
        UIKitView(
            factory = {
                val playerContainer = UIView()
                playerContainer.addSubview(avPlayerViewController.view)
                playerContainer
            },
            modifier = modifier,
            update = {
//                player.play()
//                avPlayerViewController.player?.play()
            },
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
    }
}





