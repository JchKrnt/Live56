package com.sohu.live56.view.main.square;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jch.utillib.view.CircleImageView;
import com.sohu.kurento.bean.RoomBean;
import com.sohu.live56.R;
import com.sohu.live56.bean.Room;
import com.sohu.live56.view.util.FindItemView;

import java.util.ArrayList;

/**
 * Created by jingbiaowang on 2015/8/11.
 */
public class SquareAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<Room> rooms = new ArrayList<>();

    public SquareAdapter(Context context) {

        this.context = context;
    }

    public void notifyDataSetChanged(ArrayList<Room> tempRoom) {
        rooms.clear();
        if (tempRoom != null)
            rooms.addAll(tempRoom);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.square_list_item_layout, null);

            FindItemView findItemView = (FindItemView) convertView;
            viewHolder.addrTv = findItemView.getAddrTv();
            viewHolder.bgBitmap = findItemView.getBgBitmap();
            viewHolder.headImg = findItemView.getHeadImg();
            viewHolder.nameTv = findItemView.getNameTv();
            viewHolder.numTv = findItemView.getNumTv();
            viewHolder.stateTv = findItemView.getStateTv();
            viewHolder.titleTv = findItemView.getTitleTv();

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            convertView.setPadding(0, (int) context.getResources().getDimension(R.dimen.square_liste_top_magin), 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 0);
        }

        Room room = rooms.get(position);

        //TODO set item data.
        viewHolder.titleTv.setText(room.getName());

        return convertView;
    }

    static class ViewHolder {
        TextView titleTv;
        TextView stateTv;
        Bitmap bgBitmap;
        CircleImageView headImg;
        TextView nameTv;
        TextView addrTv;
        TextView numTv;
    }

}
