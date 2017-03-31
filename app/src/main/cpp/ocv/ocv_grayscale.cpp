#include "ocv_grayscale.h"
#include "jni_helper.h"

#include <opencv2/imgproc.hpp>
#include <opencv2/cudaimgproc.hpp>

using namespace cv;

void native_ocv_grayscale(JNIEnv *env, jobject thiz, jlong addr) {
    Mat& im  = *(Mat*)addr;
    cvtColor(im, im, COLOR_BGR2GRAY);
}

void native_ocv_grayscale_gpu(JNIEnv *env, jobject thiz, jlong addr) {
    Mat& im  = *(Mat*)addr;

    cuda::GpuMat gpu_im;
    gpu_im.upload(im);

    cuda::cvtColor(gpu_im, gpu_im, COLOR_BGR2GRAY);

    gpu_im.download(im);
}
