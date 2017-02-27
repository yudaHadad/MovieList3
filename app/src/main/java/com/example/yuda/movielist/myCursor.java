package com.example.yuda.movielist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by yuda on 22/02/2017.
 */

public class myCursor extends CursorAdapter
{

    mySQLHelper mySQLHelper;

    public myCursor(Context context, Cursor c)
    {
        super(context, c);
        mySQLHelper=new mySQLHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        View v= LayoutInflater.from(context).inflate(R.layout.item_in_list , null);
        return v;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor)
    {
        String movieName= cursor.getString(cursor.getColumnIndex(DBconstants.titleColumn));
        TextView nameTV= (TextView) view.findViewById(R.id.oneItenTV);
        nameTV.setText(movieName);

        int isWatched = cursor.getInt(cursor.getColumnIndex(DBconstants.isWatchedColumn));
        final int idOfWatched = cursor.getInt(cursor.getColumnIndex(DBconstants.idColumn));

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put("isWatched", b);
                mySQLHelper.getWritableDatabase().update(DBconstants.tableName, contentValues,  "_id="+idOfWatched, null);
            }
        });

        if (isWatched==1)
        {
            checkBox.setChecked(true);
        }
        else
        {
            checkBox.setChecked(false);
        }
    }
}
