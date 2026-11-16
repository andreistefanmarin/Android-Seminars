# DMA2026 — Devices and Mobile Applications

Lecture examples and laboratory projects for the **Devices and Mobile Applications** course, academic year **2026–2027**.

The repository is a single Android Studio (Gradle) project made of independent application modules, all written in **Java** with XML layouts:

- `DMAxx` modules are the **lecture examples**; the number is the lecture they belong to.
- `G109x` modules are the **laboratory projects**, one per student group.

Each module is a standalone app that can be installed and run on its own.

## Contents

| Module | Type | Topic |
|---|---|---|
| [`DMA02`](#dma02--activity-lifecycle-and-implicit-intents) | Lecture | Activity lifecycle, `Application` class, implicit intents |
| [`DMA03`](#dma03--menus-navigation-drawer-and-dialogs) | Lecture | App bar, menus, navigation drawer, dialogs, views created in code |
| [`DMA05`](#dma05--input-controls-and-view-binding) | Lecture | Input controls, listeners, View Binding |
| [`DMA06`](#dma06--from-listview-to-recyclerview) | Lecture | `ListView`, custom adapters, `RecyclerView` |
| [`DMA07`](#dma07--asynchronous-work) | Lecture | Threads, `Handler`, executors, WorkManager, `ViewModel` |
| [`G1096` – `G1099`](#laboratory-projects-g1096--g1099) | Laboratory | Movie manager app, built step by step during the seminars |

## Lecture examples

### DMA02 — Activity lifecycle and implicit intents

A one-screen app for watching the lifecycle in Logcat.

- `MainActivity` logs every lifecycle callback (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`).
- `DMA02App` extends `Application` and implements `ActivityLifecycleCallbacks`, so the same events are also observed at application level. It also enables Material dynamic colors.
- The button starts an implicit `ACTION_SEND` intent through a chooser. Two alternatives, `ACTION_VIEW` (open a web page) and `ACTION_DIAL` (dial a number), are left commented out for comparison.
- The manifest declares an `ACTION_SEND` / `text/plain` intent filter, so the app itself shows up as a share target.

**Try it:** filter Logcat by `MainActivity` and `DMA02App`, then rotate the device, press Home and come back.

### DMA03 — Menus, navigation drawer and dialogs

Two activities covering app-level navigation and user feedback.

- `MainActivity`
    - `MaterialToolbar` with action items (search, share, settings) defined in `res/menu/top_app_bar.xml`.
    - `DrawerLayout` + `NavigationView` (`res/menu/drawer_menu.xml`), opened from the hamburger icon.
    - `OnBackPressedCallback` that closes the drawer before leaving the screen.
    - `ListView` filled from a string array, with a `PopupMenu` (open / rename / delete) on long press.
    - Edge-to-edge layout with window insets handling.
- `SecondActivity`
    - `AlertDialog` asking for confirmation.
    - A `Button` created in Java and added to the layout at runtime, with dp-to-pixel conversion.
    - `Snackbar` with a *Cancel* action that removes the button again, and a `Toast` on the negative path.

`SecondActivity` is declared in the manifest but is not opened from `MainActivity`. To run it, move the `MAIN` / `LAUNCHER` intent filter to it or start it with an explicit intent.

### DMA05 — Input controls and View Binding

One screen with the common input widgets, each wired to a listener that reports its new value in a `Toast`.

- Widgets: `Button`, `Switch`, `CheckBox`, `SeekBar`, `RadioGroup`, `ToggleButton`, `RatingBar`, plus `EditText`, `ImageView` and `ProgressBar` in the layout.
- Views are accessed through **View Binding** (`ActivityMainBinding`) instead of `findViewById`; see `buildFeatures { viewBinding = true }` in the module's build file.
- Listeners are written both as anonymous classes and as lambdas.

### DMA06 — From ListView to RecyclerView

The same data (the numbers 1–100, from `NumberData`) displayed in three steps, each one opened from the main menu.

| Step | Activity | What it shows |
|---|---|---|
| 1 | `SingleColumnActivity` | `ListView` with the built-in `ArrayAdapter`: one text value per row |
| 2 | `TwoColumnActivity` | `ListView` with a custom `BaseAdapter` (`NumberListAdapter`): decimal and binary columns, row reuse through `convertView` and the ViewHolder pattern |
| 3 | `RecyclerViewActivity` | The same two columns in a `RecyclerView` (`NumberRecyclerAdapter`), with a `LinearLayoutManager` and `DividerItemDecoration` |

Comparing `NumberListAdapter.getView()` with `onCreateViewHolder()` / `onBindViewHolder()` in `NumberRecyclerAdapter` shows what `RecyclerView` makes mandatory.

### DMA07 — Asynchronous work

A menu of seven demos about moving work off the main thread. Most of them log to Logcat under the tag `DMA07`.

| # | Activity | What it shows |
|---|---|---|
| 1 | `ThreadDemoActivity` | Heavy work on a raw `Thread`, results sent back with `runOnUiThread()`. A tap counter proves the UI stays responsive. |
| 2 | `HandlerDemoActivity` | `Handler` on the main `Looper`: `sendMessage()` with tagged messages versus `post(Runnable)` |
| 3 | `ExecutorDemoActivity` | `ExecutorService`: six tasks on a fixed pool of three threads |
| 4 | `ExportDemoActivity` | Writing a file in the background, then reporting its path on the UI thread |
| 5 | `WorkManagerDemoActivity` | `SyncWorker` scheduled as periodic work in `DMA07App` (every 15 minutes), plus a one-time request whose state is observed live |
| 6 | `ImageLoadDemoActivity` | Loading an image from a URL with Glide (needs an Internet connection) |
| 7 | `ProblemsDemoActivity` | Four problems of raw threads, each as a *broken* / *fixed* pair |

The four problems in demo 7:

| Problem | Broken | Fixed |
|---|---|---|
| Unbounded thread creation | 200 raw threads | 200 tasks on a pool of 4 |
| Cancellation | Loop with no way out | Cooperative cancellation (`volatile` flag + interrupt check) |
| Memory leak | Delayed callback holding the Activity | Static `Runnable` + `WeakReference`, callbacks removed in `onDestroy()` |
| Result delivered to a destroyed Activity | Thread updating the old instance after rotation | `ViewModel` + `LiveData` (`ProblemsViewModel`) |

**Try it:** for problems 3 and 4, rotate the device while the task is running and compare the two variants.

## Laboratory projects (G1096 – G1099)

The four modules contain the same application, written live with each group, so they differ only in small details. The app is a **movie manager** and brings together the topics from the lectures.

- `model/Movie` — `Serializable` class with one field for each type of input control: title, release date, genre, budget, duration, rating, watched, approval, on watchlist, poster URL.
- `activities/MainActivity` — list of movies in a `RecyclerView`, with a `FloatingActionButton` for adding a movie. Tapping a row opens it for editing.
- `activities/ItemActivity` — add / edit form built with Material components:

  | Field | Control |
    |---|---|
  | Title, budget, poster URL | `TextInputEditText` |
  | Release date | `DatePickerDialog` |
  | Genre | `Spinner` filled from a string array |
  | Duration | `SeekBar` |
  | Rating | `RatingBar` |
  | Approval | `RadioGroup` |
  | On watchlist | `CheckBox` |
  | Watched | `MaterialSwitch` |

- `adapter/MovieAdapter` — `RecyclerView.Adapter` with a ViewHolder, a card layout for each row (`movie_item.xml`) and an `OnMovieListener` interface that sends row clicks back to the activity.

Data moves between the two activities with the **Activity Result API** (`registerForActivityResult` and `ActivityResultContracts.StartActivityForResult`). The `Movie` travels as a `Serializable` extra, together with the position of the row when editing; a missing position means a new movie.

The projects are work in progress and follow the seminars. The list is kept in memory and starts with three sample movies. The row layout already contains a poster image and chips for approval, watched and watchlist, which the adapter does not fill yet.

## Project structure

```
DMA2026/
├── DMA02/ … DMA07/         lecture examples
├── G1096/ … G1099/         laboratory projects, one per group
├── gradle/
│   └── libs.versions.toml  shared dependency versions (version catalog)
├── build.gradle.kts        root build file
└── settings.gradle.kts     list of included modules
```

Each module follows the standard Android layout:

```
<module>/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    ├── java/…              activities, adapters, model classes
    └── res/                layouts, menus, drawables, strings, themes
```

## Build configuration

| | |
|---|---|
| Language | Java 11 |
| Android Gradle Plugin | 9.4.0 |
| Gradle | 9.9.0 (wrapper included) |
| Compile / target SDK | 37 |
| Minimum SDK | 33 |
| UI | XML layouts, Material Components, AppCompat |

Dependency versions are declared once, in `gradle/libs.versions.toml`, and referenced from the modules as `libs.…`.

`DMA07` is configured differently from the other modules: it uses a Groovy `build.gradle` with its own dependency versions, compile SDK 34, minimum SDK 24 and the `ro.ase.csie.dma07` package.

## Running an example

1. Clone the repository:
   ```
   git clone https://github.com/mihaidoinea/DMA2026.git
   ```
2. Open the root folder in Android Studio and wait for the Gradle sync to finish.
3. Choose a module (for example `DMA06` or `G1096`) from the run configurations list in the toolbar.
4. Run it on an emulator or a device with Android 13 (API 33) or newer.

From the command line, a single module can be installed with:

```
./gradlew :DMA06:installDebug
```