# Requirements
# 1) CodeWorks for Android 1R4
#   http://docs.nvidia.com/gameworks/index.html#developertools/mobile/codeworks_android/codeworks_android_1r4.htm
# 2) Android NDK with CMake, Please use r12+.
#
# References
# 1) InfiniTAM: https://github.com/victorprad/InfiniTAM
#
# Issues
# 1) CUDA Error: CUDA driver version is insufficient for CUDA runtime version (err_num=35)
#
# CUDA Toolkit Documentation: http://docs.nvidia.com/cuda/index.html

MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

MY_C_INCLUDES := $(MY_LOCAL_PATH)

include $(call all-subdir-makefiles)
