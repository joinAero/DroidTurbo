cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Werror")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
add_definitions("-DANDROID_NDK -DDISABLE_IMPORTGL")

set(SRC_DIR src/main/cpp)

# file(GLOB SRC_SUBDIRS ${SRC_DIR} ${SRC_DIR}/*)
# foreach(SRC_SUBDIR ${SRC_SUBDIRS})
#     if(IS_DIRECTORY ${SRC_SUBDIR})
#         include_directories(${SRC_SUBDIR})
#     endif()
# endforeach()

# hellogl2
add_library(gles_hellogl2 SHARED
    ${SRC_DIR}/hellogl2/gl_code.cpp)
target_link_libraries(gles_hellogl2
    android
    log
    EGL
    GLESv2)

# hellocube
add_library(gles_hellocube SHARED
    ${SRC_DIR}/hellocube/gl_cube.cpp)
target_link_libraries(gles_hellocube
    GLESv1_CM
    m)

# sanangeles
include_directories(${SRC_DIR}/sanangeles)
add_library(gles_sanangeles SHARED
    ${SRC_DIR}/sanangeles/app-android.c
    ${SRC_DIR}/sanangeles/demo.c
    ${SRC_DIR}/sanangeles/importgl.c)
target_link_libraries(gles_sanangeles
    android   # Android native application APIs
    log       # Android-specific log support
    dl        # Dynamic linker library
    GLESv1_CM # OpenGL ES 1.x Library
    m)        # Mathematics
