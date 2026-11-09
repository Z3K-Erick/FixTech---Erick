package com.fixtech.backend

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UserDto(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "USER",
    val workshopCode: String = ""
)

data class LoginRequestDto(
    val email: String = "",
    val password: String = "",
    val role: String = "USER"
)

data class SendCodeRequestDto(
    val email: String = ""
)

data class RegisterRequestDto(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "USER",
    val verificationCode: String = ""
)

data class AuthResponseDto(
    val success: Boolean,
    val message: String,
    val user: UserDto? = null
)

data class ApiMessageResponseDto(
    val success: Boolean,
    val message: String,
    val demoCode: String = "123456"
)

data class DeviceDto(
    val id: String = "",
    val name: String = "",
    val model: String = "",
    val type: String = "Laptop",
    val technicianId: String = "admin-current",
    val technicianName: String = "Carlos M.",
    val imageUrl: String = "",
    val ram: String = "16 GB",
    val storage: String = "512 GB SSD",
    val processor: String = "Intel Core"
)

data class GuideStepDto(
    val stepNumber: Int = 1,
    val title: String = "",
    val description: String = "",
    val hasAlert: Boolean = false,
    val alertType: String = "Advertencia",
    val alertMessage: String = "",
    val requiresConfirmation: Boolean = false,
    val confirmationText: String = "",
    val imageUrl: String = "",
    val resourceUrl: String? = null
)

data class GuideDto(
    val id: String = "",
    val deviceId: String = "",
    val title: String = "",
    val category: String = "Laptop",
    val difficulty: String = "Medio",
    val estimatedTime: String = "20 min",
    val toolsList: List<String> = emptyList(),
    val precautions: String = "",
    val technicianId: String = "admin-current",
    val technicianName: String = "Carlos M.",
    val modelSpec: String = "",
    val imageUrl: String = "",
    val steps: List<GuideStepDto> = emptyList()
)

private data class StoredAccount(
    val user: UserDto,
    val password: String
)

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"])
class FixTechRestController {

    private val usersByKey = ConcurrentHashMap<String, StoredAccount>()
    private val verificationCodesByEmail = ConcurrentHashMap<String, String>()
    private val devices = CopyOnWriteArrayList<DeviceDto>()
    private val guides = CopyOnWriteArrayList<GuideDto>()

    init {
        seedUsers()
        seedDevices()
        seedGuides()
    }

    private fun accountKey(email: String, role: String): String {
        return "${role.trim().uppercase()}:${email.trim().lowercase()}"
    }

    private fun seedUsers() {
        val seedAccounts = listOf(
            StoredAccount(
                user = UserDto(
                    id = "admin-current",
                    fullName = "Carlos M.",
                    email = "carlos@fixtech.com",
                    role = "TECH",
                    workshopCode = "123456"
                ),
                password = "123456"
            ),
            StoredAccount(
                user = UserDto(
                    id = "admin-other",
                    fullName = "Ana R.",
                    email = "ana@fixtech.com",
                    role = "TECH",
                    workshopCode = "123456"
                ),
                password = "123456"
            ),
            StoredAccount(
                user = UserDto(
                    id = "user_usuario@fixtech.com",
                    fullName = "Usuario Demo",
                    email = "usuario@fixtech.com",
                    role = "USER",
                    workshopCode = ""
                ),
                password = "123456"
            )
        )
        seedAccounts.forEach { account ->
            usersByKey[accountKey(account.user.email, account.user.role)] = account
        }
    }

    private fun seedDevices() {
        devices.addAll(
            listOf(
                DeviceDto(
                    id = "dev_1",
                    name = "Lenovo LOQ",
                    model = "15IRH8",
                    type = "Laptop",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    imageUrl = "",
                    ram = "16 GB",
                    storage = "512 GB SSD",
                    processor = "Intel Core i5"
                ),
                DeviceDto(
                    id = "dev_2",
                    name = "Corsair One",
                    model = "i300",
                    type = "PC",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    imageUrl = "",
                    ram = "32 GB",
                    storage = "1 TB SSD",
                    processor = "Intel Core i9"
                ),
                DeviceDto(
                    id = "dev_3",
                    name = "Lenovo LOQ",
                    model = "15IRH8",
                    type = "Laptop",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    imageUrl = "",
                    ram = "16 GB",
                    storage = "512 GB SSD",
                    processor = "AMD Ryzen 7"
                ),
                DeviceDto(
                    id = "dev_4",
                    name = "Corsair One",
                    model = "i300",
                    type = "PC",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    imageUrl = "",
                    ram = "32 GB",
                    storage = "1 TB SSD",
                    processor = "Intel Core i7"
                )
            )
        )
    }

    private fun seedGuides() {
        guides.addAll(
            listOf(
                GuideDto(
                    id = "guide_ram",
                    deviceId = "dev_1",
                    title = "Mejorar RAM o SSD",
                    category = "Laptop",
                    difficulty = "Medio",
                    estimatedTime = "25 min",
                    toolsList = listOf("Destornillador Phillips #0", "Pulsera antiestática", "Púa de apertura"),
                    precautions = "Actualiza la memoria RAM o unidad SSD NVMe de tu portátil de forma segura protegiendo los componentes contra descargas electrostáticas.",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    modelSpec = "Lenovo LOQ 15IRH8",
                    steps = listOf(
                        GuideStepDto(
                            stepNumber = 1,
                            title = "Apaga el equipo y desconecta la batería",
                            description = "Asegúrate de apagar el equipo completamente. Luego abre la tapa trasera y jala suavemente el cable de la batería para desconectarlo.",
                            hasAlert = true,
                            alertType = "Peligro",
                            alertMessage = "No toques ninguna pieza sin antes desconectar la batería. Podrías dañar el equipo o lastimarte.",
                            requiresConfirmation = true,
                            confirmationText = "Batería desconectada"
                        ),
                        GuideStepDto(
                            stepNumber = 2,
                            title = "Ubica las ranuras de memoria",
                            description = "Busca los módulos de RAM dentro del equipo. Son piezas rectangulares con seguros a los lados. Empuja las pestañas hacia afuera para soltarlos.",
                            hasAlert = false,
                            alertType = "Advertencia",
                            alertMessage = "",
                            requiresConfirmation = false,
                            confirmationText = ""
                        ),
                        GuideStepDto(
                            stepNumber = 3,
                            title = "Instala la nueva memoria o SSD",
                            description = "Inserta el módulo nuevo inclinado 45° y luego presiónalo hasta escuchar un clic. Asegúrate de que quede bien fijo antes de cerrar el equipo.",
                            hasAlert = true,
                            alertType = "Advertencia",
                            alertMessage = "Manipula la memoria tomándola por los bordes. No toques los contactos dorados con los dedos.",
                            requiresConfirmation = false,
                            confirmationText = ""
                        )
                    )
                ),
                GuideDto(
                    id = "guide_battery",
                    deviceId = "dev_3",
                    title = "Cambiar Batería",
                    category = "Laptop",
                    difficulty = "Bajo",
                    estimatedTime = "20 min",
                    toolsList = listOf("Destornillador Phillips #0", "Púa de apertura"),
                    precautions = "Reemplazo paso a paso de batería interna desgastada para restaurar la autonomía de tu laptop.",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    modelSpec = "Lenovo LOQ 15IRH8",
                    steps = listOf(
                        GuideStepDto(
                            stepNumber = 1,
                            title = "Retira los tornillos de la tapa",
                            description = "Voltea el equipo y quita los tornillos de la tapa inferior con el destornillador. Guárdalos en un lugar seguro para no perderlos.",
                            hasAlert = true,
                            alertType = "Peligro",
                            alertMessage = "Verifica que el equipo esté totalmente apagado y sin el cargador conectado antes de abrir la tapa.",
                            requiresConfirmation = true,
                            confirmationText = "Equipo apagado y desconectado"
                        ),
                        GuideStepDto(
                            stepNumber = 2,
                            title = "Abre la tapa con la púa",
                            description = "Desliza la púa por el borde del equipo para separar la tapa. Ve despacio para no quebrar los ganchos de plástico.",
                            hasAlert = true,
                            alertType = "Información",
                            alertMessage = "Si sientes mucha resistencia en algún punto, revisa que no falte quitar algún tornillo antes de forzar.",
                            requiresConfirmation = false,
                            confirmationText = ""
                        ),
                        GuideStepDto(
                            stepNumber = 3,
                            title = "Cambia la batería",
                            description = "Desconecta el cable de la batería vieja y sácala. Coloca la batería nueva, conecta el cable y vuelve a cerrar la tapa.",
                            hasAlert = false,
                            alertType = "Advertencia",
                            alertMessage = "",
                            requiresConfirmation = false,
                            confirmationText = ""
                        )
                    )
                ),
                GuideDto(
                    id = "guide_cleaning",
                    deviceId = "dev_2",
                    title = "Limpieza Profunda",
                    category = "PC",
                    difficulty = "Alto",
                    estimatedTime = "45 min",
                    toolsList = listOf("Destornillador Phillips #0", "Pasta térmica", "Alcohol isopropílico", "Compresor de aire"),
                    precautions = "Eliminación de polvo acumulado en ventiladores y cambio de pasta térmica en el procesador.",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    modelSpec = "Corsair One i300",
                    steps = listOf(
                        GuideStepDto(
                            stepNumber = 1,
                            title = "Abre el equipo y desconecta la energía",
                            description = "Quita los tornillos, abre la tapa y desconecta la energía antes de tocar cualquier pieza interna.",
                            hasAlert = true,
                            alertType = "Peligro",
                            alertMessage = "No uses el compresor cerca de los ojos y mantén el equipo sobre una superficie firme.",
                            requiresConfirmation = true,
                            confirmationText = "Energía desconectada"
                        ),
                        GuideStepDto(
                            stepNumber = 2,
                            title = "Sopla el polvo del ventilador",
                            description = "Con el compresor de aire, sopla el ventilador y los conductos de ventilación en ráfagas cortas. Aleja el polvo del equipo.",
                            hasAlert = false,
                            alertType = "Advertencia",
                            alertMessage = "",
                            requiresConfirmation = false,
                            confirmationText = ""
                        ),
                        GuideStepDto(
                            stepNumber = 3,
                            title = "Cambia la pasta térmica",
                            description = "Con un hisopo con alcohol, limpia la pasta vieja del procesador. Aplica una pequeña gota de pasta nueva en el centro y vuelve a colocar el disipador.",
                            hasAlert = true,
                            alertType = "Advertencia",
                            alertMessage = "No apliques demasiada pasta. Una gota del tamaño de un grano de arroz es suficiente.",
                            requiresConfirmation = false,
                            confirmationText = ""
                        )
                    )
                ),
                GuideDto(
                    id = "guide_gpu",
                    deviceId = "dev_4",
                    title = "Revisión de GPU y Ventiladores",
                    category = "PC",
                    difficulty = "Medio",
                    estimatedTime = "30 min",
                    toolsList = listOf("Destornillador Phillips #0", "Compresor de aire", "Pulsera antiestática"),
                    precautions = "Inspección de tarjeta gráfica, limpieza de ventiladores con aire comprimido y verificación del slot PCIe.",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    modelSpec = "Corsair One i300",
                    steps = listOf(
                        GuideStepDto(
                            stepNumber = 1,
                            title = "Retira la tapa lateral del gabinete",
                            description = "Desatornilla los dos tornillos de la tapa lateral y deslízala hacia atrás para abrirla. Coloca el gabinete sobre una superficie estable.",
                            hasAlert = true,
                            alertType = "Advertencia",
                            alertMessage = "Antes de abrir el gabinete, asegúrate de haber desconectado el cable de corriente completamente.",
                            requiresConfirmation = true,
                            confirmationText = "Cable desconectado"
                        ),
                        GuideStepDto(
                            stepNumber = 2,
                            title = "Limpia los ventiladores con aire comprimido",
                            description = "Sostén las aspas del ventilador con un dedo para que no giren. Sopla en ráfagas cortas para expulsar el polvo acumulado sin dañar los rodamientos.",
                            hasAlert = false,
                            alertType = "Información",
                            alertMessage = "",
                            requiresConfirmation = false,
                            confirmationText = ""
                        ),
                        GuideStepDto(
                            stepNumber = 3,
                            title = "Verifica que la GPU esté bien encajada",
                            description = "Empuja suavemente la tarjeta gráfica hacia abajo hasta escuchar el clic del seguro del slot PCIe. Reconecta los cables de alimentación de la GPU.",
                            hasAlert = true,
                            alertType = "Información",
                            alertMessage = "Si la GPU no hace clic al encajar, no la fuerces. Revisa que el slot esté libre de obstrucciones.",
                            requiresConfirmation = false,
                            confirmationText = ""
                        )
                    )
                )
            )
        )
    }

    @PostMapping("/auth/login")
    fun login(@RequestBody request: LoginRequestDto): ResponseEntity<AuthResponseDto> {
        val email = request.email.trim().lowercase()
        val password = request.password.trim()
        val normalizedRole = if (request.role.equals("TECH", ignoreCase = true)) "TECH" else "USER"

        if (email.isBlank() || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                AuthResponseDto(
                    success = false,
                    message = "Credenciales incorrectas",
                    user = null
                )
            )
        }

        val key = accountKey(email, normalizedRole)
        val existingAccount = usersByKey[key]
            ?: usersByKey.values.find { it.user.email.equals(email, ignoreCase = true) && it.user.role.equals(normalizedRole, ignoreCase = true) }

        if (existingAccount != null) {
            // Acepta "123456", contraseña registrada o cualquier contraseña válida en modo demo
            val passwordAccepted = password == existingAccount.password || password == "123456" || password.isNotBlank()
            return if (passwordAccepted) {
                ResponseEntity.ok(
                    AuthResponseDto(
                        success = true,
                        message = "Inicio de sesión exitoso",
                        user = existingAccount.user
                    )
                )
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    AuthResponseDto(
                        success = false,
                        message = "Credenciales incorrectas",
                        user = null
                    )
                )
            }
        }

        // En modo demo, autentica y registra en memoria si ingresa un correo válido
        val inferredName = email.substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank {
            if (normalizedRole == "TECH") "Carlos M." else "Usuario Demo"
        }
        val demoUser = UserDto(
            id = if (normalizedRole == "TECH") "admin-current" else "user_$email",
            fullName = inferredName,
            email = email,
            role = normalizedRole,
            workshopCode = if (normalizedRole == "TECH") "123456" else ""
        )
        usersByKey[key] = StoredAccount(user = demoUser, password = password)

        return ResponseEntity.ok(
            AuthResponseDto(
                success = true,
                message = "Inicio de sesión exitoso",
                user = demoUser
            )
        )
    }

    @PostMapping("/auth/send-code")
    fun sendVerificationCode(@RequestBody request: SendCodeRequestDto): ResponseEntity<ApiMessageResponseDto> {
        val email = request.email.trim()
        if (email.isBlank()) {
            return ResponseEntity.badRequest().body(
                ApiMessageResponseDto(
                    success = false,
                    message = "Ingresa un correo electrónico válido",
                    demoCode = ""
                )
            )
        }
        val demoCode = "123456"
        verificationCodesByEmail[email.lowercase()] = demoCode
        return ResponseEntity.ok(
            ApiMessageResponseDto(
                success = true,
                message = "Código de confirmación enviado a $email",
                demoCode = demoCode
            )
        )
    }

    @PostMapping("/auth/register")
    fun register(@RequestBody request: RegisterRequestDto): ResponseEntity<AuthResponseDto> {
        val name = request.name.trim()
        val email = request.email.trim().lowercase()
        val password = request.password.trim()
        val normalizedRole = if (request.role.equals("TECH", ignoreCase = true)) "TECH" else "USER"
        val verificationCode = request.verificationCode.trim()

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(
                AuthResponseDto(
                    success = false,
                    message = "Todos los campos obligatorios deben completarse",
                    user = null
                )
            )
        }

        if (normalizedRole == "TECH" && verificationCode.isBlank()) {
            return ResponseEntity.badRequest().body(
                AuthResponseDto(
                    success = false,
                    message = "Ingresa el código de verificación de taller",
                    user = null
                )
            )
        }

        val resolvedId = if (normalizedRole == "TECH") {
            "admin-current"
        } else {
            "user_$email"
        }

        val nuevoUserDto = UserDto(
            id = resolvedId,
            fullName = name,
            email = email,
            role = normalizedRole,
            workshopCode = if (normalizedRole == "TECH") verificationCode.ifBlank { "123456" } else ""
        )

        val key = accountKey(email, normalizedRole)
        usersByKey[key] = StoredAccount(user = nuevoUserDto, password = password)

        return ResponseEntity.ok(
            AuthResponseDto(
                success = true,
                message = "Cuenta registrada correctamente",
                user = nuevoUserDto
            )
        )
    }

    @GetMapping("/devices")
    fun getDevices(): List<DeviceDto> {
        return devices.toList()
    }

    @PostMapping("/devices")
    fun createDevice(@RequestBody device: DeviceDto): DeviceDto {
        val resolvedId = device.id.ifBlank { "dev_${System.currentTimeMillis()}" }
        val newDevice = device.copy(id = resolvedId)
        devices.removeIf { it.id == resolvedId }
        devices.add(newDevice)
        return newDevice
    }

    @GetMapping("/guides")
    fun getGuides(): List<GuideDto> {
        return guides.toList()
    }

    @PostMapping("/guides")
    fun createGuide(@RequestBody guide: GuideDto): GuideDto {
        val resolvedId = guide.id.ifBlank { "guide_${System.currentTimeMillis()}" }
        val normalizedSteps = guide.steps.mapIndexed { index, step ->
            if (step.stepNumber <= 0) step.copy(stepNumber = index + 1) else step
        }
        val newGuide = guide.copy(id = resolvedId, steps = normalizedSteps)
        guides.removeIf { it.id == resolvedId }
        guides.add(newGuide)
        return newGuide
    }
}
