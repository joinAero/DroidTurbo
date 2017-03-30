#ifndef JNI_HELPER_H_
#define JNI_HELPER_H_
#pragma once

#include <stdint.h>
#include <jni.h>
#include <android/log.h>

#ifndef LOG_TAG
#define LOG_TAG "DroidTurbo"
#endif
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#ifdef DEBUG
    #define DBG_LOGI(...) LOGI(__VA_ARGS__)
    #define DBG_LOGW(...) LOGW(__VA_ARGS__)
    #define DBG_LOGE(...) LOGE(__VA_ARGS__)
#else
    #define DBG_LOGI(...)
    #define DBG_LOGW(...)
    #define DBG_LOGE(...)
#endif

#define CJNIEXPORT extern "C" JNIEXPORT

#define REGISTER_NATIVES(env, cls_name, methods) \
do { \
    jint n = sizeof(methods) / sizeof(methods[0]); \
    jclass clazz = env->FindClass(cls_name); \
    jint result = env->RegisterNatives(clazz, methods, n); \
    if (result < 0) { \
        LOGE("RegisterNatives error: %s", cls_name); \
        return JNI_ERR; \
    } \
    DBG_LOGI("RegisterNatives %d methods: %s", n, cls_name); \
} while (0)

typedef struct {
    uint8_t r;
    uint8_t g;
    uint8_t b;
    uint8_t a;
} rgba_t;

#endif  // JNI_HELPER_H_
