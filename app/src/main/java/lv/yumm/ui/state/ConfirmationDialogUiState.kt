package lv.yumm.ui.state

data class ConfirmationDialogUiState (
    val title: String,
    val description: String = "",
    val cancelButtonText: String = "Cancel",
    val onCancelButtonClick: () -> Unit = {},
    val confirmButtonText: String = "Confirm",
    val onConfirmButtonClick: () -> Unit = {},
    val onDismissDialog: () -> Unit = {}
)