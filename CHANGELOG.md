# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.1-modern] - 2024-08-25

### 🚀 Added

- **Complete React Native 0.70+ support** with modern auto-linking
- **Full TypeScript definitions** with comprehensive type safety
- **Cross-platform support** for both Android and iOS
- **Modern Android build system** with Gradle 8.0+ compatibility
- **iOS CocoaPods integration** with proper podspec file
- **Comprehensive error handling** with detailed error codes
- **Performance optimizations** for modern React Native architecture
- **Complete documentation** with examples and migration guide

### 🔧 Changed

- **BREAKING**: Updated minimum React Native version to 0.70.0
- **BREAKING**: Updated minimum Android API to 21 (Android 5.0+)
- **BREAKING**: Updated minimum iOS version to 12.0+
- Updated Android `compileSdkVersion` from 23 to 34
- Replaced deprecated `compile` with `implementation` in Gradle
- Migrated from Android Support Library to AndroidX
- Updated package structure to support modern React Native

### 🐛 Fixed

- Fixed Gradle build issues with modern Android projects
- Fixed auto-linking configuration for React Native 0.60+
- Fixed iOS compilation issues with modern Xcode versions
- Fixed TypeScript compatibility issues
- Fixed permission handling for Android 12+ (API 31+)

### 📚 Documentation

- Comprehensive README with installation and usage examples
- TypeScript API documentation with complete type definitions
- Migration guide from original library
- Example implementations for various use cases
- Platform-specific setup instructions
- Troubleshooting guide with common issues and solutions

### 🔄 Migration from Original Library

If migrating from `react-native-ble-peripheral`:

1. Remove old library: `npm uninstall react-native-ble-peripheral`
2. Install modern version: `npm install https://github.com/paean-ai/react-native-ble-peripheral-modern.git`
3. Remove manual linking code (auto-linking handles everything)
4. Update Android permissions for API 31+
5. Test on both platforms

### 🏗️ Technical Details

- **Gradle**: Updated to 8.0+ compatibility
- **Android API**: Supports API 21-34
- **iOS**: Supports iOS 12.0+ with proper Swift integration
- **TypeScript**: Full type definitions with strict mode support
- **React Native**: Auto-linking compatible with RN 0.60+

### 📦 Package Changes

- Package name: `react-native-ble-peripheral` (same as original)
- GitHub repository: `paean-ai/react-native-ble-peripheral-modern`
- License: MIT (same as original)
- Dependencies: Updated to latest stable versions

### 🙏 Acknowledgments

- Original work by [Omri Himelbrand](https://github.com/himelbrand)
- Modernization by [Paean AI](https://github.com/paean-ai)
- React Native community for feedback and testing

---

## [2.0.1] - Original Release

- Original implementation by Omri Himelbrand
- Basic Android and iOS support
- Manual linking required
- Limited documentation

## [2.0.0] - Original Release

- Initial iOS support added
- Android implementation
- Basic BLE peripheral functionality

## [1.2.0] - Original Release

- Bug fixes and improvements
- Initial stable release
