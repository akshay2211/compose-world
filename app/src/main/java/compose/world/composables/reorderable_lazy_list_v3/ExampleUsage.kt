package compose.world.composables.reorderable_lazy_list_v3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R
import java.util.UUID

@Preview
@Composable
fun ExampleUsage() {
    val myItems = remember {
        mutableStateListOf("","","Koroğlu m/st","")
    }
    val reorderableItems = remember {
        mutableStateListOf(
            *myItems.mapIndexed { index, it ->
                ReorderableItem(
                    data = it,
                    lazyListIndex = index,
                    cannotGoBelowIndex = 0,
                    cannotGoAboveIndex = myItems.lastIndex + 3
                )
            }.toTypedArray()
        )
    }
    val items = rememberReorderableLazyListItems(items = myItems)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 12.dp, top = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                reorderableItems.add(
                    ReorderableItem("",reorderableItems.lastIndex + 1,0,reorderableItems.lastIndex)
                )
                myItems.add("${UUID.randomUUID().toString().take(10)}")
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "close"
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Your route",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        ReorderableLazyList(
            items = reorderableItems
        ) { index, it, _->
            Box (
                modifier = Modifier.padding(
                    vertical = 1.dp
                )
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(0.9F)
                        .background(color = Color.Gray.copy(0.1F))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(20.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Gray.copy(0.7F),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(24.dp))

                    if (it.isEmpty()) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Add your location",
                            fontSize = 16.sp,
                            color = Color.Gray.copy(0.7F)
                        )
                    }
                    else {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = it,
                            fontSize = 16.sp
                        )
                    }
                    Image(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.ic_draggable),
                        contentDescription = "drag",
                        colorFilter = ColorFilter.tint(
                            color = Color.Gray.copy(0.8F)
                        )
                    )
                }
            }
        }
    }
}