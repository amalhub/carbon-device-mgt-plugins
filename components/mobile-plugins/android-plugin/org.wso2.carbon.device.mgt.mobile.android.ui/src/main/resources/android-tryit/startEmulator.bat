@echo off
echo Welcome

java^
 -Dsdk.tools.url="https://dl.google.com/android/repository/tools_r25.2.5-windows.zip"^
  -Dplatform.tools.url="http://dl.google.com/android/repository/platform-tools_r25.0.3-windows.zip"^
  -Dbuild.tools.url="https://dl.google.com/android/repository/build-tools_r25.0.2-windows.zip"^
  -Dplatform.url="https://dl.google.com/android/repository/platform-23_r03.zip"^
  -Dsys.img.url="https://dl.google.com/android/repository/sys-img/android/x86-23_r09.zip"^
  -Dhaxm.url="https://dl.google.com/android/repository/extras/intel/haxm-windows_r6_0_5.zip"^
  -Ddownloaded.build.tool.name="android-7.1.1"^
  -Dbuild.tool.version="25.0.2"^
  -Ddownloaded.platform.name="android-6.0"^
  -Dtarget.version="android-23"^
  -Dos.target="x86"^
  -jar EmulatorJava.jar

