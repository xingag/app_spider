package com.easypermission;

/**
 * @className: NextAction
 * @classDescription: addRequestPermissionRationaleHandler的处理函数，当定义了一个权限的rationaleHandler的时候，必须在处理回调里面调用这个接口的next方法， 才能进行下一步，
 * 否则整个权限申请链条将会中断，权限申请将不会触发
 * @author: Pan_
 * @createTime: 2018/10/24
 */
public interface NextAction {

    /**
     * addRequestPermissionRationaleHandler的处理函数，当定义了一个权限的rationaleHandler的时候，必须在处理回调里面调用这个接口的next方法， 才能进行下一步，
     * 否则整个权限申请链条将会中断，权限申请将不会触发
     * @param next
     */
    public void next(NextActionType next);
}
