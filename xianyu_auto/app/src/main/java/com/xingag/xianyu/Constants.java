package com.xingag.xianyu;

/***
 * 常量
 */

public class Constants
{
    //无障碍服务名称
    public static final String serviceName = "com.xingag.xianyu/.service.XianYuService";


    //包名：闲鱼App
    public static String packageName_xianyu = "com.taobao.idlefish";

    //包名：本应用
    public static String packageName_local = "com.xingag.xianyu";

    //Lauch Activity
    public static String lauch_activity_local = "com.xingag.xianyu.MainActivity";

    //闲鱼聊天界面
    public static String class_name_chat = "com.taobao.fleamarket.message.activity.NewChatActivity";

    //闲鱼主Activity
    public static String class_name_xianyu = "com.taobao.fleamarket.home.activity.MainActivity";

    //定义一段固定的回复内容
    public static String reply_first = "让我开心的是，可以在这里遇见有趣的事物，可以遇见您。\n有什么可以帮到您的呢?\n回复【11】获取商品信息\n回复【22】获取发货信息";

    //11:商品信息
    public static String reply_11 = "亲，商品还有货\n现在可以直接拍下\n等店主看到了会第一时间发货";

    //22:发货信息
    public static String reply_22 = "亲，我们是包邮的！\n需要邮寄快递可以给我们留言";

    //other:其他
    public static String reply_other = "亲，信息收到了！\n主人会第一时间给你答复，稍等哈~";

    //闲鱼首页5个Tab
    public enum HOME_TAB
    {
        TAB_XIAN_YU,
        TAB_YU_TANG,
        TAB_FA_BU,
        TAB_XIAO_XI,
        TAB_MINE
    }

}