# SchoolTrack Mobile - Kotlin Multiplatform

Aplicativo mobile do SchoolTrack usando **Kotlin Multiplatform** com **Compose Multiplatform**.

## Tech Stack

- **Kotlin Multiplatform** (KMP) com Compose Multiplatform
- **Ktor** para networking HTTP e WebSocket
- **Koin** para injeção de dependência
- **Kotlinx Serialization** para JSON
- **Google Maps Compose** para mapa em tempo real (Android)
- **FusedLocationProvider** para GPS do motorista (Android)

## Estrutura do Projeto

```
mobile/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/com/schooltrack/mobile/
│   │   │   ├── App.kt                    # Entry point Compose
│   │   │   ├── di/AppModule.kt           # Koin DI
│   │   │   ├── data/
│   │   │   │   ├── model/ApiModels.kt    # DTOs serializáveis
│   │   │   │   └── network/
│   │   │   │       ├── ApiClient.kt      # HTTP client (Ktor)
│   │   │   │       └── TokenManager.kt   # Gerenciamento JWT
│   │   │   └── ui/
│   │   │       ├── theme/                # Paleta de cores e tipografia
│   │   │       ├── components/           # Componentes reutilizáveis
│   │   │       ├── navigation/           # Navegação entre telas
│   │   │       └── screens/
│   │   │           ├── auth/             # Login, Register, ForgotPassword
│   │   │           ├── dashboard/        # Dashboard com mapa
│   │   │           ├── notifications/    # Notificações
│   │   │           ├── schedule/         # Horários
│   │   │           └── settings/         # Configurações
│   │   └── androidMain/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/schooltrack/mobile/
│   │           ├── MainActivity.kt
│   │           ├── SchoolTrackApp.kt     # Application class
│   │           └── service/
│   │               ├── LocationService.kt      # GPS do motorista
│   │               └── BusWebSocketService.kt  # WebSocket real-time
│   └── build.gradle.kts
├── gradle/libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Telas

| Tela | Descrição |
|------|-----------|
| **Login** | Autenticação com CAPTCHA |
| **Register** | Cadastro de responsável |
| **ForgotPassword** | Recuperação de senha |
| **Dashboard** | Mapa em tempo real + alunos + ônibus ativos |
| **Notifications** | Lista de notificações com marcação de leitura |
| **Schedule** | Horários de embarque/desembarque |
| **Settings** | Perfil, preferências, tema, logout |

## Paleta de Cores

Consistente com o frontend web:
- **Primary:** `#6C4C05` (marrom dourado)
- **Secondary:** `#FFD700` (amarelo ouro)
- **Accent:** `#EF4444` (vermelho)
- **Gradient:** `#9A8F75` → `#7F755D`

## Como Rodar

1. Abra a pasta `mobile/` no **Android Studio** (Hedgehog ou superior)
2. Aguarde o Gradle sincronizar
3. Configure a `MAPS_API_KEY` no `local.properties`
4. Execute no emulador ou dispositivo físico

## API

O app se conecta ao backend Spring Boot em `http://10.0.2.2:8080/api` (emulador Android).
Para dispositivo físico, altere o `BASE_URL` em `ApiClient.kt` para o IP da máquina na rede local.

## WebSocket

Rastreamento em tempo real via WebSocket STOMP:
- **Endpoint:** `ws://10.0.2.2:8080/ws`
- **Subscribe:** `/topic/bus/{busId}/location`
- **Send:** `/app/bus/location`
