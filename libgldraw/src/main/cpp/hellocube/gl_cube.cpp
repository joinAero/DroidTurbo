#include <cmath>
#include <jni.h>
#include <GLES/gl.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
// http://iphonedevelopment.blogspot.jp/2008/12/glulookat.html
void gluLookAt(GLfloat eyex, GLfloat eyey, GLfloat eyez,
               GLfloat centerx, GLfloat centery, GLfloat centerz,
               GLfloat upx, GLfloat upy, GLfloat upz) {
    GLfloat m[16];
    GLfloat x[3], y[3], z[3];
    GLfloat mag;

    /* Make rotation matrix */

    /* Z vector */
    z[0] = eyex - centerx;
    z[1] = eyey - centery;
    z[2] = eyez - centerz;
    mag = sqrt(z[0] * z[0] + z[1] * z[1] + z[2] * z[2]);
    if (mag) {          /* mpichler, 19950515 */
        z[0] /= mag;
        z[1] /= mag;
        z[2] /= mag;
    }

    /* Y vector */
    y[0] = upx;
    y[1] = upy;
    y[2] = upz;

    /* X vector = Y cross Z */
    x[0] = y[1] * z[2] - y[2] * z[1];
    x[1] = -y[0] * z[2] + y[2] * z[0];
    x[2] = y[0] * z[1] - y[1] * z[0];

    /* Recompute Y = Z cross X */
    y[0] = z[1] * x[2] - z[2] * x[1];
    y[1] = -z[0] * x[2] + z[2] * x[0];
    y[2] = z[0] * x[1] - z[1] * x[0];

    /* mpichler, 19950515 */
    /* cross product gives area of parallelogram, which is < 1.0 for
     * non-perpendicular unit-length vectors; so normalize x, y here
     */

    mag = sqrt(x[0] * x[0] + x[1] * x[1] + x[2] * x[2]);
    if (mag) {
        x[0] /= mag;
        x[1] /= mag;
        x[2] /= mag;
    }

    mag = sqrt(y[0] * y[0] + y[1] * y[1] + y[2] * y[2]);
    if (mag) {
        y[0] /= mag;
        y[1] /= mag;
        y[2] /= mag;
    }

#define M(row,col) m[col*4+row]
    M(0, 0) = x[0];
    M(0, 1) = x[1];
    M(0, 2) = x[2];
    M(0, 3) = 0.0;
    M(1, 0) = y[0];
    M(1, 1) = y[1];
    M(1, 2) = y[2];
    M(1, 3) = 0.0;
    M(2, 0) = z[0];
    M(2, 1) = z[1];
    M(2, 2) = z[2];
    M(2, 3) = 0.0;
    M(3, 0) = 0.0;
    M(3, 1) = 0.0;
    M(3, 2) = 0.0;
    M(3, 3) = 1.0;
#undef M
    glMultMatrixf(m);

    /* Translate Eye to Origin */
    glTranslatef(-eyex, -eyey, -eyez);
}
#pragma clang diagnostic pop

void glDrawLine( GLfloat x1, GLfloat y1, GLfloat z1, GLfloat x2, GLfloat y2, GLfloat z2) {
    GLfloat verts[] = { x1,y1,z1, x2,y2,z2 };
    glVertexPointer(3, GL_FLOAT, 0, verts);
    glEnableClientState(GL_VERTEX_ARRAY);
    glDrawArrays(GL_LINES, 0, 2);
    glDisableClientState(GL_VERTEX_ARRAY);
}

void glDrawAxis(float s) {
    glColor4f(1,0,0,1);
    glDrawLine(0,0,0, s,0,0);

    glColor4f(0,1,0,1);
    glDrawLine(0,0,0, 0,s,0);

    glColor4f(0,0,1,1);
    glDrawLine(0,0,0, 0,0,s);
}

void glDrawColouredCube(GLfloat axis_min=-0.5f, GLfloat axis_max = +0.5f) {
    const GLfloat l = axis_min;
    const GLfloat h = axis_max;

    const GLfloat verts[] = {
            l,l,h,  h,l,h,  l,h,h,  h,h,h,  // FRONT
            l,l,l,  l,h,l,  h,l,l,  h,h,l,  // BACK
            l,l,h,  l,h,h,  l,l,l,  l,h,l,  // LEFT
            h,l,l,  h,h,l,  h,l,h,  h,h,h,  // RIGHT
            l,h,h,  h,h,h,  l,h,l,  h,h,l,  // TOP
            l,l,h,  l,l,l,  h,l,h,  h,l,l   // BOTTOM
    };

    glVertexPointer(3, GL_FLOAT, 0, verts);
    glEnableClientState(GL_VERTEX_ARRAY);

    glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glDrawArrays(GL_TRIANGLE_STRIP, 4, 4);

    glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
    glDrawArrays(GL_TRIANGLE_STRIP, 8, 4);
    glDrawArrays(GL_TRIANGLE_STRIP, 12, 4);

    glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
    glDrawArrays(GL_TRIANGLE_STRIP, 16, 4);
    glDrawArrays(GL_TRIANGLE_STRIP, 20, 4);

    glDisableClientState(GL_VERTEX_ARRAY);
}

void nativeInit() {
    // Set the background frame color
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    glClearDepthf(1.0f);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    glShadeModel(GL_SMOOTH);

    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
}

void nativeResize(int width, int height) {
    glViewport(0, 0, width, height);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    float ratio = 1.0f * height / width;
    // left, right, bottom, top, zNear, zFar
    glOrthof(-2, 2, -2*ratio, 2*ratio, -2, 2);
}

static GLfloat g_angle = 0;

void nativeRender() {
    // Draw background color
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Set GL_MODELVIEW transformation mode
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();  // reset the matrix to its default state

    // When using GL_MODELVIEW, you must set the view point
    gluLookAt(1,1,1, 0,0,0, 0,1,0);

    glRotatef(g_angle, 1.0f,1.0f,1.0f);

    // Draw axis
    glLineWidth(2);
    glDrawAxis(1);

    // Draw cube
    glDrawColouredCube();
}

extern "C" {

JNIEXPORT void JNICALL Java_cc_eevee_turbo_libgldraw_HelloCubeView_nativeResume(
        JNIEnv * env, jobject obj) {
}

JNIEXPORT void JNICALL Java_cc_eevee_turbo_libgldraw_HelloCubeView_nativePause(
        JNIEnv * env, jobject obj) {
}

JNIEXPORT void JNICALL Java_cc_eevee_turbo_libgldraw_HelloCubeView_nativeTouchRotate(
        JNIEnv * env, jobject obj, jfloat angle) {
    g_angle += angle;
}

JNIEXPORT void JNICALL Java_cc_eevee_turbo_libgldraw_HelloCubeRenderer_nativeInit(
        JNIEnv * env, jobject obj) {
    nativeInit();
}

JNIEXPORT void JNICALL Java_cc_eevee_turbo_libgldraw_HelloCubeRenderer_nativeResize(
        JNIEnv * env, jobject obj, jint width, jint height) {
    nativeResize(width, height);
}

JNIEXPORT void JNICALL Java_cc_eevee_turbo_libgldraw_HelloCubeRenderer_nativeRender(
        JNIEnv * env, jobject obj) {
    nativeRender();
}

};
