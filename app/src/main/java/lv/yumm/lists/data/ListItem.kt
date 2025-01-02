package lv.yumm.lists.data

import lv.yumm.recipes.data.Ingredient

data class ListItem (
    val checked: Boolean = false,
    val ingredient: Ingredient = Ingredient()
)