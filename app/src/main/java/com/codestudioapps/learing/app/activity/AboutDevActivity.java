package com.codestudioapps.learing.app.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.codestudioapps.learing.app.R;

public class AboutDevActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_about_dev);

        initToolbar(true);
        setToolbarTitle(getString(R.string.about_dev));
        enableUpButton();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

