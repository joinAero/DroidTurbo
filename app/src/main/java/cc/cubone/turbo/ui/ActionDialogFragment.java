package cc.cubone.turbo.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import cc.cubone.turbo.ui.base.BaseDialogFragment;

public class ActionDialogFragment extends BaseDialogFragment
        implements AbsListView.OnItemClickListener {

    private int[] mActions;
    private OnActionSelectListener mOnActionSelectListener;

    public ActionDialogFragment() {
    }

    public static ActionDialogFragment make(String title, @StringRes int... actions) {
        ActionDialogFragment dialog = new ActionDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putIntArray("actions", actions);
        dialog.setArguments(args);
        return dialog;
    }

    public ActionDialogFragment setOnActionSelectListener(OnActionSelectListener listener) {
        mOnActionSelectListener = listener;
        return this;
    }

    public void show(FragmentManager fm) {
        show(fm, "dlg_action");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        String title = getArguments().getString("title");
        if (TextUtils.isEmpty(title)) {
            title = "Actions";
        }

        ArrayList<String> data = new ArrayList<>();
        int[] actions = getArguments().getIntArray("actions");
        if (actions != null) {
            for (int id : actions) {
                data.add(context.getString(id));
            }
        }
        mActions = actions;

        ListView listView = new ListView(context);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, data));
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                context.getResources().getDisplayMetrics());
        listView.setPadding(0, padding, 0, 0);

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(listView)
                .setNegativeButton(android.R.string.no, null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnActionSelectListener != null) {
            mOnActionSelectListener.onActionSelect(this, mActions[position]);
        }
    }

    public interface OnActionSelectListener {
        void onActionSelect(ActionDialogFragment dialog, int action);
    }

}
