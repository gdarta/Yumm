package lv.yumm.lists.data

import lv.yumm.recipes.data.Ingredient

data class UserList(
    val list: List<Ingredient> = emptyList()
)

