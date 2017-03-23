#include "jni_helper.h"
#include "jni_utils.h"

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

static JavaVM *g_jvm = nullptr;

CJNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void */*reserved*/) {
    DBG_LOGI(__FUNCTION__);
    JNIEnv *env = nullptr;
    if (jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGW("JNI_OnLoad GetEnv error");
        return JNI_ERR;
    }
    g_jvm = jvm;

    // JNIUtils.java
    static JNINativeMethod JNIUtils_methods[] = {
        {"stringFromJNI", "()Ljava/lang/String;", reinterpret_cast<void*>(native_stringFromJNI)},
        {"grayscale", "(Landroid/graphics/Bitmap;)V", reinterpret_cast<void*>(native_grayscale)}
    };
    REGISTER_NATIVES(env, "cc/eevee/turbo/util/JNIUtils", JNIUtils_methods);

    return JNI_VERSION_1_6;
}

CJNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void */*reserved*/) {
    DBG_LOGI(__FUNCTION__);
}
