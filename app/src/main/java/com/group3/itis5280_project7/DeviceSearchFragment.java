package com.group3.itis5280_project7;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group3.itis5280_project7.databinding.FragmentDeviceSearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceSearchFragment extends Fragment implements DeviceRecyclerViewAdapter.IDeviceRecycler {
    private FragmentDeviceSearchBinding binding;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner scanner;

    ArrayList<Device> devices = new ArrayList<>();
    LinearLayoutManager layoutManager;
    DeviceRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentDeviceSearchBinding.inflate(inflater, container, false);
        bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();

        layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(mDividerItemDecoration);

        adapter = new DeviceRecyclerViewAdapter(devices, this);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (scanner != null) {
            //scanner.startScan(filters, scanSettings, scanCallback);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            } else {
                scanLeDevice();
                Log.d("Project7", "scan started");
            }
        }  else {
            Log.d("Project7", "could not get scanner object");
        }
    }

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
                    Device device = new Device(bleDevice.getName());

                    if (!devices.contains(device)) {
                        devices.add(device);
                    }

                    adapter.notifyDataSetChanged();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void connectToDevice(Device device) {

    }
}