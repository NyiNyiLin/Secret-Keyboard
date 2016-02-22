package com.nyi.secret.secretkeyboard;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nyi.secret.database.CustomKey;
import com.nyi.secret.database.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by IN-3442 on 12-Oct-15.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.KeyHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<CustomKey> keyArrayList;
    private static MyClickListener myClickListener;

    @Override
    public KeyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        KeyHolder keyHolder = new KeyHolder(view);
        return keyHolder;
    }

    @Override
    public void onBindViewHolder(KeyHolder holder, int position) {
        holder.character.setText(keyArrayList.get(position).getKEY_NAME());
        holder.text.setText(keyArrayList.get(position).getKEY_WORD());
    }

    @Override
    public int getItemCount() {

        return keyArrayList.size();
    }
    public void addKey(CustomKey dataObj, int index) {
        keyArrayList.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteKey(int index) {
        keyArrayList.remove(index);
        notifyItemRemoved(index);
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CardViewAdapter(ArrayList<CustomKey> KeyList) {
        keyArrayList=KeyList;
    }

    public static class KeyHolder extends RecyclerView.ViewHolder
            implements OnClickListener{

        TextView character,text;
        ImageButton delete;

        public KeyHolder(View itemView) {
            super(itemView);

            character = (TextView) itemView.findViewById(R.id.textview_character_in_cardview);
            text = (TextView) itemView.findViewById(R.id.textview_text_in_cardview);
            delete=(ImageButton)itemView.findViewById(R.id.row_delete);

            Log.i(LOG_TAG, "Adding Listener");
            //itemView.setOnClickListener(this);
            delete.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static ArrayList<CustomKey> getKeyArrayList() {
        return keyArrayList;
    }
}
