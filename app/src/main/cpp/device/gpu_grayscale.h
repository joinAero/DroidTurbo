#ifndef GPU_GRAYSCALE_H_
#define GPU_GRAYSCALE_H_
#pragma once

#include <android/bitmap.h>

void gpu_grayscale(const AndroidBitmapInfo &info, uint32_t *rgba_pixels);

#endif  // GPU_GRAYSCALE_H_
