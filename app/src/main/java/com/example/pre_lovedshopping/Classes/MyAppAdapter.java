package com.example.pre_lovedshopping.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pre_lovedshopping.R;
import com.example.pre_lovedshopping.model.ClassListItems;

import java.util.List;

public class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder>  //has a class viewholder which holds
{
    private List<ClassListItems> itemList;
    public Context context;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public TextView textPrice;
        public ImageView imageView;
        public View layout;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            layout = itemView;
            textName = (TextView) itemView.findViewById(R.id.cont_title_in_list);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_cont);
            textPrice = (TextView) itemView.findViewById(R.id.cont_price_in_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public MyAppAdapter(List<ClassListItems> itemsArrayList, Context context) {
        itemList = itemsArrayList;
        this.context = context;
    }

    // @NonNull
    @Override
    public MyAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_content, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, final int position) {
        final ClassListItems classListItems = itemList.get(getItemCount() - position - 1);
        holder.textName.setText(classListItems.getName());
        holder.textPrice.setText(classListItems.getPrice());

        byte[] bytes = Base64.decode(classListItems.getImg(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.imageView.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}