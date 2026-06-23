# Add project specific ProGuard rules here.

# Keep kotlinx.serialization generated serializers.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Keep @Serializable classes and their synthetic companion serializers.
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit keeps generic signatures for its interfaces.
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Room generated code.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
