# Compose Clean Blueprint

A production-ready **Jetpack Compose + Clean Architecture** template for Android, shipping working
examples of **both MVVM and MVI** presentation patterns over the same domain and data layers.

Clone it, rename the package, and start building features instead of wiring infrastructure.

---

## ✨ What's inside

- **Clean Architecture** — strict `presentation → domain → data` dependency direction.
- **Two presentation patterns**, side by side, sharing one domain/data stack:
  - **MVVM** with `StateFlow` + `UiState`.
  - **MVI** with `Intent` / `State` / `Effect` and a `Channel` for one-time events.
- **Offline-first repository** — Room is the single source of truth; the network refreshes it.
- **Hilt** dependency injection wired end to end.
- **Type-safe Navigation Compose** (serializable routes, no string parsing).
- **Material 3** theming with dynamic color + dark mode.
- **Version catalog** (`libs.versions.toml`) for centralized dependency management.

---

## 🏛️ Architecture

<img width="1024" height="1536" alt="ChatGPT Image Jun 24, 2026, 12_58_25 AM" src="https://github.com/user-attachments/assets/4cc81ceb-1194-481e-843f-ad2e5256344a" />

CORE (cross-cutting): common/Resource, common/UiState, network/*, di/*
```

**The golden rule:** dependencies point *inward*. `domain` knows nothing about Android, Room, or
Retrofit. `presentation` and `data` depend on `domain`, never the reverse.

---

## 📁 Module / package layout

| Package | Responsibility |
| --- | --- |
| `core.common` | `Resource` (operation result) and `UiState` (screen state) + extensions. |
| `core.network` | Retrofit/OkHttp construction and the auth/logging interceptor. |
| `core.di` | Hilt modules (`AppModule`, `NetworkModule`) and dispatcher qualifiers. |
| `data.local` | Room `AppDatabase`, `UserDao`, `UserEntity`. |
| `data.remote` | `UserApi` (Retrofit) and `UserDto`. |
| `data.repository` | `UserRepositoryImpl` — the offline-first implementation. |
| `data.mapper` | Pure functions converting DTO ⇄ Entity ⇄ Domain. |
| `domain.model` | Framework-free business models. |
| `domain.repository` | Repository *interfaces* (contracts). |
| `domain.usecase` | One business action per class. |
| `presentation.mvvm` | MVVM screens, ViewModels, reusable components. |
| `presentation.mvi` | MVI contract (`Intent`/`State`/`Effect`), ViewModel, screen. |
| `presentation.navigation` | Type-safe routes, nav graph, bottom bar. |
| `ui.theme` | Material 3 `Color`, `Type`, `Theme`. |

---

## 🚀 Setup

### Prerequisites
- **Android Studio** Ladybug (2024.2) or newer.
- **JDK 17** (required by Android Gradle Plugin 8.7).
- Android SDK Platform **35**.

### Steps
1. Clone the repo and open it in Android Studio (or run `./gradlew assembleDebug`).
2. Let Gradle sync — all dependencies resolve from the version catalog.
3. Run the `app` configuration on a device/emulator (min SDK 24).

The template ships pointing at the public
[`jsonplaceholder.typicode.com`](https://jsonplaceholder.typicode.com/) API so it runs with zero
configuration. Change the base URL in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://your.api/\"")
```

See the **Setup checklist** at the bottom for everything to rename when adopting the template.

---

## 🧩 How to use the MVVM template

MVVM here means: the ViewModel exposes **one immutable `UiState`** as a `StateFlow`, and the screen
renders it declaratively.

```kotlin
@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<List<User>>> = refreshTrigger
        .flatMapLatest { getUsersUseCase() }      // re-run on refresh
        .map { it.toUiState() }                   // Resource → UiState
        .stateIn(viewModelScope, WhileSubscribed(5_000), UiState.loading())

    fun refresh() = refreshTrigger.update { it + 1 }
}
```

```kotlin
@Composable
fun UserListScreen(viewModel: UserListViewModel = hiltViewModel()) {
    // ALWAYS collectAsStateWithLifecycle — never the raw collectAsState.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // render uiState.isLoading / uiState.data / uiState.errorMessage
}
```

**Use MVVM when** the screen is mostly displaying state and reacting to a handful of events. It's
the lighter-weight option.

---

## 🔁 How to use the MVI template

MVI adds strict unidirectional flow: the UI sends **Intents**, the ViewModel reduces them into a
single **State**, and one-time events go out as **Effects**.

```kotlin
// 1. Contract
sealed interface UserIntent { data object Refresh; data class UserClicked(val userId: Int) }
data class UserState(val isLoading: Boolean = false, val users: List<User> = emptyList(), ...)
sealed interface UserEffect { data class NavigateToDetail(val userId: Int); data class ShowMessage(...) }

// 2. ViewModel — single entry point + a Channel for effects
fun onIntent(intent: UserIntent) { when (intent) { ... } }
val state: StateFlow<UserState>
val effects: Flow<UserEffect>      // backed by Channel(BUFFERED)
```

```kotlin
@Composable
fun UserMviScreen(viewModel: UserMviViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect -> /* navigate / snackbar */ }
    }
    // dispatch: viewModel.onIntent(UserIntent.Refresh)
}
```

**Use MVI when** the screen has complex, interdependent state and many event types, and you want an
auditable, testable event log. It's more boilerplate but scales better for rich screens.

---

## 🛠️ Tech stack

| Concern | Library |
| --- | --- |
| UI toolkit | Jetpack Compose (Material 3) |
| Language | Kotlin + Coroutines + Flow |
| DI | Hilt |
| Local DB | Room |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Navigation | Navigation Compose (type-safe) |
| State | StateFlow / SharedFlow / Channel |
| Image loading | Coil |
| Preferences | DataStore |
| Build | Gradle (Kotlin DSL) + Version Catalog + KSP |

Exact versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## ➕ Extending: add a new feature the same way

Suppose you add a `Post` feature. Follow the dependency direction, inside-out:

1. **Domain** — `domain/model/Post.kt`, `domain/repository/PostRepository.kt`,
   `domain/usecase/GetPostsUseCase.kt`.
2. **Data** — `data/remote/dto/PostDto.kt` + `PostApi`, `data/local/entity/PostEntity.kt` + `PostDao`
   (add it to `AppDatabase` and **bump the DB version + add a migration**),
   `data/mapper/PostMapper.kt`, `data/repository/PostRepositoryImpl.kt`.
3. **DI** — `@Binds PostRepository` in `AppModule`, `@Provides PostApi` in `NetworkModule`.
4. **Presentation** — pick MVVM or MVI, copy the matching `users` / `mvi` package as a starting
   point.
5. **Navigation** — add a `@Serializable` route to `NavRoute` and a `composable<…>` entry to
   `AppNavGraph`.

---

## ⚠️ Common gotchas

- **Use `collectAsStateWithLifecycle()`**, not `collectAsState()` — the latter keeps collecting
  while the app is backgrounded, wasting work and risking leaks.
- **Don't put one-time events in state.** Navigation and snackbars belong in MVI `Effect`s (a
  `Channel`), otherwise they replay on every recomposition / config change.
- **Bump `AppDatabase.DATABASE_VERSION` and add a `Migration`** whenever the schema changes. The
  template deliberately omits `fallbackToDestructiveMigration()` so missing migrations fail loudly
  instead of silently wiping user data.
- **Keep the domain layer framework-free.** No Room/Retrofit/Android imports in `domain`.
- **JDK 17 is required.** AGP 8.7 will not run on JDK 11.
- **Inject dispatchers** (`@IoDispatcher`) instead of hard-coding `Dispatchers.IO`, so tests stay
  deterministic.

---

## 🤝 Contributing

1. Fork and create a feature branch: `git checkout -b feature/my-feature`.
2. Follow the existing package structure and the dependency direction described above.
3. Keep public classes/functions documented with KDoc.
4. Run `./gradlew lint testDebugUnitTest` before opening a PR.
5. Open a PR describing the change and the pattern (MVVM/MVI) it follows.

---

## 📄 License

Released under the [MIT License](LICENSE).
