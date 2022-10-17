package com.group3.itis5280_project7;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    ArrayList<Device> devices = new ArrayList<>();
    LinearLayoutManager layoutManager;
    DeviceRecyclerViewAdapter adapter;

    private static final String DEVICES = "DEVICES";

    public DeviceSearchFragment() {
        // Required empty public constructor
    }

    public static DeviceSearchFragment newInstance(ArrayList<Device> devices) {
        DeviceSearchFragment fragment = new DeviceSearchFragment();
        Bundle args = new Bundle();
        args.putSerializable(DEVICES, devices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Select a Device");

        if (getArguments() != null) {
            this.devices = (ArrayList<Device>) getArguments().getSerializable(DEVICES);

//            if(this.order == null) {
//                this.order = new Order();
//            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentDeviceSearchBinding.inflate(inflater, container, false);

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

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoControl();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    @SuppressLint("MissingPermission")
    public void connectToDevice(Device device) {
        Log.d("Project7", "connect to device" + device.getDevice().getAddress().toString());
        mListener.connectToDevice(device);
    }

    DeviceSearchFragment.IListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (DeviceSearchFragment.IListener) context;
    }

    public interface IListener {
        void connectToDevice(Device device);
        void gotoControl();
    }
}