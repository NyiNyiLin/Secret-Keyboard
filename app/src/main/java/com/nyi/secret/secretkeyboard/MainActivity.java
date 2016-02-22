package com.nyi.secret.secretkeyboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nyi.secret.database.CustomKey;
import com.nyi.secret.database.DatabaseHandler;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is for first time lunch or not
        db=new DatabaseHandler(this);

        if(db.getCustomCodebyKeyCode(-997)==0){
            CustomKey customKey = db.getCustomKeyByName("first_time_or_not");
            customKey.setKEY_CUSTOM_CODE(1);
            db.updateKeyText(customKey);
            Intent in=new Intent(this,How_to_use_activity.class);
            startActivity(in);

        }


        //This is for Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        ActionBar actionBar = getSupportActionBar();

        //This is for viewpager
        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());





    }

}
