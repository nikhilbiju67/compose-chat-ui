package com.nikhilbiju67.compose_chat_ui.components.input_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikhilbiju67.compose_chat_ui.styles.AttachmentStyle

@Composable
fun AttachmentSheetContent(
    showAttachmentSheet: Boolean,
    attachmentStyle: AttachmentStyle,
    onAttachmentHide: () -> Unit
) {
    var showAttachmentSheet1 = showAttachmentSheet
    Column(
        modifier = attachmentStyle.modifier ?: Modifier.fillMaxHeight().fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f).fillMaxWidth()
                .height(50.dp)
                .background(Color.Black.copy(alpha = 0.4f)).clickable {
                    onAttachmentHide()
                }
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(85.dp),
            modifier = Modifier.weight(1f).background(attachmentStyle.backGroundColor),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            items(attachmentStyle.attachmentOptions.size) { index ->
                val attachmentOption = attachmentStyle.attachmentOptions[index]
                // Replace with your list size
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = attachmentOption.attachmentModifier
                            ?: Modifier.padding(16.dp)
                                .aspectRatio(1f) // Make each item square
                                .background(
                                    attachmentOption.containerColor,
                                    shape = CircleShape
                                ).padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            attachmentOption.icon()
                        }

                    }
                    Text(
                        text = attachmentOption.label,
                        style = attachmentOption.labelStyle ?: TextStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
