#ifndef JNI_UTILS_H_
#define JNI_UTILS_H_
#pragma once

#include <jni.h>

jstring native_stringFromJNI(JNIEnv *env, jobject thiz);

void native_deviceReset(JNIEnv *env, jobject thiz);
jboolean native_deviceQuery(JNIEnv *env, jobject thiz);

void native_grayscale(JNIEnv *env, jobject thiz, jobject bitmap);
void native_grayscale_gpu(JNIEnv *env, jobject thiz, jobject bitmap);

#endif  // JNI_UTILS_H_
