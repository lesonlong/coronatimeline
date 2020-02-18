package com.longle.ui.main

import androidx.lifecycle.ViewModel
import com.longle.data.repository.MainRepository
import com.longle.di.ActivityScope
import javax.inject.Inject

/**
 * The ViewModel used in [MainActivity].
 */
@ActivityScope
class MainViewModel @Inject constructor(
    repository: MainRepository
) : ViewModel() {

    val user = repository.getUser()
}
