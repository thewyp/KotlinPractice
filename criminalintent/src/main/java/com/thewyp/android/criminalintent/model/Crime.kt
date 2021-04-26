package com.thewyp.android.criminalintent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSoled: Boolean = false,
    var suspect: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Crime

        if (id != other.id) return false
        if (title != other.title) return false
        if (date != other.date) return false
        if (isSoled != other.isSoled) return false
        if (suspect != other.suspect) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + isSoled.hashCode()
        result = 31 * result + suspect.hashCode()
        return result
    }
}
