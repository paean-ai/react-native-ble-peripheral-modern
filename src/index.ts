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

export interface BLEPeripheralInterface {
  /**
   * Set the name of the BLE peripheral device
   * @param name The device name to advertise
   */
  setName(name: string): void;

  /**
   * Add a GATT service to the peripheral
   * @param serviceUUID The UUID of the service
   * @param primary Whether this is a primary service
   */
  addService(serviceUUID: string, primary: boolean): void;

  /**
   * Add a characteristic to a service
   * @param serviceUUID The UUID of the service
   * @param characteristicUUID The UUID of the characteristic
   * @param permissions The permissions for the characteristic
   * @param properties The properties of the characteristic
   */
  addCharacteristicToService(
    serviceUUID: string,
    characteristicUUID: string,
    permissions: number,
    properties: number
  ): void;

  /**
   * Start advertising the BLE peripheral
   * @returns Promise that resolves when advertising starts
   */
  start(): Promise<void>;

  /**
   * Stop advertising the BLE peripheral
   */
  stop(): void;

  /**
   * Send notification to connected devices
   * @param serviceUUID The UUID of the service
   * @param characteristicUUID The UUID of the characteristic
   * @param data The data to send as byte array
   */
  sendNotificationToDevices(
    serviceUUID: string,
    characteristicUUID: string,
    data: number[]
  ): void;
}

// BLE Characteristic Permissions
export const BLEPermissions = {
  READABLE: 1,
  READABLE_ENCRYPTED: 2,
  READABLE_ENCRYPTED_MITM: 4,
  WRITABLE: 16,
  WRITABLE_ENCRYPTED: 32,
  WRITABLE_ENCRYPTED_MITM: 64,
  WRITABLE_SIGNED: 128,
  WRITABLE_SIGNED_MITM: 256,
} as const;

// BLE Characteristic Properties
export const BLEProperties = {
  BROADCAST: 1,
  READ: 2,
  WRITE_NO_RESPONSE: 4,
  WRITE: 8,
  NOTIFY: 16,
  INDICATE: 32,
  SIGNED_WRITE: 64,
  EXTENDED_PROPS: 128,
} as const;

// Error codes for advertising failures
export const BLEAdvertiseErrors = {
  DATA_TOO_LARGE: 1,
  TOO_MANY_ADVERTISERS: 2,
  ALREADY_STARTED: 3,
  INTERNAL_ERROR: 4,
  FEATURE_UNSUPPORTED: 5,
} as const;

export type BLEPermission =
  (typeof BLEPermissions)[keyof typeof BLEPermissions];
export type BLEProperty = (typeof BLEProperties)[keyof typeof BLEProperties];
export type BLEAdvertiseError =
  (typeof BLEAdvertiseErrors)[keyof typeof BLEAdvertiseErrors];

// Main export - the BLE Peripheral module
export default BLEPeripheral as BLEPeripheralInterface;

// Named exports for convenience
export {
  BLEPeripheral,
  BLEPermissions as Permissions,
  BLEProperties as Properties,
  BLEAdvertiseErrors as AdvertiseErrors,
};
