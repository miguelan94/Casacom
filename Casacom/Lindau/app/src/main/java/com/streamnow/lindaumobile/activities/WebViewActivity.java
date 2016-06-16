package com.streamnow.lindaumobile.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.utils.Lindau;


/** !
 * Created by Miguel Estévez on 31/1/16.
 */
public class WebViewActivity extends BaseActivity
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private WebView webView;
    private static final String TAG = "WebViewActivity";
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String apiUrlString = getIntent().getStringExtra("api_url");
        String serviceId = getIntent().getStringExtra("service_id");

        if( apiUrlString == null || apiUrlString.equals("") )
        {
            finish();
        }

        setContentView(R.layout.activity_web_view);

        View backView = findViewById(R.id.view_bgnd);
        ImageView imageView = (ImageView) findViewById(R.id.bgnd_image);
        this.webView = (WebView)findViewById(R.id.webView);

        if( serviceId != null && (serviceId.equals("29") || serviceId.equals("57")) )
        {
            FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.webview_top_frame);
            topFrameLayout.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)this.webView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            this.webView.setLayoutParams(params);
        }
        else
        {
            int colorTop = Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop;

            backView.setBackgroundColor(colorTop);
            imageView.setColorFilter(colorTop, PorterDuff.Mode.SRC_ATOP);
            imageView.invalidate();

            final GestureDetector gdt = new GestureDetector(new GestureListener());

            imageView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(final View view, final MotionEvent event)
                {
                    gdt.onTouchEvent(event);
                    return true;
                }
            });
        }


        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setDomStorageEnabled(true);

        if(apiUrlString.contains("youtube"))
        {
            this.webView.getSettings().setUseWideViewPort(true);
            this.webView.getSettings().setLoadWithOverviewMode(true);
            this.webView.canGoBack();
            this.webView.setWebChromeClient(new WebChromeClient()
            {
            });
        }

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        // final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.i(TAG, "Finished loading URL: " + url);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
        });
        webView.loadUrl(apiUrlString);
    }

    public void onPause()
    {
        super.onPause();
        this.webView.onPause();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                WebViewActivity.this.finish();
                return false;
            }
            return false;
        }
    }
}
