package com.fixtech.data.repository

import com.fixtech.core.common.Constants
import com.fixtech.data.local.dao.GuideDao
import com.fixtech.data.local.entity.GuideEntity
import com.fixtech.data.local.entity.GuideStepEntity
import com.fixtech.data.local.entity.UserFavoriteGuideEntity
import com.fixtech.data.local.entity.toDomain
import com.fixtech.data.local.entity.toEntity
import com.fixtech.domain.model.Guide
import com.fixtech.domain.repository.GuideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GuideRepositoryImpl(
    private val guideDao: GuideDao
) : GuideRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            ensureSeedData()
        }
    }

    override fun observeAllGuides(currentUserId: String): Flow<List<Guide>> {
        return combine(
            guideDao.observeAllGuides(currentUserId),
            guideDao.observeAllSteps()
        ) { guideRows, allSteps ->
            val stepsByGuide = allSteps.groupBy { it.guideId }
            guideRows.map { row ->
                val steps = stepsByGuide[row.guide.id].orEmpty().map { it.toDomain() }
                row.toDomain(steps)
            }
        }
    }

    override fun observeGuidesForDevice(deviceId: String, currentUserId: String): Flow<List<Guide>> {
        return combine(
            guideDao.observeGuidesForDevice(deviceId, currentUserId),
            guideDao.observeAllSteps()
        ) { guideRows, allSteps ->
            val stepsByGuide = allSteps.groupBy { it.guideId }
            guideRows.map { row ->
                val steps = stepsByGuide[row.guide.id].orEmpty().map { it.toDomain() }
                row.toDomain(steps)
            }
        }
    }

    override fun observeFavoriteGuidesCount(userId: String): Flow<Int> {
        return guideDao.observeFavoriteGuidesCount(userId)
    }

    override fun observeTechGuidesCount(technicianId: String): Flow<Int> {
        return guideDao.observeTechGuidesCount(technicianId)
    }

    override suspend fun toggleFavoriteGuide(userId: String, deviceId: String, guideId: String) {
        val isFav = guideDao.isGuideFavorite(userId, deviceId, guideId)
        if (isFav) {
            guideDao.deleteFavoriteGuide(userId, deviceId, guideId)
        } else {
            guideDao.insertFavoriteGuide(
                UserFavoriteGuideEntity(
                    userId = userId,
                    deviceId = deviceId,
                    guideId = guideId,
                    pinnedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun insertGuideWithSteps(guide: Guide) {
        val stepEntities = guide.steps.mapIndexed { idx, step ->
            step.copy(stepNumber = idx + 1).toEntity(guide.id)
        }
        guideDao.upsertGuideWithSteps(guide.toEntity(), stepEntities)
    }

    override suspend fun deleteGuideCascade(guideId: String) {
        guideDao.deleteGuideCascade(guideId)
    }

    override suspend fun ensureSeedData() {
        if (guideDao.getGuidesCount() == 0) {
            val sep = Constants.TOOLS_SEPARATOR
            val seedGuides = listOf(
                GuideEntity(
                    id = "guide_ram",
                    deviceId = "dev_1",
                    title = "Mejorar RAM o SSD",
                    category = "Laptop",
                    difficulty = "Medio",
                    estimatedTime = "25 min",
                    toolsList = listOf("Destornillador Phillips #0", "Pulsera antiestática", "Púa de apertura").joinToString(sep),
                    precautions = "Actualiza la memoria RAM o unidad SSD NVMe de tu portátil de forma segura protegiendo los componentes contra descargas electrostáticas.",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    modelSpec = "Lenovo LOQ 15IRH8"
                ),
                GuideEntity(
                    id = "guide_battery",
                    deviceId = "dev_3",
                    title = "Cambiar Batería",
                    category = "Laptop",
                    difficulty = "Bajo",
                    estimatedTime = "20 min",
                    toolsList = listOf("Destornillador Phillips #0", "Púa de apertura").joinToString(sep),
                    precautions = "Reemplazo paso a paso de batería interna desgastada para restaurar la autonomía de tu laptop.",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    modelSpec = "Lenovo LOQ 15IRH8"
                ),
                GuideEntity(
                    id = "guide_cleaning",
                    deviceId = "dev_2",
                    title = "Limpieza Profunda",
                    category = "PC",
                    difficulty = "Alto",
                    estimatedTime = "45 min",
                    toolsList = listOf("Destornillador Phillips #0", "Pasta térmica", "Alcohol isopropílico", "Compresor de aire").joinToString(sep),
                    precautions = "Eliminación de polvo acumulado en ventiladores y cambio de pasta térmica en el procesador.",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    modelSpec = "Corsair One i300"
                ),
                GuideEntity(
                    id = "guide_gpu",
                    deviceId = "dev_4",
                    title = "Revisión de GPU y Ventiladores",
                    category = "PC",
                    difficulty = "Medio",
                    estimatedTime = "30 min",
                    toolsList = listOf("Destornillador Phillips #0", "Compresor de aire", "Pulsera antiestática").joinToString(sep),
                    precautions = "Inspección de tarjeta gráfica, limpieza de ventiladores con aire comprimido y verificación del slot PCIe.",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    modelSpec = "Corsair One i300"
                )
            )

            val seedSteps = listOf(
                // guide_ram
                GuideStepEntity(
                    id = "guide_ram_step_1",
                    guideId = "guide_ram",
                    stepNumber = 1,
                    title = "Apaga el equipo y desconecta la batería",
                    description = "Asegúrate de apagar el equipo completamente. Luego abre la tapa trasera y jala suavemente el cable de la batería para desconectarlo.",
                    hasAlert = true,
                    alertType = "Peligro",
                    alertTitle = "Peligro",
                    alertMessage = "No toques ninguna pieza sin antes desconectar la batería. Podrías dañar el equipo o lastimarte.",
                    requiresConfirmation = true,
                    confirmationText = "Batería desconectada"
                ),
                GuideStepEntity(
                    id = "guide_ram_step_2",
                    guideId = "guide_ram",
                    stepNumber = 2,
                    title = "Ubica las ranuras de memoria",
                    description = "Busca los módulos de RAM dentro del equipo. Son piezas rectangulares con seguros a los lados. Empuja las pestañas hacia afuera para soltarlos.",
                    hasAlert = false,
                    alertType = "Advertencia",
                    alertTitle = "Advertencia",
                    alertMessage = "",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                GuideStepEntity(
                    id = "guide_ram_step_3",
                    guideId = "guide_ram",
                    stepNumber = 3,
                    title = "Instala la nueva memoria o SSD",
                    description = "Inserta el módulo nuevo inclinado 45° y luego presiónalo hasta escuchar un clic. Asegúrate de que quede bien fijo antes de cerrar el equipo.",
                    hasAlert = true,
                    alertType = "Advertencia",
                    alertTitle = "Advertencia",
                    alertMessage = "Manipula la memoria tomándola por los bordes. No toques los contactos dorados con los dedos.",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                // guide_battery
                GuideStepEntity(
                    id = "guide_battery_step_1",
                    guideId = "guide_battery",
                    stepNumber = 1,
                    title = "Retira los tornillos de la tapa",
                    description = "Voltea el equipo y quita los tornillos de la tapa inferior con el destornillador. Guárdalos en un lugar seguro para no perderlos.",
                    hasAlert = true,
                    alertType = "Peligro",
                    alertTitle = "Peligro",
                    alertMessage = "Verifica que el equipo esté totalmente apagado y sin el cargador conectado antes de abrir la tapa.",
                    requiresConfirmation = true,
                    confirmationText = "Equipo apagado y desconectado"
                ),
                GuideStepEntity(
                    id = "guide_battery_step_2",
                    guideId = "guide_battery",
                    stepNumber = 2,
                    title = "Abre la tapa con la púa",
                    description = "Desliza la púa por el borde del equipo para separar la tapa. Ve despacio para no quebrar los ganchos de plástico.",
                    hasAlert = true,
                    alertType = "Información",
                    alertTitle = "Información",
                    alertMessage = "Si sientes mucha resistencia en algún punto, revisa que no falte quitar algún tornillo antes de forzar.",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                GuideStepEntity(
                    id = "guide_battery_step_3",
                    guideId = "guide_battery",
                    stepNumber = 3,
                    title = "Cambia la batería",
                    description = "Desconecta el cable de la batería vieja y sácala. Coloca la batería nueva, conecta el cable y vuelve a cerrar la tapa.",
                    hasAlert = false,
                    alertType = "Advertencia",
                    alertTitle = "Advertencia",
                    alertMessage = "",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                // guide_cleaning
                GuideStepEntity(
                    id = "guide_cleaning_step_1",
                    guideId = "guide_cleaning",
                    stepNumber = 1,
                    title = "Abre el equipo y desconecta la batería",
                    description = "Quita los tornillos, abre la tapa y desconecta la energía antes de tocar cualquier pieza interna.",
                    hasAlert = true,
                    alertType = "Peligro",
                    alertTitle = "Peligro",
                    alertMessage = "No uses el compresor cerca de los ojos y mantén el equipo sobre una superficie firme.",
                    requiresConfirmation = true,
                    confirmationText = "Batería desconectada"
                ),
                GuideStepEntity(
                    id = "guide_cleaning_step_2",
                    guideId = "guide_cleaning",
                    stepNumber = 2,
                    title = "Sopla el polvo del ventilador",
                    description = "Con el compresor de aire, sopla el ventilador y los conductos de ventilación en ráfagas cortas. Aleja el polvo del equipo.",
                    hasAlert = false,
                    alertType = "Advertencia",
                    alertTitle = "Advertencia",
                    alertMessage = "",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                GuideStepEntity(
                    id = "guide_cleaning_step_3",
                    guideId = "guide_cleaning",
                    stepNumber = 3,
                    title = "Cambia la pasta térmica",
                    description = "Con un hisopo con alcohol, limpia la pasta vieja del procesador. Aplica una pequeña gota de pasta nueva en el centro y vuelve a colocar el disipador.",
                    hasAlert = true,
                    alertType = "Advertencia",
                    alertTitle = "Advertencia",
                    alertMessage = "No apliques demasiada pasta. Una gota del tamaño de un grano de arroz es suficiente.",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                // guide_gpu
                GuideStepEntity(
                    id = "guide_gpu_step_1",
                    guideId = "guide_gpu",
                    stepNumber = 1,
                    title = "Retira la tapa lateral del gabinete",
                    description = "Desatornilla los dos tornillos de la tapa lateral y deslízala hacia atrás para abrirla. Coloca el gabinete sobre una superficie estable.",
                    hasAlert = true,
                    alertType = "Advertencia",
                    alertTitle = "Advertencia",
                    alertMessage = "Antes de abrir el gabinete, asegúrate de haber desconectado el cable de corriente completamente.",
                    requiresConfirmation = true,
                    confirmationText = "Cable desconectado"
                ),
                GuideStepEntity(
                    id = "guide_gpu_step_2",
                    guideId = "guide_gpu",
                    stepNumber = 2,
                    title = "Limpia los ventiladores con aire comprimido",
                    description = "Sostén las aspas del ventilador con un dedo para que no giren. Sopla en ráfagas cortas para expulsar el polvo acumulado sin dañar los rodamientos.",
                    hasAlert = false,
                    alertType = "Información",
                    alertTitle = "Información",
                    alertMessage = "",
                    requiresConfirmation = false,
                    confirmationText = ""
                ),
                GuideStepEntity(
                    id = "guide_gpu_step_3",
                    guideId = "guide_gpu",
                    stepNumber = 3,
                    title = "Verifica que la GPU esté bien encajada",
                    description = "Empuja suavemente la tarjeta gráfica hacia abajo hasta escuchar el clic del seguro del slot PCIe. Reconecta los cables de alimentación de la GPU.",
                    hasAlert = true,
                    alertType = "Información",
                    alertTitle = "Información",
                    alertMessage = "Si la GPU no hace clic al encajar, no la fuerces. Revisa que el slot esté libre de obstrucciones.",
                    requiresConfirmation = false,
                    confirmationText = ""
                )
            )

            guideDao.insertGuides(seedGuides)
            guideDao.insertSteps(seedSteps)
        }
    }
}
