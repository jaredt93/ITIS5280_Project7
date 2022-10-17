package com.group3.itis5280_project7;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group3.itis5280_project7.databinding.FragmentControlBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ControlFragment extends Fragment {
    private FragmentControlBinding binding;
    Device device = null;
    private static final String DEVICE = "DEVICE";

    public ControlFragment() {
        // Required empty public constructor
    }

    public static ControlFragment newInstance(Device device) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putSerializable(DEVICE, device);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.device = (Device) getArguments().getSerializable(DEVICE);
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
        binding = FragmentControlBinding.inflate(inflater, container, false);
        getActivity().setTitle("Light Temp Beep");
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonDeviceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.searchDevices();
            }
        });

        if (device != null) {
            binding.textViewFindDevice.setText("Connected");

            binding.buttonLight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.buttonLight.setText("ON");
                }
            });


            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.d("Project7", "run");
                    BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(UUID.fromString("FB959362-F26E-43A9-927C-7E17D8FB2D8D"), BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                            BluetoothGattCharacteristic.PERMISSION_READ);
                    //mListener.readTempCharacteristic(characteristic);
                }
            }, 0, 10000);//put here time 1000 milliseconds=1 second

            //binding.textViewTemp.setText(device.g + "°F");

            binding.buttonBeep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            binding.textViewFindDevice.setText("Not Connected");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    IListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (IListener) context;
    }

    public interface IListener {
        void searchDevices();
        void readTempCharacteristic(BluetoothGattCharacteristic characteristic);
    }
}