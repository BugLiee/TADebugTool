package com.thinkingdata.tadebugtool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupInputView;
import com.thinkingdata.tadebugtool.utils.GetWinPoint;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private PopupAppListView appListView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.getDatabase();
        mActivity = this;
        setContentView(R.layout.activity_main);
        if (!TAUtil.checkFloatingPermission(mActivity)) {
            TAUtil.requestFloatingPermission(mActivity);
        }
        AppInfoRecyclerViewAdapter adapter = new AppInfoRecyclerViewAdapter(mActivity, TAUtil.queryAllPackages(mActivity));
        adapter.setOnItemClickListener(new AppInfoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(String packageName) {
                appListView.dismiss();
                //弹出软键盘和editText
                PopupInputView popupInputView = new PopupInputView(mActivity);
                popupInputView.setOnSubmitClickListener(new PopupInputView.OnSubmitClickListener() {
                    @Override
                    public void onClick(List<String> appIDs) {
                        popupInputView.dismiss();
                        FloatLayout floatLayout = FloatLayout.getInstance(mActivity);
                        floatLayout.init(packageName, appIDs);
                        floatLayout.attachToWindow();
                        moveTaskToBack(true);
                        TAUtil.startAppWithPackageName(mActivity, packageName);
                    }
                });
                popupInputView.show();
            }
        });
        appListView = new PopupAppListView(mActivity, adapter);

        Button btn = this.findViewById(R.id.btn11);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appListView.show();
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
        toolbar.setTitle("Global");
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeader();
            }
        });

        //
    }

    private void handlerIntent() {
        String msg = getIntent().getStringExtra("errmsg");
        if (msg != null) {
            getIntent().removeExtra("errmsg");
            //show
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("连接失败")
                    .setMessage(msg)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handlerIntent();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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