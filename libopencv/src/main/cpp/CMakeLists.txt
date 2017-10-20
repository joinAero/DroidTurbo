cmake_minimum_required(VERSION 3.4.1)

get_filename_component(PRO_ROOT_DIR "${PROJECT_SOURCE_DIR}/../../../.." ABSOLUTE)
message(STATUS "PRO_ROOT_DIR: ${PRO_ROOT_DIR}")

# flags

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# libs

set(OpenCV_SDK_DIR "${PRO_ROOT_DIR}/OpenCV-android-sdk")

add_library(opencv_java SHARED IMPORTED)
set_target_properties(opencv_java PROPERTIES
  IMPORTED_LOCATION "${OpenCV_SDK_DIR}/sdk/native/libs/${ANDROID_ABI}/libopencv_java3.so"
)

# includes

include_directories(
  ${OpenCV_SDK_DIR}/sdk/native/jni/include
)

# modules

add_subdirectory(facedetect)
add_subdirectory(tutorial2)
