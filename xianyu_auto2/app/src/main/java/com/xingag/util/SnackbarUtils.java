package com.xingag.util;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.Space;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xingag.xianyu.R;

import java.lang.ref.WeakReference;

/**
 Snackbar工具类
 功能:
 1:设置Snackbar显示时间长短
 1.1:Snackbar.LENGTH_SHORT       {@link SnackbarUtils#Short(View, String)}
 1.2:Snackbar.LENGTH_LONG        {@link SnackbarUtils#Long(View, String)}
 1.3:Snackbar.LENGTH_INDEFINITE  {@link SnackbarUtils#Indefinite(View, String)}
 1.4:CUSTOM                      {@link SnackbarUtils#Custom(View, String, int)}
 2:设置Snackbar背景颜色
 2.1:color_info      {@link SnackbarUtils#info()}
 2.2:color_confirm   {@link SnackbarUtils#confirm()}
 2.3:color_warning   {@link SnackbarUtils#warning()}
 2.4:color_danger    {@link SnackbarUtils#danger()}
 2.5:CUSTOM          {@link SnackbarUtils#backColor(int)}
 3:设置TextView(@+id/snackbar_text)的文字颜色
 {@link SnackbarUtils#messageColor(int)}
 4:设置Button(@+id/snackbar_action)的文字颜色
 {@link SnackbarUtils#actionColor(int)}
 5:设置Snackbar背景的透明度
 {@link SnackbarUtils#alpha(float)}
 6:设置Snackbar显示的位置
 {@link SnackbarUtils#gravityFrameLayout(int)}
 {@link SnackbarUtils#gravityCoordinatorLayout(int)}
 6.1:Gravity.TOP;
 6.2:Gravity.BOTTOM;
 6.3:Gravity.CENTER;
 7:设置Button(@+id/snackbar_action)文字内容 及 点击监听
 {@link SnackbarUtils#setAction(int, View.OnClickListener)}
 {@link SnackbarUtils#setAction(CharSequence, View.OnClickListener)}
 8:设置Snackbar展示完成 及 隐藏完成 的监听
 {@link SnackbarUtils#()}
 9:设置TextView(@+id/snackbar_text)左右两侧的图片
 {@link SnackbarUtils#leftAndRightDrawable(Drawable, Drawable)}
 {@link SnackbarUtils#leftAndRightDrawable(Integer, Integer)}
 10:设置TextView(@+id/snackbar_text)中文字的对齐方式
 默认效果就是居左对齐
 {@link SnackbarUtils#messageCenter()}   居中对齐
 {@link SnackbarUtils#messageRight()}    居右对齐
 注意:这两个方法要求SDK>=17.{@link View#setTextAlignment(int)}
 本来想直接设置Gravity,经试验发现在 TextView(@+id/snackbar_text)上,design_layout_snackbar_include.xml
 已经设置了android:textAlignment="viewStart",单纯设置Gravity是无效的.
 TEXT_ALIGNMENT_GRAVITY:{@link View#TEXT_ALIGNMENT_GRAVITY}
 11:向Snackbar布局中添加View(Google不建议,复杂的布局应该使用DialogFragment进行展示)
 {@link SnackbarUtils#addView(int, int)}
 {@link SnackbarUtils#addView(View, int)}
 注意:使用addView方法的时候要注意新加布局的大小和Snackbar内文字长度，Snackbar过大或过于花哨了可不好看
 12:设置Snackbar布局的外边距
 {@link SnackbarUtils#margins(int)}
 {@link SnackbarUtils#margins(int, int, int, int)}
 注意:经试验发现,调用margins后再调用 gravityFrameLayout,则margins无效.
 为保证margins有效,应该先调用 gravityFrameLayout,在 show() 之前调用 margins
 SnackbarUtil.Long(bt9,"设置Margin值").backColor(0XFF330066).gravityFrameLayout(Gravity.TOP).margins(20,40,60,80).show();
 13:设置Snackbar布局的圆角半径值
 {@link SnackbarUtils#radius(float)}
 14:设置Snackbar布局的圆角半径值及边框颜色及边框宽度
 {@link SnackbarUtils#radius(int, int, int)}
 15:设置Snackbar显示在指定View的上方
 {@link SnackbarUtils#above(View, int, int, int)}
 注意:
 1:此方法实际上是 {@link SnackbarUtils#gravityFrameLayout(int)}和{@link SnackbarUtils#margins(int, int, int, int)}的结合.
 不可与 {@link SnackbarUtils#margins(int, int, int, int)} 混用.
 2:暂时仅仅支持单行Snackbar,因为方法中涉及的{@link SnackbarUtils#calculateSnackBarHeight()}暂时仅支持单行Snackbar高度计算.
 16:设置Snackbar显示在指定View的下方
 {@link SnackbarUtils#bellow(View, int, int, int)}
 注意:同15
 参考:
 //写的很好的Snackbar源码分析
 http://blog.csdn.net/wuyuxing24/article/details/51220415
 //借鉴了作者部分写法,自定义显示时间 及 向Snackbar中添加View
 http://www.jianshu.com/p/cd1e80e64311
 //借鉴了作者部分写法,4种类型的背景色 及 方法调用的便捷性
 http://www.jianshu.com/p/e3c82b98f151
 //大神'工匠若水'的文章'Android应用坐标系统全面详解',用于计算Snackbar显示的精确位置
 http://blog.csdn.net/yanbober/article/details/50419117
 示例:
 在Activity中:
 int total = 0;
 int[] locations = new int[2];
 getWindow().findViewById(android.R.id.content).getLocationInWindow(locations);
 total = locations[1];
 SnackbarUtil.Custom(bt_multimethods,"10s+左右drawable+背景色+圆角带边框+指定View下方",1000*10)
 .leftAndRightDrawable(R.mipmap.i10,R.mipmap.i11)
 .backColor(0XFF668899)
 .radius(16,1,Color.BLUE)
 .bellow(bt_margins,total,16,16)
 .show();
 * 作者:幻海流心
 * 邮箱:wall0920@163.com
 * 2016/11/2 13:56
 */

public class SnackbarUtils {
    //设置Snackbar背景颜色
    private static final int color_info = 0XFF2094F3;
    private static final int color_confirm = 0XFF4CB04E;
    private static final int color_warning = 0XFFFEC005;
    private static final int color_danger = 0XFFF44336;
    //工具类当前持有的Snackbar实例
    private static WeakReference<Snackbar> snackbarWeakReference;

    private SnackbarUtils(){
        throw new RuntimeException("禁止无参创建实例");
    }

    private SnackbarUtils(@Nullable WeakReference<Snackbar> snackbarWeakReference){
        this.snackbarWeakReference = snackbarWeakReference;
    }

    /**
     * 获取 mSnackbar
     * @return
     */
    public Snackbar getSnackbar() {
        if(this.snackbarWeakReference != null && this.snackbarWeakReference.get()!=null){
            return this.snackbarWeakReference.get();
        }else {
            return null;
        }
    }

    /**
     * 初始化Snackbar实例
     *      展示时间:Snackbar.LENGTH_SHORT
     * @param view
     * @param message
     * @return
     */
    public static SnackbarUtils Short(View view, String message){
        /*
        <view xmlns:android="http://schemas.android.com/apk/res/android"
          class="android.support.design.widget.Snackbar$SnackbarLayout"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:layout_gravity="bottom"
          android:theme="@style/ThemeOverlay.AppCompat.Dark"
          style="@style/Widget.Design.Snackbar" />
        <style name="Widget.Design.Snackbar" parent="android:Widget">
            <item name="android:minWidth">@dimen/design_snackbar_min_width</item>
            <item name="android:maxWidth">@dimen/design_snackbar_max_width</item>
            <item name="android:background">@drawable/design_snackbar_background</item>
            <item name="android:paddingLeft">@dimen/design_snackbar_padding_horizontal</item>
            <item name="android:paddingRight">@dimen/design_snackbar_padding_horizontal</item>
            <item name="elevation">@dimen/design_snackbar_elevation</item>
            <item name="maxActionInlineWidth">@dimen/design_snackbar_action_inline_max_width</item>
        </style>
        <shape xmlns:android="http://schemas.android.com/apk/res/android"
            android:shape="rectangle">
            <corners android:radius="@dimen/design_snackbar_background_corner_radius"/>
            <solid android:color="@color/design_snackbar_background_color"/>
        </shape>
        <color name="design_snackbar_background_color">#323232</color>
        */
        return new SnackbarUtils(new WeakReference<Snackbar>(Snackbar.make(view,message,Snackbar.LENGTH_SHORT))).backColor(0XFF323232);
    }
    /**
     * 初始化Snackbar实例
     *      展示时间:Snackbar.LENGTH_LONG
     * @param view
     * @param message
     * @return
     */
    public static SnackbarUtils Long(View view, String message){
        return new SnackbarUtils(new WeakReference<Snackbar>(Snackbar.make(view,message,Snackbar.LENGTH_LONG))).backColor(0XFF323232);
    }
    /**
     * 初始化Snackbar实例
     *      展示时间:Snackbar.LENGTH_INDEFINITE
     * @param view
     * @param message
     * @return
     */
    public static SnackbarUtils Indefinite(View view, String message){
        return new SnackbarUtils(new WeakReference<Snackbar>(Snackbar.make(view,message,Snackbar.LENGTH_INDEFINITE))).backColor(0XFF323232);
    }
    /**
     * 初始化Snackbar实例
     *      展示时间:duration 毫秒
     * @param view
     * @param message
     * @param duration 展示时长(毫秒)
     * @return
     */
    public static SnackbarUtils Custom(View view, String message, int duration){
        return new SnackbarUtils(new WeakReference<Snackbar>(Snackbar.make(view,message,Snackbar.LENGTH_SHORT).setDuration(duration))).backColor(0XFF323232);
    }

    /**
     * 设置mSnackbar背景色为  color_info
     */
    public SnackbarUtils info(){
        if(getSnackbar()!=null){
            getSnackbar().getView().setBackgroundColor(color_info);
        }
        return this;
    }
    /**
     * 设置mSnackbar背景色为  color_confirm
     */
    public SnackbarUtils confirm(){
        if(getSnackbar()!=null){
            getSnackbar().getView().setBackgroundColor(color_confirm);
        }
        return this;
    }
    /**
     * 设置Snackbar背景色为   color_warning
     */
    public SnackbarUtils warning(){
        if(getSnackbar()!=null){
            getSnackbar().getView().setBackgroundColor(color_warning);
        }
        return this;
    }
    /**
     * 设置Snackbar背景色为   color_warning
     */
    public SnackbarUtils danger(){
        if(getSnackbar()!=null){
            getSnackbar().getView().setBackgroundColor(color_danger);
        }
        return this;
    }

    /**
     * 设置Snackbar背景色
     * @param backgroundColor
     */
    public SnackbarUtils backColor(@ColorInt int backgroundColor){
        if(getSnackbar()!=null){
            getSnackbar().getView().setBackgroundColor(backgroundColor);
        }
        return this;
    }

    /**
     * 设置TextView(@+id/snackbar_text)的文字颜色
     * @param messageColor
     */
    public SnackbarUtils messageColor(@ColorInt int messageColor){
        if(getSnackbar()!=null){
            ((TextView)getSnackbar().getView().findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
        return this;
    }

    /**
     * 设置Button(@+id/snackbar_action)的文字颜色
     * @param actionTextColor
     */
    public SnackbarUtils actionColor(@ColorInt int actionTextColor){
        if(getSnackbar()!=null){
            ((Button)getSnackbar().getView().findViewById(R.id.snackbar_action)).setTextColor(actionTextColor);
        }
        return this;
    }

    /**
     * 设置   Snackbar背景色 + TextView(@+id/snackbar_text)的文字颜色 + Button(@+id/snackbar_action)的文字颜色
     * @param backgroundColor
     * @param messageColor
     * @param actionTextColor
     */
    public SnackbarUtils colors(@ColorInt int backgroundColor, @ColorInt int messageColor, @ColorInt int actionTextColor){
        if(getSnackbar()!=null){
            getSnackbar().getView().setBackgroundColor(backgroundColor);
            ((TextView)getSnackbar().getView().findViewById(R.id.snackbar_text)).setTextColor(messageColor);
            ((Button)getSnackbar().getView().findViewById(R.id.snackbar_action)).setTextColor(actionTextColor);
        }
        return this;
    }

    /**
     * 设置Snackbar 背景透明度
     * @param alpha
     * @return
     */
    public SnackbarUtils alpha(float alpha){
        if(getSnackbar()!=null){
            alpha = alpha>=1.0f?1.0f:(alpha<=0.0f?0.0f:alpha);
            getSnackbar().getView().setAlpha(alpha);
        }
        return this;
    }

    /**
     * 设置Snackbar显示的位置
     * @param gravity
     */
    public SnackbarUtils gravityFrameLayout(int gravity){
        if(getSnackbar()!=null){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getSnackbar().getView().getLayoutParams().width,getSnackbar().getView().getLayoutParams().height);
            params.gravity = gravity;
            getSnackbar().getView().setLayoutParams(params);
        }
        return this;
    }

    /**
     * 设置Snackbar显示的位置,当Snackbar和CoordinatorLayout组合使用的时候
     * @param gravity
     */
    public SnackbarUtils gravityCoordinatorLayout(int gravity){
        if(getSnackbar()!=null){
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(getSnackbar().getView().getLayoutParams().width,getSnackbar().getView().getLayoutParams().height);
            params.gravity = gravity;
            getSnackbar().getView().setLayoutParams(params);
        }
        return this;
    }

    /**
     * 设置按钮文字内容 及 点击监听
     *      {@link Snackbar#setAction(CharSequence, View.OnClickListener)}
     * @param resId
     * @param listener
     * @return
     */
    public SnackbarUtils setAction(@StringRes int resId, View.OnClickListener listener){
        if(getSnackbar()!=null){
            return setAction(getSnackbar().getView().getResources().getText(resId), listener);
        }else {
            return this;
        }
    }

    /**
     * 设置按钮文字内容 及 点击监听
     *      {@link Snackbar#setAction(CharSequence, View.OnClickListener)}
     * @param text
     * @param listener
     * @return
     */
    public SnackbarUtils setAction(CharSequence text, View.OnClickListener listener){
        if(getSnackbar()!=null){
            getSnackbar().setAction(text,listener);
        }
        return this;
    }

    /**
     * 设置 mSnackbar 展示完成 及 隐藏完成 的监听
     * @param setCallback
     * @return
     */
    public SnackbarUtils setCallback(Snackbar.Callback setCallback){
        if(getSnackbar()!=null){
            getSnackbar().setCallback(setCallback);
        }
        return this;
    }

    /**
     * 设置TextView(@+id/snackbar_text)左右两侧的图片
     * @param leftDrawable
     * @param rightDrawable
     * @return
     */
    public SnackbarUtils leftAndRightDrawable(@Nullable @DrawableRes Integer leftDrawable, @Nullable @DrawableRes Integer rightDrawable){
        if(getSnackbar()!=null){
            Drawable drawableLeft = null;
            Drawable drawableRight = null;
            if(leftDrawable!=null){
                try {
                    drawableLeft = getSnackbar().getView().getResources().getDrawable(leftDrawable.intValue());
                }catch (Exception e){
                }
            }
            if(rightDrawable!=null){
                try {
                    drawableRight = getSnackbar().getView().getResources().getDrawable(rightDrawable.intValue());
                }catch (Exception e){
                }
            }
            return leftAndRightDrawable(drawableLeft,drawableRight);
        }else {
            return this;
        }
    }

    /**
     * 设置TextView(@+id/snackbar_text)左右两侧的图片
     * @param leftDrawable
     * @param rightDrawable
     * @return
     */
    public SnackbarUtils leftAndRightDrawable(@Nullable Drawable leftDrawable, @Nullable Drawable rightDrawable){
        if(getSnackbar()!=null){
            TextView message = (TextView) getSnackbar().getView().findViewById(R.id.snackbar_text);
            LinearLayout.LayoutParams paramsMessage = (LinearLayout.LayoutParams) message.getLayoutParams();
            paramsMessage = new LinearLayout.LayoutParams(paramsMessage.width, paramsMessage.height,0.0f);
            message.setLayoutParams(paramsMessage);
            message.setCompoundDrawablePadding(message.getPaddingLeft());
            int textSize = (int) message.getTextSize();
            Log.e("Jet","textSize:"+textSize);
            if(leftDrawable!=null){
                leftDrawable.setBounds(0,0,textSize,textSize);
            }
            if(rightDrawable!=null){
                rightDrawable.setBounds(0,0,textSize,textSize);
            }
            message.setCompoundDrawables(leftDrawable,null,rightDrawable,null);
            LinearLayout.LayoutParams paramsSpace = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
            ((Snackbar.SnackbarLayout)getSnackbar().getView()).addView(new Space(getSnackbar().getView().getContext()),1,paramsSpace);
        }
        return this;
    }

    /**
     * 设置TextView(@+id/snackbar_text)中文字的对齐方式 居中
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public SnackbarUtils messageCenter(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            if(getSnackbar()!=null){
                TextView message = (TextView) getSnackbar().getView().findViewById(R.id.snackbar_text);
                //View.setTextAlignment需要SDK>=17
                message.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                message.setGravity(Gravity.CENTER);
            }
        }
        return this;
    }

    /**
     * 设置TextView(@+id/snackbar_text)中文字的对齐方式 居右
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public SnackbarUtils messageRight(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            if(getSnackbar()!=null){
                TextView message = (TextView) getSnackbar().getView().findViewById(R.id.snackbar_text);
                //View.setTextAlignment需要SDK>=17
                message.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                message.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
            }
        }
        return this;
    }

    /**
     * 向Snackbar布局中添加View(Google不建议,复杂的布局应该使用DialogFragment进行展示)
     * @param layoutId  要添加的View的布局文件ID
     * @param index
     * @return
     */
    public SnackbarUtils addView(int layoutId, int index) {
        if(getSnackbar()!=null){
            //加载布局文件新建View
            View addView = LayoutInflater.from(getSnackbar().getView().getContext()).inflate(layoutId,null);
            return addView(addView,index);
        }else {
            return this;
        }
    }

    /**
     * 向Snackbar布局中添加View(Google不建议,复杂的布局应该使用DialogFragment进行展示)
     * @param addView
     * @param index
     * @return
     */
    public SnackbarUtils addView(View addView, int index) {
        if(getSnackbar()!=null){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);//设置新建布局参数
            //设置新建View在Snackbar内垂直居中显示
            params.gravity= Gravity.CENTER_VERTICAL;
            addView.setLayoutParams(params);
            ((Snackbar.SnackbarLayout)getSnackbar().getView()).addView(addView,index);
        }
        return this;
    }

    /**
     * 设置Snackbar布局的外边距
     *      注:经试验发现,调用margins后再调用 gravityFrameLayout,则margins无效.
     *          为保证margins有效,应该先调用 gravityFrameLayout,在 show() 之前调用 margins
     * @param margin
     * @return
     */
    public SnackbarUtils margins(int margin){
        if(getSnackbar()!=null){
            return margins(margin,margin,margin,margin);
        }else {
            return this;
        }
    }

    /**
     * 设置Snackbar布局的外边距
     *      注:经试验发现,调用margins后再调用 gravityFrameLayout,则margins无效.
     *         为保证margins有效,应该先调用 gravityFrameLayout,在 show() 之前调用 margins
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public SnackbarUtils margins(int left, int top, int right, int bottom){
        if(getSnackbar()!=null){
            ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
            ((ViewGroup.MarginLayoutParams) params).setMargins(left,top,right,bottom);
            getSnackbar().getView().setLayoutParams(params);
        }
        return this;
    }

    /**
     * 经试验发现:
     *      执行过{@link SnackbarUtils#backColor(int)}后:background instanceof ColorDrawable
     *      未执行过{@link SnackbarUtils#backColor(int)}:background instanceof GradientDrawable
     * @return
     */
    /*
    public SnackbarUtils radius(){
        Drawable background = snackbarWeakReference.get().getView().getBackground();
        if(background instanceof GradientDrawable){
            Log.e("Jet","radius():GradientDrawable");
        }
        if(background instanceof ColorDrawable){
            Log.e("Jet","radius():ColorDrawable");
        }
        if(background instanceof StateListDrawable){
            Log.e("Jet","radius():StateListDrawable");
        }
        Log.e("Jet","radius()background:"+background.getClass().getSimpleName());
        return new SnackbarUtils(mSnackbar);
    }
    */

    /**
     * 通过SnackBar现在的背景,获取其设置圆角值时候所需的GradientDrawable实例
     * @param backgroundOri
     * @return
     */
    private GradientDrawable getRadiusDrawable(Drawable backgroundOri){
        GradientDrawable background = null;
        if(backgroundOri instanceof GradientDrawable){
            background = (GradientDrawable) backgroundOri;
        }else if(backgroundOri instanceof ColorDrawable){
            int backgroundColor = ((ColorDrawable)backgroundOri).getColor();
            background = new GradientDrawable();
            background.setColor(backgroundColor);
        }else {
        }
        return background;
    }
    /**
     * 设置Snackbar布局的圆角半径值
     * @param radius    圆角半径
     * @return
     */
    public SnackbarUtils radius(float radius){
        if(getSnackbar()!=null){
            //将要设置给mSnackbar的背景
            GradientDrawable background = getRadiusDrawable(getSnackbar().getView().getBackground());
            if(background != null){
                radius = radius<=0?12:radius;
                background.setCornerRadius(radius);
                getSnackbar().getView().setBackgroundDrawable(background);
            }
        }
        return this;
    }

    /**
     * 设置Snackbar布局的圆角半径值及边框颜色及边框宽度
     * @param radius
     * @param strokeWidth
     * @param strokeColor
     * @return
     */
    public SnackbarUtils radius(int radius, int strokeWidth, @ColorInt int strokeColor){
        if(getSnackbar()!=null){
            //将要设置给mSnackbar的背景
            GradientDrawable background = getRadiusDrawable(getSnackbar().getView().getBackground());
            if(background != null){
                radius = radius<=0?12:radius;
                strokeWidth = strokeWidth<=0?1:(strokeWidth>=getSnackbar().getView().findViewById(R.id.snackbar_text).getPaddingTop()?2:strokeWidth);
                background.setCornerRadius(radius);
                background.setStroke(strokeWidth,strokeColor);
                getSnackbar().getView().setBackgroundDrawable(background);
            }
        }
        return this;
    }

    /**
     * 计算单行的Snackbar的高度值(单位 pix)
     * @return
     */
    private int calculateSnackBarHeight(){
        /*
        <TextView
                android:id="@+id/snackbar_text"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:paddingTop="@dimen/design_snackbar_padding_vertical"
                android:paddingBottom="@dimen/design_snackbar_padding_vertical"
                android:paddingLeft="@dimen/design_snackbar_padding_horizontal"
                android:paddingRight="@dimen/design_snackbar_padding_horizontal"
                android:textAppearance="@style/TextAppearance.Design.Snackbar.Message"
                android:maxLines="@integer/design_snackbar_text_max_lines"
                android:layout_gravity="center_vertical|left|start"
                android:ellipsize="end"
                android:textAlignment="viewStart"/>
        */
        //文字高度+paddingTop+paddingBottom : 14sp + 14dp*2
        int SnackbarHeight = ScreenUtil.dp2px(getSnackbar().getView().getContext(),28) + ScreenUtil.sp2px(getSnackbar().getView().getContext(),14);
        Log.e("Jet","直接获取MessageView高度:"+getSnackbar().getView().findViewById(R.id.snackbar_text).getHeight());
        return SnackbarHeight;
    }

    /**
     * 设置Snackbar显示在指定View的上方
     *      注:暂时仅支持单行的Snackbar,因为{@link SnackbarUtils#calculateSnackBarHeight()}暂时仅支持单行Snackbar的高度计算
     * @param targetView        指定View
     * @param contentViewTop    Activity中的View布局区域 距离屏幕顶端的距离
     * @param marginLeft        左边距
     * @param marginRight       右边距
     * @return
     */
    public SnackbarUtils above(View targetView, int contentViewTop, int marginLeft, int marginRight){
        if(getSnackbar()!=null){
            marginLeft = marginLeft<=0?0:marginLeft;
            marginRight = marginRight<=0?0:marginRight;
            int[] locations = new int[2];
            targetView.getLocationOnScreen(locations);
            Log.e("Jet","距离屏幕左侧:"+locations[0]+"==距离屏幕顶部:"+locations[1]);
            int snackbarHeight = calculateSnackBarHeight();
            Log.e("Jet","Snackbar高度:"+snackbarHeight);
            //必须保证指定View的顶部可见 且 单行Snackbar可以完整的展示
            if(locations[1] >= contentViewTop+snackbarHeight){
                gravityFrameLayout(Gravity.BOTTOM);
                ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
                ((ViewGroup.MarginLayoutParams) params).setMargins(marginLeft,0,marginRight,getSnackbar().getView().getResources().getDisplayMetrics().heightPixels-locations[1]);
                getSnackbar().getView().setLayoutParams(params);
            }
        }
        return this;
    }

    //CoordinatorLayout
    public SnackbarUtils aboveCoordinatorLayout(View targetView, int contentViewTop, int marginLeft, int marginRight){
        if(getSnackbar()!=null){
            marginLeft = marginLeft<=0?0:marginLeft;
            marginRight = marginRight<=0?0:marginRight;
            int[] locations = new int[2];
            targetView.getLocationOnScreen(locations);
            Log.e("Jet","距离屏幕左侧:"+locations[0]+"==距离屏幕顶部:"+locations[1]);
            int snackbarHeight = calculateSnackBarHeight();
            Log.e("Jet","Snackbar高度:"+snackbarHeight);
            //必须保证指定View的顶部可见 且 单行Snackbar可以完整的展示
            if(locations[1] >= contentViewTop+snackbarHeight){
                gravityCoordinatorLayout(Gravity.BOTTOM);
                ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
                ((ViewGroup.MarginLayoutParams) params).setMargins(marginLeft,0,marginRight,getSnackbar().getView().getResources().getDisplayMetrics().heightPixels-locations[1]);
                getSnackbar().getView().setLayoutParams(params);
            }
        }
        return this;
    }

    /**
     * 设置Snackbar显示在指定View的下方
     *      注:暂时仅支持单行的Snackbar,因为{@link SnackbarUtils#calculateSnackBarHeight()}暂时仅支持单行Snackbar的高度计算
     * @param targetView        指定View
     * @param contentViewTop    Activity中的View布局区域 距离屏幕顶端的距离
     * @param marginLeft        左边距
     * @param marginRight       右边距
     * @return
     */
    public SnackbarUtils bellow(View targetView, int contentViewTop, int marginLeft, int marginRight){
        if(getSnackbar()!=null){
            marginLeft = marginLeft<=0?0:marginLeft;
            marginRight = marginRight<=0?0:marginRight;
            int[] locations = new int[2];
            targetView.getLocationOnScreen(locations);
            int snackbarHeight = calculateSnackBarHeight();
            int screenHeight = ScreenUtil.getScreenHeight(getSnackbar().getView().getContext());
            //必须保证指定View的底部可见 且 单行Snackbar可以完整的展示
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                //为什么要'+2'? 因为在Android L(Build.VERSION_CODES.LOLLIPOP)以上,例如Button会有一定的'阴影(shadow)',阴影的大小由'高度(elevation)'决定.
                //为了在Android L以上的系统中展示的Snackbar不要覆盖targetView的阴影部分太大比例,所以人为减小2px的layout_marginBottom属性.
                if(locations[1]+targetView.getHeight()>=contentViewTop&&locations[1]+targetView.getHeight()+snackbarHeight+2<=screenHeight){
                    gravityFrameLayout(Gravity.BOTTOM);
                    ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
                    ((ViewGroup.MarginLayoutParams) params).setMargins(marginLeft,0,marginRight,screenHeight - (locations[1]+targetView.getHeight()+snackbarHeight+2));
                    getSnackbar().getView().setLayoutParams(params);
                }
            }else {
                if(locations[1]+targetView.getHeight()>=contentViewTop&&locations[1]+targetView.getHeight()+snackbarHeight<=screenHeight){
                    gravityFrameLayout(Gravity.BOTTOM);
                    ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
                    ((ViewGroup.MarginLayoutParams) params).setMargins(marginLeft,0,marginRight,screenHeight - (locations[1]+targetView.getHeight()+snackbarHeight));
                    getSnackbar().getView().setLayoutParams(params);
                }
            }
        }
        return this;
    }

    public SnackbarUtils bellowCoordinatorLayout(View targetView, int contentViewTop, int marginLeft, int marginRight){
        if(getSnackbar()!=null){
            marginLeft = marginLeft<=0?0:marginLeft;
            marginRight = marginRight<=0?0:marginRight;
            int[] locations = new int[2];
            targetView.getLocationOnScreen(locations);
            int snackbarHeight = calculateSnackBarHeight();
            int screenHeight = ScreenUtil.getScreenHeight(getSnackbar().getView().getContext());
            //必须保证指定View的底部可见 且 单行Snackbar可以完整的展示
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                //为什么要'+2'? 因为在Android L(Build.VERSION_CODES.LOLLIPOP)以上,例如Button会有一定的'阴影(shadow)',阴影的大小由'高度(elevation)'决定.
                //为了在Android L以上的系统中展示的Snackbar不要覆盖targetView的阴影部分太大比例,所以人为减小2px的layout_marginBottom属性.
                if(locations[1]+targetView.getHeight()>=contentViewTop&&locations[1]+targetView.getHeight()+snackbarHeight+2<=screenHeight){
                    gravityCoordinatorLayout(Gravity.BOTTOM);
                    ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
                    ((ViewGroup.MarginLayoutParams) params).setMargins(marginLeft,0,marginRight,screenHeight - (locations[1]+targetView.getHeight()+snackbarHeight+2));
                    getSnackbar().getView().setLayoutParams(params);
                }
            }else {
                if(locations[1]+targetView.getHeight()>=contentViewTop&&locations[1]+targetView.getHeight()+snackbarHeight<=screenHeight){
                    gravityCoordinatorLayout(Gravity.BOTTOM);
                    ViewGroup.LayoutParams params = getSnackbar().getView().getLayoutParams();
                    ((ViewGroup.MarginLayoutParams) params).setMargins(marginLeft,0,marginRight,screenHeight - (locations[1]+targetView.getHeight()+snackbarHeight));
                    getSnackbar().getView().setLayoutParams(params);
                }
            }
        }
        return this;
    }


    /**
     * 显示 mSnackbar
     */
    public void show(){
        Log.e("Jet","show()");
        if(getSnackbar()!=null){
            Log.e("Jet","show");
            getSnackbar().show();
        }else {
            Log.e("Jet","已经被回收");
        }
    }
}
