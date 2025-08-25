module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir:
          "../node_modules/@paean/react-native-ble-peripheral/android/",
        packageImportPath: "import com.himelbrand.ble.peripheral.RNBLEPackage;",
      },
      ios: {
        project: "ios/RNBLEPeripheral.xcodeproj",
      },
    },
  },
};
