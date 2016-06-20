package com.streamnow.lindaumobile.activities;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.datamodel.LDService;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.utils.MenuAdapter;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MenuActivity extends BaseActivity
{
    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        categoryId = this.getIntent().getStringExtra("category_id");
        RequestParams requestParams = new RequestParams();
        requestParams.add("id",sessionUser.userInfo.id);
        LDConnection.post("getNotifications", requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                Log.d("JSON", "JSONObject MenuActivity---------->: " + response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("login onFailure json");
            }
        });

        ArrayList<? extends IMenuPrintable> adapterArray;

        if( categoryId == null )
        {
            adapterArray = sessionUser.getAvailableServicesForSession();

        }
        else
        {
            adapterArray = sessionUser.getAvailableServicesForCategoryId(categoryId);
        }
        RelativeLayout mainBackground = (RelativeLayout) findViewById(R.id.main_menu_background);
        mainBackground.setBackgroundColor(sessionUser.userInfo.partner.colorTop);

        final ListView listView = (ListView) findViewById(R.id.main_menu_list_view);
        listView.setAdapter(new MenuAdapter(this, adapterArray));



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                menuItemClicked(position);
            }
        });
    }

    private void menuItemClicked(int position)
    {
        ArrayList<? extends IMenuPrintable> services;

        if( getIntent().getBooleanExtra("sub_menu", false))
        {
            services = sessionUser.getAvailableServicesForCategoryId(categoryId);
            LDService service = (LDService) services.get(position);
            if (service.type.equals("2"))//webView service
            {
                if(service.id.equals("29")){//tv
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(service.apiUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        intent.setPackage(null);
                        startActivity(intent);
                    }
                }
                else{
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", service.apiUrl);
                    intent.putExtra("service_id", service.id);
                    startActivity(intent);
                }

            }
            else if (service.type.equals("3"))//servicio youtube
            {
                // TODO Open youtube video here
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                startActivity(intent);
            }
        }
        else
        {
            services = sessionUser.getAvailableServicesForCategoryId(sessionUser.categories.get(position).id);

             System.out.println("clicked on item with title " + sessionUser.categories.get(position).name + " it has " + services.size() + " services available");

            if (services.size() == 1)
            {
                LDService service = (LDService) services.get(0);

                if (service.type.equals("1"))
                {
                    if(service.id.equals("53") || service.id.equals("20"))
                    {
                        Intent intent = new Intent(this, ContactActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(this, DocmanMenuActivity.class);
                        intent.putExtra("root_menu", true);
                        intent.putExtras( new Bundle());
                        startActivity(intent);
                    }
                }
                else if (service.type.equals("2"))
                {
                    Log.d("ID","ID seleccionado: " + service.id);
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", service.apiUrl);
                    Log.d("API","APU URL: -------------->" + service.apiUrl);
                    startActivity(intent);


                }
                else if (service.type.equals("3"))
                {
                    // TODO Open youtube video here
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                    startActivity(intent);
                }
            }
            else if (services.size() > 1)
            {
                Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra("category_id", sessionUser.categories.get(position).id);
                intent.putExtra("sub_menu", true);
                startActivity(intent);
            }
        }
    }
}
