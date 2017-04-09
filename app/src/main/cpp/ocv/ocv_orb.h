#ifndef OCV_ORB_H_
#define OCV_ORB_H_
#pragma once

#include <jni.h>

void native_ocv_orb(JNIEnv *env, jobject thiz, jlong addr);
void native_ocv_orb_gpu(JNIEnv *env, jobject thiz, jlong addr);

#endif  // OCV_ORB_H_
