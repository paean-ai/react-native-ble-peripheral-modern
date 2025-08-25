import { NativeModules, Platform } from "react-native";

const LINKING_ERROR =
  `The package '@paean/react-native-ble-peripheral' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: "" }) +
  "- You rebuilt the app after installing the package\n" +
  "- You are not using Expo managed workflow\n";

const BLEPeripheral = NativeModules.BLEPeripheral
  ? NativeModules.BLEPeripheral
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

// BLE Characteristic Permissions
const BLEPermissions = {
  READABLE: 1,
  READABLE_ENCRYPTED: 2,
  READABLE_ENCRYPTED_MITM: 4,
  WRITABLE: 16,
  WRITABLE_ENCRYPTED: 32,
  WRITABLE_ENCRYPTED_MITM: 64,
  WRITABLE_SIGNED: 128,
  WRITABLE_SIGNED_MITM: 256,
};

// BLE Characteristic Properties
const BLEProperties = {
  BROADCAST: 1,
  READ: 2,
  WRITE_NO_RESPONSE: 4,
  WRITE: 8,
  NOTIFY: 16,
  INDICATE: 32,
  SIGNED_WRITE: 64,
  EXTENDED_PROPS: 128,
};

// Error codes for advertising failures
const BLEAdvertiseErrors = {
  DATA_TOO_LARGE: 1,
  TOO_MANY_ADVERTISERS: 2,
  ALREADY_STARTED: 3,
  INTERNAL_ERROR: 4,
  FEATURE_UNSUPPORTED: 5,
};

// Main export - the BLE Peripheral module
export default BLEPeripheral;

// Named exports for convenience
export {
  BLEPeripheral,
  BLEPermissions as Permissions,
  BLEProperties as Properties,
  BLEAdvertiseErrors as AdvertiseErrors,
};
