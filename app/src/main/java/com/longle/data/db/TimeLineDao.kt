package com.longle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.longle.data.model.Timeline

/**
 * The Data Access Object for the User class.
 */
@Dao
interface TimeLineDao {

    @Query("SELECT * FROM Timeline ORDER BY stayFrom DESC LIMIT 1")
    fun getTimeLine(): Timeline?

    @Query("SELECT * FROM Timeline ORDER BY stayFrom DESC")
    fun getTimeLines(): LiveData<List<Timeline>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timeLine: Timeline)

    @Query("DELETE FROM Timeline WHERE stayTo - stayFrom <= 180000") // 3 minutes
    fun cleanUp()
}
