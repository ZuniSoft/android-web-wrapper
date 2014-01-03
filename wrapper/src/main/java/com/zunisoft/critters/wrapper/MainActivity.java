package com.zunisoft.critters.wrapper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainActivity extends Activity {
    private WebView mWebView;
    private Button button;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

    /**
     * WebViewClient subclass loads all hyperlinks in the existing WebView
     */
    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * WebChromeClient subclass handles UI-related calls
     * Note: think chrome as in decoration, not the Chrome browser
     */
    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(Intent.createChooser(i,"Image Chooser"), FILECHOOSER_RESULTCODE);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        addListenerOnButton();

        mWebView = (WebView) findViewById(R.id.webView1);

        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.setWebViewClient(new GeoWebViewClient());
        mWebView.setWebChromeClient(new GeoWebChromeClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);

        mWebView.loadUrl("https://www.critterhealthrecords.com");
    }

    /** Button click listener for about box */
    public void addListenerOnButton() {
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AboutBox.Show(MainActivity.this);
            }
        });
    }

    /** Back button pressed event */
    @Override
    public void onBackPressed() {
        // Pop the browser back stack or exit the activity
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    /** Activity result event */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }
    }
}
