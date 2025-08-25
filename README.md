# React Native BLE Peripheral Modern

[![npm version](https://badge.fury.io/js/%40paean%2Freact-native-ble-peripheral.svg)](https://github.com/paean-ai/react-native-ble-peripheral-modern)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Modern, TypeScript-enabled BLE peripheral simulator for React Native applications. This is a modernized fork of [himelbrand/react-native-ble-peripheral](https://github.com/himelbrand/react-native-ble-peripheral) with updated dependencies, React Native 0.70+ support, and enhanced features.

## 🚀 Features

- ✅ **Modern React Native Support**: Compatible with React Native 0.70+
- ✅ **Cross-Platform**: Supports both Android and iOS
- ✅ **TypeScript**: Full TypeScript support with complete type definitions
- ✅ **Auto-linking**: No manual linking required for React Native 0.60+
- ✅ **Modern Android**: Updated to latest Android APIs and Gradle
- ✅ **iOS Support**: Includes proper podspec for CocoaPods integration
- ✅ **Error Handling**: Comprehensive error codes and handling
- ✅ **Performance**: Optimized for modern React Native architecture

## 📱 Platform Support

| Platform | Support | Notes                  |
| -------- | ------- | ---------------------- |
| Android  | ✅ Full | API 21+ (Android 5.0+) |
| iOS      | ✅ Full | iOS 12.0+              |

## 🔧 Installation

### Using npm

```bash
npm install https://github.com/paean-ai/react-native-ble-peripheral-modern.git
```

### Using yarn

```bash
yarn add https://github.com/paean-ai/react-native-ble-peripheral-modern.git
```

### iOS Setup

```bash
cd ios && pod install
```

### Android Setup

No additional setup required - auto-linking handles everything!

## 📋 Permissions

### Android

Add to your `android/app/src/main/AndroidManifest.xml`:

```xml
<!-- Bluetooth permissions for Android 11 and below -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />

<!-- Location permissions (required for BLE on all Android versions) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Bluetooth permissions for Android 12+ (API 31+) -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
```

### iOS

Add to your `ios/YourApp/Info.plist`:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to simulate BLE peripheral devices for testing purposes.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to simulate BLE peripheral devices for testing purposes.</string>
```

## 🎯 Quick Start

```typescript
import BLEPeripheral, {
  BLEPermissions,
  BLEProperties,
} from "react-native-ble-peripheral";

// Set device name
BLEPeripheral.setName("MyTestDevice");

// Add a service
const serviceUUID = "12345678-1234-1234-1234-123456789abc";
BLEPeripheral.addService(serviceUUID, true);

// Add a characteristic
const characteristicUUID = "12345678-1234-1234-1234-123456789abd";
BLEPeripheral.addCharacteristicToService(
  serviceUUID,
  characteristicUUID,
  BLEPermissions.READABLE | BLEPermissions.WRITABLE,
  BLEProperties.READ | BLEProperties.WRITE | BLEProperties.NOTIFY
);

// Start advertising
BLEPeripheral.start()
  .then(() => {
    console.log("✅ BLE advertising started");
  })
  .catch((error) => {
    console.error("❌ Failed to start advertising:", error);
  });

// Send notification to connected devices
const data = [0x01, 0x02, 0x03, 0x04];
BLEPeripheral.sendNotificationToDevices(serviceUUID, characteristicUUID, data);

// Stop advertising
BLEPeripheral.stop();
```

## 📚 API Reference

### Methods

#### `setName(name: string): void`

Sets the device name for advertising.

#### `addService(serviceUUID: string, primary: boolean): void`

Adds a GATT service to the peripheral.

#### `addCharacteristicToService(serviceUUID: string, characteristicUUID: string, permissions: number, properties: number): void`

Adds a characteristic to a service.

#### `start(): Promise<void>`

Starts BLE advertising. Returns a promise that resolves when advertising starts successfully.

#### `stop(): void`

Stops BLE advertising.

#### `sendNotificationToDevices(serviceUUID: string, characteristicUUID: string, data: number[]): void`

Sends notification data to all connected devices.

### Constants

#### BLE Permissions

```typescript
export const BLEPermissions = {
  READABLE: 1,
  READABLE_ENCRYPTED: 2,
  READABLE_ENCRYPTED_MITM: 4,
  WRITABLE: 16,
  WRITABLE_ENCRYPTED: 32,
  WRITABLE_ENCRYPTED_MITM: 64,
  WRITABLE_SIGNED: 128,
  WRITABLE_SIGNED_MITM: 256,
};
```

#### BLE Properties

```typescript
export const BLEProperties = {
  BROADCAST: 1,
  READ: 2,
  WRITE_NO_RESPONSE: 4,
  WRITE: 8,
  NOTIFY: 16,
  INDICATE: 32,
  SIGNED_WRITE: 64,
  EXTENDED_PROPS: 128,
};
```

#### Advertising Error Codes

```typescript
export const BLEAdvertiseErrors = {
  DATA_TOO_LARGE: 1, // Advertising data too large (>31 bytes)
  TOO_MANY_ADVERTISERS: 2, // No advertising instance available
  ALREADY_STARTED: 3, // Advertising already started
  INTERNAL_ERROR: 4, // Internal error
  FEATURE_UNSUPPORTED: 5, // Feature not supported on this platform
};
```

## 🔍 Error Handling

```typescript
BLEPeripheral.start().catch((error) => {
  switch (error.code) {
    case BLEAdvertiseErrors.DATA_TOO_LARGE:
      console.error("Advertising data is too large");
      break;
    case BLEAdvertiseErrors.TOO_MANY_ADVERTISERS:
      console.error("Too many advertisers active");
      break;
    case BLEAdvertiseErrors.ALREADY_STARTED:
      console.error("Advertising already started");
      break;
    default:
      console.error("Unknown advertising error:", error);
  }
});
```

## 🎨 TypeScript Support

This library includes complete TypeScript definitions:

```typescript
interface BLEPeripheralInterface {
  setName(name: string): void;
  addService(serviceUUID: string, primary: boolean): void;
  addCharacteristicToService(
    serviceUUID: string,
    characteristicUUID: string,
    permissions: number,
    properties: number
  ): void;
  start(): Promise<void>;
  stop(): void;
  sendNotificationToDevices(
    serviceUUID: string,
    characteristicUUID: string,
    data: number[]
  ): void;
}
```

## 🔄 Migration from Original Library

If you're migrating from `react-native-ble-peripheral`:

1. **Remove old library**:

   ```bash
   npm uninstall react-native-ble-peripheral
   ```

2. **Install modern version**:

   ```bash
   npm install https://github.com/paean-ai/react-native-ble-peripheral-modern.git
   ```

3. **Remove manual linking** (if you had it):

   - Remove from `android/settings.gradle`
   - Remove from `android/app/build.gradle`
   - Remove from `MainApplication.java` or `MainApplication.kt`

4. **Update imports** (optional - import path remains the same):
   ```typescript
   // Works the same as before
   import BLEPeripheral from "react-native-ble-peripheral";
   ```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your-username/react-native-ble-peripheral-modern.git`
3. Install dependencies: `npm install`
4. Make your changes
5. Test your changes
6. Submit a pull request

## 📄 License

MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Original work by [Omri Himelbrand](https://github.com/himelbrand)
- Modernized and maintained by [Paean AI](https://github.com/paean-ai)

## 📞 Support

- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/paean-ai/react-native-ble-peripheral-modern/issues)
- 💡 **Feature Requests**: [GitHub Issues](https://github.com/paean-ai/react-native-ble-peripheral-modern/issues)
- 📚 **Documentation**: [GitHub Wiki](https://github.com/paean-ai/react-native-ble-peripheral-modern/wiki)

---

**Made with ❤️ for the React Native community**
