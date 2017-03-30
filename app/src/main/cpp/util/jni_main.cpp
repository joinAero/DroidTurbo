#include "jni_helper.h"
#include "jni_utils.h"

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
        {"deviceQuery", "()Z", reinterpret_cast<void*>(native_deviceQuery)},
        {"grayscale", "(Landroid/graphics/Bitmap;)V", reinterpret_cast<void*>(native_grayscale)},
        {"grayscale_gpu", "(Landroid/graphics/Bitmap;)V", reinterpret_cast<void*>(native_grayscale_gpu)}
    };
    REGISTER_NATIVES(env, "cc/eevee/turbo/util/JNIUtils", JNIUtils_methods);

    return JNI_VERSION_1_6;
}

CJNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void */*reserved*/) {
    DBG_LOGI(__FUNCTION__);
}
