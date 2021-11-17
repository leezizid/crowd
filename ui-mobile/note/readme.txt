此工程为安卓版本和苹果版本提供源码、调试和构建支持，需要注意以下事项：
1、index.js、AppDelegate.m、MainActivity.java，这三个文件中包含的根组件名称统一为app，无论对于任何版本，都不要更改
    （该名称无实际意义，上述三个文件必须保持一致）

2、LK
    index.js：import App from './apps/lk/LKApp'
    app_version改为最新版本
（1）苹果版本IPA打包
    BundleId：com.xxx.yyy.lk
    DisplayName：唠嗑
（2）安卓版本APK打包
    build.gradle/applicationId：com.xxx.yyy.lk
    build.gradle/versionName：改为最新版本
    strings.xml/app_name：唠嗑
    AndroidManifest.xml/android:icon:@mipmap/lk
    cd android && ./gradlew assembleRelease
（3）PPK打包
    pushy bundle --platform ios
    pushy bundle --platform android
