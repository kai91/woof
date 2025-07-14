# Woof - Dog Breed Quiz App 🐕

A fun and interactive Android app that tests your knowledge of dog breeds through a photo quiz format. Users are presented with a series of dog photos and must choose the correct breed from multiple choices. At the end of the quiz, users can see their results.

## Demo

https://github.com/kai91/woof/blob/main/output/recording.mp4

[Download Debug APK](output/app-debug.apk)

## Technical Overview

The app is built with modern Android development practices and libraries:

### Architecture & UI
- **UI Framework**: Jetpack Compose for a modern, declarative UI approach
- **Architecture Pattern**: Basic MVVM with Android ViewModel
- **State Management**: ViewModel with StateFlow

### Dependencies & Tools
- **Dependency Injection**: Hilt for clean and efficient DI
- **Image Loading**: Coil for efficient image loading and caching & Lottie
- **Networking**: Retrofit for API communication
- **Concurrency**: Kotlin Coroutines & Flow

### Testing
The project includes both unit tests and UI tests:
- Unit tests for business logic (QuizGenerator, ViewModels)
- Basic Compose UI test for StartActivity

## Project Structure

```
app/
├── api/           # API service and response models
├── di/            # Dependency injection modules
├── model/         # Data models
├── quiz/          # Quiz generation logic
├── repository/    # Data repository layer
├── screen/        # UI screens and ViewModels
└── ui/            # UI theme and components
```

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Run the app on an emulator or physical device

Or simply download and install the [debug APK](output/app-debug.apk) directly on your device. 