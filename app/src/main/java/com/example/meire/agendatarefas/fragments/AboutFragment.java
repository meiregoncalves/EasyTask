package com.example.meire.agendatarefas.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.meire.agendatarefas.R;

public class AboutFragment extends Fragment {
    private TextView tvVersion;
    private TextView tvDeveloper;
    public AboutFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemview = inflater.inflate(R.layout.fragment_about, container, false);
        tvVersion = (TextView) itemview.findViewById(R.id.tvVersion);
        tvDeveloper = (TextView) itemview.findViewById(R.id.tvDeveloper);

        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        tvDeveloper.setText(getString(R.string.developer_text) + " " + getString(R.string.developer_name));
        tvVersion.setText(getString(R.string.version_text) + " " + version);

        return itemview;
    }
}
