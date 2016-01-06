package cc.cubone.turbo.core.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SimpleRecyclerViewAdapter<Data, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements View.OnClickListener {

    private List<Data> mDataList;

    private OnItemViewCreateListener<Data, VH> mOnItemViewCreateListener;
    private OnItemViewClickListener<Data> mOnItemClickListener;

    protected SimpleRecyclerViewAdapter(@NonNull List<Data> dataList) {
        mDataList = dataList;
    }

    public SimpleRecyclerViewAdapter(@NonNull List<Data> dataList,
                                     @NonNull OnItemViewCreateListener<Data, VH> listener) {
        mDataList = dataList;
        mOnItemViewCreateListener = listener;
    }

    protected void setOnItemViewCreateListener(@NonNull OnItemViewCreateListener<Data, VH> listener) {
        mOnItemViewCreateListener = listener;
    }

    public void setOnItemViewClickListener(@Nullable OnItemViewClickListener<Data> listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mOnItemViewCreateListener.onItemViewTypeCreate(position);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int res = mOnItemViewCreateListener.onItemViewResourceCreate(viewType);
        View v = inflater.inflate(res, parent, false);
        v.setOnClickListener(this);
        return mOnItemViewCreateListener.onItemViewHolderCreate(v, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Data data = mDataList.get(position);
        //if (data == null) return;
        holder.itemView.setTag(position);
        mOnItemViewCreateListener.onItemViewHolderBind(data, holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            final int position = (int) v.getTag();
            mOnItemClickListener.onItemViewClick(v, position, mDataList.get(position));
        }
    }

    public interface OnItemViewCreateListener<Data, VH extends RecyclerView.ViewHolder> {
        int onItemViewTypeCreate(int position);
        int onItemViewResourceCreate(int viewType);
        VH onItemViewHolderCreate(View itemView, int viewType);
        void onItemViewHolderBind(Data data, VH holder, int position);
    }

    public interface OnItemViewClickListener<Data> {
        void onItemViewClick(View view, int position, Data data);
    }

}
