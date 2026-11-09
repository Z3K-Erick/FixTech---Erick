package com.fixtech.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fixtech.data.local.FixTechDatabase
import com.fixtech.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DeviceDaoTest {

    private lateinit var database: FixTechDatabase
    private lateinit var deviceDao: DeviceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FixTechDatabase::class.java
        ).allowMainThreadQueries().build()
        deviceDao = database.deviceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndRetrieveDevice() = runBlocking {
        val dummyDevice = DeviceEntity(
            id = "test_dev_01",
            name = "Dell XPS 15",
            model = "9520",
            type = "Laptop",
            technicianId = "tech_01",
            technicianName = "Carlos M.",
            imageUrl = "https://example.com/dell.jpg",
            ram = "32 GB",
            storage = "1 TB SSD",
            processor = "Intel i7"
        )

        deviceDao.insertDevice(dummyDevice)

        val allIds = deviceDao.getAllDeviceIds()
        assertTrue(allIds.contains("test_dev_01"))

        val devices = deviceDao.observeExploreDevices("tech_01").first()
        val retrieved = devices.find { it.device.id == "test_dev_01" }
        assertNotNull(retrieved)
        assertEquals("Dell XPS 15", retrieved?.device?.name)
        assertEquals("9520", retrieved?.device?.model)
    }
}
