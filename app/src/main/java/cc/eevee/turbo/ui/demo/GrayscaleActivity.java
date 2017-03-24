package cc.eevee.turbo.ui.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.ui.base.BaseActivity;
import cc.eevee.turbo.util.AssetsUtils;
import cc.eevee.turbo.util.JNIUtils;
import cc.eevee.turbo.util.TimeCost;

public class GrayscaleActivity extends BaseActivity {

    @BindView(R.id.text_cpu) TextView mTextCpu;
    @BindView(R.id.image_cpu) ImageView mImageCpu;

    @BindView(R.id.text_gpu) TextView mTextGpu;
    @BindView(R.id.image_gpu) ImageView mImageGpu;

    HandlerThread mHandlerThread;
    Handler mAsyncHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grayscale);
        ButterKnife.bind(this);
        initToolbar();
        initViews();
    }

    private void initViews() {
        mHandlerThread = new HandlerThread("GrayscaleActivity");
        mHandlerThread.start();

        mAsyncHandler = new Handler(mHandlerThread.getLooper());
        mAsyncHandler.post(() -> {
            TimeCost.beg("load");
            Bitmap bm = AssetsUtils.loadBitmap(GrayscaleActivity.this, "lenna.jpg");
            TimeCost costLoad = TimeCost.end("load").log();
            mTextCpu.post(() -> mTextCpu.append(costLoad.toLineString()));

            mImageCpu.post(() -> {
                mImageCpu.setImageBitmap(bm);
                // grayscale after display
                mAsyncHandler.post(() -> {
                    TimeCost.beg("grayscale");
                    JNIUtils.grayscale(bm);
                    TimeCost costGray = TimeCost.end("grayscale").log();
                    mTextCpu.post(() -> mTextCpu.append("\n"+costGray.toLineString()));
                    // ensure flush image view
                    mImageCpu.postInvalidate();
                });
            });
        });

        mAsyncHandler.post(() -> {
            TimeCost.beg("load");
            Bitmap bm = AssetsUtils.loadBitmap(GrayscaleActivity.this, "lenna.jpg");
            TimeCost costLoad = TimeCost.end("load").log();
            mTextGpu.post(() -> mTextGpu.append(costLoad.toLineString()));

            mImageGpu.post(() -> {
                mImageGpu.setImageBitmap(bm);
                // grayscale after display
                mAsyncHandler.post(() -> {
                    TimeCost.beg("grayscale");
                    JNIUtils.grayscale_gpu(bm);
                    TimeCost costGray = TimeCost.end("grayscale").log();
                    mTextGpu.post(() -> mTextGpu.append("\n"+costGray.toLineString()));
                    // ensure flush image view
                    mImageGpu.postInvalidate();
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
}
