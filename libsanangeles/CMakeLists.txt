cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}  -Wall -Werror")
add_definitions("-DANDROID_NDK -DDISABLE_IMPORTGL")

set(SRC_DIR src/main/cpp)

include_directories(${SRC_DIR})

add_library(sanangeles SHARED
            ${SRC_DIR}/app-android.c
            ${SRC_DIR}/demo.c
            ${SRC_DIR}/importgl.c)

# Include libraries needed for sanangeles lib
target_link_libraries(sanangeles
                      android
                      dl
                      GLESv1_CM
                      log
                      m)
