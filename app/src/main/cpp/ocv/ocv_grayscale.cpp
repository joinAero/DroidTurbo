#include "ocv_grayscale.h"
#include "jni_helper.h"

#include <opencv2/imgproc.hpp>

using namespace cv;

void native_ocv_grayscale(JNIEnv *env, jobject thiz, jlong addr) {
    DBG_LOGI(__func__);
    TIME_BEG_FUNC2;

    Mat& im  = *(Mat*)addr;
    cvtColor(im, im, COLOR_BGR2GRAY);

    TIME_END_FUNC2;
}
