package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CreateRecipeScreen() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Scaffold { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ){
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = { Text(text = "Recipe Title") },
                    singleLine = true
                )
                TextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = { Text(text = "Recipe Description") },
                    minLines = 5
                )
                LazyColumn {
                    items(5) {
                        IngredientCard()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientCard() {
    var amnt by remember { mutableStateOf("") }
    var amntExpanded by remember { mutableStateOf(false) }
    val amntOptions = remember { listOf(1, 2, 3, 4, 5, 6, 7, 8) }
    var name by remember {mutableStateOf("")}
    var msr by remember { mutableStateOf("")}
    Column (
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = { Text(text = "Ingredient")},
            modifier = Modifier
        )
        Row {
            ExposedDropdownMenuBox(
                expanded = amntExpanded,
                onExpandedChange = { amntExpanded = !amntExpanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = amnt,
                    onValueChange = {
                        amnt = it
                    },
                    label = { Text(text = "Amount") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = amntExpanded,
                    onDismissRequest = { amntExpanded = false },
                ) {
                    amntOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option.toString()) },
                            onClick = {
                                amntExpanded = false
                            }
                        )
                    }
                }
            }
            TextField(
                value = msr,
                onValueChange = {
                    msr = it
                },
                label = { Text(text = "Measurement") },
                modifier = Modifier.weight(1f)
            )

        }
    }
}

@Preview
@Composable
fun CreateRecipePreview() {
    CreateRecipeScreen()
}