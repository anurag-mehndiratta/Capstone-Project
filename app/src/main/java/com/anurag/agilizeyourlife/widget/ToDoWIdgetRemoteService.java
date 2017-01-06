package com.anurag.agilizeyourlife.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.anurag.agilizeyourlife.R;
import com.anurag.agilizeyourlife.db.TaskTable;
import com.anurag.agilizeyourlife.db.TasksProvider;

/**
 * Created by anura on 12/15/2016.
 */

public class ToDoWIdgetRemoteService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            //Cursor data is initially set to null
            private Cursor data = null;


            @Override
            public void onCreate() {
                //Intentionally left blank
            }

            @Override
            public void onDataSetChanged() {
                //If data is not null close the cursor
                if (data != null) {
                    data.close();
                }

                final long idToken = Binder.clearCallingIdentity();

                //Create a query for fetching the stock Quote
                data = getContentResolver().query(TasksProvider.CONTENT_URI,
                        new String[]{TaskTable.COLUMN_ID, TaskTable.COLUMN_TITLE, TaskTable.COLUMN_DESCRIPTION,
                                TaskTable.COLUMN_LIST},
                        TaskTable.COLUMN_LIST+ " = 3",
                        null,
                        null);

                Binder.restoreCallingIdentity(idToken);
            }

            @Override
            public void onDestroy() {
                //On destory close the cursor and assign it to null because it's destroyed
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            /**
             * Current count of the cursor data
             * @return Number of items in the cursor
             */
            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            /**
             * This method handles the main business logic
             * @param position
             * @return
             */
            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                //Reusing the same list item as for the main activity
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_itemlist);

                views.setTextViewText(R.id.task_title, data.getString(data.getColumnIndex(TaskTable.COLUMN_TITLE)));

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndexOrThrow(TaskTable.COLUMN_ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
