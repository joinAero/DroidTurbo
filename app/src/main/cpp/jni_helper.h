#ifndef JNI_HELPER_H_
#define JNI_HELPER_H_
#pragma once

#include <stdint.h>
#include <jni.h>

#include "log.hpp"

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

// RGBA
typedef struct {
    uint8_t r;
    uint8_t g;
    uint8_t b;
    uint8_t a;
} rgba_t;

#endif  // JNI_HELPER_H_
