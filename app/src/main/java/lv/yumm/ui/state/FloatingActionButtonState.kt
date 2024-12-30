package lv.yumm.ui.state

data class FloatingActionButtonState(
    val shouldShow: Boolean = false,
    val onClick: () -> Unit = {}
)
