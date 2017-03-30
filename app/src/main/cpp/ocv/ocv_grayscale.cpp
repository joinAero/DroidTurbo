#include "ocv_grayscale.h"
#include "jni_helper.h"

#include <opencv2/imgproc.hpp>

using namespace cv;

void native_ocv_grayscale(JNIEnv *env, jobject thiz, jlong addr) {
    Mat& im  = *(Mat*)addr;
    cvtColor(im, im, COLOR_BGR2GRAY);
}
