package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

const val BlinkTime = 500
@Composable
fun BlinkView(
    modifier: Modifier = Modifier,
    blink: Boolean = true,
    child: @Composable () -> Unit,
) {
    var visible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = child.toString()) {
        while (blink) {
            delay(BlinkTime.milliseconds)
            visible = !visible
        }
    }

    Box(modifier = Modifier.size(30.dp)) {
        AnimatedVisibility(
            modifier = modifier,
            visible = visible||blink==false,
            enter = fadeIn(animationSpec = TweenSpec(durationMillis = 20)),
            exit = fadeOut(animationSpec = TweenSpec(durationMillis = 20))
        ) {
            child()
        }
    }

}

