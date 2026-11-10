package com.fixtech.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fixtech.data.local.FixTechDatabase
import com.fixtech.data.local.entity.DeviceEntity
import com.fixtech.data.local.entity.GuideEntity
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
class GuideDaoTest {

    private lateinit var database: FixTechDatabase
    private lateinit var guideDao: GuideDao
    private lateinit var deviceDao: DeviceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FixTechDatabase::class.java
        ).allowMainThreadQueries().build()
        guideDao = database.guideDao()
        deviceDao = database.deviceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndRetrieveGuidesByDevice() = runBlocking {
        val deviceId = "dev_test_01"
        val dummyDevice = DeviceEntity(
            id = deviceId,
            name = "Lenovo ThinkPad",
            model = "T14",
            type = "Laptop",
            technicianId = "tech_test_01",
            technicianName = "Carlos M.",
            imageUrl = "",
            ram = "16 GB",
            storage = "512 GB SSD",
            processor = "AMD Ryzen 5"
        )
        deviceDao.insertDevice(dummyDevice)

        val dummyGuide = GuideEntity(
            id = "guide_test_01",
            deviceId = deviceId,
            title = "Reemplazo de Teclado",
            difficulty = "Medio",
            estimatedTime = "20 min",
            toolsList = "Destornillador PH0||Púa",
            precautions = "Desconectar la batería antes de manipular.",
            technicianId = "tech_test_01",
            technicianName = "Carlos M.",
            category = "Laptop",
            modelSpec = "ThinkPad T14 Gen 2",
            imageUrl = "https://example.com/keyboard.jpg"
        )

        guideDao.insertGuide(dummyGuide)

        val guidesForDevice = guideDao.observeGuidesForDevice(deviceId = deviceId, currentUserId = "tech_test_01").first()
        val retrieved = guidesForDevice.find { it.guide.id == "guide_test_01" }

        assertNotNull(retrieved)
        assertEquals("Reemplazo de Teclado", retrieved?.guide?.title)
        assertEquals(deviceId, retrieved?.guide?.deviceId)
        assertEquals("Medio", retrieved?.guide?.difficulty)
        assertEquals("20 min", retrieved?.guide?.estimatedTime)
        assertEquals("tech_test_01", retrieved?.guide?.technicianId)
        assertEquals("Laptop", retrieved?.guide?.category)
        assertEquals("ThinkPad T14 Gen 2", retrieved?.guide?.modelSpec)
    }

    @Test
    fun deleteGuide_removesRecord() = runBlocking {
        val deviceId = "dev_test_02"
        val dummyGuide = GuideEntity(
            id = "guide_test_02",
            deviceId = deviceId,
            title = "Limpieza de Ventilador",
            difficulty = "Bajo",
            estimatedTime = "15 min",
            toolsList = "Aire comprimido",
            precautions = "Usar protección ocular.",
            technicianId = "tech_test_01",
            technicianName = "Carlos M.",
            category = "Laptop",
            modelSpec = "Generic",
            imageUrl = ""
        )

        guideDao.insertGuide(dummyGuide)
        val allIdsBefore = guideDao.getAllGuideIds()
        assertTrue(allIdsBefore.contains("guide_test_02"))

        guideDao.deleteGuideCascade("guide_test_02")

        val allIdsAfter = guideDao.getAllGuideIds()
        assertTrue(!allIdsAfter.contains("guide_test_02"))

        val guidesForDevice = guideDao.observeGuidesForDevice(deviceId = deviceId, currentUserId = "tech_test_01").first()
        val retrieved = guidesForDevice.find { it.guide.id == "guide_test_02" }
        assertEquals(null, retrieved)
        assertTrue(guidesForDevice.isEmpty())
    }
}
