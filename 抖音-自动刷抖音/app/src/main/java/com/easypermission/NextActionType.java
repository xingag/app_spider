package com.easypermission;

/**
 * @className: NextActionType
 * @classDescription:NextAction 回调的类型
 * @author: Pan_
 * @createTime: 2018/10/24
 */
public enum NextActionType {
    /**
     * 忽略这个权限，后面的步骤不会申请这个权限
     */
    IGNORE,

    /**
     * 表示继续处理，继续下一个权限
     */
    NEXT,

    /**
     * 停止处理，直接回调onCancel函数
     */
    STOP;
}
