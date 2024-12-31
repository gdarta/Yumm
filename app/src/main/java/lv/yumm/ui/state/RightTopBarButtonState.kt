package lv.yumm.ui.state

import lv.yumm.R

data class RightTopBarButtonState(
    val shouldShow: Boolean = false,
    val resId: Int = R.drawable.ic_add_list,
    val onClick: () -> Unit = {}
)
