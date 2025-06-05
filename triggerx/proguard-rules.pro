# TriggerX Library ProGuard Rules

# Keep the main public API classes and their members that were causing issues
-keep class com.meticha.triggerx.TriggerXActivity { *; }
-keep class com.meticha.triggerx.TriggerXAlarmScheduler { *; }
-keep class com.meticha.triggerx.dsl.TriggerX { *; }
-keep class com.meticha.triggerx.dsl.TriggerXConfig { *; }
-keep class com.meticha.triggerx.permission.PermissionState { *; }
-keep class com.meticha.triggerx.permission.TriggerXPermissionComposableKt { *; } # For top-level composable
-keep interface com.meticha.triggerx.provider.TriggerXDataProvider { *; }

# Keep other public API elements and necessary internal structures
-keep public interface com.meticha.triggerx.logger.TriggerXLogger { *; }

# Keep Android Components declared in the library's AndroidManifest.xml and their critical members
-keep class com.meticha.triggerx.DefaultTriggerActivity { <init>(...); }
-keepclassmembers class com.meticha.triggerx.DefaultTriggerActivity {
    public void onCreate(android.os.Bundle);
    public void AlarmContent(androidx.compose.runtime.Composer, int);
}

-keep class com.meticha.triggerx.receivers.TriggerXAlarmReceiver { <init>(...); }
-keepclassmembers class com.meticha.triggerx.receivers.TriggerXAlarmReceiver {
    public void onReceive(android.content.Context, android.content.Intent);
}

-keep class com.meticha.triggerx.services.TriggerXForegroundService { *; }

# Keep members of TriggerXActivity (base class)
-keepclassmembers class com.meticha.triggerx.TriggerXActivity {
    public void onCreate(android.os.Bundle);
    public abstract void AlarmContent(androidx.compose.runtime.Composer, int);
}

# Keep TriggerXPreferences as it's used for loading config
-keep class com.meticha.triggerx.preference.TriggerXPreferences {
    public static final com.meticha.triggerx.preference.TriggerXPreferences INSTANCE;
    public java.lang.Object load(android.content.Context, kotlin.coroutines.Continuation);
    public java.lang.Object save(android.content.Context, com.meticha.triggerx.dsl.TriggerXConfig, kotlin.coroutines.Continuation);
}

# Keep Enums and their necessary methods
-keepclassmembers enum com.meticha.triggerx.permission.PermissionType {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Kotlin metadata for reflection and interoperability
-keepattributes Signature,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,InnerClasses,EnclosingMethod,Kotlin*

# Standard ProGuard rules often included:
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