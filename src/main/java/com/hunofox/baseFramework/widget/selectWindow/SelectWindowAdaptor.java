package com.hunofox.baseFramework.widget.selectWindow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hunofox.baseFramework.R;

import java.util.List;


/**
 * 项目名称：MyLibApp
 * 项目作者：胡玉君
 * 创建日期：2017/9/19 13:37.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class SelectWindowAdaptor extends RecyclerView.Adapter<SelectWindowAdaptor.FolderHolder> implements View.OnClickListener{

    private final List<String> datas;
    private int selectedPosition = 0;

    public SelectWindowAdaptor(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public FolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptor_select_window, parent, false);
        view.setOnClickListener(this);
        return new FolderHolder(view);
    }

    @Override
    public void onBindViewHolder(FolderHolder hd, int position) {
        hd.itemView.setTag(position);
        String bean = datas.get(position);
        hd.tvName.setText(bean);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View v) {
        setSelectedPosition((int) v.getTag());
        if(listener != null){
            listener.onItemClick(v, (int) v.getTag());
        }
    }

    //设置被选中的位置
    public void setSelectedPosition(int selectedPosition){
        if(this.selectedPosition == selectedPosition) return;
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    static final class FolderHolder extends RecyclerView.ViewHolder{

        private final TextView tvName;

        public FolderHolder(View v) {
            super(v);

            tvName = (TextView) v.findViewById(R.id.tv_name);
        }
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }
}
