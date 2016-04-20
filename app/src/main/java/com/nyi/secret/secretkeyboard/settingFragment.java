package com.nyi.secret.secretkeyboard;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nyi.secret.database.CustomKey;
import com.nyi.secret.database.DatabaseHandler;

import java.util.zip.Inflater;

/**
 * Created by IN-3442 on 11-Oct-15.
 */
public class settingFragment extends android.support.v4.app.Fragment{
    TextView enable,change,test,feedback,about,howToUse;
    SwitchCompat vibrate,sound;
    DatabaseHandler db;
    FloatingActionButton fab;
    SeekBar seekBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.setting_layout,container,false);
        db=new DatabaseHandler(getContext());

        enable=(TextView) v.findViewById(R.id.enable_keyboard);
        change=(TextView) v.findViewById(R.id.switch_input_method);
        test=(TextView) v.findViewById(R.id.testKeyoard);
        sound=(SwitchCompat) v.findViewById(R.id.sound);
        vibrate=(SwitchCompat) v.findViewById(R.id.vibrate);
        feedback=(TextView) v.findViewById(R.id.Feedback);
        about=(TextView) v.findViewById(R.id.About_Us);
        howToUse=(TextView) v.findViewById(R.id.how_to_use);
        //sseekBar=(SeekBar) v.findViewById(R.id.seekBar);

        final InputMethodManager imeManager=(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        final Intent i=new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);

        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenTestDialogBox();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imeManager != null) {
                    imeManager.showInputMethodPicker();

                }
            }
        });

        howToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getActivity(),How_to_use_activity.class);
                settingFragment.this.startActivity(in);
            }
        });
/*
        This is for key press on sound
         */

        if(db.getCustomCodebyKeyCode(-999)==0)sound.setChecked(false);
        else sound.setChecked(true);

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomKey customKey = db.getCustomKeyByName("Sound");

                if (isChecked) customKey.setKEY_CUSTOM_CODE(1);
                else customKey.setKEY_CUSTOM_CODE(0);
                db.updateKeyText(customKey);
            }
        });

        /*
        This is for vibrate
         */
        if(db.getCustomCodebyKeyCode(-998)==0)vibrate.setChecked(false);
        else vibrate.setChecked(true);

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomKey customKey = db.getCustomKeyByName("Vibrate");

                if(isChecked) customKey.setKEY_CUSTOM_CODE(1);
                else customKey.setKEY_CUSTOM_CODE(0);
                db.updateKeyText(customKey);
            }
        });

        /*
        This is for feed back
         */
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{  "nyinyilin.dev@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Secret Keyboard");
                //i.putExtra(Intent.EXTRA_TEXT   , "Testing");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        This is for about us
         */
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenAboutUsDialogBox();
            }
        });
/*

        */
/*
        This is for seek bar
         *//*

        seekBar.setProgress(5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/

        return v;
    }
    private void OpenTestDialogBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.pop_up_test_layout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Test Keyboard");
        alert.setView(promptView);

        TextView test=(TextView)promptView.findViewById(R.id.pop_up_edit_test);
        test.requestFocus();
        test.setTextColor(Color.BLACK);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        // create an alert dialog
        AlertDialog alert1 = alert.create();

        alert1.show();

    }
    private void OpenAboutUsDialogBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.pop_up_about_us_layout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        //alert.setTitle("About");
        alert.setView(promptView);

        TextView aboutUsTest=(TextView) promptView.findViewById(R.id.About_Us_Test);

        //String str2="* Aye Myat Thu\n* Aye Sin Lwin\n* Ei Thinzar Phyo\n* Kaung Satt Linn\n* Nyi Nyi Lin\n* Phyo Thaw Kaung\n* Soe Thadar Hpyu\n* Win Lei Thwe\n";
        String str3 = "*\tIn this keyboard, you can encrypt and decrypt your text.\n*\tYou can also set 'Long-press word' in each character.";

        aboutUsTest.setText(str3);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        // create an alert dialog
        AlertDialog alert1 = alert.create();

        alert1.show();

    }
}
