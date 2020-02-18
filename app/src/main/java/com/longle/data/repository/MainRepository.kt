package com.longle.data.repository

import androidx.lifecycle.LiveData
import com.longle.data.Result
import com.longle.data.model.Timeline
import com.longle.data.model.User

interface MainRepository {

    fun getUser(): LiveData<Result<User>>

    fun getTimeLines(): LiveData<List<Timeline>>
}
