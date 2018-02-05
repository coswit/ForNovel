package com.coswit.www.fornovel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.coswit.www.fornovel.R;
import com.coswit.www.fornovel.searchBook.SearchBookActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolbar_main)
    Toolbar toolbarMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbarMain.setTitle("小说");
        toolbarMain.inflateMenu(R.menu.menu_toolbar_main);
        toolbarMain.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_search) {
                    startActivity(new Intent(MainActivity.this, SearchBookActivity.class));
                }
                return true;
            }
        });


    }


    @Override
    protected void onResume() {
        Log.d("MainActivity", "onRe");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "onRe");
        super.onStop();
    }


    @Override
    protected void onPause() {
        Log.d("MainActivity", "onPause");
        super.onPause();
    }
}
