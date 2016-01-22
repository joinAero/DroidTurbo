package cc.cubone.turbo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.base.BaseFragment;

/**
 * Test fragment.
 */
public class ColorPageFragment extends BaseFragment {

    public static final int RED = 0xFFE57373;
    public static final int PINK = 0xFFF06292;
    public static final int PURPLE = 0xFFBA68C8;

    private static final String ARG_PAGE    = "page";
    private static final String ARG_COLOR   = "color";
    private static final String ARG_TITLE   = "title";

    private int mPage;
    private int mColor;
    private String mTitle;

    public ColorPageFragment() {
    }

    public static ColorPageFragment newInstance(int page, int color) {
        return newInstance(page, color, "Page " + page);
    }

    public static ColorPageFragment newInstance(int page, int color, String title) {
        ColorPageFragment fragment = new ColorPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_COLOR, color);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
            mColor = getArguments().getInt(ARG_COLOR);
            mTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setBackgroundColor(mColor);

        TextView textView = ButterKnife.findById(view, R.id.text);
        textView.setText(mTitle);
    }

}
