package com.longle.ui.facemask

import androidx.lifecycle.ViewModel
import com.longle.data.repository.MainRepository
import com.longle.di.FragmentScope
import javax.inject.Inject

/**
 * The ViewModel used in [FacemaskFragment].
 */
@FragmentScope
class FacemaskViewModel @Inject constructor(
    repository: MainRepository
) : ViewModel() {
    // nothing
}
