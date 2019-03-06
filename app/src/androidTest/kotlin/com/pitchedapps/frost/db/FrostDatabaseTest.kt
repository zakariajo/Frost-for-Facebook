package com.pitchedapps.frost.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class FrostDatabaseTest {

    private lateinit var db: FrostDatabase

    @BeforeTest
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val privateDb = Room.inMemoryDatabaseBuilder(
            context, FrostPrivateDatabase::class.java
        ).build()
        val publicDb = Room.inMemoryDatabaseBuilder(
            context, FrostPublicDatabase::class.java
        ).build()
        db = FrostDatabase(privateDb, publicDb)
    }

    @AfterTest
    fun after() {
        db.close()
    }

    @Test
    fun basicCookie() {
        val cookie = CookieEntity(id = 1234L, name = "testName", cookie = "testCookie")
        runBlocking {
            db.cookieDao().insertCookie(cookie)
            val cookies = db.cookieDao().selectAll()
            assertEquals(listOf(cookie), cookies, "Cookie mismatch")
        }
    }

    @Test
    fun deleteCookie() {
        val cookie = CookieEntity(id = 1234L, name = "testName", cookie = "testCookie")

        runBlocking {
            db.cookieDao().insertCookie(cookie)
            db.cookieDao().deleteById(cookie.id + 1)
            assertEquals(
                listOf(cookie),
                db.cookieDao().selectAll(),
                "Cookie list should be the same after inexistent deletion"
            )
            db.cookieDao().deleteById(cookie.id)
            assertEquals(emptyList(), db.cookieDao().selectAll(), "Cookie list should be empty after deletion")
        }
    }

    @Test
    fun insertCookie() {
        val cookie = CookieEntity(id = 1234L, name = "testName", cookie = "testCookie")
        runBlocking {
            db.cookieDao().insertCookie(cookie)
            assertEquals(listOf(cookie), db.cookieDao().selectAll(), "Cookie insertion failed")
            db.cookieDao().insertCookie(cookie.copy(name = "testName2"))
            assertEquals(
                listOf(cookie.copy(name = "testName2")),
                db.cookieDao().selectAll(),
                "Cookie replacement failed"
            )
            db.cookieDao().insertCookie(cookie.copy(id = 123L))
            assertEquals(
                setOf(cookie.copy(id = 123L), cookie.copy(name = "testName2")),
                db.cookieDao().selectAll().toSet(),
                "New cookie insertion failed"
            )
        }
    }
}