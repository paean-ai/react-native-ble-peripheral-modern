# React Native BLE Peripheral Modern - Examples

This document provides practical examples of using the modern BLE peripheral library.

## 🎯 Basic Usage Example

```typescript
import React, { useEffect, useState } from "react";
import { View, Text, Button, Alert } from "react-native";
import BLEPeripheral, {
  BLEPermissions,
  BLEProperties,
  BLEAdvertiseErrors,
} from "react-native-ble-peripheral";

const BLEPeripheralExample: React.FC = () => {
  const [isAdvertising, setIsAdvertising] = useState(false);
  const [deviceName] = useState("MyTestDevice");

  const SERVICE_UUID = "12345678-1234-1234-1234-123456789abc";
  const CHARACTERISTIC_UUID = "12345678-1234-1234-1234-123456789abd";

  const setupBLEPeripheral = () => {
    try {
      // Set device name
      BLEPeripheral.setName(deviceName);

      // Add a service
      BLEPeripheral.addService(SERVICE_UUID, true);

      // Add a characteristic with read, write, and notify properties
      BLEPeripheral.addCharacteristicToService(
        SERVICE_UUID,
        CHARACTERISTIC_UUID,
        BLEPermissions.READABLE | BLEPermissions.WRITABLE,
        BLEProperties.READ | BLEProperties.WRITE | BLEProperties.NOTIFY
      );

      console.log("✅ BLE peripheral setup completed");
    } catch (error) {
      console.error("❌ Failed to setup BLE peripheral:", error);
      Alert.alert("Setup Error", "Failed to setup BLE peripheral");
    }
  };

  const startAdvertising = async () => {
    try {
      await BLEPeripheral.start();
      setIsAdvertising(true);
      console.log("✅ BLE advertising started");
      Alert.alert("Success", "BLE advertising started");
    } catch (error: any) {
      console.error("❌ Failed to start advertising:", error);

      let errorMessage = "Unknown error";
      switch (error.code) {
        case BLEAdvertiseErrors.DATA_TOO_LARGE:
          errorMessage = "Advertising data is too large";
          break;
        case BLEAdvertiseErrors.TOO_MANY_ADVERTISERS:
          errorMessage = "Too many advertisers active";
          break;
        case BLEAdvertiseErrors.ALREADY_STARTED:
          errorMessage = "Advertising already started";
          break;
        case BLEAdvertiseErrors.INTERNAL_ERROR:
          errorMessage = "Internal BLE error";
          break;
        case BLEAdvertiseErrors.FEATURE_UNSUPPORTED:
          errorMessage = "BLE advertising not supported on this device";
          break;
      }

      Alert.alert("Advertising Error", errorMessage);
    }
  };

  const stopAdvertising = () => {
    try {
      BLEPeripheral.stop();
      setIsAdvertising(false);
      console.log("🛑 BLE advertising stopped");
      Alert.alert("Stopped", "BLE advertising stopped");
    } catch (error) {
      console.error("❌ Failed to stop advertising:", error);
    }
  };

  const sendNotification = () => {
    if (!isAdvertising) {
      Alert.alert("Error", "Must be advertising before sending notifications");
      return;
    }

    try {
      // Send some test data
      const testData = [0x01, 0x02, 0x03, 0x04, 0x05];
      BLEPeripheral.sendNotificationToDevices(
        SERVICE_UUID,
        CHARACTERISTIC_UUID,
        testData
      );
      console.log("📤 Notification sent:", testData);
      Alert.alert("Sent", "Notification sent to connected devices");
    } catch (error) {
      console.error("❌ Failed to send notification:", error);
      Alert.alert("Send Error", "Failed to send notification");
    }
  };

  useEffect(() => {
    setupBLEPeripheral();

    // Cleanup on unmount
    return () => {
      if (isAdvertising) {
        BLEPeripheral.stop();
      }
    };
  }, []);

  return (
    <View style={{ flex: 1, padding: 20, justifyContent: "center" }}>
      <Text style={{ fontSize: 24, textAlign: "center", marginBottom: 20 }}>
        BLE Peripheral Test
      </Text>

      <Text style={{ textAlign: "center", marginBottom: 20 }}>
        Device Name: {deviceName}
      </Text>

      <Text style={{ textAlign: "center", marginBottom: 20 }}>
        Status: {isAdvertising ? "📡 Advertising" : "⭕ Stopped"}
      </Text>

      <Button
        title={isAdvertising ? "Stop Advertising" : "Start Advertising"}
        onPress={isAdvertising ? stopAdvertising : startAdvertising}
      />

      <View style={{ marginTop: 20 }}>
        <Button
          title="Send Test Notification"
          onPress={sendNotification}
          disabled={!isAdvertising}
        />
      </View>
    </View>
  );
};

export default BLEPeripheralExample;
```

## 🏥 Smart Health Device Simulator

```typescript
import React, { useEffect, useState } from "react";
import BLEPeripheral, {
  BLEPermissions,
  BLEProperties,
} from "react-native-ble-peripheral";

const HealthDeviceSimulator: React.FC = () => {
  const [heartRate, setHeartRate] = useState(72);
  const [isSimulating, setIsSimulating] = useState(false);

  // Standard Heart Rate Service UUID
  const HEART_RATE_SERVICE_UUID = "180D";
  const HEART_RATE_MEASUREMENT_CHAR_UUID = "2A37";

  const setupHealthDevice = () => {
    BLEPeripheral.setName("HealthDevice-Simulator");

    // Add Heart Rate Service
    BLEPeripheral.addService(HEART_RATE_SERVICE_UUID, true);

    // Add Heart Rate Measurement Characteristic
    BLEPeripheral.addCharacteristicToService(
      HEART_RATE_SERVICE_UUID,
      HEART_RATE_MEASUREMENT_CHAR_UUID,
      BLEPermissions.READABLE,
      BLEProperties.READ | BLEProperties.NOTIFY
    );
  };

  const startSimulation = async () => {
    try {
      await BLEPeripheral.start();
      setIsSimulating(true);

      // Start simulating heart rate data
      const interval = setInterval(() => {
        // Simulate realistic heart rate variations
        const newHeartRate = 60 + Math.random() * 40; // 60-100 BPM
        setHeartRate(Math.round(newHeartRate));

        // Send heart rate data (simplified format)
        const heartRateData = [
          0x00, // Flags: Heart Rate Value Format bit = 0 (UINT8)
          Math.round(newHeartRate), // Heart Rate Measurement Value
        ];

        BLEPeripheral.sendNotificationToDevices(
          HEART_RATE_SERVICE_UUID,
          HEART_RATE_MEASUREMENT_CHAR_UUID,
          heartRateData
        );
      }, 1000); // Send every second

      // Store interval for cleanup
      (global as any).heartRateInterval = interval;
    } catch (error) {
      console.error("Failed to start health device simulation:", error);
    }
  };

  const stopSimulation = () => {
    BLEPeripheral.stop();
    setIsSimulating(false);

    if ((global as any).heartRateInterval) {
      clearInterval((global as any).heartRateInterval);
    }
  };

  useEffect(() => {
    setupHealthDevice();

    return () => {
      if (isSimulating) {
        stopSimulation();
      }
    };
  }, []);

  return (
    <View style={{ flex: 1, padding: 20, justifyContent: "center" }}>
      <Text style={{ fontSize: 24, textAlign: "center", marginBottom: 20 }}>
        Health Device Simulator
      </Text>

      <Text style={{ fontSize: 48, textAlign: "center", marginBottom: 20 }}>
        ❤️ {heartRate} BPM
      </Text>

      <Button
        title={isSimulating ? "Stop Simulation" : "Start Simulation"}
        onPress={isSimulating ? stopSimulation : startSimulation}
      />
    </View>
  );
};
```

## 🎮 Game Controller Simulator

```typescript
import React, { useState } from "react";
import BLEPeripheral, {
  BLEPermissions,
  BLEProperties,
} from "react-native-ble-peripheral";

const GameControllerSimulator: React.FC = () => {
  const [isConnected, setIsConnected] = useState(false);

  // Custom Game Controller Service
  const CONTROLLER_SERVICE_UUID = "12345678-1234-1234-1234-123456789def";
  const BUTTON_CHAR_UUID = "12345678-1234-1234-1234-123456789de0";
  const JOYSTICK_CHAR_UUID = "12345678-1234-1234-1234-123456789de1";

  const setupController = () => {
    BLEPeripheral.setName("GameController-Pro");

    BLEPeripheral.addService(CONTROLLER_SERVICE_UUID, true);

    // Button presses characteristic
    BLEPeripheral.addCharacteristicToService(
      CONTROLLER_SERVICE_UUID,
      BUTTON_CHAR_UUID,
      BLEPermissions.READABLE,
      BLEProperties.READ | BLEProperties.NOTIFY
    );

    // Joystick position characteristic
    BLEPeripheral.addCharacteristicToService(
      CONTROLLER_SERVICE_UUID,
      JOYSTICK_CHAR_UUID,
      BLEPermissions.READABLE,
      BLEProperties.READ | BLEProperties.NOTIFY
    );
  };

  const sendButtonPress = (buttonId: number) => {
    if (!isConnected) return;

    const buttonData = [buttonId, 0x01]; // Button ID + Press state
    BLEPeripheral.sendNotificationToDevices(
      CONTROLLER_SERVICE_UUID,
      BUTTON_CHAR_UUID,
      buttonData
    );

    // Send button release after 100ms
    setTimeout(() => {
      const releaseData = [buttonId, 0x00];
      BLEPeripheral.sendNotificationToDevices(
        CONTROLLER_SERVICE_UUID,
        BUTTON_CHAR_UUID,
        releaseData
      );
    }, 100);
  };

  const sendJoystickPosition = (x: number, y: number) => {
    if (!isConnected) return;

    // Convert to 8-bit values (-128 to 127)
    const xByte = Math.max(-128, Math.min(127, Math.round(x * 127)));
    const yByte = Math.max(-128, Math.min(127, Math.round(y * 127)));

    const joystickData = [
      xByte + 128, // Convert to 0-255 range
      yByte + 128,
    ];

    BLEPeripheral.sendNotificationToDevices(
      CONTROLLER_SERVICE_UUID,
      JOYSTICK_CHAR_UUID,
      joystickData
    );
  };

  // ... rest of component implementation
};
```

## 🔗 Integration with Existing Apps

### Smart Glasses Simulator Integration

```typescript
// This is how you can integrate with the Smart Glasses simulator
// from the main PaeanOS application

import { SimulatedGlassesService } from "./services/SimulatedGlassesService";

const GlassesApp: React.FC = () => {
  const [glassesService] = useState(() => new SimulatedGlassesService());

  useEffect(() => {
    // Initialize the glasses service
    glassesService
      .initialize()
      .then(() => {
        console.log("Glasses service initialized");

        // Start advertising as smart glasses
        return glassesService.startAdvertising();
      })
      .then(() => {
        console.log("Smart glasses advertising started");
      })
      .catch((error) => {
        console.error("Failed to setup smart glasses:", error);
      });

    return () => {
      glassesService.cleanup();
    };
  }, []);

  // ... rest of component
};
```

## 📱 Testing with Central Devices

To test your BLE peripheral, you can use:

1. **nRF Connect** (iOS/Android) - Professional BLE scanner app
2. **BLE Scanner** (Android) - Simple scanning app
3. **LightBlue Explorer** (iOS) - Apple's BLE development tool
4. **Custom React Native Central App** using `react-native-ble-plx`

### Example Central App Testing Code

```typescript
import { BleManager } from "react-native-ble-plx";

const testPeripheral = async () => {
  const manager = new BleManager();

  // Scan for your peripheral
  manager.startDeviceScan(null, null, (error, device) => {
    if (error) {
      console.error("Scan error:", error);
      return;
    }

    if (device?.name === "MyTestDevice") {
      console.log("Found peripheral:", device);
      manager.stopDeviceScan();

      // Connect and test services
      // ... connection logic
    }
  });
};
```

## 🚀 Performance Tips

1. **Limit Notification Frequency**: Don't send notifications faster than 10Hz (100ms intervals)
2. **Optimize Data Size**: Keep notification payloads under 20 bytes for best compatibility
3. **Handle Disconnections**: Always cleanup resources when devices disconnect
4. **Test on Real Devices**: Simulators may not accurately represent BLE behavior

## 🐛 Troubleshooting

### Common Issues

1. **"Advertising failed"**: Check that Bluetooth is enabled and permissions are granted
2. **"Service not found"**: Ensure services are added before starting advertising
3. **"Notification failed"**: Verify the characteristic has NOTIFY property enabled
4. **"Connection drops"**: Check for proper cleanup and resource management

### Debug Logging

Enable debug logging to troubleshoot issues:

```typescript
// Add this to see detailed BLE logs
if (__DEV__) {
  console.log("BLE Debug mode enabled");
}
```

For more examples and advanced usage, check the [main documentation](README.md).
