#include "gpu_grayscale.h"
#include "gpu_helper.h"

__global__ void kernel_grayscale(
        rgba_t * const rgba_pixels, const uint32_t n) {
    const unsigned int idx = (blockIdx.x * blockDim.x) + threadIdx.x;
    //const unsigned int idy = (blockIdx.y * blockDim.y) + threadIdx.y;
    //const unsigned int tid = ((gridDim.x * blockDim.x) * idy) + idx;
    if (idx < n) {
        // Y = 0.299*R + 0.587*G + 0.114*B
        rgba_t &rgba = rgba_pixels[idx];
        uint8_t gray = (uint8_t) (0.299 * rgba.r + 0.587 * rgba.g + 0.114 * rgba.b);
        rgba.r = rgba.g = rgba.b = gray;
    }
}

void gpu_grayscale(const AndroidBitmapInfo &info, rgba_t *rgba_pixels) {
    const uint32_t n = info.width * info.height;
    GpuArray<rgba_t> gpu_rgba_pixels(n);
    gpu_rgba_pixels.Set(rgba_pixels, n);
    TIME_BEG("kernel_grayscale");
    kernel_grayscale<<<n/256+1, 256>>>(gpu_rgba_pixels.GetData(), n);
    TIME_END("kernel_grayscale");
    gpu_rgba_pixels.Get(rgba_pixels, n);
}
