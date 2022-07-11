package com.thinkingdata.tadebugtool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.bean.TAInstance;
import com.thinkingdata.tadebugtool.ui.adapter.AppInfoRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.adapter.EventListRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.fragments.HistoryFragment;
import com.thinkingdata.tadebugtool.ui.fragments.HomeFragment;
import com.thinkingdata.tadebugtool.ui.widget.DragFloatActionButton;
import com.thinkingdata.tadebugtool.ui.widget.FloatLayout;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupAppListView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupHeaderView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupInputView;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.litepal.LitePal;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private PopupAppListView appListView;
    private Toolbar toolbar;
    private String currentLabel = "主页";
    private DragFloatActionButton floatActionButton;
    private boolean connected = false;
    private NavController navController ;
    private HomeFragment homeFragment;

    private List<TAInstance> instances;
    private List<TAEvent> currentEvents;
    private boolean isItemOpened = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //primary
        LitePal.getDatabase();
        mActivity = this;
        setContentView(R.layout.activity_main);
        SnackbarUtil.setContentView(findViewById(R.id.root_msg_rl));
        //permission
        if (!TAUtil.checkFloatingPermission(mActivity)) {
            TAUtil.requestFloatingPermission(mActivity);
        }
        //query app List
        queryAppList();
        initView();
    }

    private void initData() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if ((instances == null || connected) && !isItemOpened) {
                    instances = LitePal.findAll(TAInstance.class);
                    if (instances.size() > 0) {
                        TAInstance instance = instances.get(instances.size()-1);
                        String instanceName = instance.getName() + instance.getTimestamp();
                        currentEvents = LitePal.where("instanceName = ?", instanceName).find(TAEvent.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                homeFragment.notifyDataChange(currentEvents);
                            }
                        });
                    }
                    if (!connected) {
                        cancel();
                    }
                }
            }
            //连接中则5秒查一次
        }, 100, connected ? 5000 : 100);
    }


    public static boolean isAdvance = false;

    private void initView() {
        floatActionButton = findViewById(R.id.float_btn);
        floatActionButton.setUnLockListener(new DragFloatActionButton.OnUnLockListener() {
            @Override
            public void unLock() {
                isAdvance = true;
            }
        });

        floatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatActionButton.showFromHidden();

                if (floatActionButton.isShowing()) {
                    //show app list
                    appListView.show();
                }
            }
        });
        //bind navigation & fragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        BottomNavigationView navigationView = (BottomNavigationView) this.findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);
        homeFragment = (HomeFragment) (((NavHostFragment) getSupportFragmentManager().getFragments().get(0)).getChildFragmentManager().getFragments().get(0));
        homeFragment.initItemStateListener(new EventListRecyclerViewAdapter.StateChangeListener() {
            @Override
            public void onStateChange(boolean isClosed) {
                isItemOpened = !isClosed;
            }
        });
        //initToolbar
        toolbar = this.findViewById(R.id.toolbar);
        toolbar.setTitle("Global");
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryShowHeader();
            }
        });
    }

    private void queryAppList() {
        AppInfoRecyclerViewAdapter adapter = new AppInfoRecyclerViewAdapter(mActivity, TAUtil.queryAllPackages(mActivity));
        adapter.setOnItemClickListener(new AppInfoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(String packageName, String appName, Drawable appIcon) {
                appListView.dismiss();
                if (isAdvance){
                    initAndShowFloatLayout(packageName, appName, appIcon, null);

                    return;
                }
                //弹出软键盘和editText
                PopupInputView popupInputView = new PopupInputView(mActivity);
                popupInputView.setOnSubmitClickListener(new PopupInputView.OnSubmitClickListener() {
                    @Override
                    public void onClick(List<String> appIDs) {
                        popupInputView.dismiss();
                        initAndShowFloatLayout(packageName, appName, appIcon, appIDs);
                    }
                });
                popupInputView.show();
            }
        });
        appListView = new PopupAppListView(mActivity, adapter);
    }

    private void initAndShowFloatLayout(String packageName, String appName, Drawable appIcon, List<String> appIDs) {
        FloatLayout floatLayout = FloatLayout.getInstance(mActivity);
        if (isAdvance) {
            floatLayout.setAdvance();
        }
        floatLayout.init(packageName, appName, appIcon, appIDs);
        floatLayout.setServiceConnectedListener(new FloatLayout.ServiceConnectedListener() {
            @Override
            public void onConnected(boolean isConnected) {
                connected = isConnected;
                if (navController != null) {
                    homeFragment.notifyStateChange(connected);
                }
            }
        });
        floatLayout.attachToWindow();
        moveTaskToBack(true);
        TAUtil.startAppWithPackageName(mActivity, packageName);
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
        instances = null;
        initData();
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

    private void tryShowHeader() {
        if (instances.size() > 0) {
            PopupHeaderView popupHeaderView = new PopupHeaderView(mActivity);
            //取最新一次的实例（上次 或 当次）
            popupHeaderView.initRVData(instances.get(instances.size()-1));
            popupHeaderView.showAtLocation(mActivity.findViewById(R.id.root_msg_rl), Gravity.TOP | Gravity.START, 0, 0);
        } else {
            SnackbarUtil.showSnackbarMid("暂时还没有进行过应用调试哦-.-");
        }
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