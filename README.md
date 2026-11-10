# FixTech - Plataforma Movil para Diagnostico y Mantenimiento Tecnico

FixTech es una aplicacion movil nativa desarrollada para el sistema operativo Android en Kotlin, orientada a solucionar la problematica del soporte tecnico preventivo y correctivo en equipos de computo (laptops y computadoras de escritorio). Incorpora un frontend con Jetpack Compose, arquitectura MVVM, persistencia local con Room ORM, consumo de API REST externa y un backend desacoplado en Spring Boot.

---

## Nota sobre la Organizacion de Ramas

Conforme a las directrices del curso:
* **main:** Contiene el cascaron inicial del proyecto.
* **develop:** Rama de integracion final donde se encuentra la version completa, estabilizada y evaluable del producto.
* **pruebas / beta:** Ramas de trabajo donde se realizaron las pruebas funcionales, correcciones graficas y validaciones previas.

---

## Datos Institucionales y del Equipo

* **Institucion:** Universidad Politecnica de Chiapas (UPChiapas)
* **Carrera:** Ingenieria en Tecnologias de la Informacion e Innovacion Digital (ITIID)
* **Asignatura:** 4C.26 Desarrollo de Aplicaciones Moviles
* **Periodo:** Septiembre - Diciembre 2026
* **Grupo:** 4 C
* **Docente Titular:** Ing. Montserrat Viridiana Ramos Lopez
* **Integrantes del Equipo:**
  * Erick Fabian Gutierrez Vazquez (Matricula: 253707)
  * Carlos David Barrientos Marina (Matricula: 253384)

---

## Funcionalidades Principales

1. **Diagnostico Interactivo Asistido:** Cuestionarios estructurados segun sintomas observables (pantallas azules, sobrecalentamiento, lentitud, fallas de encendido o bateria) para identificar componentes afectados y sugerir manuales pertinentes.
2. **Catalogo y Gestion de Dispositivos:** Registro categorizado de equipos informaticos con especificaciones tecnicas (marca, modelo, CPU, RAM y almacenamiento) con persistencia local y sincronizacion remota.
3. **Guias de Reparacion Paso a Paso:** Procedimientos tecnicos desglosados con detalle de herramientas requeridas, precauciones de seguridad antiestatica y soporte para evidencias fotograficas.
4. **Doble Rol de Usuario:**
   * **Usuario Convencional:** Acceso a diagnosticos rapidos, consulta de manuales y almacenamiento de equipos favoritos.
   * **Tecnico Certificado:** Publicacion de nuevas guias de reparacion, alta de dispositivos y carga de comprobantes profesionales.
5. **Persistencia Local y Funcionamiento Offline:** Almacenamiento relacional en SQLite mediante Room ORM para garantizar operatividad y acceso a guias sin conexion a internet.

---

## Integraciones del Dispositivo y Sistema Operativo

Se implementaron tres funcionalidades nativas del hardware y del sistema:

1. **Autenticacion Biometrica (BiometricAuthHelper):** Acceso mediante huella dactilar o reconocimiento facial implementado con las librerias oficiales BiometricPrompt y BiometricManager.
2. **Gestion de Documentos y PDF (CertificateDocumentHelper / DocumentPickerHelper):** Carga y almacenamiento de certificaciones tecnicas en formato PDF e imagenes, validando limites de tamaño (hasta 10 MB) y tipos MIME autorizados en almacenamiento interno privado.
3. **Camara Nativa y Galeria Multimedia (MediaPickerHelper):** Captura de fotografias mediante FileProvider y ActivityResultContracts.TakePicture, complementado con seleccion desde galeria con PickVisualMedia y gestion de permisos de camara en tiempo de ejecucion.

---

## Arquitectura del Software (MVVM)

El codigo sigue el patron Model-View-ViewModel con flujo unidireccional de datos (UDF):

* **com.fixtech.presentation:** Pantallas declarativas en Jetpack Compose, ViewModels y estados observables mediante StateFlow (UiState).
* **com.fixtech.domain:** Modelos de negocio puros (Device, Guide, User, Certificate) y contratos de repositorios.
* **com.fixtech.data.local:** Base de datos relacional Room (FixTechDatabase v4), DAOs y SessionManager para control de sesion.
* **com.fixtech.data.remote:** Cliente HTTP Retrofit (FixTechApiService) y Data Transfer Objects (DTOs).
* **com.fixtech.data.repository:** Implementaciones de repositorios como fuente unica de la verdad (Single Source of Truth).
* **com.fixtech.integration:** Modulos independientes para integraciones de biometria, documentos PDF y camara.
* **com.fixtech.di:** Contenedor de dependencias centralizado (AppContainer) y fabrica de ViewModels (FixTechViewModelFactory).
* **com.fixtech.navigation:** Grafo de navegacion centralizado (AppNavGraph) y rutas tipadas (Routes).

---

## Requisitos de Compilacion y Ejecucion

* **Entorno de desarrollo:** Android Studio Ladybug (2024.2.1) o superior.
* **JDK:** Java Development Kit 21.
* **SDK de Android:** compileSdk 35, minSdk 26 (Android 8.0 Oreo), targetSdk 35.
* **Gradle:** 8.7 con Kotlin DSL.

### Instrucciones de Compilacion:

1. Clonar el repositorio:
   git clone https://github.com/Z3K-Erick/FixTech---Erick.git
   cd FixTech---Erick

2. Cambiar a la rama de integracion:
   git checkout develop

3. Abrir el proyecto en Android Studio y sincronizar dependencias de Gradle.

4. Compilar el archivo APK:
   ./gradlew assembleDebug

El instalable generado estara en:
app/build/outputs/apk/debug/app-debug.apk

### Ejecucion del Servidor Backend (Opcional):

cd backend
./gradlew bootRun

El servicio REST estara disponible en http://localhost:8080/api.
