package com.srap.nga.logic.session

import com.srap.nga.utils.StorageUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManager @Inject constructor() {
    private val _isLoggedIn = MutableStateFlow(StorageUtils.Token.isNotBlank())
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _userId = MutableStateFlow(StorageUtils.Uid)
    val userId = _userId.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<SessionNavigationEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun markLoggedIn(uid: Int) {
        _userId.value = uid
        _isLoggedIn.value = true
        _navigationEvents.tryEmit(SessionNavigationEvent.Home)
    }

    fun markLoggedOut() {
        if (!_isLoggedIn.value) return
        StorageUtils.Token = ""
        StorageUtils.Uid = 0
        _userId.value = 0
        _isLoggedIn.value = false
        _navigationEvents.tryEmit(SessionNavigationEvent.Login)
    }
}

enum class SessionNavigationEvent {
    Home,
    Login,
}
