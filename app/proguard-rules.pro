# Keep Room entities and DAO methods (reflection-free, but safe default for release).
-keep class com.example.travelagency.data.** { *; }

# kotlinx.serialization for navigation routes.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class com.example.travelagency.** { kotlinx.serialization.KSerializer serializer(...); }
