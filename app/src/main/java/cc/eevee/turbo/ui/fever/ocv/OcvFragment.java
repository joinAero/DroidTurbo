package cc.eevee.turbo.ui.fever.ocv;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.eevee.turbo.R;
import cc.eevee.turbo.libopencv.calib.CameraCalibrationActivity;
import cc.eevee.turbo.libopencv.facedetect.FaceDetectionActivity;
import cc.eevee.turbo.libopencv.tutorial1.Tutorial1Activity;
import cc.eevee.turbo.libopencv.tutorial2.Tutorial2Activity;
import cc.eevee.turbo.libopencv.tutorial3.Tutorial3Activity;
import cc.eevee.turbo.model.DataInfo;
import cc.eevee.turbo.ui.base.ListSightFragment;
import cc.eevee.turbo.util.ContextUtils;
import cc.eevee.turbo.view.InfoRecyclerViewAdapter;
import pl.droidsonroids.gif.GifDrawable;

public class OcvFragment extends ListSightFragment implements
        InfoRecyclerViewAdapter.OnItemViewClickListener<DataInfo<Class>> {

    public OcvFragment() {
    }

    public static OcvFragment newInstance() {
        return new OcvFragment();
    }

    @Override
    public void onViewPrepared(RecyclerView recyclerView) {
        InfoRecyclerViewAdapter<DataInfo<Class>, InfoRecyclerViewAdapter.ViewHolder> adapter
                = InfoRecyclerViewAdapter.create(createInfos(), R.layout.item_card);
        adapter.setOnItemViewClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private List<DataInfo<Class>> createInfos() {
        List<DataInfo<Class>> infos = new ArrayList<>();
        infos.add(createInfo("Tutorial 1", R.string.ocv_t1, null, Tutorial1Activity.class));
        infos.add(createInfo("Tutorial 2", R.string.ocv_t2, null, Tutorial2Activity.class));
        infos.add(createInfo("Tutorial 3", R.string.ocv_t3, null, Tutorial3Activity.class));
        infos.add(createInfo("Camera Calibration", R.string.ocv_calib, null, CameraCalibrationActivity.class));
        infos.add(createInfo("Face Detection", R.string.ocv_face_detect, null, FaceDetectionActivity.class));
        infos.add(createInfo(R.string.ocv_grayscale, "OpenCV Grayscale Sample", null, OcvGrayscaleActivity.class));
        infos.add(createInfo(R.string.ocv_orb, "OpenCV ORB Sample", null, OcvORBActivity.class));
        return infos;
    }

    private DataInfo<Class> createInfo(String title, int descId, String gifAsset, Class<?> cls) {
        Drawable drawable = null;
        if (gifAsset != null) {
            try {
                drawable = new GifDrawable(getActivity().getAssets(), gifAsset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new DataInfo<Class>(title, getString(descId), drawable, cls);
    }

    private DataInfo<Class> createInfo(int titleId, String desc, String gifAsset, Class<?> cls) {
        Drawable drawable = null;
        if (gifAsset != null) {
            try {
                drawable = new GifDrawable(getActivity().getAssets(), gifAsset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new DataInfo<Class>(getString(titleId), desc, drawable, cls);
    }

    @Override
    public void onItemViewClick(View view, int position, DataInfo<Class> data) {
        ContextUtils.startActivity(getActivity(), data.getData());
    }

}
