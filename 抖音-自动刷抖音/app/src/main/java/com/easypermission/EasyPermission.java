package com.easypermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @className: EasyPermission
 * @classDescription:便捷易用的Android 6.0 动态权限适配库，使用方便，无其他引用依赖，copy 即用
 * @author: Pan_
 * @createTime: 2018/10/24
 */
public class EasyPermission implements NextAction {
    public static final String TAG = "EasyPermission";
    private Activity mActivity;
    private LinkedList<String> mPermissionList = new LinkedList<>();
    private PermissionRequestListener mPermissionRequestListener;
    private String mCurPermission;

    private HashMap<String, RequestPermissionRationalListener> mRequestPermissionRationalListenerMap = new HashMap<>();
    private HashMap<String, GrantResult> mPermissionGrantMap = new HashMap<>();

    public EasyPermission(Activity activity) {
        mActivity = activity;
    }

    /**
     * 创建一个EasyPermission实例，一切从这里开始
     *
     * @param activity
     * @return
     */
    public static EasyPermission with(Activity activity) {
        return new EasyPermission(activity);
    }

    /**
     * 添加一个需要获取的权限
     *
     * @param permission
     * @return
     */
    public EasyPermission addPermission(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return this;
        }

        mPermissionList.add(permission);
        return this;
    }


    /**
     * 添加一组需要获取的权限
     *
     * @param permission
     * @return
     */
    public EasyPermission addPermissions(String... permission) {
        if (permission == null || permission.length <= 0) {
            return this;
        }

        mPermissionList.addAll(Arrays.asList(permission));
        return this;
    }

    /**
     * 添加一组需要获取的权限
     *
     * @param permission
     * @return
     */
    public EasyPermission addPermissions(String[]... permission) {
        if (permission == null || permission.length <= 0) {
            return this;
        }

        for (String[] group : permission) {
            mPermissionList.addAll(Arrays.asList(group));
        }
        return this;
    }


    /**
     * 添加一组需要获取的权限
     *
     * @param permission
     * @return
     */
    public EasyPermission addPermissions(List<String> permission) {
        if (permission == null || permission.isEmpty()) {
            return this;
        }

        mPermissionList.addAll(permission);
        return this;
    }

    /**
     * 添加一个权限的Rational的处理
     *
     * @param permission
     * @param listener
     * @return
     */
    public EasyPermission addRequestPermissionRationaleHandler(String permission, RequestPermissionRationalListener listener) {
        if (TextUtils.isEmpty(permission) || listener == null) {
            return this;
        }

        mRequestPermissionRationalListenerMap.put(permission, listener);
        return this;
    }

    /**
     * 判断是否已经该权限
     *
     * @param context     上下文
     * @param permissions 权限数组
     * @return
     */
    public static boolean isPermissionGrant(Context context, String... permissions) {
        for (String permission : permissions) {
            if (context.checkPermission(permission, android.os.Process.myPid(), Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 打开权限设置页面
     *
     * @param context 上下文
     */
    public static void openSettingPage(Context context) {
        PermissionSettingPage.start(context, false);
    }

    /**
     * 打开权限设置页面
     *
     * @param context 上下文
     * @param newTask 是否开启新的堆栈打开
     */
    public static void openSettingPage(Context context, boolean newTask) {
        PermissionSettingPage.start(context, newTask);
    }

    /**
     * 开始申请权限
     *
     * @param listener
     */
    public void request(PermissionRequestListener listener) {
        if (listener == null) {
            return;
        }
        if (mPermissionList.isEmpty()) {
            throw new RuntimeException("must add some permission to request!!");
        }

        if (Build.VERSION.SDK_INT < 23) {
            Log.i(TAG, "targetSdk < 23 ,no need to request permission dynamic");
            HashMap<String, GrantResult> grantMap = new HashMap<>();
            for (String permission : mPermissionList) {
                grantMap.put(permission, GrantResult.GRANT);
            }
            listener.onGrant(grantMap);
            return;
        }
        PermissionUtils.checkPermissions(mActivity, mPermissionList);
        mPermissionRequestListener = listener;
        pollPermission();
    }


    @TargetApi(23)
    private void pollPermission() {
        if (mPermissionList.isEmpty()) {
            Log.i(TAG, "permission检查完成，开始申请权限");
            PermissionRequestFragment.build(mPermissionGrantMap, mPermissionRequestListener).go(mActivity);
            return;
        }
        String permission = mPermissionList.pollFirst();

        if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
           if(PermissionUtils.isHasInstallPermission(mActivity)){
               mPermissionGrantMap.put(permission, GrantResult.GRANT);
               pollPermission();
           }else{
               mPermissionGrantMap.put(permission, GrantResult.DENIED);
               pollPermission();
           }

        } else if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
            if(PermissionUtils.isHasOverlaysPermission(mActivity)){
                mPermissionGrantMap.put(permission, GrantResult.GRANT);
                pollPermission();
            }else{
                mPermissionGrantMap.put(permission, GrantResult.DENIED);
                pollPermission();
            }
        } else if (mActivity.checkPermission(permission, android.os.Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
            mPermissionGrantMap.put(permission, GrantResult.GRANT);
            pollPermission();
        } else {
            mPermissionGrantMap.put(permission, GrantResult.DENIED);
            if (mRequestPermissionRationalListenerMap.get(permission) != null) {
                mCurPermission = permission;
                mRequestPermissionRationalListenerMap.get(permission).onRequestPermissionRational(permission, mActivity.shouldShowRequestPermissionRationale(permission), this);
            } else {
                pollPermission();
            }
        }
    }

    @Override
    public void next(NextActionType next) {
        if (next == null) {
            mPermissionGrantMap.put(mCurPermission, GrantResult.IGNORE);
            pollPermission();
            return;
        }
        switch (next) {
            case NEXT:
                pollPermission();
                break;
            case IGNORE:
                mPermissionGrantMap.put(mCurPermission, GrantResult.IGNORE);
                pollPermission();
                break;
            case STOP:
                mPermissionRequestListener.onCancel(mCurPermission);
                break;
        }
    }
}
