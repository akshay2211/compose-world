package compose.world

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Task() {
    var selectedIndices by remember { mutableStateOf(setOf<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column (
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            for(i in 0..100) {
                var isChecked by rememberSaveable { mutableStateOf(false) }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = !isChecked
                    }
                )
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun TaskPrev() {
    Task()
}