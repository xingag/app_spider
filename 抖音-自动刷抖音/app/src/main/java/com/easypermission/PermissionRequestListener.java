package com.easypermission;

import java.util.Map;

/**
 * @className: PermissionRequestListener
 * @classDescription:
 * @author: Pan_
 * @createTime: 2018/10/25
 */
public abstract class PermissionRequestListener {
    /**
     * 权限申请结果的回调，map里面对应着需要申请的权限和结果
     * @param result
     */
    public abstract void onGrant(Map<String,GrantResult> result);

    /**
     * 当用户在RequestPermissionRationalListner的next函数里面回调STOP，则会调用这个回调，返回stop时候的权限的字符串
     * @param stopPermission
     */
    public abstract void onCancel(String stopPermission);
}
