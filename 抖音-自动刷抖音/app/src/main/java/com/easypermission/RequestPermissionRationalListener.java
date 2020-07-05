package com.easypermission;

/**
 * @className: RequestPermissionRationalListener
 * @classDescription:
 * @author: Pan_
 * @createTime: 2018/10/24
 */
public interface RequestPermissionRationalListener {

    /**
     * 申请权限Rationale的处理回调函数，可以在这个函数里面处理权限rational的情况，例如弹一些dialog提示用户允许开启权限等，但是调用完成后必须调用NextAction
     * 方法，传进去一个NextActionType,否则后续操作不会执行，同时NextActionType最好不要传空，传空默认当成IGNORE处理
     * @param permission
     * @param requestPermissionRationaleResult
     * @param nextAction
     */
    public void onRequestPermissionRational(String permission,boolean requestPermissionRationaleResult,NextAction nextAction);
}
