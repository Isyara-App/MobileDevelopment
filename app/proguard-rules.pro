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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit suspend APIs rely on generic signatures and runtime annotations.
# Without these, release builds can crash with ClassCastException when calling endpoints.
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

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
