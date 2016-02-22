package com.nyi.secret.secretkeyboard;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nyi.secret.database.CustomKey;
import com.nyi.secret.database.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by IN-3442 on 11-Oct-15.
 */
public class inputFragment extends android.support.v4.app.Fragment{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    DatabaseHandler db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.input_layout,container,false);
        db=new DatabaseHandler(getContext());


        //This is for card view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CardViewAdapter(db.getAllKey());
        //mAdapter = new CardViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
/*
        //This is for textview
        TextView txt=(TextView)v.findViewById(R.id.input_dialogue);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OpenCategroyDialogBox();
                openCharacterInputDialoge();
            }
        });*/

        //FloatinActionButton
        FloatingActionButton fab =(FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenInputDialogBox(new CustomKey(), false);
            }
        });



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CardViewAdapter) mAdapter).setOnItemClickListener(new CardViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
                CustomKey customKey = ((CardViewAdapter) mAdapter).getKeyArrayList().get(position);
                /*customKey.setKEY_WORD("");
                db.updateKeyText(customKey);
                ((CardViewAdapter) mAdapter).deleteKey(position);*/

                showMenu(v, customKey);
            }
        });


        //This is to change the recyvler view
        mAdapter = new CardViewAdapter(db.getAllKey());
        mRecyclerView.setAdapter(mAdapter);
        Log.i("This is change", "Ha Ha AH");

    }



    private ArrayList<CustomKey> getDataSet() {
        ArrayList results = new ArrayList<CustomKey>();
        for (int index = 0; index < 20; index++) {
            CustomKey obj = new CustomKey("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(index, obj);
        }
        return results;
    }



    public void OpenInputDialogBox(final CustomKey customKey, final Boolean isData) {


        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.pupup_input_layout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Add");
        alert.setView(promptView);

        final EditText popup_input_text = (EditText) promptView.findViewById(R.id.popup_hidden_text);
        //final Button popup_select_button=(Button) promptView.findViewById(R.id.select_button);

        final String[] input_char = new String[1];


        //This is for spinner
        final String []character={"Please Select Character","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        final Spinner spinner=(Spinner)promptView.findViewById(R.id.spinner);
        //ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.character_array,R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter adapter=new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,character);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), parent.getItemAtPosition(position) + "", Toast.LENGTH_LONG).show();
                input_char[0] = character[position];
                if(position!=0) popup_input_text.setText(db.getTextByKeyName(character[position].toLowerCase()));
                else Toast.makeText(getContext(),"Please Select Character",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
/*
        //This is for button
        popup_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //String input_char = popup_input_char.getText().toString();
                String input_word=popup_input_text.getText().toString();

                // Do something with value!
                    if(input_char[0].compareTo(character[0])==0){
                        Toast.makeText(getContext(),"Please Select Character",Toast.LENGTH_SHORT).show();
                        OpenInputDialogBox(customKey,isData);

                    }else {
                        CustomKey customKey = db.getCustomKeyByName(input_char[0].toLowerCase());
                        customKey.setKEY_WORD(input_word);
                        db.updateKeyText(customKey);

                        //This is to change the recyvler view
                        mAdapter = new CardViewAdapter(db.getAllKey());
                        mRecyclerView.setAdapter(mAdapter);
                    }

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                //Toast.makeText(getContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //This is for selection
        String st;
        if(isData) {
            st=customKey.getKEY_NAME();
            for(int i=0; i<character.length; i++){
                if(character[i].toLowerCase().compareTo(st)==0){
                    final int finalI = i;
                    spinner.post(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setSelection(finalI);
                        }
                    });
                    alert.setTitle("Edit");
                    spinner.setEnabled(false);
                }
            }
        }

    // create an alert dialog
    AlertDialog alert1 = alert.create();

    alert1.show();

    }
    public void showMenu(View v, final CustomKey customKey){
        PopupMenu popup=new PopupMenu(getActivity(),v);
        popup.getMenuInflater().inflate(R.menu.custom_option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_edit:
                        OpenInputDialogBox(customKey,true);

                        return true;
                    case R.id.menu_delete:
                        customKey.setKEY_WORD("");
                        db.updateKeyText(customKey);
                        onResume();
                        return true;
                    default: return  false;
                }

            }
        });
        popup.show();
    }


}
