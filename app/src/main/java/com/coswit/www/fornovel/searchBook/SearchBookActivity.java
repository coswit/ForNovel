package com.coswit.www.fornovel.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.coswit.www.fornovel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchBookActivity extends AppCompatActivity {

    @BindView(R.id.sv_searchActivity)
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ButterKnife.bind(this);

        mSearchView.setIconified(false);

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
