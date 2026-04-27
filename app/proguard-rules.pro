# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line/file information so release-like debug APK stack traces
# still point to useful source locations in Logcat.
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit suspend APIs rely on generic signatures and runtime annotations.
# Without these, release builds can crash with ClassCastException when calling endpoints.
# Keep Retrofit HTTP interfaces and their annotated methods available for reflection.
-keep,allowobfuscation interface com.isyara.app.data.remote.retrofit.ApiService
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowshrinking,allowobfuscation class kotlin.coroutines.Continuation

# Keep Gson models used by API parsing in release builds.
-keep class com.isyara.app.data.remote.response.** { *; }
-keep class com.isyara.app.data.remote.retrofit.ApiService$* { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# MediaPipe Tasks uses generated protobuf fields from native/JNI code. R8 cannot
# see those reflective/native accesses, so release minification can remove fields
# such as ExternalFile.filePointerMeta_ and make the detector fail to initialize.
-keep class com.google.mediapipe.** { *; }
-keep class com.google.protobuf.** { *; }
