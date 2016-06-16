package com.streamnow.lindaumobile.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 12/2/16.
 */
public class DocMenuAdapter extends BaseAdapter
{
    private ArrayList<? extends IMenuPrintable> items;
    private Context context;

    public DocMenuAdapter(Context context, ArrayList<? extends IMenuPrintable> items)
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

        IMenuPrintable dmElement = items.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.row_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.row_text);
        textView.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        textView.setText(dmElement.getRowTitleText());

        if( dmElement.getIconUrlString() == null )
        {
            if( dmElement.getRowTitleText().equals(context.getString(R.string.general_docs)) )
            {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.general_docs_icon));
            }
            else if( dmElement.getRowTitleText().equals(context.getString(R.string.personal_docs)) )
            {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.personal_docs_icon));
            }
        }
        else
        {
            Picasso.with(context)
                    .load(dmElement.getIconUrlString())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageView);
        }

        return convertView;
    }
}
