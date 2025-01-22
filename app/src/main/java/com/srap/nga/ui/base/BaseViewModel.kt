package com.srap.nga.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.srap.nga.logic.repository.NetworkRepo

abstract class BaseViewModel(
    open val networkRepo: NetworkRepo,
) : ViewModel() {
    /**
     * 加载完成
     */
    var isLoaded by mutableStateOf(false)
}