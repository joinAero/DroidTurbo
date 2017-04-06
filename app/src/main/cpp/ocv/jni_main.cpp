#include "jni_helper.h"
#include "ocv_grayscale.h"
#include "ocv_orb.h"

CJNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void */*reserved*/) {
    DBG_LOGI(__FUNCTION__);
    JNIEnv *env = nullptr;
    if (jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGW("JNI_OnLoad GetEnv error");
        return JNI_ERR;
    }

    static JNINativeMethod OcvGrayscaleActivity_methods[] = {
        {"grayscale", "(J)V", reinterpret_cast<void*>(native_ocv_grayscale)}
    };
    REGISTER_NATIVES(env, "cc/eevee/turbo/ui/fever/ocv/OcvGrayscaleActivity",
        OcvGrayscaleActivity_methods);

    static JNINativeMethod OcvORBActivity_methods[] = {
        {"orb", "(J)V", reinterpret_cast<void*>(native_ocv_orb)}
    };
    REGISTER_NATIVES(env, "cc/eevee/turbo/ui/fever/ocv/OcvORBActivity",
        OcvORBActivity_methods);

    return JNI_VERSION_1_6;
}

CJNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void */*reserved*/) {
    DBG_LOGI(__FUNCTION__);
}
