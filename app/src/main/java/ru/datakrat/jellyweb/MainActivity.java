package ru.datakrat.jellyweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int PICK_3DS_REQUEST = 1;

    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button onWebButton = findViewById(R.id.main_on_web_button);
        onWebButton.setOnClickListener(this);

        Button crosswalkButton = findViewById(R.id.main_crosswalk_button);
        crosswalkButton.setOnClickListener(this);

        resultTextView = findViewById(R.id.main_result_text_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_on_web_button:
                onWeb();
                break;
            case R.id.main_crosswalk_button:
                onCrosswalk();
                break;
        }
    }

    private void onWeb() {
        Intent intentWeb = new Intent(this, Web3dsActivity.class);
        prepareIntent(intentWeb);
        startActivityForResult(intentWeb, PICK_3DS_REQUEST);
    }

    private void onCrosswalk() {
        Intent intentCrosswalk = new Intent(this, CrosswalkActivity.class);
        prepareIntent(intentCrosswalk);
        startActivityForResult(intentCrosswalk, PICK_3DS_REQUEST);
    }

    private void prepareIntent(Intent intent) {
        String acsUrl = "https://web.rbsuat.com/acs/auth/start.do";
        String mdOrder = "b7f03c47-39ed-7fd5-b7f0-3c4700002d0c";
        String paReq = "eJxVkttygjAQhl/F4R5zEFGcNY7HqTPVSsULL2OIwiioEVrK0zdRqPVuv83m382/gUGRnBpfUt3ic9q3SBNbDZmKcxinh761CWZ21xowCCIl5WQtRa4kg4W83fhBNuKwb2F33xKeF9qUOJ7teF7H3oU7z96LnXT3AmNMOhaD1fBTXhlUjZju06SAatSKSkQ8zRhwcR3Nl8yhHRdjQBVCItV8wghtOW230wX0YEh5IlnIM35UPAN0RxDnPM3UD3OdFqAaIFcnFmXZpYdQfaGpckAmD+g5wCo30U3rFHHIFhO/WJbTYlFOySIQZFlu6EdwbOu4D8hUgFaTjGLSxRTTBqE9p9sjHqB7HnhiBmAtbYR5zoPgYpoMX47+p0D7rPQa6jfUBLK4nFOpK7R5fzGg58zjN2OhyLQ5kV/MROR/h8F74m9n1/W0dMaj62pbfHFj7L3IKMbaHuKRh6QBQEYGVTtD1bp19PINfgFeu7tR";
        String termUrl = "https://web.rbsuat.com/ab/rest/finish3ds.do";

        String encMdOrder = null;
        String encPaReq = null;
        String encTermUrl = null;
        try {
            encMdOrder = URLEncoder.encode(mdOrder, "UTF-8");
            encPaReq = URLEncoder.encode(paReq, "UTF-8");
            encTermUrl = URLEncoder.encode(termUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        intent.putExtra(Web3dsActivity.ASC_URL, acsUrl);
        intent.putExtra(Web3dsActivity.MD_ORDER, encMdOrder);
        intent.putExtra(Web3dsActivity.PA_REQ, encPaReq);
        intent.putExtra(Web3dsActivity.TERM_URL, encTermUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_3DS_REQUEST:
                String pares = null;
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.getStringExtra("PARES") != null) {
                            pares = data.getStringExtra("PARES");
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    pares = "Operation cancelled";
                }
                resultTextView.setText(pares);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
