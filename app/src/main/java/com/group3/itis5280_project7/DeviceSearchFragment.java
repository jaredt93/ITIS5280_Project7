package com.group3.itis5280_project7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.group3.itis5280_project7.databinding.FragmentDeviceSearchBinding;

import java.util.List;

public class DeviceSearchFragment extends Fragment {
    private FragmentDeviceSearchBinding binding;
    BluetoothAdapter adapter;
    BluetoothLeScanner scanner;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentDeviceSearchBinding.inflate(inflater, container, false);



        adapter = BluetoothAdapter.getDefaultAdapter();
        scanner = adapter.getBluetoothLeScanner();

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 001);
        }


        List<ScanFilter> filters = null;
        ScanSettings scanSettings = null;


//        if (scanner != null) {
//            //scanner.startScan(filters, scanSettings, scanCallback);
//            scanner.startScan(scanCallback);
//            Log.d("Project7", "scan started");
//        }  else {
//            Log.d("Project7", "could not get scanner object");
//        }


        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




//        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//        if (bluetoothAdapter == null) {
//            // Device doesn't support Bluetooth
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//
//
//
//        private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        private boolean scanning;
//        private Handler handler = new Handler();
//
//        // Stops scanning after 10 seconds.
//        private static final long SCAN_PERIOD = 10000;
//
//        private void scanLeDevice() {
//            if (!scanning) {
//                // Stops scanning after a predefined scan period.
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scanning = false;
//                        bluetoothLeScanner.stopScan(leScanCallback);
//                    }
//                }, SCAN_PERIOD);
//
//                scanning = true;
//                bluetoothLeScanner.startScan(leScanCallback);
//            } else {
//                scanning = false;
//                bluetoothLeScanner.stopScan(leScanCallback);
//            }
//        }




    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();        // ...do whatever you want with this found device
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}