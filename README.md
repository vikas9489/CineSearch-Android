# 🎬 CineSearch-Android


A modern Android application to browse, search, and explore movies using the [OMDb API](http://www.omdbapi.com/). Built with industry-standard Android architecture — clean, scalable, and fully testable.

---

## 📱 Screenshots

| Movie List (Home) | Search Screen | Movie Detail |
|:-:|:-:|:-:|
| Browse popular movies with infinite scroll | Search any title with live results | Full movie details with cast, plot & ratings |

---

## ✨ Features

- **Home Screen** — Auto-loads popular movies on launch; filter inline without leaving the screen
- **Search Screen** — Debounced live search with real-time results
- **Detail Screen** — Full movie info: plot, director, cast, IMDb rating, runtime, awards
- **Infinite Scroll** — Automatically loads more results as you scroll (Paging 3)
- **Black & Green Theme** — Custom dark Material 3 design system throughout
- **Offline-graceful** — Clear error states for network failures at every load stage

---

## 🏗️ Architecture

This project follows **MVVM (Model-View-ViewModel)** combined with **Clean Architecture** principles, separating concerns across three distinct layers:

```
┌─────────────────────────────────────────────┐
│                   UI Layer                   │
│  Composables  ←→  ViewModels  ←→  UiState   │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│               Domain / Repository            │
│         MovieRepository (single source)      │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│                 Data Layer                   │
│    OmdbApiService  |  MoviePagingSource      │
└─────────────────────────────────────────────┘
```

### Why MVVM?
- **Separation of concerns** — UI knows nothing about network calls; ViewModels know nothing about Composables
- **Lifecycle safety** — ViewModels survive configuration changes (screen rotation); no data re-fetch on rotate
- **Testability** — Each layer can be tested independently with mocks

---

## 🗂️ Project Structure

```
app/src/main/java/com/movieapp/
│
├── MovieApp.kt                        # Application class (Hilt entry point)
├── MainActivity.kt                    # Single Activity host
│
├── data/
│   ├── api/
│   │   └── OmdbApiService.kt          # Retrofit interface — API contract
│   ├── model/
│   │   ├── Movie.kt                   # Search result model + response wrapper
│   │   └── MovieDetail.kt             # Full detail model
│   ├── paging/
│   │   └── MoviePagingSource.kt       # Paging 3 source — handles page loading
│   └── repository/
│       └── MovieRepository.kt         # Single source of truth for all data
│
├── di/
│   └── AppModule.kt                   # Hilt DI module — wires OkHttp/Retrofit/API
│
└── ui/
    ├── theme/
    │   ├── Color.kt                   # Black & green palette
    │   ├── Theme.kt                   # MaterialTheme dark color scheme
    │   └── Type.kt                    # Typography scale
    ├── navigation/
    │   └── NavGraph.kt                # Navigation routes and host
    ├── movielist/
    │   ├── MovieListScreen.kt         # Home screen composable
    │   └── MovieListViewModel.kt      # Home screen state + paging flow
    ├── search/
    │   ├── SearchScreen.kt            # Search screen composable
    │   └── SearchViewModel.kt         # Search state + debounced flow
    └── detail/
        ├── DetailScreen.kt            # Detail screen composable
        └── DetailViewModel.kt         # Detail state (Loading/Success/Error)

app/src/test/java/com/movieapp/
├── SearchViewModelTest.kt             # ViewModel unit tests
├── MovieRepositoryTest.kt             # Repository unit tests
└── MoviePagingSourceTest.kt           # PagingSource unit tests
```

---

## 🛠️ Tech Stack

### Core Language

| Technology | Version | Why |
|---|---|---|
| **Kotlin** | 1.9.23 | Concise syntax, null safety, coroutine support baked in. Eliminates entire classes of NullPointerException bugs at compile time. |

---

### UI — Jetpack Compose

| Technology | Why |
|---|---|
| **Jetpack Compose** | Declarative UI framework — UI is a pure function of state. No XML, no `findViewById`, no manual view updates. When state changes, only the affected Composable re-renders (recomposition). |
| **Material 3** | Google's latest design system. Provides `MaterialTheme`, color schemes, typography, and components (Card, TopAppBar, OutlinedTextField) that adapt to the theme automatically. |
| **Navigation Compose** | Type-safe navigation between screens using route strings. Manages the back stack automatically. `NavHost` + `composable()` replaces `FragmentManager` entirely. |

**Black & Green Theme** is implemented via a custom `darkColorScheme`:
```kotlin
private val MovieColorScheme = darkColorScheme(
    primary    = Green600,    // #00C853 — buttons, icons, accents
    background = Black,       // #000000 — screen backgrounds
    surface    = DarkSurface, // #1A1A1A — cards
    ...
)
```
Every Composable reads colors from `MaterialTheme.colorScheme.*` — changing the theme in one place re-skins the entire app.

---

### Asynchronous Programming — Coroutines & Flow

| Concept | Why |
|---|---|
| **Kotlin Coroutines** | Replaces callbacks and RxJava for async work. `suspend fun` reads like sequential code but runs asynchronously on a thread pool. No callback hell. |
| **StateFlow** | Hot stream of state that Composables observe via `collectAsStateWithLifecycle()`. When a new value is emitted, Compose re-renders the affected UI. |
| **Flow** | Cold stream used for the paging data pipeline. Transforms are lazy — no work happens until a collector subscribes. |
| **`flatMapLatest`** | Cancels the previous search coroutine when a new query arrives. Without this, an older slower request could overwrite a newer faster one. |
| **`debounce(400ms)`** | Waits 400ms after the user stops typing before firing an API call. Prevents a request on every single keystroke. |
| **`cachedIn(viewModelScope)`** | Caches paging data in the ViewModel scope. Scroll position and loaded pages survive recomposition and back-stack returns. |
| **`viewModelScope`** | Coroutines launched here are automatically cancelled when the ViewModel is cleared (screen is permanently gone). Zero manual lifecycle management. |

---

### Networking — Retrofit + OkHttp

| Technology | Why |
|---|---|
| **Retrofit 2** | Turns the OMDb REST API into a type-safe Kotlin interface. Annotate a function with `@GET` and Retrofit handles URL building, serialization, and execution. |
| **Gson Converter** | Automatically maps JSON response fields to Kotlin data classes using `@SerializedName`. |
| **OkHttp** | The HTTP engine underneath Retrofit. Handles connection pooling, timeouts, and interceptors. |
| **API Key Interceptor** | Instead of passing `apikey` in every function call, an `OkHttp Interceptor` appends it to every outgoing request automatically: |

```kotlin
val apiKeyInterceptor = Interceptor { chain ->
    val url = chain.request().url.newBuilder()
        .addQueryParameter("apikey", API_KEY)
        .build()
    chain.proceed(chain.request().newBuilder().url(url).build())
}
```

This keeps `OmdbApiService` clean and means the key is managed in one place.

---

### Pagination — Paging 3

| Concept | Why |
|---|---|
| **Paging 3** | Google's official pagination library. Handles page loading, load states (Loading/Error/NotLoading), and integrates directly with `LazyColumn` via `collectAsLazyPagingItems()`. |
| **`PagingSource`** | Defines how to load a single page. `load()` is called by Paging 3 when the user scrolls near the end of the current data. |
| **`PagingConfig(pageSize = 10)`** | Matches the OMDb API's 10-results-per-page limit. `prefetchDistance = 2` triggers the next page load when 2 items from the bottom are visible. |

**How infinite scroll works:**
```
User scrolls down
    → LazyColumn reaches prefetch threshold
    → Paging 3 calls MoviePagingSource.load(page + 1)
    → API returns next 10 movies
    → List grows automatically — no manual "load more" button needed
```

Load states are exposed per position:
- `loadState.refresh` — initial load or refresh
- `loadState.append` — loading next page at the bottom

---

### Dependency Injection — Hilt

| Concept | Why |
|---|---|
| **Hilt** | Google's DI framework built on Dagger. Removes the need to manually create and pass dependencies (OkHttpClient → Retrofit → ApiService → Repository → ViewModel). |
| **`@HiltAndroidApp`** | Marks `MovieApp` as the DI container root. Hilt generates the component hierarchy from this. |
| **`@AndroidEntryPoint`** | Enables Hilt injection in `MainActivity`. Required for Compose `hiltViewModel()` to work. |
| **`@HiltViewModel` + `@Inject`** | Hilt automatically provides the `MovieRepository` when creating `SearchViewModel`. No ViewModel factory boilerplate. |
| **`@Module` + `@InstallIn(SingletonComponent)`** | `AppModule` tells Hilt how to construct `OkHttpClient`, `Retrofit`, and `OmdbApiService`. Singleton scope ensures only one instance exists for the app's lifetime. |

**Without DI:**
```kotlin
// Manual wiring — fragile, hard to test, repeated everywhere
val okHttp = OkHttpClient.Builder()...build()
val retrofit = Retrofit.Builder().client(okHttp)...build()
val api = retrofit.create(OmdbApiService::class.java)
val repo = MovieRepository(api)
val vm = SearchViewModel(repo)
```

**With Hilt:**
```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository  // Hilt provides this
) : ViewModel()
```

---

### Image Loading — Coil

| Technology | Why |
|---|---|
| **Coil** | Kotlin-first image loading library. `AsyncImage()` is a Composable — no Glide targets or lifecycle management needed. Handles caching, memory pressure, and placeholder/error states automatically. |

---

### Testing

| Library | Why |
|---|---|
| **JUnit 4** | Standard test runner for unit tests. |
| **MockK** | Kotlin-native mocking library. `mockk()`, `coEvery { }`, `coVerify { }` work naturally with `suspend` functions — no Java ceremony. |
| **`kotlinx-coroutines-test`** | Provides `runTest` (replaces `runBlocking` in tests), `UnconfinedTestDispatcher`, and `Dispatchers.setMain()` to control coroutine execution in tests. |
| **`paging-testing`** | `TestPager` allows testing `PagingSource.load()` in isolation without a real `LazyColumn`. |
| **Turbine** | Stream testing library for `Flow`. `flow.test { assertEquals(...) }` instead of manual `collect` in coroutines. |

**Three test classes:**

| Class | Tests |
|---|---|
| `SearchViewModelTest` | ViewModel state changes, initial empty query, query update behaviour |
| `MovieRepositoryTest` | Success/failure/exception paths for `getMovieDetail`, verifies API call count |
| `MoviePagingSourceTest` | Page loading, empty results, error states, next/prev key calculation |

---

## 🌐 API Reference

Base URL: `https://www.omdbapi.com/`

| Endpoint | Purpose |
|---|---|
| `/?s={query}&page={n}&apikey={key}` | Search movies by title — returns 10 results per page |
| `/?i={imdbID}&apikey={key}` | Fetch full details for a single movie by IMDb ID |

The `imdbID` (e.g. `tt0372784`) returned in search results is used as the key to navigate to the detail screen.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 34
- JDK 17
- Internet connection (for Gradle sync & API calls)

### Setup

1. **Clone / open the project**
   ```bash
   # Clone
   git clone <repo-url>
   cd MovieApp
   ```
   Or open the folder directly in Android Studio via **File → Open**.

2. **Sync Gradle**
   Android Studio will prompt — click **Sync Now**. All dependencies download automatically.

3. **Run the app**
   Select a device or emulator and click ▶ Run (`Shift+F10`).

4. **Run unit tests**
   ```bash
   ./gradlew test
   ```
   Or right-click any test class in Android Studio → **Run Tests**.

### API Key

The key `c66fd0f9` is already configured in `OmdbApiService.kt`. To use your own:
```kotlin
// app/src/main/java/com/movieapp/data/api/OmdbApiService.kt
const val API_KEY = "your_key_here"
```
Get a free key at [omdbapi.com/apikey.aspx](http://www.omdbapi.com/apikey.aspx).

---

## 🧭 Navigation Flow

```
MovieListScreen  ──[tap movie]──▶  DetailScreen
     │
     └──[search icon]──▶  SearchScreen  ──[tap movie]──▶  DetailScreen
```

All screens use `NavController.popBackStack()` for back navigation, which integrates with the system back button automatically.

---

## 📦 Dependencies Summary

```groovy
// Compose BOM — pins all Compose library versions together
implementation platform('androidx.compose:compose-bom:2024.04.01')

// UI
implementation 'androidx.compose.material3:material3'
implementation 'androidx.navigation:navigation-compose:2.7.7'
implementation 'io.coil-kt:coil-compose:2.5.0'

// Architecture
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
implementation 'androidx.paging:paging-compose:3.2.1'

// DI
implementation 'com.google.dagger:hilt-android:2.51'
ksp 'com.google.dagger:hilt-compiler:2.51'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Testing
testImplementation 'io.mockk:mockk:1.13.9'
testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
```

---

## 🎨 Design System

| Token | Value | Usage |
|---|---|---|
| `primary` | `#00C853` (Green) | Buttons, icons, active borders, highlights |
| `background` | `#000000` (Black) | Screen backgrounds |
| `surface` | `#1A1A1A` (Dark grey) | Cards |
| `surfaceVariant` | `#212121` | Poster placeholder backgrounds |
| `onSurfaceVariant` | `#9E9E9E` | Subtitles, secondary text |
| `error` | `#CF6679` | Error messages |

---

## 📄 License

```
MIT License — free to use, modify, and distribute.
```

---

> Built with Kotlin • Jetpack Compose • Hilt • Coroutines • Paging 3 • Retrofit • OMDb API
