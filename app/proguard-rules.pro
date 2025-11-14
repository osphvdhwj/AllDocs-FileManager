# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Apache POI classes
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**

# Keep PDF viewer
-keep class com.github.barteksc.pdfviewer.** { *; }

# Keep commons-compress
-keep class org.apache.commons.compress.** { *; }
-dontwarn org.apache.commons.compress.**

# Keep data classes
-keep class com.alldocs.filemanager.model.** { *; }

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**