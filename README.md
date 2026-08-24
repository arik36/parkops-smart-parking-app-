# ParkOps — Smart Parking Management

Android application for managing multi-tenant parking facilities in real time. Drivers reserve and release spots, staff file incident reports, and administrators design parking layouts and manage their team — all from a single role-aware client.

Built with Kotlin, Jetpack Compose and Clean Architecture over a Supabase (PostgreSQL + PostgREST) backend.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.02-4285F4?logo=jetpackcompose&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-PostgREST-3ECF8E?logo=supabase&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-24-orange)

---

## Screenshots
(some of the functions in the interface)

-parking view (universal module)
<img width="290" height="568" alt="image" src="https://github.com/user-attachments/assets/a5ffae7d-99ac-4b64-ad6c-eef7537e056a" />

-administrative notices (admin module)
<img width="306" height="614" alt="image" src="https://github.com/user-attachments/assets/a74cef57-21fe-404e-90c5-4e3a98f8ea19" />

-staff management module
<img width="307" height="603" alt="image" src="https://github.com/user-attachments/assets/eda47e31-67fc-4934-804b-57fce6e7e2fa" />

-history by client
<img width="290" height="587" alt="image" src="https://github.com/user-attachments/assets/47a93550-451c-40d8-b782-fc00153a6e89" />

---

## Features

### Drivers
- Email/password sign-up and login backed by Supabase Auth (JWT).
- Browse organizations, parking lots and floors; view a live grid of spot availability.
- Reserve a spot, convert the reservation to an active occupancy on arrival, and release it when leaving.
- Reservations expire automatically server-side, so abandoned holds free up the spot.
- Personal reservation history.
- Offline mode: cached data stays readable when the network drops, with an in-app banner signalling the degraded state.

### Staff
- File incident reports against a specific spot or facility.
- Review previously filed reports.
- Export any report to PDF directly on-device.

### Administrators
- Visual layout editor: create, move and delete parking spots and layout elements on a floor grid.
- Approve or reject pending staff access requests.
- List organization staff members and revoke access.

---

## Architecture

Clean Architecture with three layers and unidirectional data flow. The UI observes `StateFlow` exposed by the ViewModels; ViewModels depend only on domain interfaces, which the data layer implements.

```
ui/          Compose screens, ViewModels, UI state, theme
  ├─ auth        login / register
  ├─ map         map, reservations, admin dialogs, layout grid
  ├─ splash      session restore
  └─ components  reusable Compose primitives

domain/      Framework-free business layer
  ├─ model       ParkingLot, ParkingSpot, Reservation, IncidentReport, ...
  └─ repository  AuthRepository, ParkingRepository (interfaces)

data/        Implementation details
  ├─ remote      Retrofit ApiService + DTOs
  ├─ local       Room database, DAOs, offline cache entities
  └─ repository  Repository implementations, DTO ↔ domain mapping

di/          Hilt modules (Network, Database, Repository)
core/        TokenManager (DataStore), OfflineModeManager
```

The backend is Supabase. Reads go through PostgREST table endpoints; every write and every privileged operation goes through a Postgres RPC function (`reserve_spot`, `admin_create_parking_spot`, `staff_create_incident_report`, …), which keeps authorization logic in the database rather than in the client.

---

## Tech stack

| Concern | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| State | `StateFlow` + ViewModel |
| DI | Hilt |
| Networking | Retrofit 2, OkHttp 4, Gson |
| Local storage | Room, DataStore Preferences |
| Async | Kotlin Coroutines |
| Backend | Supabase — PostgreSQL, PostgREST, Auth (JWT), RPC functions |
| Min / target SDK | 24 / 35 |

---

## Getting started

### Prerequisites
- Android Studio (Ladybug or newer)
- JDK 11
- A Supabase project with the ParkOps schema and RPC functions

### Configuration

Credentials are not committed. Add them to `local.properties` in the project root:

```properties
SUPABASE_URL=https://<your-project>.supabase.co/
SUPABASE_ANON_KEY=<your-anon-key>
```

They are exposed to the app through `BuildConfig` at build time.

### Run

```bash
git clone https://github.com/arik36/parkops-smart-parking-app.git
cd parkops-smart-parking-app
./gradlew assembleDebug
```

Or open the project in Android Studio and run the `app` configuration on a device or emulator with API 24+.

---

## Roadmap

- Unit tests for repositories and ViewModels; instrumentation tests for the reservation flow
- Split `HomeTab` into smaller composables
- Realtime spot updates via Supabase Realtime instead of manual refresh
- Push notifications for reservation expiry
- R8/ProGuard enabled on release builds

---

## Author

**Ariadne Lizett Macías Campos** — Computer Systems Engineering, Instituto Tecnológico de León.
**Liseth Yareth Lara Lopez** — Computer Systems Engineering, Instituto Tecnológico de León.
Academic project, 2026.
