package components.input_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    inputFieldStyle: InputFieldStyle

) {

    val buttonSize = 50
    Column(modifier = Modifier) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Box(
                modifier = Modifier.clickable {
                    onAttachmentClick()
                },

                ) {
                Image(
                    painter = painterResource(Res.drawable.baseline_attachment_24),
                    contentDescription = "Attachment button",
                )
                // Pass the current state and count to the content composable

            }
            TextField(
                value,
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Transparent,
                    backgroundColor = inputFieldStyle.textFieldBackGroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = inputFieldStyle.inputTextStyle.color
                ),
                shape = RoundedCornerShape(25.dp),
                onValueChange = onChange,
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            )
        }
    }
}
