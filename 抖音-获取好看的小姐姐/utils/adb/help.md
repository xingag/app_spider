Help on class ADB in module adbUtils.utils.adbUtils:

class ADB(__builtin__.object)
 |  单个设备，可不传入参数device_id
 |  
 |  Methods defined here:
 |  
 |  __init__(self, device_id='')
 |  
 |  adb(self, args)
 |      # adb命令
 |  
 |  callPhone(self, number)
 |      启动拨号器拨打电话
 |      usage: callPhone(10086)
 |  
 |  clearAppData(self, packageName)
 |      清除应用用户数据
 |      usage: clearAppData("com.android.contacts")
 |  
 |  fastboot(self)
 |      进入fastboot模式
 |  
 |  getAndroidVersion(self)
 |      获取设备中的Android版本号，如4.2.2
 |  
 |  getAppStartTotalTime(self, component)
 |      获取启动应用所花时间
 |      usage: getAppStartTotalTime("com.android.settings/.Settings")
 |  
 |  getBatteryLevel(self)
 |      获取电池电量
 |  
 |  getBatteryStatus(self)
 |      获取电池充电状态
 |      BATTERY_STATUS_UNKNOWN：未知状态
 |      BATTERY_STATUS_CHARGING: 充电状态
 |      BATTERY_STATUS_DISCHARGING: 放电状态
 |      BATTERY_STATUS_NOT_CHARGING：未充电
 |      BATTERY_STATUS_FULL: 充电已满
 |  
 |  getBatteryTemp(self)
 |      获取电池温度
 |  
 |  getCurrentActivity(self)
 |      获取当前运行应用的activity
 |  
 |  getCurrentPackageName(self)
 |      获取当前运行的应用的包名
 |  
 |  getDeviceID(self)
 |      获取设备id号，return serialNo
 |  
 |  getDeviceModel(self)
 |      获取设备型号
 |  
 |  getDeviceState(self)
 |      获取设备状态： offline | bootloader | device
 |  
 |  getFocusedPackageAndActivity(self)
 |      获取当前应用界面的包名和Activity，返回的字符串格式为：packageName/activityName
 |  
 |  getMatchingAppList(self, keyword)
 |      模糊查询与keyword匹配的应用包名列表
 |      usage: getMatchingAppList("qq")
 |  
 |  getPid(self, packageName)
 |      获取进程pid
 |      args:
 |      - packageName -: 应用包名
 |      usage: getPid("com.android.settings")
 |  
 |  getScreenResolution(self)
 |      获取设备屏幕分辨率，return (width, high)
 |  
 |  getSdkVersion(self)
 |      获取设备SDK版本号     
 |  
 |  getSystemAppList(self)
 |      获取设备中安装的系统应用包名列表
 |  
 |  getThirdAppList(self)
 |      获取设备中安装的第三方应用包名列表
 |  
 |  installApp(self, appFile)
 |      安装app，app名字不能含中文字符
 |      args:
 |      - appFile -: app路径
 |      usage: install("d:\apps\Weico.apk")
 |  
 |  isInstall(self, packageName)
 |      判断应用是否安装，已安装返回True，否则返回False
 |      usage: isInstall("com.example.apidemo")
 |  
 |  killProcess(self, pid)
 |      杀死应用进程
 |      args:
 |      - pid -: 进程pid值
 |      usage: killProcess(154)
 |      注：杀死系统应用进程需要root权限
 |  
 |  longPress(self, e=None, x=None, y=None)
 |      长按屏幕的某个坐标位置, Android 4.4
 |      usage: longPress(e)
 |             longPress(x=0.5, y=0.5)
 |  
 |  longPressByRatio(self, ratioWidth, ratioHigh)
 |      通过比例长按屏幕某个位置, Android.4.4
 |      usage: longPressByRatio(0.5, 0.5) 长按屏幕中心位置
 |  
 |  longPressElement(self, e)
 |      长按元素, Android 4.4
 |  
 |  longPressKey(self, keycode)
 |      发送一个按键长按事件，Android 4.4以上
 |      usage: longPressKey(keycode.HOME)
 |  
 |  quitApp(self, packageName)
 |      退出app，类似于kill掉进程
 |      usage: quitApp("com.android.settings")
 |  
 |  reboot(self)
 |      重启设备
 |  
 |  removeApp(self, packageName)
 |      卸载应用
 |      args:
 |      - packageName -:应用包名，非apk名
 |  
 |  resetCurrentApp(self)
 |      重置当前应用
 |  
 |  sendKeyEvent(self, keycode)
 |      发送一个按键事件
 |      args:
 |      - keycode -:
 |      http://developer.android.com/reference/android/view/KeyEvent.html
 |      usage: sendKeyEvent(keycode.HOME)
 |  
 |  sendText(self, string)
 |      发送一段文本，只能包含英文字符和空格，多个空格视为一个空格
 |      usage: sendText("i am unique")
 |  
 |  shell(self, args)
 |      # adb shell命令
 |  
 |  startActivity(self, component)
 |      启动一个Activity
 |      usage: startActivity(component = "com.android.settinrs/.Settings")
 |  
 |  startWebpage(self, url)
 |      使用系统默认浏览器打开一个网页
 |      usage: startWebpage("http://www.baidu.com")
 |  
 |  swipe(self, e1=None, e2=None, start_x=None, start_y=None, end_x=None, end_y=None, duration=' ')
 |      滑动事件，Android 4.4以上可选duration(ms)
 |      usage: swipe(e1, e2)
 |             swipe(e1, end_x=200, end_y=500)
 |             swipe(start_x=0.5, start_y=0.5, e2)
 |  
 |  swipeByCoord(self, start_x, start_y, end_x, end_y, duration=' ')
 |      滑动事件，Android 4.4以上可选duration(ms)
 |      usage: swipe(800, 500, 200, 500)
 |  
 |  swipeByRatio(self, start_ratioWidth, start_ratioHigh, end_ratioWidth, end_ratioHigh, duration=' ')
 |      通过比例发送滑动事件，Android 4.4以上可选duration(ms)
 |      usage: swipeByRatio(0.9, 0.5, 0.1, 0.5) 左滑
 |  
 |  swipeToDown(self)
 |      下滑屏幕
 |  
 |  swipeToLeft(self)
 |      左滑屏幕
 |  
 |  swipeToRight(self)
 |      右滑屏幕
 |  
 |  swipeToUp(self)
 |      上滑屏幕
 |  
 |  touch(self, e=None, x=None, y=None)
 |      触摸事件
 |      usage: touch(e), touch(x=0.5,y=0.5)
 |  
 |  touchByElement(self, element)
 |      点击元素
 |      usage: touchByElement(Element().findElementByName(u"计算器"))
 |  
 |  touchByRatio(self, ratioWidth, ratioHigh)
 |      通过比例发送触摸事件
 |      args:
 |      - ratioWidth -:width占比, 0<ratioWidth<1
 |      - ratioHigh -: high占比, 0<ratioHigh<1
 |      usage: touchByRatio(0.5, 0.5) 点击屏幕中心位置
 |  
 |  ----------------------------------------------------------------------   

on module adbUtils.utils.element in adbUtils.utils:

Help on class Element in module adbUtils.utils.element:

class Element(__builtin__.object)
 |  通过元素定位
 |  
 |  Methods defined here:
 |  
 |  __init__(self, device_id='')
 |      初始化，获取系统临时文件存储目录，定义匹配数字模式
 |  
 |  findElementByClass(self, className)
 |      通过元素类名定位单个元素
 |      usage: findElementByClass("android.widget.TextView")
 |  
 |  findElementByContentDesc(self, contentDesc)
 |      通过元素的content-desc定位单个元素
 |  
 |  findElementById(self, id)
 |      通过元素的resource-id定位单个元素
 |      usage: findElementsById("com.android.deskclock:id/imageview")
 |  
 |  findElementByName(self, name)
 |      通过元素名称定位单个元素
 |      usage: findElementByName(u"设置")
 |  
 |  findElementsByClass(self, className)
 |      通过元素类名定位多个相同class的元素
 |  
 |  findElementsByContentDesc(self, contentDesc)
 |      通过元素的content-desc定位多个相同的元素
 |  
 |  findElementsById(self, id)
 |      通过元素的resource-id定位多个相同id的元素
 |  
 |  findElementsByName(self, name)
 |      通过元素名称定位多个相同text的元素
 |  
 |  getElementBoundByClass(self, className)
 |      通过元素类名获取单个元素的区域
 |  
 |  getElementBoundByContentDesc(self, contentDesc)
 |      通过元素content-desc获取单个元素的区域
 |  
 |  getElementBoundById(self, id)
 |      通过元素id获取单个元素的区域
 |  
 |  getElementBoundByName(self, name)
 |      通过元素名称获取单个元素的区域
 |  
 |  getElementBoundsByClass(self, className)
 |      通过元素类名获取多个相同class元素的区域
 |  
 |  getElementBoundsByContentDesc(self, contentDesc)
 |      通过元素content-desc获取多个相同元素的区域
 |  
 |  getElementBoundsById(self, id)
 |      通过元素id获取多个相同resource-id元素的区域
 |  
 |  getElementBoundsByName(self, name)
 |      通过元素名称获取多个相同text元素的区域
 |  
 |  isElementsCheckedByClass(self, className)
 |      通过元素类名判断checked的布尔值，返回布尔值列表
 |  
 |  isElementsCheckedById(self, id)
 |      通过元素id判断checked的布尔值，返回布尔值列表
 |  
 |  isElementsCheckedByName(self, name)
 |      通过元素名称判断checked的布尔值，返回布尔值列表
 |  
 |  ----------------------------------------------------------------------


FUNCTIONS
    PATH lambda p


on module adbUtils.utils.imageUtils in adbUtils.utils:

NAME
    adbUtils.utils.imageUtils - #coding=utf-8



CLASSES
    __builtin__.object
        ImageUtils
    
    class ImageUtils(__builtin__.object)
     |  Methods defined here:
     |  
     |  __init__(self)
     |      初始化，获取系统临时文件存放目录
     |  
     |  loadImage(self, imageName)
     |      加载本地图片
     |      usage: lodImage("d:\screen\image.png")
     |  
     |  sameAs(self, loadImage)
     |      比较两张截图的相似度，完全相似返回True
     |      usage： load = loadImage("d:\screen\image.png")
     |              screen().subImage(100, 100, 400, 400).sameAs(load)
     |  
     |  screenShot(self)
     |      截取设备屏幕
     |  
     |  subImage(self, box)
     |      截取指定像素区域的图片
     |      usage: box = (100, 100, 600, 600)
     |            screenShot().subImage(box)
     |  
     |  writeToFile(self, dirPath, imageName, form='png')
     |      将截屏文件写到本地
     |      usage: screenShot().writeToFile("d:\screen", "image")
     |  
     |  ----------------------------------------------------------------------
