module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir: "android/",
        packageImportPath: "import com.himelbrand.ble.peripheral.RNBLEPackage;",
      },
      ios: {
        podspecPath: "react-native-ble-peripheral.podspec",
      },
    },
  },
};
