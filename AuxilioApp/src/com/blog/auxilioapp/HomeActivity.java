
package com.blog.auxilioapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdView;

@SuppressLint("SetJavaScriptEnabled")
public class HomeActivity extends Activity {

    private WebView wv;
    private ProgressDialog progressBar;
    boolean loadingFinished = true;
    boolean redirect = false;
    boolean istab;
    private String url;
    private AdView ads;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        url = "http://www.auxilioebd.blogspot.com/";
        istab = isTablet(this);

        setContentView(R.layout.activity_home);

        if (istab) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }

        wv = (WebView) findViewById(R.id.webview);

        showLoadingDialog();

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setAppCacheMaxSize(80 * 1024 * 1024);
        wv.getSettings().setAllowFileAccess(true);
        // wv.getSettings().setAppCacheEnabled(true);

        try {
            if (isOnline())
            {
                wv.clearCache(true);
                wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            }
            else {
                wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            }
        } catch (Exception e) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

            dlgAlert.setMessage("");
            dlgAlert.setTitle("Error");
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

        // Load the URLs inside the WebView, not in the external web browser
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);

                if (progress == 100)
                    setActionBar();

            }

        });

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl)
            {
                // Handle the error, implements manow.
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                wv.loadUrl(urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;
                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    dismissLoadingDialog();
                } else {
                    redirect = false;
                }

            }

        });
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.loadUrl(this.url);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Evento de botao voltar tratado. Se tiver pagina para voltar, VOLTA. Se
     * nao, Finish tuto.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if (wv.canGoBack()) {
                        wv.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void showLoadingDialog() {

        if (progressBar == null) {
            progressBar = new ProgressDialog(this);
            progressBar.setTitle("Carregando...");
            progressBar.setMessage("Aguarde ...");
        }
        progressBar.show();
    }

    public void dismissLoadingDialog() {

        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void setActionBar() {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.actionbar);
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (ads != null) {
            // ads.destroy();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
