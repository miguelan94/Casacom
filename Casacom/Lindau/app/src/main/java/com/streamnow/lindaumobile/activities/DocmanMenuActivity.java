package com.streamnow.lindaumobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.streamnow.lindaumobile.datamodel.DMCategory;
import com.streamnow.lindaumobile.datamodel.DMDocument;
import com.streamnow.lindaumobile.datamodel.DMElement;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.DocMenuAdapter;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/** !
 * Created by Miguel Estévez on 31/1/16.
 */
public class DocmanMenuActivity extends BaseActivity //se abre en menuActivity
{
    private boolean isRootMenu;
    private ArrayList<IMenuPrintable> adapterArray;

    private ProgressDialog progressDialog;

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docman_menu);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        RelativeLayout mainBackground = (RelativeLayout) findViewById(R.id.main_background);
        mainBackground.setBackgroundColor(sessionUser.userInfo.partner.colorTop);

        this.isRootMenu = getIntent().getBooleanExtra("root_menu", false);

        if( this.isRootMenu )
        {
            progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

            RequestParams requestParams = new RequestParams("access_token", sessionUser.accessToken);
            LDConnection.post("getDocsInfo", requestParams, new ResponseHandlerJson());
        }
        else
        {
            String categoyId = getIntent().getStringExtra("category_id");

            switch (categoyId)
            {
                case "-1":
                    this.adapterArray = Lindau.getInstance().getUserTree();
                    break;
                case "-2":
                    this.adapterArray = Lindau.getInstance().getRepoTree();
                    break;
                default:
                    this.adapterArray = Lindau.getInstance().getTreeWithCategoryId(categoyId);
                    break;
            }

            final ListView listView = (ListView) findViewById(R.id.docman_menu_list_view);
            listView.setAdapter(new DocMenuAdapter(DocmanMenuActivity.this, this.adapterArray));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    menuItemClicked(position);
                }
            });
        }
    }

    private void menuItemClicked(int position)
    {
        DMElement element = (DMElement) this.adapterArray.get(position);

        if( element.elementType == DMElement.DMElementType.DMElementTypeCategory )
        {
            DMCategory category = (DMCategory) element;
            Intent intent = new Intent(this, DocmanMenuActivity.class);

            if( this.isRootMenu )
            {
                if( category.getRowTitleText().equals(getString(R.string.personal_docs)) )
                {
                    intent.putExtra("category_id", "-1");
                }
                else if( category.getRowTitleText().equals(getString(R.string.general_docs)) )
                {
                    intent.putExtra("category_id", "-2");
                }
            }
            else
            {
                intent.putExtra("category_id", category.id);
            }

            startActivity(intent);
        }
        else if( element.elementType == DMElement.DMElementType.DMElementTypeDocument )
        {
            // TODO Open webview with document shown inside (call WebViewActiviy with some url)
            DMDocument document = (DMDocument) element;
            Intent intent = new Intent(this, WebViewActivity.class);

            String urlAsString = LDConnection.getAbsoluteUrl("getDocInfo") + "?access_token=" + sessionUser.accessToken + "&doc_id=" + document.id;
            String encodedURL = null;
            try
            {
                encodedURL = URLEncoder.encode(urlAsString, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            String docUrl = "http://docs.google.com/gview?embedded=true&url=" + encodedURL;
            intent.putExtra("api_url", docUrl);
            startActivity(intent);
        }
    }

    private void showAlertDialog(String msg)
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    private class ResponseHandlerJson extends JsonHttpResponseHandler
    {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
        {
            try
            {
                if( response.getString("status").equals("ok") )
                {
                    ArrayList<IMenuPrintable> userTreeArray = new ArrayList<>();
                    userTreeArray.addAll(DMCategory.categoriesWithArray(response.getJSONArray("usertree")));
                    Log.d("JSON", "JSON Object,userTree-------------->" + response.getJSONArray("usertree"));
                    ArrayList<IMenuPrintable> repoTreeArray = new ArrayList<>();
                    repoTreeArray.addAll(DMCategory.categoriesWithArray(response.getJSONArray("repotree")));
                    Log.d("JSON", "JSON Object,repoTree-------------->" + response.getJSONArray("repotree"));
                    if( userTreeArray.size() > 0 || repoTreeArray.size() > 0 )
                    {
                        Lindau.getInstance().setUserTree(userTreeArray);
                        Lindau.getInstance().setRepoTree(repoTreeArray);

                        adapterArray = new ArrayList<>();
                        if( repoTreeArray.size() > 0)  adapterArray.add(new DMCategory(getString(R.string.general_docs)));
                        if( userTreeArray.size() > 0) adapterArray.add(new DMCategory(getString(R.string.personal_docs)));

                        final ListView listView = (ListView) findViewById(R.id.docman_menu_list_view);
                        listView.setAdapter(new DocMenuAdapter(DocmanMenuActivity.this, adapterArray));

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                menuItemClicked(position);
                            }
                        });
                    }
                    else
                    {
                        new AlertDialog.Builder(DocmanMenuActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage(getString(R.string.no_docs_available))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which) { finish(); }
                                })
                                .show();
                    }
                }
                else
                {
                    showAlertDialog(getString(R.string.network_error));
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
                showAlertDialog(getString(R.string.network_error));
            }
            // Log.d("JSON", "JSONObject OK: " + response.toString());
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable)
        {
            showAlertDialog(getString(R.string.network_error));
            System.out.println("getContact onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
        {
            showAlertDialog(getString(R.string.network_error));
            System.out.println("getContact onFailure json");
            progressDialog.dismiss();
        }
    }
}
