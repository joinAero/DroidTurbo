#include "ocv_grayscale.h"

#include <opencv2/imgproc.hpp>
#include <opencv2/cudaimgproc.hpp>

#include "time_cost.hpp"

using namespace cv;

void native_ocv_grayscale(JNIEnv *env, jobject thiz, jlong addr) {
    DBG_LOGI(__func__);
    TIME_BEG_FUNC2;

    Mat &im  = *(Mat*)addr;
    cvtColor(im, im, COLOR_BGR2GRAY);

    TIME_END_FUNC2;
}

void native_ocv_grayscale_gpu(JNIEnv *env, jobject thiz, jlong addr) {
    DBG_LOGI(__func__);
    TIME_BEG_FUNC2;

    Mat& im  = *(Mat*)addr;

    cuda::GpuMat gpu_im;
    gpu_im.upload(im);

    cuda::cvtColor(gpu_im, gpu_im, COLOR_BGR2GRAY);

    gpu_im.download(im);

    TIME_END_FUNC2;
}
