package com.example.ventilrechneropel.serialize

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.ventilrechneropel.MyApp

@Entity(primaryKeys = ["user_name","motor_name"], tableName = "motor_set")
data class MotorSet (
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "motor_name") val motorName: String,
    @ColumnInfo (name = "jsonized_motor") val jsonizedMotor: String
)

@Dao
interface MotorSetDao {
    @Query("SELECT * FROM motor_set")
    suspend fun loadAll(): List<MotorSet>

    @Query("SELECT * FROM motor_set WHERE user_name  = :userName AND " +
            "motor_name = :motorName")
    suspend fun loadByName(userName: String, motorName: String) : MotorSet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg motorSets: MotorSet)

    @Delete
    suspend fun delete(motorSet: MotorSet)

    @Update
    suspend fun update(motorSet: MotorSet)
}

@Database(entities = [MotorSet::class], version = 1)
abstract class LocalDatabase : RoomDatabase () {
    abstract fun motorDao(): MotorSetDao
}

sealed class AppDatabase {
    companion object {
        private var db : LocalDatabase? = null
        fun getInstance() : LocalDatabase {
            if (db == null) {
                db = Room.databaseBuilder(
                    MyApp.getContext(),
                    LocalDatabase::class.java, "application-database"
                ).build()
            }
            return db as LocalDatabase
        }
    }
}

