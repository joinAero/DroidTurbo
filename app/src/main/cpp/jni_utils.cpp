#include "jni_utils.h"
#include "jni_helper.h"

#include <functional>

#include <android/bitmap.h>

#include "device/gpu_grayscale.h"

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

using RGBAPixelsProcessor = std::function<void(const AndroidBitmapInfo &, uint32_t *)>;

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
    uint32_t *rgba_pixels = static_cast<uint32_t*>(pixels);

    if (processor) processor(info, rgba_pixels);

    AndroidBitmap_unlockPixels(env, bitmap);
}

static void grayscale(const AndroidBitmapInfo &info, uint32_t *rgba_pixels) {
    uint32_t n = info.width * info.height;
    uint32_t gray = 0;
    for (uint32_t i = 0; i < n; ++i) {
        // Y = 0.299*R + 0.587*G + 0.114*B
        uint32_t &rgba = rgba_pixels[i];
        gray = (uint32_t) (
                0.299 * ((rgba >> 24) & 0xFF) +
                0.587 * ((rgba >> 16) & 0xFF) +
                0.114 * ((rgba >> 8) & 0xFF));
        rgba = ((gray & 0xFF) << 24) +
                ((gray & 0xFF) << 16) +
                ((gray & 0xFF) << 8) +
                (gray & 0xFF);
    }
}

void native_grayscale(JNIEnv *env, jobject thiz, jobject bitmap) {
    DBG_LOGI(__FUNCTION__);
    process_rgba_pixels(env, thiz, bitmap, grayscale);
}

void native_grayscale_gpu(JNIEnv *env, jobject thiz, jobject bitmap) {
    DBG_LOGI(__FUNCTION__);
    process_rgba_pixels(env, thiz, bitmap, gpu_grayscale);
}
