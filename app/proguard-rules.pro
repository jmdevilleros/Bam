# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep the entire custom card engine (we reimplemented it)
-keep class com.teoktonos.bam.model.cards.** { *; }

# Keep all Bam* game classes (they are heavily reflected / used by name in some places)
-keep class com.teoktonos.bam.Bam** { *; }

# Keep ViewBinding generated classes
-keep class com.teoktonos.bam.databinding.** { *; }

# Uncomment these if you need line numbers for debugging stack traces
#-keepattributes SourceFile,LineNumberTable
#-renamesourcefileattribute SourceFile
