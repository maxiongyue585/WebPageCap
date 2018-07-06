package com.xm.demo.refreshurl;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xm.demo.refreshurl.Activity.BrowserActivity;
import com.xm.demo.refreshurl.Activity.UrlsActivity;
import com.xm.demo.refreshurl.service.GuardService;
import com.xm.demo.refreshurl.service.RequestService;
import com.xm.demo.refreshurl.utils.SPUtils;
import com.xm.demo.refreshurl.utils.ToastUtils;
import com.xm.demo.refreshurl.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xm.demo.refreshurl.service.RequestService.DEF_TIME;
import static com.xm.demo.refreshurl.service.RequestService.MIN;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private Intent mServiceIntent;

    private Intent mGuardServiceIntent;


    /**
     * 需要进行检测的权限数组
     */
    protected String[] mNeedPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSION_REQUESTCODE = 0;//权限检测请求码
    protected static final int APPLICATION_PERMISSIONS_REQUESTCODE = 2;//应用权限列表返回码

    @BindView(R.id.btn_open_browser)
    Button btnOpenBrowser;
    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.btn_stop_task)
    Button btnStopTask;
    @BindView(R.id.btn_tasks)
    Button btnTasks;
    @BindView(R.id.et_input_time)
    EditText etInputTime;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_save_path)
    TextView tvSavePath;
    @BindView(R.id.tv_last_lauch_time)
    TextView tvLastLauchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions(mNeedPermissions);
        }
//        initData();
        initView();
        initListener();
    }

    @OnClick({R.id.btn_open_browser, R.id.btn_start_task, R.id.btn_stop_task, R.id.btn_tasks, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open_browser:
                startActivity(BrowserActivity.class);
                break;
            case R.id.btn_start_task:
                if (!Utils.isServiceRunning(this, "com.xm.demo.refreshurl.service.RequestService")) {
                    startTasks();
                } else {
                    ToastUtils.showShort(this, "该任务已经启动");
                }

                break;
            case R.id.btn_stop_task:
                stopTasks();
                SPUtils.setParam(getApplicationContext(), SPUtils.IS_STOP_SERVICE, true);
                break;
            case R.id.btn_tasks:
                startActivity(UrlsActivity.class);
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }


    /**
     * activity跳转（无参数）
     *
     * @param className
     */
    public void startActivity(Class<?> className) {
        Intent intent = new Intent(mContext, className);
        startActivity(intent);
    }


    private void initView() {
        String minute = String.valueOf((long) SPUtils.getParam(getApplicationContext(), SPUtils.K_INTERVAL, DEF_TIME) / MIN);
        etInputTime.setText(minute);
    }

    private void initData() {

        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "wb_capture";
        File dir_wbcapture = new File(filePath);
        if (!dir_wbcapture.exists()) {
            tvSavePath.setText("路径不存在,请启动任务");
        } else {
//            List<File> dirs = Utils.getDirs(dir_wbcapture);//获取wb_capture目录下所有目录
//            if (dirs == null || dirs.size() == 0) {
//                tvSavePath.setText("路径下为空，请启动任务");
//            } else {
//                Utils.listFileSortByModifyTime(dirs);
//                tvSavePath.setText(filePath);
//                tvLastLauchTime.setText(String.format("上次启动时间:%s", Utils.formatToNowTime(dirs.get(dirs.size() - 1).lastModified())));
//            }
            tvSavePath.setText(filePath);
            tvLastLauchTime.setText(String.format("上次启动时间:%s", Utils.formatToNowTime(dir_wbcapture.lastModified())));
        }

    }

    private void initListener() {
        mServiceIntent = new Intent(this, RequestService.class);
        mGuardServiceIntent = new Intent(this, GuardService.class);
        SPUtils.setParam(getApplicationContext(), SPUtils.IS_STOP_SERVICE, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    //启动任务
    private void startTasks() {
        startService(mServiceIntent);
        startService(mGuardServiceIntent);
        Log.d(TAG, "startTasks: ");
    }

    //停止任务
    private void stopTasks() {
        stopService(mServiceIntent);
        stopService(mGuardServiceIntent);
        Log.d(TAG, "stopTasks: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTasks();
    }

    //监听返回键正常退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SPUtils.setParam(getApplicationContext(), SPUtils.IS_STOP_SERVICE, true);
        }
        return super.onKeyDown(keyCode, event);
    }

    //设置时间进行保存
    private void save() {
        String inputValue = etInputTime.getText().toString();
        if (TextUtils.isEmpty(inputValue)) {
            ToastUtils.showShort(this, "输入内容不能为空");
            return;
        }
        if (Utils.isNumber(inputValue)) {
            long min = 0;
            if (Utils.isInteger(inputValue)) {//输入整数
                min = Integer.parseInt(inputValue) * MIN;
            } else if (Utils.isDouble(inputValue)) {//输入小数
                min = (long) (Double.parseDouble(inputValue) * MIN);
            }

            if (min >= DEF_TIME) {
                //设置时间
                SPUtils.setParam(this, SPUtils.K_INTERVAL, min);
                ToastUtils.showShort(this, "设置成功");
            } else {
                ToastUtils.showShort(this, "最小任务间隔10分钟");
            }
            Log.d(TAG, "onViewClicked: " + min);
        } else {
            //排除只输入.
            ToastUtils.showShort(this, "请输入正确的格式");
            return;
        }
    }

    /**
     * 需要权限检查
     *
     * @param permissions
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSION_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                new AlertDialog.Builder(this).setTitle("系统提示").setMessage("未取得相应权限，此功能无法使用。请前往应用权限设置打开权限。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelable(false).create().show();
            }
        }
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, APPLICATION_PERMISSIONS_REQUESTCODE);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APPLICATION_PERMISSIONS_REQUESTCODE) {// 来自权限列表界面

        }
    }


}
