package com.nyi.secret.secretkeyboard;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nyi.secret.database.CustomKey;
import com.nyi.secret.database.DatabaseHandler;

/**
 * Created by IN-3442 on 11-Oct-15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            settingFragment settingFragment=new settingFragment();

            return settingFragment;
        }
        else {
            inputFragment inputFragment=new inputFragment();

            return inputFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0) return "Setting";
        else return "Input";
    }
}
