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

    @BindView(R.id.text) TextView mTextView;
    @BindView(R.id.image) ImageView mImageView;

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
            mTextView.post(() -> mTextView.append(costLoad.toLineString()));

            mImageView.post(() -> {
                mImageView.setImageBitmap(bm);
                // grayscale after display
                mAsyncHandler.post(() -> {
                    TimeCost.beg("grayscale");
                    JNIUtils.grayscale(bm);
                    TimeCost costGray = TimeCost.end("grayscale").log();
                    mTextView.post(() -> mTextView.append("\n"+costGray.toLineString()));
                    // ensure flush image view
                    mImageView.postInvalidate();
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
