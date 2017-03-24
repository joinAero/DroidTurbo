#ifndef GPU_GRAYSCALE_H_
#define GPU_GRAYSCALE_H_
#pragma once

#include <android/bitmap.h>
#include "jni_helper.h"

void gpu_grayscale(const AndroidBitmapInfo &info, rgba_t *rgba_pixels);

#endif  // GPU_GRAYSCALE_H_
