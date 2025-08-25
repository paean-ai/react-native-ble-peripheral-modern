package com.himelbrand.ble.peripheral;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;



/**
 * {@link NativeModule} that allows JS to open the default browser
 * for an url.
 */
public class RNBLEModule extends ReactContextBaseJavaModule{

    ReactApplicationContext reactContext;
    HashMap<String, BluetoothGattService> servicesMap;
    HashSet<BluetoothDevice> mBluetoothDevices;
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothGattServer mGattServer;
    BluetoothLeAdvertiser advertiser;
    AdvertiseCallback advertisingCallback;
    String name;
    boolean advertising;
    private Context context;

    public RNBLEModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.context = reactContext;
        this.servicesMap = new HashMap<String, BluetoothGattService>();
        this.advertising = false;
        this.name = "RN_BLE";
    }

    @Override
    public String getName() {
        return "BLEPeripheral";
    }

    // Helper method to send events to JavaScript
    private void sendEvent(String eventName, WritableMap params) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
        }
    }

    @ReactMethod
    public void setName(String name) {
        this.name = name;
        Log.i("RNBLEModule", "name set to " + name);
    }

    @ReactMethod
    public void addService(String uuid, Boolean primary) {
        UUID SERVICE_UUID = UUID.fromString(uuid);
        int type = primary ? BluetoothGattService.SERVICE_TYPE_PRIMARY : BluetoothGattService.SERVICE_TYPE_SECONDARY;
        BluetoothGattService tempService = new BluetoothGattService(SERVICE_UUID, type);
        if(!this.servicesMap.containsKey(uuid))
            this.servicesMap.put(uuid, tempService);
    }

    @ReactMethod
    public void addCharacteristicToService(String serviceUUID, String uuid, Integer permissions, Integer properties) {
        UUID CHAR_UUID = UUID.fromString(uuid);
        BluetoothGattCharacteristic tempChar = new BluetoothGattCharacteristic(CHAR_UUID, properties, permissions);
        this.servicesMap.get(serviceUUID).addCharacteristic(tempChar);
    }

    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    mBluetoothDevices.add(device);
                    
                    // Send connection event to JavaScript
                    WritableMap connectionData = Arguments.createMap();
                    connectionData.putString("deviceId", device.getAddress());
                    connectionData.putString("deviceName", device.getName());
                    connectionData.putDouble("timestamp", System.currentTimeMillis() / 1000.0);
                    sendEvent("onDeviceConnected", connectionData);
                    
                    Log.i("RNBLEModule", "📱 [Android] Device connected event sent: " + device.getAddress());
                    
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    mBluetoothDevices.remove(device);
                    
                    // Send disconnection event to JavaScript
                    WritableMap disconnectionData = Arguments.createMap();
                    disconnectionData.putString("deviceId", device.getAddress());
                    disconnectionData.putString("deviceName", device.getName());
                    disconnectionData.putDouble("timestamp", System.currentTimeMillis() / 1000.0);
                    sendEvent("onDeviceDisconnected", disconnectionData);
                    
                    Log.i("RNBLEModule", "📱 [Android] Device disconnected event sent: " + device.getAddress());
                }
            } else {
                mBluetoothDevices.remove(device);
                
                // Send disconnection event for failed connections
                WritableMap disconnectionData = Arguments.createMap();
                disconnectionData.putString("deviceId", device.getAddress());
                disconnectionData.putString("deviceName", device.getName());
                disconnectionData.putString("error", "Connection failed with status: " + status);
                disconnectionData.putDouble("timestamp", System.currentTimeMillis() / 1000.0);
                sendEvent("onDeviceDisconnected", disconnectionData);
                
                Log.w("RNBLEModule", "📱 [Android] Device connection failed, disconnection event sent: " + device.getAddress());
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            if (offset != 0) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
                        /* value (optional) */ null);
                return;
            }
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                    offset, characteristic.getValue());
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                    responseNeeded, offset, value);
            characteristic.setValue(value);
            
            // Create event data for JavaScript
            WritableMap writeData = Arguments.createMap();
            WritableArray data = Arguments.createArray();
            for (byte b : value) {
                data.pushInt((int) b & 0xFF); // Convert to unsigned byte
            }
            writeData.putArray("data", data);
            writeData.putString("characteristicUUID", characteristic.getUuid().toString());
            writeData.putString("deviceId", device.getAddress());
            writeData.putString("deviceName", device.getName());
            writeData.putDouble("timestamp", System.currentTimeMillis() / 1000.0);
            
            // Send event to JavaScript
            sendEvent("onCharacteristicWrite", writeData);
            
            Log.i("RNBLEModule", "📥 [Android] Characteristic write event sent to JavaScript: " + 
                  characteristic.getUuid().toString() + ", data length: " + value.length);
            
            if (responseNeeded) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }
        }
    };

    @ReactMethod
    public void start(final Promise promise){
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter.setName(this.name);
        // Ensures Bluetooth is available on the device and it is enabled. If not,
// displays a dialog requesting user permission to enable Bluetooth.

        mBluetoothDevices = new HashSet<>();
        mGattServer = mBluetoothManager.openGattServer(reactContext, mGattServerCallback);
        for (BluetoothGattService service : this.servicesMap.values()) {
            mGattServer.addService(service);
        }
        advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();


        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)  // Don't include device name to save space
                .setIncludeTxPowerLevel(false); // Don't include TX power to save space
        
        // Add only one service UUID to stay within 31-byte limit
        if (!this.servicesMap.isEmpty()) {
            BluetoothGattService firstService = this.servicesMap.values().iterator().next();
            dataBuilder.addServiceUuid(new ParcelUuid(firstService.getUuid()));
            Log.i("RNBLEModule", "Adding service UUID: " + firstService.getUuid().toString());
        }
        
        AdvertiseData data = dataBuilder.build();
        Log.i("RNBLEModule", "Advertising data: " + data.toString());
        
        // Use scan response to include device name (additional 31 bytes)
        AdvertiseData scanResponse = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)  // Include device name in scan response
                .build();
        Log.i("RNBLEModule", "Scan response data: " + scanResponse.toString());

        advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                advertising = true;
                promise.resolve("Success, Started Advertising");

            }

            @Override
            public void onStartFailure(int errorCode) {
                advertising = false;
                Log.e("RNBLEModule", "Advertising onStartFailure: " + errorCode);
                promise.reject("Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising(settings, data, scanResponse, advertisingCallback);

    }
    @ReactMethod
    public void stop(){
        if (mGattServer != null) {
            mGattServer.close();
        }
        if (mBluetoothAdapter !=null && mBluetoothAdapter.isEnabled() && advertiser != null) {
            // If stopAdvertising() gets called before close() a null
            // pointer exception is raised.
            advertiser.stopAdvertising(advertisingCallback);
        }
        advertising = false;
    }
    @ReactMethod
    public void sendNotificationToDevices(String serviceUUID,String charUUID,ReadableArray message) {
        byte[] decoded = new byte[message.size()];
        for (int i = 0; i < message.size(); i++) {
            decoded[i] = new Integer(message.getInt(i)).byteValue();
        }
        BluetoothGattCharacteristic characteristic = servicesMap.get(serviceUUID).getCharacteristic(UUID.fromString(charUUID));
        characteristic.setValue(decoded);
        boolean indicate = (characteristic.getProperties()
                & BluetoothGattCharacteristic.PROPERTY_INDICATE)
                == BluetoothGattCharacteristic.PROPERTY_INDICATE;
        for (BluetoothDevice device : mBluetoothDevices) {
            // true for indication (acknowledge) and false for notification (un-acknowledge).
            mGattServer.notifyCharacteristicChanged(device, characteristic, indicate);
        }
    }
    @ReactMethod
    public void isAdvertising(Promise promise){
        promise.resolve(this.advertising);
    }

}
