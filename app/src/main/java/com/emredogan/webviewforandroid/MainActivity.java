package com.emredogan.webviewforandroid;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private CustomWebViewClient webViewClient;
    private String Url = "your web site";
    private String exampleURL ="http://ceng4u.com";

    ProgressDialog mProgressDialog;
    private boolean isCacheClear = true; // her girişte çerezlerin silinmesini istiyorsanız true veriler silinmesin istiyorsanız false yapınız.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mProgressDialog = new ProgressDialog(this); //ProgressDialog objesi oluşturuyoruz
        mProgressDialog.setMessage("Hazırlanıyor...");  //ProgressDialog Yükleniyor yazısı

        webViewClient = new CustomWebViewClient();  //CustomWebViewClient classdan webViewClient objesi oluşturuyoruz

        webView = (WebView) findViewById(R.id.webView); //webview mızı xml anasayfa.xml deki webview bağlıyoruz

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new MyChrome());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient); //oluşturduğumuz webViewClient objesini webViewımıza set ediyoruz
        webView.loadUrl(Url);
        cacheClear();
    }

    private void cacheClear(){

        if(isCacheClear){
            webView.clearHistory();
            webView.clearCache(true);
            webView.clearFormData();
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }
    }
    //Lütfen bu kısımları kurcalamayın.
    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    private class CustomWebViewClient extends WebViewClient {
        //Alttaki methodların hepsini kullanmak zorunda deilsiniz
        //Hangisi işinize yarıyorsa onu kullanabilirsiniz.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { //Sayfa yüklenirken çalışır
            super.onPageStarted(view, url, favicon);

            if (!mProgressDialog.isShowing())//mProgressDialog açık mı kontrol ediliyor
            {
                mProgressDialog.show();//mProgressDialog açık değilse açılıyor yani gösteriliyor ve yükleniyor yazısı çıkıyor
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {//sayfamız yüklendiğinde çalışıyor.
            super.onPageFinished(view, url);

            if (mProgressDialog.isShowing()) {//mProgressDialog açık mı kontrol
                mProgressDialog.dismiss();//mProgressDialog açıksa kapatılıyor
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Bu method açılan sayfa içinden başka linklere tıklandığında açılmasına yarıyor.
            //Bu methodu override etmez yada edip içini boş bırakırsanız ilk url den açılan sayfa dışında başka sayfaya geçiş yapamaz

            view.loadUrl(url);//yeni tıklanan url i açıyor
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {


            //BU method webview yüklenirken herhangi bir hatayla karşilaşilırsa hata kodu dönüyor.
            //Dönen hata koduna göre kullanıcıyı bilgilendirebilir yada gerekli işlemleri yapabilirsiniz
            //errorCode ile hatayı alabilirsiniz
            //	if(errorCode==-8){
            // Timeout
            //	} şeklinde kullanabilirsiniz

            //Hata Kodları aşağıdadır...

     	/*
      *  /** Generic error
     public static final int ERROR_UNKNOWN = -1;

     /** Server or proxy hostname lookup failed
     public static final int ERROR_HOST_LOOKUP = -2;

     /** Unsupported authentication scheme (not basic or digest)
     public static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;

     /** User authentication failed on server
     public static final int ERROR_AUTHENTICATION = -4;

     /** User authentication failed on proxy
     public static final int ERROR_PROXY_AUTHENTICATION = -5;

     /** Failed to connect to the server
     public static final int ERROR_CONNECT = -6;

     /** Failed to read or write to the server
     public static final int ERROR_IO = -7;

     /** Connection timed out
     public static final int ERROR_TIMEOUT = -8;

     /** Too many redirects
     public static final int ERROR_REDIRECT_LOOP = -9;

     /** Unsupported URI scheme
     public static final int ERROR_UNSUPPORTED_SCHEME = -10;

     /** Failed to perform SSL handshake
     public static final int ERROR_FAILED_SSL_HANDSHAKE = -11;

     /** Malformed URL
     public static final int ERROR_BAD_URL = -12;

     /** Generic file error
     public static final int ERROR_FILE = -13;

     /** File not found
     public static final int ERROR_FILE_NOT_FOUND = -14;

     /** Too many requests during this load
     public static final int ERROR_TOO_MANY_REQUESTS = -15;
     	*/
            // İstediğiniz isimleri verebilirsiniz.
        }
    }

    public void onBackPressed() //Android Back Buttonunu Handle ettik. Back butonu bir önceki sayfaya geri dönecek
    {
        if (webView.canGoBack()) {//eğer varsa bir önceki sayfaya gidecek
            webView.goBack();
            webView.clearHistory();
            webView.clearCache(true);
        } else {//Sayfa yoksa uygulamadan çıkacak
            webView.clearHistory();
            webView.clearCache(true);
            super.onBackPressed();

        }
    }
}




