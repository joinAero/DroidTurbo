cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Werror")
add_definitions("-DANDROID_NDK -DDISABLE_IMPORTGL")

set(SRC_DIR src/main/cpp)

# file(GLOB SRC_SUBDIRS ${SRC_DIR} ${SRC_DIR}/*)
# foreach(SRC_SUBDIR ${SRC_SUBDIRS})
#     if(IS_DIRECTORY ${SRC_SUBDIR})
#         include_directories(${SRC_SUBDIR})
#     endif()
# endforeach()

# sanangeles
include_directories(${SRC_DIR}/sanangeles)
add_library(sanangeles SHARED
    ${SRC_DIR}/sanangeles/app-android.c
    ${SRC_DIR}/sanangeles/demo.c
    ${SRC_DIR}/sanangeles/importgl.c)
target_link_libraries(sanangeles
    # Android native application APIs
    android
    # Dynamic linker library
    dl
    # OpenGL ES 1.x Library
    GLESv1_CM
    # Android-specific log support
    log
    # Mathematics
    m)
