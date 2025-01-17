package lv.yumm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID
import timber.log.Timber


open class BaseViewModel: ViewModel() {
    private val _currentUserId = MutableStateFlow(EMPTY_USER_ID)
    val currentUserId = _currentUserId.asStateFlow()

    private val _message = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = _message

    // Post in background thread
    fun postMessage(message: String) {
        _message.value = (Event(message))
    }

    fun postUserId(uid: String) {
        _currentUserId.value = uid
    }
}

/*
* Used as a wrapper for data that is exposed via a LiveData that represents an event.
*/
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}