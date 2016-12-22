package com.ssau.geolocation;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssau.geolocation.menu.MenuItem;

import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter {

    public static final int HEAD = 0;

    private List<MenuItem> menuItems;
    private Context context;

    public MenuAdapter(List<MenuItem> menuItems, Context context) {
        this.menuItems = menuItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_menu, parent, false);
                return new ViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TextView textView = viewHolder.itemText;
            String title = textView.getResources().getString(menuItems.get(position).getTitleId());
            textView.setText(title);
            viewHolder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuItems.get(position).activate(context);
                }
            });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView itemText;
        View content;

        public ViewHolder(View itemView) {
            super(itemView);
            this.content = itemView;
            itemText = (AppCompatTextView) itemView.findViewById(R.id.item_text_view);
        }
    }
}
