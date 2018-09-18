package com.smartwatch.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartwatch.R;
import com.smartwatch.model.Package;
import com.smartwatch.utils.OnPackageClickListener;

import java.util.List;


public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
    List<Package> mPackageList;
    OnPackageClickListener mListener;

    public PackageAdapter(List<Package> packageList, OnPackageClickListener listener) {
        mPackageList = packageList;
        mListener = listener;
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_card,
                parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackageViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Package packageItem = mPackageList.get(position);
        holder.name.setText(packageItem.getName());
        holder.mobileData.setText("Mobile: "+packageItem.getMobileData() + " MB");
        holder.wifiData.setText("Wifi: "+packageItem.getWifiData() + " MB");
        try {
            holder.icon.setImageDrawable(holder.context.getPackageManager().getApplicationIcon
                    (packageItem.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mPackageList.size();
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView name, mobileData, wifiData;
        AppCompatImageView icon;


        public PackageViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = (TextView) itemView.findViewById(R.id.name);
            mobileData = (TextView) itemView.findViewById(R.id.mobile_data);
            wifiData = (TextView) itemView.findViewById(R.id.wifi_data);
            icon = (AppCompatImageView) itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PackageAdapter.this.mListener.onClick(mPackageList.get(getAdapterPosition()));
                }
            });
        }
    }
}
