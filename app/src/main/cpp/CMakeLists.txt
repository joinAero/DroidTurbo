cmake_minimum_required(VERSION 3.4.1)

get_filename_component(PRO_ROOT_DIR "${PROJECT_SOURCE_DIR}/../../../.." ABSOLUTE)
message(STATUS "PRO_ROOT_DIR: ${PRO_ROOT_DIR}")

# flags

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# options

add_definitions(-DLOG_TAG=\"DroidTurbo\")
add_definitions(-DDEBUG)
add_definitions(-DTIME_COST)

# libs

set(OpenCV_SDK_DIR "${PRO_ROOT_DIR}/OpenCV-android-sdk")

add_library(OpenCV_CORE SHARED IMPORTED)
set_target_properties(OpenCV_CORE PROPERTIES
  IMPORTED_LOCATION "${OpenCV_SDK_DIR}/sdk/native/libs/${ANDROID_ABI}/libopencv_java3.so"
)

# includes

include_directories(
  ${CMAKE_SOURCE_DIR}
  ${CMAKE_SOURCE_DIR}/base
  ${OpenCV_SDK_DIR}/sdk/native/jni/include
)

# modules

add_subdirectory(ocv)
add_subdirectory(util)
