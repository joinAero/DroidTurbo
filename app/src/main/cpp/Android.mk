MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

MY_C_INCLUDES := $(MY_LOCAL_PATH) $(MY_LOCAL_PATH)/base

include $(call all-subdir-makefiles)
