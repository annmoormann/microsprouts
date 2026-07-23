package com.microsprouts.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microsprouts.tasks.data.entity.Category
import com.microsprouts.tasks.ui.theme.BrandPalette
import com.microsprouts.tasks.ui.theme.MutedGray
import com.microsprouts.tasks.ui.theme.SlateText
import com.microsprouts.tasks.ui.theme.WarmSand

@Composable
fun CategoryChip(
    category: Category,
    categoryIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    // Obtain assigned color from BrandPalette using index or color property
    val accentColor: Color = BrandPalette.getColorForIndex(categoryIndex)
    val shape = RoundedCornerShape(8.dp)

    Surface(
        modifier = modifier
            .clip(shape)
            .border(width = 0.5.dp, color = MutedGray.copy(alpha = 0.5f), shape = shape),
        shape = shape,
        color = WarmSand,
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(24.dp)
        ) {
            // Left vertical edge bar in brand accent color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .background(accentColor)
            )

            // Category Label Text
            Text(
                text = category.name,
                color = SlateText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
