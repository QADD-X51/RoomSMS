package com.example.roomsms.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.roomsms.R;

import java.util.ArrayList;
import java.util.Objects;

public class ManageUsersListViewAdapter extends ArrayAdapter<ManageUserModel> {

    ArrayList<ManageUserModel> items;
    Context context;
    String currentUser;
    public ManageUsersListViewAdapter(Context context, ArrayList<ManageUserModel> items, String currentUser) {
        super(context, R.layout.chat_row, items);
        this.items = items;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.manage_users_row, null);

            TextView userName = convertView.findViewById(R.id.UserName);
            userName.setText(items.get(position).getUsername());

            TextView roleName = convertView.findViewById(R.id.RoleName);
            roleName.setText(items.get(position).getRole());

            ImageView kickButton = convertView.findViewById(R.id.KickUserButton);
            ImageView changeAdminButton = convertView.findViewById(R.id.ChangeAdminButton);

            changeAdminButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.make_admin));
            kickButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.kick));

            if(Objects.equals(items.get(position).getRole(), "Owner") ||
                    Objects.equals(items.get(position).getUsername(),currentUser))  {
                changeAdminButton.setImageDrawable(null);
                kickButton.setImageDrawable(null);

                return convertView;
            }

            if(!Objects.equals(items.get(position).getRole(), "Normal")) {
                changeAdminButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.remove_admin));

            }

            kickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showKickDialog();
                }
            });

            changeAdminButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showChangeAdminDialog();
                }
            });

            return convertView;
        }

        TextView userName = convertView.findViewById(R.id.UserName);
        userName.setText(items.get(position).getUsername());

        TextView roleName = convertView.findViewById(R.id.RoleName);
        roleName.setText(items.get(position).getRole());

        ImageView kickButton = convertView.findViewById(R.id.KickUserButton);
        ImageView changeAdminButton = convertView.findViewById(R.id.ChangeAdminButton);

        if(Objects.equals(items.get(position).getRole(), "Owner") ||
                Objects.equals(items.get(position).getUsername(), currentUser)) {
            changeAdminButton.setImageDrawable(null);
            kickButton.setImageDrawable(null);

            return convertView;
        }

        if(!Objects.equals(items.get(position).getRole(), "Normal")) {
            changeAdminButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.remove_admin));
        }

        kickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKickDialog();
            }
        });

        changeAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeAdminDialog();
            }
        });

        return convertView;
    }

    public void showKickDialog(){
        Toast.makeText(context, "Kick", Toast.LENGTH_SHORT).show();
    }

    public void showChangeAdminDialog() {
        Toast.makeText(context, "Admin", Toast.LENGTH_SHORT).show();
    }
}
