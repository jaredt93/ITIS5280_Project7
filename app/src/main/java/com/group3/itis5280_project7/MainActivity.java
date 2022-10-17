package com.group3.itis5280_project7;

import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;

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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.group3.itis5280_project7.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
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
        UUID JARED_GOOGLE_PIXEL = UUID.fromString("df70f520-08df-5711-c1ba-826883fbf944");
        UUID JARED_NEXUS = UUID.fromString("df8d4ea1-38b6-fd8e-aeaf-30041f48400d");
        UUID[] serviceUUIDs = new UUID[]{ JARED_GOOGLE_PIXEL, JARED_NEXUS };

        List<ScanFilter> filters = null;

        if(serviceUUIDs != null) {
            filters = new ArrayList<>();
            for (UUID serviceUUID : serviceUUIDs) {
                ScanFilter filter = new ScanFilter.Builder()
                        //.setServiceUuid(new ParcelUuid(serviceUUID))
                        .setDeviceName("Smart Bulb")
                        .build();
                filters.add(filter);
                Log.d("Project7", "scanLeDevice: " + filters.toString());
            }
        }

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
                    //Log.d("Project7", "scan result" + result.getScanRecord().getServiceUuids());

                    BluetoothDevice bleDevice = result.getDevice();
                    Device device = new Device(bleDevice.getName(), bleDevice);

                    if (!devices.contains(device)) {
                        devices.add(device);
                        replaceFragment(DeviceSearchFragment.newInstance(devices));
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    Log.d("Project7", "scan batch");
                }

                @Override
                public void onScanFailed(int errorCode) {
                    // Ignore for now
                    Log.d("Project7", "scan fail" + errorCode);
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
    };

    @SuppressLint("MissingPermission")
    @Override
    public void readTempCharacteristic(BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "trying to read characteristic");
        if (bluetoothAdapter == null || gatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = gatt.getService(UUID.fromString("df8d4ea1-38b6-fd8e-aeaf-30041f48400d"));
        if(mCustomService == null){
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(UUID.fromString("fb959362-f26e-43a9-927c-7e17d8fb2d8d"));

        Log.d(TAG, "trying to read characteristic" + mReadCharacteristic.getPermissions());
        if(gatt.readCharacteristic(mReadCharacteristic) == false){
            Log.d(TAG, "Failed to read characteristic");
        }

        gatt.readCharacteristic(mReadCharacteristic);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
//        String unknownServiceString = getResources().
//                getString(R.string.unknown_service);
//        String unknownCharaString = getResources().
//                getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData =
                new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
                new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData =
                    new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            Log.d(TAG, "displayGattServices: uuid " + uuid);
//            currentServiceData.put(
//                    LIST_NAME, SampleGattAttributes.
//                            lookup(uuid, unknownServiceString));
//            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData =
                        new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                Log.d(TAG, "displayGattCharacteristics: uuid " + uuid);
//                currentCharaData.put(
//                        LIST_NAME, SampleGattAttributes.lookup(uuid,
//                                unknownCharaString));
                currentCharaData.put("", uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
            Log.d(TAG, "displayGattServices: " + mGattCharacteristics);
        }
    }


    //private Queue<Runnable> commandQueue;
    //private boolean commandQueueBusy;

//    public boolean readCharacteristic(final BluetoothGattCharacteristic characteristic) {
//        if(gatt == null) {
//            Log.d("Project7", "ERROR: Gatt is 'null', ignoring read request");
//            return false;
//        }
//
//        // Check if characteristic is valid
//        if(characteristic == null) {
//            Log.d(TAG, "ERROR: Characteristic is 'null', ignoring read request");
//            return false;
//        }
//
//        // Check if this characteristic actually has READ property
//        if((characteristic.getProperties() & PROPERTY_READ) == 0 ) {
//            Log.d(TAG, "ERROR: Characteristic cannot be read");
//            return false;
//        }
//
//        // Enqueue the read command now that all checks have been passed
//        boolean result = commandQueue.add(new Runnable() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void run() {
//                if(!gatt.readCharacteristic(characteristic)) {
//                    Log.d(TAG, String.format("ERROR: readCharacteristic failed for characteristic: %s", characteristic.getUuid()));
//                    completedCommand();
//                } else {
//                    Log.d(TAG, String.format("reading characteristic <%s>", characteristic.getUuid()));
//                    //nrTries++;
//                }
//            }
//        });
//
//        if(result) {
//            nextCommand();
//        } else {
//            Log.e(TAG, "ERROR: Could not enqueue read characteristic command");
//        }
//        return result;
//    }

//    private void nextCommand() {
//        // If there is still a command being executed then bail out
//        if(commandQueueBusy) {
//            return;
//        }
//
//        // Check if we still have a valid gatt object
//        if (gatt == null) {
//            Log.e(TAG, String.format("ERROR: GATT is 'null' for peripheral '%s', clearing command queue"));
//            commandQueue.clear();
//            commandQueueBusy = false;
//            return;
//        }
//
//        // Execute the next command in the queue
//        if (commandQueue.size() > 0) {
//            final Runnable bluetoothCommand = commandQueue.peek();
//            commandQueueBusy = true;
//            //nrTries = 0;
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        bluetoothCommand.run();
//                    } catch (Exception ex) {
//
//                    }
//                }
//            });
//        }
//    }


//    public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
//        // Perform some checks on the status field
//        if (status != GATT_SUCCESS) {
//            Log.e(TAG, String.format(Locale.ENGLISH,"ERROR: Read failed for characteristic: %s, status %d", characteristic.getUuid(), status));
//            completedCommand();
//            return;
//        }
//
//        // Characteristic has been read so processes it
//
//
//
//        completedCommand();
//    }
//
//    private void completedCommand() {
//        commandQueueBusy = false;
//        //isRetrying = false;
//        commandQueue.poll();
//        nextCommand();
//    }

}