# SuperCall

一个第三方来电秀Demo，主要通过 **BroadcastReceiver +悬浮窗显示实现** 和 **InCallService + Activity实现**

本人博客地址：
[Android 来电秀总结 -CSDN ](https://blog.csdn.net/qq_27948659/article/details/113593058)
[Android 来电秀总结 -简书 ](https://www.jianshu.com/p/167437971441)

## 后续更新方向
1. 添加包活lib，提高App在设置成功后 退居后台，成功拉起的概率
2. 项目中已经包含lib_ijk的代码，我们可以添加视频来电展示，添加美女或者豪车等全屏视频，效果更佳。
3. 由于反编译能力有限，对于多种机型权限的跳转（后续可以开起 无障碍服务，直接一步搞定多种需要用户手动设置操作）
4. 该Demo中有一部分不完善的Rom 权限跳转机制，后续还需要时间来完善。


## 版本迭代
>V1.2.0 使用activity替换悬浮窗，Service优化，实现拨号盘，完善电话状态监听及接听和挂断功能
>V1.1.0 适配电话接听及挂断
>V1.0.0 使用悬浮窗简单实现Android 9.0版本第三方电话应用

## 效果图

![WechatIMG39.jpeg](https://upload-images.jianshu.io/upload_images/6188347-f7356545da353b79.jpeg?imageMogr2/auto-orient/strip%7CimageView2/2/w/300)

## 感谢

[来电秀实现](https://ljd1996.github.io/2019/12/20/Android%E6%9D%A5%E7%94%B5%E7%A7%80%E5%AE%9E%E8%B7%B5/)
