# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/marcel/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#Line numbers
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#NetGuard
-keepnames class cn.wsgwz.tun.** { *; }

#JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

#JNI callbacks
-keep class cn.wsgwz.tun.Allowed { *; }
-keep class cn.wsgwz.tun.Packet { *; }
-keep class cn.wsgwz.tun.ResourceRecord { *; }
-keep class cn.wsgwz.tun.Usage { *; }
-keep class cn.wsgwz.tun.ServiceTun {
    void nativeExit(java.lang.String);
    void nativeError(int, java.lang.String);
    void logPacket(cn.wsgwz.tun.Packet);
    void dnsResolved(cn.wsgwz.tun.ResourceRecord);
    boolean isDomainBlocked(java.lang.String);
    cn.wsgwz.tun.Allowed isAddressAllowed(cn.wsgwz.tun.Packet);
    void accountUsage(cn.wsgwz.tun.Usage);
}

-dontwarn android.support.**

#Support library
-keep class android.support.v7.widget.** { *; }
-dontwarn android.support.v4.**

#Picasso
#-dontwarn com.squareup.okhttp.**

#AdMob
#-dontwarn com.google.android.gms.internal.**
