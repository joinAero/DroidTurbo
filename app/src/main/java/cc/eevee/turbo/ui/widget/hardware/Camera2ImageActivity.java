package cc.eevee.turbo.ui.widget.hardware;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Script;
import android.support.v8.renderscript.Type;
import android.util.Size;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.BuildConfig;
import cc.eevee.turbo.R;
import cc.eevee.turbo.core.util.Log;
import cc.eevee.turbo.ui.base.BaseActivity;

public class Camera2ImageActivity extends BaseActivity {

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2ImageActivity";

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened. We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraCaptureSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * A {@link CameraCaptureSession} for camera capture.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * The {@link Size} of camera capture.
     */
    private Size mCaptureSize;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new Runnable() {
                Image image = reader.acquireNextImage();
                @Override
                public void run() {
                    try {
                        final Bitmap bm = YUV_420_888_toRGB(image, image.getWidth(), image.getHeight());
                        mImageView.post(() -> mImageView.setImageBitmap(bm));
                    } finally {
                        image.close();
                    }
                }
            });
        }
    };

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * An {@link ImageView} for camera capture.
     */
    @BindView(R.id.image) ImageView mImageView;

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_image);
        ButterKnife.bind(this);
        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        openCamera();
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Sets up member variables related to camera.
     */
    private void setupCamera() {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                printCameraCharacteristics(cameraId, characteristics);

                // We don't use a front facing camera in this sample.
                if (characteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.YUV_420_888, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                mCaptureSize = largest;
                Log.i(TAG, "CaptureSize: " + mCaptureSize.getWidth() + "x" + mCaptureSize.getHeight());

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            new ErrorDialog().show(getSupportFragmentManager(), "dialog");
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mImageView`.
     */
    private void configureTransform() {
        Activity activity = getActivity();
        if (null == mImageView || null == mCaptureSize || null == activity) {
            return;
        }

        int viewWidth = mImageView.getWidth();
        int viewHeight = mImageView.getHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            mImageView.post(this::configureTransform);
            return;
        }

        //int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int imageWidth = mCaptureSize.getWidth();
        int imageHeight = mCaptureSize.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90, 0, 0);
        matrix.postTranslate(
                imageHeight + (viewWidth - imageHeight) * 0.5f,
                (viewHeight - imageWidth) * 0.5f);
        float scale = Math.min(
                (float) viewHeight / imageWidth,
                (float) viewWidth / imageHeight);
        matrix.postScale(scale, scale, viewWidth * 0.5f, viewHeight * 0.5f);

        mImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mImageView.setImageMatrix(matrix);
    }

    /**
     * Opens the camera specified by {@link Camera2ImageActivity#mCameraId}.
     */
    private void openCamera() {
        setupCamera();
        configureTransform();

        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            //noinspection MissingPermission
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera capture.
     */
    private void createCameraCaptureSession() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Here, we create a CameraCaptureSession for camera capture.
            mCameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            mCaptureSession = cameraCaptureSession;
                            try {
                                mCaptureSession.setRepeatingRequest(captureBuilder.build(),
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * A {@link Handler} for showing {@link Toast}s.
     */
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            if (activity != null) {
                Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(String text) {
        // We show a Toast by sending request message to mMessageHandler. This makes sure that the
        // Toast is shown on the UI thread.
        Message message = Message.obtain();
        message.obj = text;
        mMessageHandler.sendMessage(message);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ErrorDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage("This device doesn't support Camera2 API.")
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> activity.finish())
                    .create();
        }

    }

    // *********************************************************************************************

    private void printCameraCharacteristics(String cameraId, CameraCharacteristics characteristics) {
        if (!BuildConfig.DEBUG) return;
        final String tag = "CameraCharacteristics";
        Log.i(tag, "------------------------------------------------------------");
        Log.i(tag, "Camera Id: " + cameraId);

        for (CameraCharacteristics.Key<?> key : characteristics.getKeys()) {
            Log.i(tag, key.getName());
            if (key == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) {
                Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                String value;
                switch (level) {
                    case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED: value = "LIMITED"; break;
                    case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL: value = "FULL"; break;
                    case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY: value = "LEGACY"; break;
                    case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_3: value = "3"; break;
                    default: value = "UNKNOWN";
                }
                Log.i(tag, value);
            } else if (key == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES) {
                int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                if (capabilities == null) continue;

                SparseArray<String> names = new SparseArray<>();
                for (Field f : CameraMetadata.class.getDeclaredFields()) {
                    if (f.getName().startsWith("REQUEST_AVAILABLE_CAPABILITIES")) {
                        f.setAccessible(true);
                        try {
                            names.put(f.getInt(null), f.getName());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (int c : capabilities) {
                    Log.i(tag, names.get(c, String.valueOf(c)));
                }
            } else if (key == CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) {
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) continue;
                Log.i(tag, "Outputs:");

                SparseArray<String> names = new SparseArray<>();
                for (Field f : ImageFormat.class.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) {
                        if (f.getType() == int.class) {
                            f.setAccessible(true);
                            try {
                                names.put(f.getInt(null), f.getName());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for (int f : map.getOutputFormats()) {
                    Log.i(tag, "  " + names.get(f, String.valueOf(f)));
                    Size[] sizes = map.getOutputSizes(f);
                    if (sizes != null) {
                        for (Size sz : sizes) {
                            Log.i(tag, "    " + sz.toString());
                        }
                    }
                }
            }
        }
        Log.i(tag, "------------------------------------------------------------");
    }

    /**
     * Has anyone managed to obtain a YUV_420_888 frame using RenderScript and the new Camera API?
     *   https://stackoverflow.com/questions/30653287/has-anyone-managed-to-obtain-a-yuv-420-888-frame-using-renderscript-and-the-new/35994288#35994288
     * YUV_420_888 interpretation on Samsung Galaxy S7 (Camera2)
     *   https://stackoverflow.com/questions/36212904/yuv-420-888-interpretation-on-samsung-galaxy-s7-camera2
     * RenderScript
     *   https://developer.android.com/guide/topics/renderscript/compute.html
     */
    private Bitmap YUV_420_888_toRGB(Image image, int width, int height) {
        // Get the three image planes
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] y = new byte[buffer.remaining()];
        buffer.get(y);

        buffer = planes[1].getBuffer();
        byte[] u = new byte[buffer.remaining()];
        buffer.get(u);

        buffer = planes[2].getBuffer();
        byte[] v = new byte[buffer.remaining()];
        buffer.get(v);

        // get the relevant RowStrides and PixelStrides
        // (we know from documentation that PixelStride is 1 for y)
        int yRowStride = planes[0].getRowStride();
        int uvRowStride = planes[1].getRowStride();  // we know from documentation that RowStride is the same for u and v.
        int uvPixelStride = planes[1].getPixelStride();  // we know from documentation that PixelStride is the same for u and v.

        // rs creation just for demo. Create rs just once in onCreate and use it again.
        RenderScript rs = RenderScript.create(this);
        ScriptC_yuv420888 mYuv420 = new ScriptC_yuv420888(rs);

        // Y,U,V are defined as global allocations, the out-Allocation is the Bitmap.
        // Note also that uAlloc and vAlloc are 1-dimensional while yAlloc is 2-dimensional.
        Type.Builder typeUcharY = new Type.Builder(rs, Element.U8(rs));
        typeUcharY.setX(yRowStride).setY(height);
        Allocation yAlloc = Allocation.createTyped(rs, typeUcharY.create());
        yAlloc.copyFrom(y);
        mYuv420.set_ypsIn(yAlloc);

        Type.Builder typeUcharUV = new Type.Builder(rs, Element.U8(rs));
        // note that the size of the u's and v's are as follows:
        //      (  (width/2)*PixelStride + padding  ) * (height/2)
        // =    (RowStride                          ) * (height/2)
        // but I noted that on the S7 it is 1 less...
        typeUcharUV.setX(u.length);
        Allocation uAlloc = Allocation.createTyped(rs, typeUcharUV.create());
        uAlloc.copyFrom(u);
        mYuv420.set_uIn(uAlloc);

        Allocation vAlloc = Allocation.createTyped(rs, typeUcharUV.create());
        vAlloc.copyFrom(v);
        mYuv420.set_vIn(vAlloc);

        // handover parameters
        mYuv420.set_picWidth(width);
        mYuv420.set_uvRowStride(uvRowStride);
        mYuv420.set_uvPixelStride(uvPixelStride);

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Allocation outAlloc = Allocation.createFromBitmap(rs, outBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        Script.LaunchOptions lo = new Script.LaunchOptions();
        lo.setX(0, width);  // by this we ignore the y’s padding zone, i.e. the right side of x between width and yRowStride
        lo.setY(0, height);

        mYuv420.forEach_doConvert(outAlloc, lo);
        outAlloc.copyTo(outBitmap);

        return outBitmap;
    }

}
