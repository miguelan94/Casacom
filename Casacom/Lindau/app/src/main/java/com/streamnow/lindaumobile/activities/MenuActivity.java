package com.streamnow.lindaumobile.activities;

import android.content.Intent;
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

public class MenuActivity extends BaseActivity//se crea en loginActivity
{
    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser(); //guardamos la sesion del usuario
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
            adapterArray = sessionUser.getAvailableServicesForCategoryId(categoryId);//guardamos los servicios disponibles segun la categoria
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

        if( getIntent().getBooleanExtra("sub_menu", false))//Si no hay submenu es porque cuando picnhes tiene que abrir un nuevo  servicio concreto como un webView
        {
            services = sessionUser.getAvailableServicesForCategoryId(categoryId);
            LDService service = (LDService) services.get(position);
            if (service.type.equals("2"))//servicio de webView
            {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", service.apiUrl);
                intent.putExtra("service_id", service.id);
                startActivity(intent);
            }
            else if (service.type.equals("3"))//servicio youtube
            {
                // TODO Open youtube video here
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                startActivity(intent);
            }
        }
        else//si hay submenu (tras pinchar en un item. No cuenta el principal). Submenu puede ser con 1 solo
        {
            services = sessionUser.getAvailableServicesForCategoryId(sessionUser.categories.get(position).id);//obtienes los servicios segun la categoria que has pinchado

             System.out.println("clicked on item with title " + sessionUser.categories.get(position).name + " it has " + services.size() + " services available");

            if (services.size() == 1)
            {
                LDService service = (LDService) services.get(0);

                if (service.type.equals("1"))
                {
                    if(service.id.equals("53") || service.id.equals("20")) //servicio de contactos
                    {
                        Intent intent = new Intent(this, ContactActivity.class); //abrimos contacActivity
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
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", service.apiUrl);
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
                Intent intent = new Intent(this, MenuActivity.class);//volvemos abrir otra vez esta actividad pero ahora con un nuevo menu
                intent.putExtra("category_id", sessionUser.categories.get(position).id);//guardamos en una variable la categoria y asi usarla cuando se vuelva a abrir
                intent.putExtra("sub_menu", true);//ponemos sub-menu a true ya que habra más de un servicio
                startActivity(intent);
            }
        }
    }
}
