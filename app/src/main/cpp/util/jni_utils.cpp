#include "jni_utils.h"
#include "jni_helper.h"

#include <functional>

#include <android/bitmap.h>

jstring native_stringFromJNI(JNIEnv *env, jobject thiz) {
#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
    #define ABI "armeabi"
  #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif
    return env->NewStringUTF("Hello from JNI! Compiled with ABI " ABI ".");
}

using RGBAPixelsProcessor = std::function<void(const AndroidBitmapInfo &, rgba_t *)>;

static void process_rgba_pixels(JNIEnv *env, jobject thiz, jobject bitmap, RGBAPixelsProcessor processor) {
    AndroidBitmapInfo info;
    int result;
    if ((result = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed, error=%d", result);
        return;
    }
    DBG_LOGI("Bitmap size: %dx%d", info.width, info.height);
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }

    void *pixels;
    if ((result = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed, error=%d", result);
    }
    rgba_t *rgba_pixels = static_cast<rgba_t*>(pixels);

    if (processor) processor(info, rgba_pixels);

    AndroidBitmap_unlockPixels(env, bitmap);
}

static void grayscale(const AndroidBitmapInfo &info, rgba_t *rgba_pixels) {
    uint32_t n = info.width * info.height;
    uint8_t gray = 0;
    for (uint32_t i = 0; i < n; ++i) {
        // Y = 0.299*R + 0.587*G + 0.114*B
        rgba_t &rgba = rgba_pixels[i];
        gray = (uint8_t) (0.299 * rgba.r + 0.587 * rgba.g + 0.114 * rgba.b);
        rgba.r = rgba.g = rgba.b = gray;
    }
}

void native_grayscale(JNIEnv *env, jobject thiz, jobject bitmap) {
    DBG_LOGI(__FUNCTION__);
    process_rgba_pixels(env, thiz, bitmap, grayscale);
}
