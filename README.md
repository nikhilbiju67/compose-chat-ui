
# Compose Multiplatform Chat Interface

https://github.com/user-attachments/assets/86a1fd48-7fcc-4fdc-9ebc-dbcc2633c429

This project is a Compose Multiplatform demonstration showcasing a chat interface. The application is designed to run seamlessly across Android, iOS, desktop, and web platforms, delivering a consistent user experience.

## Features

- **Chat Interface**: A responsive and intuitive chat UI designed using Jetpack Compose.
- **Multiplatform Support**: Runs on Android, iOS, desktop, and web platforms with platform-specific optimizations.
- **Audio Support**: Audio functionality works across all platforms, ensuring robust multimedia communication.
  - **Desktop Note**: For playing audio on desktop, please install [VLC Media Player](https://www.videolan.org/vlc/).
- **Record Functionality**:
  - Currently under development.
  - Will support audio recording across supported platforms when complete.




## Tech Stack

- **Jetpack Compose Multiplatform**: For building the user interface.
- **Kotlin Multiplatform**: For shared business logic across platforms.
- **Audio Frameworks**:
  - Platform-specific libraries for audio playback and recording.
  - Integration with VLC for desktop audio playback.
- **Modern UI/UX**: Built with Compose's declarative UI for a clean, maintainable codebase.

## Getting Started

### Prerequisites

- **Android**: Android Studio Arctic Fox or later.
- **iOS**: Xcode 13 or later.
- **Desktop**: Ensure VLC Media Player is installed.
- **Web**: A modern browser like Chrome, Firefox, or Safari.

### Running the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/compose-multiplatform-chat.git
   ```
2. Open the project in your preferred IDE (Android Studio recommended for Android and Desktop; Xcode for iOS).
3. Select the desired target platform and build the project.
4. Run the application.

## Future Plans

- **Enhanced Audio Features**:
  - Completion of the record functionality.
  - Support for advanced audio formats.
- **Customizable Themes**:
  - Add support for user-customizable themes and UI elements.
- **Improved Platform Integration**:
  - Leverage native features on iOS and Android for a richer experience.
- **WebRTC Integration**:
  - Real-time voice and video support for web and mobile platforms.

## Contribution

Contributions are welcome! If you would like to contribute:

1. Fork the repository.
2. Create a new branch for your feature/bug fix.
3. Commit your changes and push to your fork.
4. Create a pull request to the main repository.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- [Jetpack Compose Multiplatform](https://www.jetbrains.com/compose/) for enabling seamless UI development across platforms.
- [VLC Media Player](https://www.videolan.org/vlc/) for desktop audio playback.
- Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html), [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform), [Kotlin/Wasm](https://kotl.in/wasm/).

