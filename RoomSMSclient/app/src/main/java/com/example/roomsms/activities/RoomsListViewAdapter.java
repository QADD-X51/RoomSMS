package com.example.roomsms.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomsms.R;

import java.util.ArrayList;

public class RoomsListViewAdapter  extends ArrayAdapter<RoomModel> {

    ArrayList<RoomModel> items;
    Context context;

    public RoomsListViewAdapter(Context context, ArrayList<RoomModel> items) {
        super(context, R.layout.rooms_list_row, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.rooms_list_row, null);

            TextView roomName = convertView.findViewById(R.id.RoomName);
            roomName.setText(items.get(position).GetName());

            TextView owner = convertView.findViewById(R.id.Owner);
            owner.setText(items.get(position).GetOwner());

            return convertView;
        }

        TextView roomName = convertView.findViewById(R.id.RoomName);
        roomName.setText(items.get(position).GetName());

        TextView owner = convertView.findViewById(R.id.Owner);
        owner.setText(items.get(position).GetOwner());

        return convertView;
    }
}
