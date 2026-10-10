# QuestRings Production ProGuard & R8 Optimization Rules

# -------------------------------------------------------------
# 1. Keep Annotated Data Models & Serialization
# -------------------------------------------------------------
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <methods>;
}

# Keep Domain Models and Entities for Firestore & Room Deserialization
-keep class com.questrings.domain.model.** { *; }
-keep class com.questrings.domain.config.** { *; }
-keep class com.questrings.data.local.entity.** { *; }
-keep class com.questrings.data.mapper.** { *; }

# -------------------------------------------------------------
# 2. Firebase & Google Play Services
# -------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses, EnclosingMethod
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Firestore field reflection & getters/setters
-keepclassmembers class com.questrings.domain.model.** {
    public <methods>;
    public <fields>;
}

# -------------------------------------------------------------
# 3. Room Database
# -------------------------------------------------------------
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep class com.questrings.data.local.dao.** { *; }
-keep class com.questrings.data.local.QuestRingsDatabase_Impl { *; }

# -------------------------------------------------------------
# 4. Hilt & Dagger
# -------------------------------------------------------------
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.ComponentEntryPoint { *; }
-keep class com.questrings.di.** { *; }

# -------------------------------------------------------------
# 5. Jetpack Compose & Kotlin Coroutines
# -------------------------------------------------------------
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# -------------------------------------------------------------
# 6. WorkManager
# -------------------------------------------------------------
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class com.questrings.data.sync.** { *; }
