package com.group3.itis5280_project7;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group3.itis5280_project7.databinding.ActivityMainBinding;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ControlFragment.IListener, DeviceSearchFragment.IListener {
    private static final String TAG = "Project7";
    private ActivityMainBinding binding;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner scanner;
    BluetoothGatt gatt;

    ArrayList<Device> devices = new ArrayList<>();
    Device connectedDevice;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layoutView, new ControlFragment(), "ControlFragment")
                .commit();

        requestBlePermissions(this, 001);

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        requestBlePermissions(this, 001);
        scanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported!", Toast.LENGTH_LONG).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 001);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutView, fragment)
                .commit();
    }

    // Bluetooth Permissions
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void searchDevices() {
        if (scanner != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            } else {
                scanLeDevice();
                replaceFragment(DeviceSearchFragment.newInstance(devices));
                Log.d("Project7", "scan started");
            }
        }  else {
            Log.d("Project7", "could not get scanner object");
        }
    }

    byte[] onOff = new byte[0];

    @SuppressLint("MissingPermission")
    @Override
    public void pressBulbButton() {
        byte[] on = "1".getBytes(StandardCharsets.UTF_8);
        byte[] off = "0".getBytes(StandardCharsets.UTF_8);

        if (connectedDevice.getLightOn()) {
            bulbChar.setValue(off);
        } else {
            bulbChar.setValue(on);
        }

        gatt.writeCharacteristic(bulbChar);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void pressBeepButton() {
        byte[] on = "1".getBytes(StandardCharsets.UTF_8);
        byte[] off = "0".getBytes(StandardCharsets.UTF_8);

        Log.d(TAG, "CONNECTED DEVICE: " + connectedDevice.getBeepOn());
        if (connectedDevice.getBeepOn()) {
            beepChar.setValue(off);
        } else {
            beepChar.setValue(on);
        }

        gatt.writeCharacteristic(beepChar);
    }

    // Blueooth intent permissions response
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 001) {
            if (resultCode == -1) {

            }
        }
    }

    // SCANNING
    private boolean scanning;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
//        UUID JARED_GOOGLE_PIXEL = UUID.fromString("df70f520-08df-5711-c1ba-826883fbf944");
//        UUID JARED_NEXUS = UUID.fromString("df8d4ea1-38b6-fd8e-aeaf-30041f48400d");
//        UUID[] serviceUUIDs = new UUID[]{ JARED_GOOGLE_PIXEL, JARED_NEXUS };

//        if(serviceUUIDs != null) {
//            filters = new ArrayList<>();
//            for (UUID serviceUUID : serviceUUIDs) {
//                ScanFilter filter = new ScanFilter.Builder()
//                        //.setServiceUuid(new ParcelUuid(serviceUUID))
//                        .setDeviceName("Smart Bulb")
//                        .build();
//                filters.add(filter);
//                Log.d("Project7", "scanLeDevice: " + filters.toString());
//            }
//        }

        List<ScanFilter> filters = null;
        filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceName("Smart Bulb")
                .build();
        filters.add(filter);

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();

        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    scanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            Log.d("Project7", "scan start here");
            scanner.startScan(filters, scanSettings, leScanCallback);
        } else {
            scanning = false;
            scanner.stopScan(leScanCallback);
        }
    }

    // Device scan callback.
    @SuppressLint("MissingPermission")
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d("Project7", "scan result" + result.getDevice().getName());

                    BluetoothDevice bleDevice = result.getDevice();
                    Device device = new Device(bleDevice.getName(), bleDevice.getAddress(), bleDevice);

                    if (!devices.contains(device)) {
                        devices.add(device);
                        replaceFragment(DeviceSearchFragment.newInstance(devices));
                    }
                }
            };

    // CONNECT TO DEVICE
    @SuppressLint("MissingPermission")
    @Override
    public void connectToDevice(Device device) {
        if (!device.getConnected() || gatt == null) {
            connectedDevice = device;
            gatt = device.getDevice().connectGatt(this, false, bluetoothGattCallback);
        } else {
            gatt.disconnect();
        }
    }

    @Override
    public void gotoControl() {
        replaceFragment(new ControlFragment());
    }

    // BLUETOOTH GATT CALLBACKS
    private static final int GATT_SUCCESS = 0x0000;
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, final int newState) {
            if (status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("Project7", "connected to GATT");
                    connectedDevice.setConnected(true);
                    replaceFragment(ControlFragment.newInstance(connectedDevice));
                    // We successfully connected, proceed with service discovery
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("Project7", "Disconnected to GATT");
                    // We successfully disconnected on our own request
                    connectedDevice.setConnected(false);
                    connectedDevice = null;
                    gatt.close();

                } else {
                    // We're CONNECTING or DISCONNECTING, ignore for now
                    Log.d("Project7", "else");
                }
            } else {
                Log.d("Project7", "error");
                // An error happened...figure out what happened!
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.d(TAG, "onServicesDiscovered");

                displayGattServices(gatt.getServices());
            } else {
                Log.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status
        ) {
            Log.d(TAG, "on read characteristic");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                    Log.d(TAG, "format UINT16.");
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    Log.d(TAG, "format UINT8.");
                }

                final int temp = characteristic.getIntValue(format, 1);
                Log.d(TAG, String.format("Received ", temp));

                //replaceFragment(ControlFragment.newInstance(connectedDevice));
            } else {
                Log.d(TAG, "Not read");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            if (characteristic.getUuid().toString().equals("0ced9345-b31f-457d-a6a2-b3db9b03e39a")){
                byte[] value = characteristic.getValue();
                String str = new String(value);
                Log.d(TAG, "onCharacteristicChanged: temp " + str );
                connectedDevice.setTemperature(str);
                replaceFragment(ControlFragment.newInstance(connectedDevice));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (characteristic.getUuid().toString().equals("fb959362-f26e-43a9-927c-7e17d8fb2d8d")) {
                byte[] value = characteristic.getValue();
                String str = new String(value);
                Log.d(TAG, "onCharacteristicWrite: bulb " + str);

                if (str.equals("1")) {
                    connectedDevice.setLightOn(true);
                    replaceFragment(ControlFragment.newInstance(connectedDevice));
                } else {
                    connectedDevice.setLightOn(false);
                    replaceFragment(ControlFragment.newInstance(connectedDevice));
                }
            } else if (characteristic.getUuid().toString().equals("ec958823-f26e-43a9-927c-7e17d8f32a90")) {
                byte[] value = characteristic.getValue();
                String str = new String(value);
                Log.d(TAG, "onCharacteristicWrite: BEEP " + str );

                if(str.equals("1")) {
                    connectedDevice.setBeepOn(true);
                } else {
                    connectedDevice.setBeepOn(false);
                }
            }
        }
    };

    BluetoothGattCharacteristic tempChar, bulbChar, beepChar;

    @SuppressLint("MissingPermission")
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) {
            for (BluetoothGattCharacteristic characteristic: gattService.getCharacteristics()) {
                Log.d(TAG, "displayGattServices: " + characteristic.getUuid().toString());
                if(characteristic.getUuid().toString().equals("0ced9345-b31f-457d-a6a2-b3db9b03e39a")){
                    tempChar = characteristic;
                    gatt.setCharacteristicNotification(tempChar, true);
                } else if(characteristic.getUuid().toString().equals("fb959362-f26e-43a9-927c-7e17d8fb2d8d")) {
                    bulbChar = characteristic;
                } else if(characteristic.getUuid().toString().equals("ec958823-f26e-43a9-927c-7e17d8f32a90")) {
                    beepChar = characteristic;
                }
            }
        }
    }
}