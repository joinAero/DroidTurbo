MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

MY_SUBDIR_MKS := $(call all-subdir-makefiles)

$(info CUDA_TOOLKIT_ROOT: $(CUDA_TOOLKIT_ROOT))
$(info NDK_ROOT: $(NDK_ROOT))

# prebuilt

# cudart cufft nppc nppi npps ...

LOCAL_PATH := $(MY_LOCAL_PATH)
include $(CLEAR_VARS)
LOCAL_MODULE := cudart_static
LOCAL_SRC_FILES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/lib/libcudart_static.a
LOCAL_EXPORT_INCLUDES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include
include $(PREBUILT_STATIC_LIBRARY)

LOCAL_PATH := $(MY_LOCAL_PATH)
include $(CLEAR_VARS)
LOCAL_MODULE := cudart
LOCAL_SRC_FILES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/lib/libcudart.so
LOCAL_EXPORT_INCLUDES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include
include $(PREBUILT_SHARED_LIBRARY)

# jni_utils

LOCAL_PATH := $(MY_LOCAL_PATH)

include $(CLEAR_VARS)

LOCAL_MODULE := jni_utils
LOCAL_SRC_FILES := \
    jni_main.cpp \
    jni_utils.cpp
LOCAL_LDLIBS += -llog -ljnigraphics
LOCAL_SHARED_LIBRARIES += cudart
LOCAL_STATIC_LIBRARIES += gpu
#LOCAL_CPPFLAGS += -DDEBUG

include $(BUILD_SHARED_LIBRARY)

# subdirs

include $(MY_SUBDIR_MKS)

# Requirements
# 1) CodeWorks for Android 1R4
#   http://docs.nvidia.com/gameworks/index.html#developertools/mobile/codeworks_android/codeworks_android_1r4.htm
# 2) Android NDK with CMake, Please use r12+.
#
# References
# 1) InfiniTAM: https://github.com/victorprad/InfiniTAM
