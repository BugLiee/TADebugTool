package com.thinkingdata.tadebugtool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thinkingdata.tadebugtool.ui.adapter.AppInfoRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.widget.FloatLayout;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupAppListView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupHeaderView;
import com.thinkingdata.tadebugtool.utils.TAUtil;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private PopupAppListView appListView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);
        if (!TAUtil.checkFloatingPermission(mActivity)) {
            TAUtil.requestFloatingPermission(mActivity);
        }
        AppInfoRecyclerViewAdapter adapter = new AppInfoRecyclerViewAdapter(mActivity, TAUtil.queryAllPackages(mActivity));
        adapter.setOnItemClickListener(new AppInfoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(String packageName) {
                FloatLayout floatLayout = FloatLayout.getInstance(mActivity);
                floatLayout.init(packageName);
                floatLayout.attachToWindow();
                moveTaskToBack(true);
                TAUtil.startAppWithPackageName(mActivity, packageName);
            }
        });
        appListView = new PopupAppListView(mActivity, adapter);

        Button btn = this.findViewById(R.id.btn11);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 0.8f;
                mActivity.getWindow().setAttributes(lp);
                appListView.showAtLocation(mActivity.findViewById(R.id.root_CL), Gravity.BOTTOM | Gravity.START, 0, 0);
            }
        });

        //设置标题栏是否随navigation变化
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_extension, R.id.nav_guidance)
                .build();*/
        //        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController((BottomNavigationView) this.findViewById(R.id.nav_view), navController);

        toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeader();
            }
        });
    }

    private void showHeader() {
        PopupHeaderView popupHeaderView = new PopupHeaderView(mActivity);
        popupHeaderView.showAtLocation(mActivity.findViewById(R.id.root_CL), Gravity.TOP | Gravity.START, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAUtil.FLOAT_PERMISSION_REQUEST_CODE) {
            // requestCode always == 0 [PackageManager.PERMISSION_GRANTED]
            if (!TAUtil.checkFloatingPermission(mActivity)) {
                // no permission
                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("PermissionChecker")
                        .setMessage("Permission denied, please allow permission !")
                        .setPositiveButton("To open", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TAUtil.requestFloatingPermission(mActivity);
                            }
                        });
                dialog.show();
            }
        }
    }
}