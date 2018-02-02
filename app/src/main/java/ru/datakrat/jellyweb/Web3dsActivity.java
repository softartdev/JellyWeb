package ru.datakrat.jellyweb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class Web3dsActivity extends Activity {
    public static final String TAG = "Web3dsActivity";

    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static final String REDIRECT_FORM = "redirect_form";
    public static final String ASC_URL = "acs_url";
    public static final String MD_ORDER = "md_order";
    public static final String PA_REQ = "pa_req";
    public static final String TERM_URL = "term_url";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_3ds);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.web_3ds_progress_bar);
        mProgressBar.setMax(100);
        mWebView = (WebView) findViewById(R.id.web_3ds_web_view);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // this lines did not solve 'net::ERR_PROXY_CONNECTION_FAILED' problem
        mWebView.setWebViewClient(new BarsWebViewClient());
        mWebView.setWebChromeClient(new BarsWebChromeClient());

        Intent intent = getIntent();
        String redirectForm = intent.getStringExtra(REDIRECT_FORM);
        String acsUrl = intent.getStringExtra(ASC_URL);
        String mdOrder = intent.getStringExtra(MD_ORDER);
        String paReq = intent.getStringExtra(PA_REQ);
        String termUrl = intent.getStringExtra(TERM_URL);

        if (redirectForm != null) {
            mWebView.loadData(redirectForm, "text/html", "en_US");
        } else if (mdOrder != null && paReq != null && termUrl != null) {
            String postData = "MD=" + mdOrder + "&PaReq=" + paReq + "&TermUrl=" + termUrl;
            mWebView.postUrl(acsUrl, postData.getBytes());
        } else {
            mWebView.loadUrl(acsUrl);
        }
    }

    private class BarsWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG,"onPageStarted: " + url + ", Parameters: " + view.getOriginalUrl());
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG,"onReceivedError, errorCode: " + errorCode + ", description: " + description + ", fallingUrl: " + failingUrl);
            onError(description);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d(TAG,"onReceivedSslError: " + error.toString());
            if (error.hasError(2) || error.hasError(3)) {
                handler.proceed(); // Ignore SSL certificate errors
            } else {
                handler.cancel();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG,"onPageFinished: " + url);
            Web3dsActivity.this.setTitle(getString(R.string.secure_3ds));
            if (view.getTitle() != null) {
                if (!view.getTitle().contains("html")) {
                    Web3dsActivity.this.setTitle(view.getTitle());
                }
            }
            super.onPageFinished(view, url);

            if (url.contains("PaRes")){
                Intent intent = new Intent();
                String paRes = Uri.parse(url).getQueryParameter("PaRes");
                Log.d(TAG,"Success, PaRes=" + paRes);
                intent.putExtra("PARES", paRes);
                setResult(RESULT_OK, intent);
                finish();
            }

            if (url.contains("Cancel") || url.contains("cancel")){
                Log.d(TAG,"Cancel");
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private void onError(String description) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_payment_message)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.cancel());
        if (description != null) {
            alertBuilder.setMessage(description);
        }
        alertBuilder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class BarsWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG,"onJsAlert, message: " + message);
            result.confirm();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
