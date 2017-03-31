#ifndef OCV_GRAYSCALE_H_
#define OCV_GRAYSCALE_H_
#pragma once

#include <jni.h>

void native_ocv_grayscale(JNIEnv *env, jobject thiz, jlong addr);
void native_ocv_grayscale_gpu(JNIEnv *env, jobject thiz, jlong addr);

#endif  // OCV_GRAYSCALE_H_
