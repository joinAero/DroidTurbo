MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

LOCAL_PATH := $(MY_LOCAL_PATH)

include $(CLEAR_VARS)

LOCAL_MODULE := jni_utils
LOCAL_SRC_FILES := \
  jni_main.cpp \
  jni_utils.cpp
LOCAL_LDLIBS += -llog -ljnigraphics
#LOCAL_CPPFLAGS += -DDEBUG

include $(BUILD_SHARED_LIBRARY)
