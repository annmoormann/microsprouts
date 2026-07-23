package com.microsprouts.tasks.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.microsprouts.tasks.MainActivity
import com.microsprouts.tasks.R
import com.microsprouts.tasks.data.database.MicroSproutsDatabase
import com.microsprouts.tasks.data.entity.TaskList

class TodayWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = MicroSproutsDatabase.getDatabase(context)
        val allRawTasks = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            database.taskDao().getAllTasksRaw()
        }
        val todayCount = allRawTasks.count { it.parentId == null && it.currentList == TaskList.TODAY && !it.isCompleted }

        provideContent {
            TodayWidgetContent(count = todayCount)
        }
    }
}

@Composable
fun TodayWidgetContent(count: Int) {
    val intent = Intent(Intent.ACTION_VIEW).setClassName(
        "com.microsprouts.tasks",
        "com.microsprouts.tasks.MainActivity"
    )

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(
                ColorProvider(
                    day = Color.White,
                    night = Color(0xFF1E2421) // Soft dark mode surface background
                )
            )
            .clickable(actionStartActivity(intent))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.app_logo),
                contentDescription = "MicroSprouts Logo",
                modifier = GlanceModifier.size(20.dp)
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = "MicroSprouts",
                style = TextStyle(
                    color = ColorProvider(
                        day = Color(0xFF6C8E75), // Sage Green
                        night = Color(0xFF8FA897)  //Brighter Sage for dark mode
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        Text(
            text = count.toString(),
            style = TextStyle(
                color = ColorProvider(
                    day = Color(0xFF2C3E35), // Deep Slate
                    night = Color(0xFFE2E8E4) // Off-white text for dark mode
                ),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = "Today Tasks",
            style = TextStyle(
                color = ColorProvider(
                    day = Color(0xFF7A8B82),
                    night = Color(0xFFA0ACA5)
                ),
                fontSize = 12.sp
            )
        )
    }
}