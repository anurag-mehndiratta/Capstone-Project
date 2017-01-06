package com.anurag.agilizeyourlife.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anurag.agilizeyourlife.CompletedTasksActivity;
import com.anurag.agilizeyourlife.ItemListFragment.OnListFragmentInteractionListener;
import com.anurag.agilizeyourlife.LifeBacklogActivity;
import com.anurag.agilizeyourlife.R;
import com.anurag.agilizeyourlife.SprintCycleActivity;
import com.anurag.agilizeyourlife.ToDoListActivity;
import com.anurag.agilizeyourlife.db.TaskTable;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyItemListRecyclerViewAdapter extends RecyclerView.Adapter<MyItemListRecyclerViewAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private OnListFragmentInteractionListener mListener;

    public MyItemListRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itemlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int idx_id = mCursor.getColumnIndex(TaskTable.COLUMN_ID);
        int idx_title = mCursor.getColumnIndex(TaskTable.COLUMN_TITLE);
        int idx_description = mCursor.getColumnIndex(TaskTable.COLUMN_DESCRIPTION);
        int idx_list = mCursor.getColumnIndex(TaskTable.COLUMN_LIST);

        final Task task = new Task();
        task.setTaskId(Integer.parseInt(mCursor.getString(idx_id)));
        task.setTaskTitle(mCursor.getString(idx_title));
        task.setTaskDescription(mCursor.getString(idx_description));
        task.setTaskListId(Integer.parseInt(mCursor.getString(idx_list)));

        holder.mItem = task;
        String tempPos = ++position+"";
        holder.mIdView.setText(tempPos);
        holder.mContentView.setText(mCursor.getString(idx_title));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mItem", "On click");
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    Log.d("holder item",holder.mItem.getTaskTitle());
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        if(mContext instanceof CompletedTasksActivity){
            holder.imageButton1.setVisibility(View.GONE);
        }else {
            holder.imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("imageButton1", "On click");
                    if (mContext instanceof LifeBacklogActivity) {
                        ((LifeBacklogActivity) mContext).editTask(task);
                    } else if (mContext instanceof SprintCycleActivity) {
                        ((SprintCycleActivity) mContext).editTask(task);
                    } else if (mContext instanceof ToDoListActivity) {
                        ((ToDoListActivity) mContext).editTask(task);
                    }
                }
            });
        }

        holder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("imageButton2", "On click");
                if(mContext instanceof LifeBacklogActivity){
                    ((LifeBacklogActivity)mContext).deleteFromList(task);
                }else if(mContext instanceof SprintCycleActivity){
                    ((SprintCycleActivity)mContext).deleteFromList(task);
                }else if(mContext instanceof ToDoListActivity){
                    ((ToDoListActivity)mContext).deleteFromList(task);
                }else if(mContext instanceof CompletedTasksActivity){
                    ((CompletedTasksActivity)mContext).deleteTaskPermanently(task);
                }
            }
        });

        if (mContext instanceof LifeBacklogActivity) {
            holder.imageButton3.setContentDescription(mContext.getResources().getString(R.string.moveTaskToSprint));
        } else if (mContext instanceof SprintCycleActivity) {
            holder.imageButton3.setContentDescription(mContext.getResources().getString(R.string.moveTaskToToDoList));
        } else if(mContext instanceof ToDoListActivity){
            holder.imageButton3.setBackgroundResource(R.drawable.ic_checkmark_48);
            holder.imageButton3.setContentDescription(mContext.getResources().getString(R.string.markTaskCompleted));
        }
        if(mContext instanceof CompletedTasksActivity){
            holder.imageButton3.setVisibility(View.GONE);
        }else {
            holder.imageButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("imageButton3", "On click");
                    if (mContext instanceof LifeBacklogActivity) {
                        ((LifeBacklogActivity) mContext).moveToSprint(task);
                    } else if (mContext instanceof SprintCycleActivity) {
                        ((SprintCycleActivity) mContext).moveToToDoList(task);
                    } else if (mContext instanceof ToDoListActivity) {
                        ((ToDoListActivity) mContext).markCompleted(task);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                int idx_id = mCursor.getColumnIndex(TaskTable.COLUMN_ID);
                return mCursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageButton imageButton1;
        public final ImageButton imageButton2;
        public final ImageButton imageButton3;
        public Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            imageButton1 = (ImageButton) view.findViewById(R.id.imageButton1);
            imageButton2 = (ImageButton) view.findViewById(R.id.imageButton2);
            imageButton3 = (ImageButton) view.findViewById(R.id.imageButton3);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void setCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
