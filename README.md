# 📂 FilePicker

A modern, lightweight, and easy-to-use file picker library for Android applications.

[![](https://jitpack.io/v/dev.pranav/FilePicker.svg)](https://jitpack.io/#dev.pranav/FilePicker)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://www.android.com)

<div align="center">
  <img src="screenshots/1.webp" width="30%" alt="Screenshot 1"/>
  <img src="screenshots/2.webp" width="30%" alt="Screenshot 2"/>
  <img src="screenshots/3.webp" width="30%" alt="Screenshot 3"/>
</div>

## Features

- 🔍 **Easy File Browsing**: Navigate through device storage with a clean, intuitive interface
- 📁 **Folder Selection**: Select folders or individual files based on your needs
- 🔧 **File Filtering**: Filter files by extension to show only relevant content
- 🧩 **Material Design**: Modern UI consistent with Material Design guidelines
- 🔒 **Permission Handling**: Automatic handling of storage permission requests
- 🎨 **Customizable**: Easily customize the appearance to match your app's theme
- 📱 **Edge-to-Edge Support**: Full support for modern Android edge-to-edge displays

## Installation

### Step 1: Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Or for Gradle Kotlin DSL (build.gradle.kts):

```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}
```

### Step 2: Add the dependency

```gradle
dependencies {
    implementation 'com.github.PranavPurwar:filepicker:eb0d77f1d0'
}
```

Or for Gradle Kotlin DSL:

```kotlin
dependencies {
    implementation("com.github.PranavPurwar:filepicker:eb0d77f1d0")
}
```

## Usage

### Basic Implementation

```kotlin
// Create file picker options
val options = FilePickerOptions().apply {
    // Set to true if you want to select folders, false for files
    selectFolder = false

    title = "Select a File" // Optional: Set custom title for the dialog

    // Optional: Filter files by extension
    extensions = arrayOf("pdf", "doc", "txt")
}

// Create callback to handle selection
val callback = object : FilePickerCallback() {
    override fun onFileSelected(file: File) {
        // Handle the selected file
        Log.d("FilePicker", "Selected: ${file.absolutePath}")
    }

    override fun onFileSelectionCancelled(): Boolean {
        // Handle cancellation
        Log.d("FilePicker", "Selection cancelled")
        return true // Return true to dismiss the dialog
    }
}

// Show the file picker dialog
FilePickerDialogFragment(options, callback).show(supportFragmentManager, "filePicker")
```

### Set Custom Title

```kotlin
val options = FilePickerOptions().apply {
    title = "Select a File" // Set custom title for the dialog
}
```

### Select Folders

```kotlin
val options = FilePickerOptions().apply {
    selectFolder = true
}
```

### Filter Files by Extension

```kotlin
val options = FilePickerOptions().apply {
    // Only show images
    extensions = arrayOf("jpg", "jpeg", "png", "gif")
}
```

## Permissions

FilePicker automatically handles requesting the necessary storage permissions:

- For Android 11+ (API 30+): `MANAGE_EXTERNAL_STORAGE` permission
- For older versions: `READ_EXTERNAL_STORAGE` permission

Make sure to add these permissions to your AndroidManifest.xml:

```xml
<!-- For Android 10 and below -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <!-- For Android 11 and above -->
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
tools:ignore="ScopedStorage" />
```

## Customization

You can customize the appearance of the FilePicker by overriding the following styles in your app's
theme:

```xml

<style name="Theme.FilePicker.Dialog" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <!-- Override colors and styles here -->
    <item name="colorPrimary">@color/your_primary_color</item>
    <item name="colorOnPrimary">@color/your_on_primary_color</item>
    <!-- Add more customizations as needed -->
</style>
```

## Sample App

Check out the sample app in the `app` module for a complete implementation example.

## Requirements

- Android API level 21 (Android 5.0 Lollipop) or higher
- AndroidX

## License

```
MIT License

Copyright (c) 2025 Pranav

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
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/amazing-feature`)
3. Commit your Changes (`git commit -m 'Add some amazing feature'`)
4. Push to the Branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## About

Created with ❤️ by [Pranav](https://github.com/username)

## Donate

If you find this project helpful, consider supporting it by donating via PayPal:

[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://paypal.me/pranavpurwar)
