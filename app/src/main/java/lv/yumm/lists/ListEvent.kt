package lv.yumm.lists

import lv.yumm.recipes.data.Ingredient

sealed class ListEvent {

    class CreateNewList(): ListEvent()
    class DeleteList(val id: String): ListEvent()
    class OpenList(val id: String): ListEvent()

    class UpdateTitle(val title: String): ListEvent()

    class AddItem(): ListEvent()
    class UpdateItem(val index: Int, val item: Ingredient): ListEvent()
    class DeleteItem(val index: Int): ListEvent()

    class CheckItem(val index: Int, val checked: Boolean): ListEvent()

    class ValidateAndSave(): ListEvent()
}