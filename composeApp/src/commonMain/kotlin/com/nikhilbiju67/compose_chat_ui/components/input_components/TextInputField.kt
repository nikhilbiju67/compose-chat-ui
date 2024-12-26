package components.input_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.nikhilbiju67.compose_chat_ui.styles.AttachmentStyle
import com.nikhilbiju67.compose_chat_ui.styles.InputFieldStyle
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.baseline_attachment_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {},
    onAttachmentClick: () -> Unit = {},
    value: String,
    inputFieldStyle: InputFieldStyle,
    attachmentStyle: AttachmentStyle

) {

    val buttonSize = 50
    Column(modifier = Modifier) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Box(
                modifier = Modifier.clip(CircleShape)
                    .background(attachmentStyle.attachmentIconBackGroundColor).clickable {
                        onAttachmentClick()
                    },

                ) {
                Image(
                    painter = painterResource(Res.drawable.baseline_attachment_24),
                    contentDescription = "Attachment button",
                    colorFilter = ColorFilter.tint(attachmentStyle.attachmentIconColor),
                )
                // Pass the current state and count to the content composable

            }
            TextField(
                value,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    ),
                shape = RoundedCornerShape(25.dp),
                onValueChange = onChange,
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            )
        }
    }
}
