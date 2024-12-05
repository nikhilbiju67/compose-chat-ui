import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Enum to define bubble alignment
enum class BubbleAlignment {
    Left, Right
}

// Custom chat bubble shape with alignment
fun ChatBubbleShape(alignment: BubbleAlignment): Shape {
    return GenericShape { size, _ ->
        val tailWidth = 20f
        val tailHeight = 10f
        val cornerRadius = 30f

        // Add rounded rectangle
        addRoundRect(
            RoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height - tailHeight,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        )

        // Draw the tail based on alignment
        when (alignment) {
            BubbleAlignment.Right -> {
                moveTo(size.width, size.height - tailHeight)
                lineTo(size.width + tailWidth, size.height - tailHeight / 2)
                lineTo(size.width, size.height)
                close()
            }
            BubbleAlignment.Left -> {
                moveTo(0f, size.height - tailHeight)
                lineTo(-tailWidth, size.height - tailHeight / 2)
                lineTo(0f, size.height)
                close()
            }
        }
    }
}

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    alignment: BubbleAlignment,
    backgroundColor: Color = MaterialTheme.colors.onSurface,
    child: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = ChatBubbleShape(alignment))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        child()
    }
}
