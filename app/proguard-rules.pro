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


# Start from here
-dontwarn com.gemalto.jp2.JP2Decoder
-dontwarn com.gemalto.jp2.JP2Encoder
-dontwarn org.slf4j.impl.StaticLoggerBinder

# My custom
# Preserve all Gson models
-keep class com.graphees.statspos.domain.models.** { *; }

# Preserve Gson generic type information
-keepattributes Signature
-keepattributes *Annotation*

# Preserve Gson TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Prevent R8 from modifying iText classes
#-keep public class com.itextpdf.** { *; }
-keep class com.itextpdf.kernel.pdf.** { *; }
-keep class com.itextpdf.kernel.actions.** { *; }
-keep class com.itextpdf.commons.actions.** { *; }
-keep class com.itextpdf.io.** { *; }
-keep class com.itextpdf.layout.** { *; }

-dontwarn com.fasterxml.jackson.annotation.JsonInclude$Include
-dontwarn com.fasterxml.jackson.core.JsonGenerator$Feature
-dontwarn com.fasterxml.jackson.core.JsonProcessingException
-dontwarn com.fasterxml.jackson.core.PrettyPrinter
-dontwarn com.fasterxml.jackson.core.type.TypeReference
-dontwarn com.fasterxml.jackson.core.util.DefaultIndenter
-dontwarn com.fasterxml.jackson.core.util.DefaultPrettyPrinter$Indenter
-dontwarn com.fasterxml.jackson.core.util.DefaultPrettyPrinter
-dontwarn com.fasterxml.jackson.databind.DeserializationFeature
-dontwarn com.fasterxml.jackson.databind.JavaType
-dontwarn com.fasterxml.jackson.databind.JsonNode
-dontwarn com.fasterxml.jackson.databind.ObjectMapper
-dontwarn com.fasterxml.jackson.databind.ObjectWriter
-dontwarn com.fasterxml.jackson.databind.SerializationFeature
-dontwarn java.awt.Canvas
-dontwarn java.awt.Color
-dontwarn java.awt.Image
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ColorModel
-dontwarn java.awt.image.ImageProducer
-dontwarn java.awt.image.MemoryImageSource
-dontwarn java.awt.image.PixelGrabber
-dontwarn javax.imageio.ImageIO