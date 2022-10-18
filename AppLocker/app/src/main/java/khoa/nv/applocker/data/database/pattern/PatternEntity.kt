package khoa.nv.applocker.data.database.pattern

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pattern")
data class PatternEntity(
    @PrimaryKey
    val pattern: String
)