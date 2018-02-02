package ru.datakrat.jellyweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.xwalk.core.XWalkView;

public class CrosswalkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crosswalk);

        XWalkView xWalkView = findViewById(R.id.crosswalk_view);

        Intent intent = getIntent();
        String redirectForm = intent.getStringExtra(Web3dsActivity.REDIRECT_FORM);
        String acsUrl = intent.getStringExtra(Web3dsActivity.ASC_URL);
        String mdOrder = intent.getStringExtra(Web3dsActivity.MD_ORDER);
        String paReq = intent.getStringExtra(Web3dsActivity.PA_REQ);
        String termUrl = intent.getStringExtra(Web3dsActivity.TERM_URL);

        xWalkView.loadUrl(acsUrl);
    }
}
