package com.easypermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @className: PermissionRequestFragment
 * @classDescription:权限申请代理fragment
 * @author: Pan_
 * @createTime: 2018/10/25
 */

public final class PermissionRequestFragment extends Fragment implements Runnable {
    private int mRequestCode;
    private PermissionRequestListener mPermissionRequestListener;
    private HashMap<String, GrantResult> mPermissionGrantMap = new HashMap<>();
    private RequestCodeGenerater mRequestCodeGenerater = new RequestCodeGenerater();


    /**
     * build函数构造一个用于权限请求的fragment
     *
     * @param permissionMap
     * @param permissionRequestListener
     * @return
     */
    public static PermissionRequestFragment build(HashMap<String, GrantResult> permissionMap, PermissionRequestListener permissionRequestListener) {
        PermissionRequestFragment fragment = new PermissionRequestFragment();
        fragment.setPermissionGrantMap(permissionMap);
        fragment.setPermissionRequestListener(permissionRequestListener);
        return fragment;
    }


    public void setPermissionRequestListener(PermissionRequestListener permissionRequestListener) {
        mPermissionRequestListener = permissionRequestListener;
    }

    public void setPermissionGrantMap(HashMap<String, GrantResult> permissionGrantMap) {
        mPermissionGrantMap = permissionGrantMap;
    }

    /**
     * 开始请求
     */
    public void go(Activity activity) {
        if (activity != null) {
            if (Looper.getMainLooper() != Looper.myLooper()) {
                throw new RuntimeException("you must request permission in main thread!!");
            }
            activity.getFragmentManager().beginTransaction().add(this, activity.getClass().getName()).commit();
        } else {
            throw new RuntimeException("activity is null!!");
        }
    }

    private boolean isBackCall;//是否已经回调了，避免安装权限和悬浮窗同时请求导致的重复回调

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (!isBackCall && requestCode == mRequestCode) {
            isBackCall = true;
            //需要延迟执行，不然有些华为机型授权了但是获取不到权限
            getActivity().getWindow().getDecorView().postDelayed(this, 500);
        }
    }


    @Override
    public void run() {
        //请求其他危险权限
        startRequestPermission();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRequestCode = mRequestCodeGenerater.generate();
        if ((mPermissionGrantMap.containsKey(Permission.REQUEST_INSTALL_PACKAGES) && mPermissionGrantMap.get(Permission.REQUEST_INSTALL_PACKAGES) == GrantResult.DENIED)
                || (mPermissionGrantMap.containsKey(Permission.SYSTEM_ALERT_WINDOW) && mPermissionGrantMap.get(Permission.SYSTEM_ALERT_WINDOW) == GrantResult.DENIED)) {
            if (mPermissionGrantMap.containsKey(Permission.REQUEST_INSTALL_PACKAGES)) {
                //跳转到允许安装未知来源设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, mRequestCode);
            }

            if (mPermissionGrantMap.containsKey(Permission.SYSTEM_ALERT_WINDOW)) {
                //跳转到悬浮窗设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, mRequestCode);
            }

        } else {
            startRequestPermission();
        }
    }

    @TargetApi(23)
    private void startRequestPermission() {
        ArrayList<String> needRequestPermissionList = new ArrayList<>();
        Set<Map.Entry<String, GrantResult>> entrySet = mPermissionGrantMap.entrySet();
        for (Map.Entry<String, GrantResult> entry : entrySet) {
            if (entry.getValue() == GrantResult.DENIED) {
                needRequestPermissionList.add(entry.getKey());
            }
        }

        for (String permission : needRequestPermissionList) {
            Log.i(EasyPermission.TAG, "需要申请的权限：" + permission);
        }
        if (needRequestPermissionList.isEmpty() && mPermissionRequestListener != null) {
            Log.i(EasyPermission.TAG, "没有需要申请的权限，直接回调");
            mPermissionRequestListener.onGrant(mPermissionGrantMap);
            return;
        }
        String[] permissionArray = needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]);
        if(permissionArray!=null&&permissionArray.length>0){
            requestPermissions(permissionArray, mRequestCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != mRequestCode) {
            Log.i(EasyPermission.TAG, "requestCode不一致，不处理");
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            Log.i(EasyPermission.TAG, "onRequestPermissionsResult返回状态，权限：" + permissions[i] + "  是否授权：" + grantResults[i]);
            String permission = permissions[i];
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
                if (PermissionUtils.isHasInstallPermission(getActivity().getApplicationContext())) {
                    mPermissionGrantMap.put(permission, GrantResult.GRANT);
                } else {
                    mPermissionGrantMap.put(permission, GrantResult.DENIED);
                }

            } else if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                if (PermissionUtils.isHasOverlaysPermission(getActivity().getApplicationContext())) {
                    mPermissionGrantMap.put(permission, GrantResult.GRANT);
                } else {
                    mPermissionGrantMap.put(permission, GrantResult.DENIED);
                }
            } else {
                mPermissionGrantMap.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED ? GrantResult.GRANT : GrantResult.DENIED);
            }

        }


        //打印返回结果
        Set<Map.Entry<String, GrantResult>> entrySet = mPermissionGrantMap.entrySet();
        Log.i(EasyPermission.TAG, "打印最终返回结果：");
        for (Map.Entry<String, GrantResult> entry : entrySet) {
            Log.i(EasyPermission.TAG, "权限：" + entry.getKey() + "  状态：" + entry.getValue());
        }
        if (mPermissionRequestListener != null) {
            mPermissionRequestListener.onGrant(mPermissionGrantMap);
        }
        getFragmentManager().beginTransaction().remove(this).commit();
    }


    /**
     * requestCode的生成器，用于生成不重复的requestCode
     */
    private static class RequestCodeGenerater {
        /**
         * 生成初始化值
         */
        private volatile static int FACTOR_REQUEST_CODE = 0;

        /**
         * 同步生成方法
         *
         * @return
         */
        public synchronized int generate() {
            return FACTOR_REQUEST_CODE++;
        }
    }

}
