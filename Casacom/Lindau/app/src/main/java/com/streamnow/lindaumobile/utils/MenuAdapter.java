package com.streamnow.lindaumobile.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 2/2/16.
 */
public class MenuAdapter extends BaseAdapter
{
    private ArrayList<? extends  IMenuPrintable> items;
    private Context context;

    public MenuAdapter(Context context, ArrayList<? extends  IMenuPrintable> items)
    {
        this.context = context;
        this.items = items;

        if(this.items == null)
        {
            this.items = new ArrayList<>();
        }
    }

    @Override
    public int getCount()
    {
        return this.items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if( convertView == null )
        {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.main_menu_row, parent, false);
        }
        IMenuPrintable menuPrintable = items.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.row_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.row_text);
        textView.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        Picasso.with(context)
                .load(menuPrintable.getIconUrlString())
                .into(imageView);
        // imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));
        textView.setText(menuPrintable.getRowTitleText());


        return convertView;
    }
}



