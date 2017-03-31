package cc.eevee.turbo.ui.fever.ocv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.ui.base.BaseActivity;
import cc.eevee.turbo.util.AssetsUtils;
import cc.eevee.turbo.util.TimeCost;

public class OcvGrayscaleActivity extends BaseActivity {

    @BindView(R.id.text_cpu) TextView mTextCpu;
    @BindView(R.id.image_cpu) ImageView mImageCpu;

    @BindView(R.id.text_gpu) TextView mTextGpu;
    @BindView(R.id.image_gpu) ImageView mImageGpu;

    HandlerThread mHandlerThread;
    Handler mAsyncHandler;

    static {
        System.loadLibrary("ocv_grayscale");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grayscale);
        ButterKnife.bind(this);
        initToolbar();
        initViews();
    }

    private void initViews() {
        mHandlerThread = new HandlerThread("OcvGrayscaleActivity");
        mHandlerThread.start();

        mAsyncHandler = new Handler(mHandlerThread.getLooper());
        mAsyncHandler.post(() -> {
            TimeCost.beg("load4cpu");
            //Bitmap bm = AssetsUtils.loadBitmap(OcvGrayscaleActivity.this, "lenna.jpg");
            Mat mat = AssetsUtils.loadMat(OcvGrayscaleActivity.this, "lenna.jpg",
                    Imgcodecs.CV_LOAD_IMAGE_COLOR);
            TimeCost costLoad = TimeCost.end("load4cpu").log();
            mTextCpu.post(() -> mTextCpu.append(costLoad.toLineString()));

            mImageCpu.post(() -> {
                //noinspection ConstantConditions
                Bitmap bm = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.RGB_565);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);  // matToBitmap() need BGR
                Utils.matToBitmap(mat, bm);
                mImageCpu.setImageBitmap(bm);
                // grayscale after display
                mAsyncHandler.post(() -> {
                    TimeCost.beg("grayscale_cpu");
                    grayscale(mat.getNativeObjAddr());
                    TimeCost costGray = TimeCost.end("grayscale_cpu").log();
                    mTextCpu.post(() -> mTextCpu.append("\n"+costGray.toLineString()));
                    mImageCpu.post(() -> {
                        // update image view
                        Utils.matToBitmap(mat, bm);
                        mImageCpu.setImageBitmap(bm);
                    });
                });
            });
        });

        mAsyncHandler.post(() -> {
            TimeCost.beg("load4gpu");
            Mat mat = AssetsUtils.loadMat(OcvGrayscaleActivity.this, "lenna.jpg",
                    Imgcodecs.CV_LOAD_IMAGE_COLOR);
            TimeCost costLoad = TimeCost.end("load4gpu").log();
            mTextGpu.post(() -> mTextGpu.append(costLoad.toLineString()));

            mImageGpu.post(() -> {
                //noinspection ConstantConditions
                Bitmap bm = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.RGB_565);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);  // matToBitmap() need BGR
                Utils.matToBitmap(mat, bm);
                mImageGpu.setImageBitmap(bm);
                // grayscale after display
                mAsyncHandler.post(() -> {
                    TimeCost.beg("grayscale_gpu");
                    grayscale_gpu(mat.getNativeObjAddr());
                    TimeCost costGray = TimeCost.end("grayscale_gpu").log();
                    mTextGpu.post(() -> mTextGpu.append("\n"+costGray.toLineString()));
                    mImageGpu.post(() -> {
                        // update image view
                        Utils.matToBitmap(mat, bm);
                        mImageGpu.setImageBitmap(bm);
                    });
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAsyncHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quitSafely();
        mHandlerThread.interrupt();
    }

    public native void grayscale(long matAddr);
    public native void grayscale_gpu(long matAddr);

}
