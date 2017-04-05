LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := jni_utils
LOCAL_SRC_FILES := \
  jni_main.cpp \
  jni_utils.cpp
LOCAL_C_INCLUDES += $(MY_C_INCLUDES)
LOCAL_SHARED_LIBRARIES += cudart
LOCAL_STATIC_LIBRARIES += gpu
LOCAL_LDLIBS += -llog -ljnigraphics
LOCAL_CPPFLAGS += -DDEBUG -DTIME_COST

include $(BUILD_SHARED_LIBRARY)
