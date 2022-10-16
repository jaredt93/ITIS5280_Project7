package com.group3.itis5280_project7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {
    ArrayList<Device> devices;
    IDeviceRecycler mListener;
    private Context context;

    public DeviceRecyclerViewAdapter(ArrayList<Device> devices, IDeviceRecycler mListener) {
        this.devices = devices;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_device_row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.device = device;

        holder.textViewBluetoothDevice.setText(device.getName());
        holder.textViewUUID.setText(device.getUuid());

        if (device.getConnected()) {
            holder.textViewConnected.setText("Connected");
        } else {
            holder.textViewConnected.setText("Tap to Connect");
        }
    }

    @Override
    public int getItemCount() {
        return this.devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBluetoothDevice;
        TextView textViewUUID;
        TextView textViewConnected;

        View rootView;
        int position;
        Device device;
        IDeviceRecycler mListener;

        public ViewHolder(@NonNull View itemView, IDeviceRecycler mListener) {
            super(itemView);
            rootView = itemView;
            this.mListener = mListener;

            textViewBluetoothDevice = itemView.findViewById(R.id.textViewBluetoothDevice);
            textViewUUID = itemView.findViewById(R.id.textViewUUID);
            textViewConnected = itemView.findViewById(R.id.textViewConnected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.connectToDevice(device);
                }
            });
        }
    }

    interface IDeviceRecycler {
        void connectToDevice(Device device);
    }
}

