# 🧱 Compose Clean Blueprint

> **Production-ready Jetpack Compose + Clean Architecture template for Android.**  
> Ships working examples of **both MVVM and MVI** over the same domain and data layers.  
> Clone it, rename the package, and start building features — not infrastructure.

---

## 📑 Table of Contents

- [What's Inside](#-whats-inside)
- [Architecture](#-architecture)
- [Module Layout](#-module--package-layout)
- [Setup](#-setup)
- [MVVM Pattern Guide](#-how-to-use-the-mvvm-template)
- [MVI Pattern Guide](#-how-to-use-the-mvi-template)
- [Tech Stack](#-tech-stack)
- [Adding a New Feature](#-extending-add-a-new-feature-the-same-way)
- [Common Gotchas](#-common-gotchas)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ What's Inside

| Feature | Detail |
|---|---|
| **Clean Architecture** | Strict `presentation → domain → data` dependency direction |
| **MVVM Pattern** | `StateFlow` + `UiState` — lightweight, great for most screens |
| **MVI Pattern** | `Intent / State / Effect` + `Channel` — scales for complex screens |
| **Offline-First** | Room is the single source of truth; network refreshes it |
| **Hilt DI** | Dependency injection wired end to end |
| **Type-Safe Navigation** | Serializable routes, zero string parsing |
| **Material 3** | Dynamic color + dark mode support out of the box |
| **Version Catalog** | `libs.versions.toml` — one place for all dependency versions |

Both patterns share the **same domain and data layers** — pick the one that fits your screen, or use both in the same project.

---

## 🏛️ Architecture

<p align="center">
  <img src="https://github.com/user-attachments/assets/4cc81ceb-1194-481e-843f-ad2e5256344a" alt="Compose Clean Blueprint Architecture Diagram" width="600"/>
</p>

The architecture is split into **three layers** with a strict one-way dependency rule:

| Layer | Contains | Depends On |
|---|---|---|
| **Presentation** | Screens, ViewModels (MVVM & MVI) | Domain only |
| **Domain** | Models, Repository interfaces, UseCases | Nothing (framework-free) |
| **Data** | Room, Retrofit, Repository implementations, Mappers | Domain only |
| **Core** | Resource, UiState, Network, Hilt modules | — (cross-cutting) |

> **The golden rule:** dependencies point *inward*.  
> `domain` knows nothing about Android, Room, or Retrofit.  
> `presentation` and `data` depend on `domain` — never the reverse.

---

## 📁 Module / Package Layout

| Package | Responsibility |
|---|---|
| `core.common` | `Resource` (operation result) and `UiState` (screen state) + extensions |
| `core.network` | Retrofit/OkHttp construction and the auth/logging interceptor |
| `core.di` | Hilt modules (`AppModule`, `NetworkModule`) and dispatcher qualifiers |
| `data.local` | Room `AppDatabase`, `UserDao`, `UserEntity` |
| `data.remote` | `UserApi` (Retrofit) and `UserDto` |
| `data.repository` | `UserRepositoryImpl` — the offline-first implementation |
| `data.mapper` | Pure functions converting DTO ⇄ Entity ⇄ Domain model |
| `domain.model` | Framework-free business models |
| `domain.repository` | Repository *interfaces* (contracts) |
| `domain.usecase` | One business action per class |
| `presentation.mvvm` | MVVM screens, ViewModels, reusable components |
| `presentation.mvi` | MVI contract (`Intent`/`State`/`Effect`), ViewModel, screen |
| `presentation.navigation` | Type-safe routes, nav graph, bottom bar |
| `ui.theme` | Material 3 `Color`, `Type`, `Theme` |

---

## 🚀 Setup

### Prerequisites

- **Android Studio** Ladybug (2024.2) or newer
- **JDK 17** — required by Android Gradle Plugin 8.7
- **Android SDK Platform 35**

### Steps

```bash
# 1. Clone the repo
git clone https://github.com/your-username/compose-clean-blueprint.git

# 2. Open in Android Studio and let Gradle sync
#    All dependencies resolve from the version catalog automatically.

# 3. Run on device or emulator (min SDK 24)
./gradlew assembleDebug
```

The template points at the public [`jsonplaceholder.typicode.com`](https://jsonplaceholder.typicode.com/) API — it runs with **zero configuration**.

To point at your own API, update `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://your.api/\"")
```

### Setup Checklist (When Adopting the Template)

- [ ] Rename the root package from `com.example.composeclean` to your package name
- [ ] Update `applicationId` in `app/build.gradle.kts`
- [ ] Replace `BASE_URL` with your API endpoint
- [ ] Replace `User` domain model, DAO, and API with your own entities
- [ ] Update app name in `strings.xml`
- [ ] Remove or replace the sample `jsonplaceholder` API key/config

---

## 🧩 How to Use the MVVM Template

MVVM here means: the ViewModel exposes **one immutable `UiState`** as a `StateFlow`, and the screen renders it declaratively.

```kotlin
@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<List<User>>> = refreshTrigger
        .flatMapLatest { getUsersUseCase() }   // re-run on refresh
        .map { it.toUiState() }                // Resource → UiState
        .stateIn(viewModelScope, WhileSubscribed(5_000), UiState.loading())

    fun refresh() = refreshTrigger.update { it + 1 }
}
```

```kotlin
@Composable
fun UserListScreen(viewModel: UserListViewModel = hiltViewModel()) {
    // ✅ Always use collectAsStateWithLifecycle — never collectAsState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.errorMessage != null -> ErrorState(uiState.errorMessage)
        else -> UserList(users = uiState.data ?: emptyList())
    }
}
```

**Use MVVM when** the screen mostly displays state and reacts to a handful of events. It's the lighter-weight option.

---

## 🔁 How to Use the MVI Template

MVI adds strict unidirectional data flow: UI sends **Intents**, the ViewModel reduces them into a single **State**, and one-time events go out as **Effects**.

```kotlin
// 1. Define the contract
sealed interface UserIntent {
    data object Refresh : UserIntent
    data class UserClicked(val userId: Int) : UserIntent
}

data class UserState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface UserEffect {
    data class NavigateToDetail(val userId: Int) : UserEffect
    data class ShowMessage(val message: String) : UserEffect
}
```

```kotlin
// 2. ViewModel — single entry point + Channel for effects
@HiltViewModel
class UserMviViewModel @Inject constructor(...) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _effects = Channel<UserEffect>(Channel.BUFFERED)
    val effects: Flow<UserEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.Refresh -> loadUsers()
            is UserIntent.UserClicked -> {
                viewModelScope.launch {
                    _effects.send(UserEffect.NavigateToDetail(intent.userId))
                }
            }
        }
    }
}
```

```kotlin
// 3. Screen — dispatch intents, collect effects with LaunchedEffect
@Composable
fun UserMviScreen(viewModel: UserMviViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UserEffect.NavigateToDetail -> navController.navigate(...)
                is UserEffect.ShowMessage -> snackbarHostState.showSnackbar(...)
            }
        }
    }

    // Dispatch: viewModel.onIntent(UserIntent.Refresh)
}
```

**Use MVI when** the screen has complex, interdependent state and many event types. More boilerplate, but scales better for rich screens and is easier to test.

---

## 🛠️ Tech Stack

| Concern | Library |
|---|---|
| UI Toolkit | Jetpack Compose (Material 3) |
| Language | Kotlin + Coroutines + Flow |
| Dependency Injection | Hilt |
| Local Database | Room |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Navigation | Navigation Compose (type-safe, serializable routes) |
| State Management | StateFlow / SharedFlow / Channel |
| Image Loading | Coil |
| Preferences | DataStore |
| Build | Gradle (Kotlin DSL) + Version Catalog + KSP |

> Exact versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## ➕ Extending: Add a New Feature the Same Way

Follow the dependency direction — always build **inside-out** (domain first, data second, presentation last).

Suppose you're adding a `Post` feature:

**Step 1 — Domain layer**
```
domain/model/Post.kt
domain/repository/PostRepository.kt       ← interface only
domain/usecase/GetPostsUseCase.kt
```

**Step 2 — Data layer**
```
data/remote/dto/PostDto.kt
data/remote/api/PostApi.kt
data/local/entity/PostEntity.kt
data/local/dao/PostDao.kt                 ← add to AppDatabase + bump DB version + add Migration
data/mapper/PostMapper.kt
data/repository/PostRepositoryImpl.kt
```

**Step 3 — Hilt DI**
```kotlin
// AppModule.kt
@Binds abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

// NetworkModule.kt
@Provides fun providePostApi(retrofit: Retrofit): PostApi = retrofit.create(PostApi::class.java)
```

**Step 4 — Presentation layer**

Pick MVVM or MVI. Copy the matching `users` or `mvi` package as a starting point and adapt.

**Step 5 — Navigation**
```kotlin
// NavRoute.kt
@Serializable data object Posts : NavRoute
@Serializable data class PostDetail(val postId: Int) : NavRoute

// AppNavGraph.kt
composable<Posts> { PostListScreen() }
composable<PostDetail> { backStackEntry ->
    val route: PostDetail = backStackEntry.toRoute()
    PostDetailScreen(postId = route.postId)
}
```

---

## ⚠️ Common Gotchas

| Pitfall | Why It Matters | Fix |
|---|---|---|
| Using `collectAsState()` | Keeps collecting while app is backgrounded — wastes resources and risks leaks | Always use `collectAsStateWithLifecycle()` |
| Putting navigation in state | Replays on every recomposition and config change | Use MVI `Effect`s (Channel) for one-time events |
| Forgetting DB migrations | Silent data wipes are worse than crashes | Bump `DATABASE_VERSION` and add a `Migration` every schema change — `fallbackToDestructiveMigration()` is intentionally omitted |
| Android imports in domain | Breaks the clean boundary — domain must be framework-free | No Room, Retrofit, or Android SDK imports in `domain.*` |
| Hard-coding `Dispatchers.IO` | Makes unit tests non-deterministic | Inject `@IoDispatcher` via Hilt instead |
| Running on JDK 11 | AGP 8.7 requires JDK 17 | Set `JAVA_HOME` to a JDK 17 installation |

---

## 🤝 Contributing

Contributions are welcome! Please follow the existing structure and patterns.

```bash
# 1. Fork and create a feature branch
git checkout -b feature/my-feature

# 2. Lint and test before opening a PR
./gradlew lint testDebugUnitTest

# 3. Open a PR describing:
#    - What changed
#    - Which pattern it follows (MVVM / MVI)
#    - Why the change is needed
```

Guidelines:
- Follow the existing package structure and the `presentation → domain → data` dependency direction.
- Add KDoc to all public classes and functions.
- Keep the domain layer framework-free.
- One use case per class.

---

## 📄 License

```
MIT License

Copyright (c) 2026 compose-clean-blueprint contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

<div align="center">

Made with ❤️ for the Android community · Give it a ⭐ if it helped you ship faster

</div>
