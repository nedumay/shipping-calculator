# Project specific ProGuard rules

# Keep Room entities and DAOs
-keep class ru.nedumayy.shippingcalculator.data.db.model.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

#Keep model domain
-keep class ru.nedumayy.shippingcalculator.domain.model.** { *; }

# Hilt/Dagger rules
-keep class com.google.dagger.** { *; }
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class * extends dagger.hilt.internal.UnsafeCasts
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep class * implements dagger.hilt.internal.GeneratedComponentManager
-keep class * implements dagger.hilt.internal.UnsafeCasts

# Keep Compose/Kotlin specific attributes
-keepattributes Signature, Annotation, InnerClasses, EnclosingMethod
-keepattributes SourceFile, LineNumberTable

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext$ScheduledPatch {
    volatile <fields>;
}

# Navigation Compose
-keep class androidx.navigation.compose.** { *; }

# Retain information for debugging stack traces
-renamesourcefileattribute SourceFile
