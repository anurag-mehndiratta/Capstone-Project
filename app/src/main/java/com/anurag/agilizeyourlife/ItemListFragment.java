package com.anurag.agilizeyourlife;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.agilizeyourlife.recyclerview.Task;
import com.anurag.agilizeyourlife.recyclerview.MyItemListRecyclerViewAdapter;
import com.anurag.agilizeyourlife.db.TaskTable;
import com.anurag.agilizeyourlife.db.TasksProvider;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_LIST_ID = "listId";
    private int mColumnCount = 1;
    private int mListId = 1;
    MyItemListRecyclerViewAdapter mAdapter;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    public static ItemListFragment newInstance(int columnCount, int listId) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_LIST_ID, listId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mListId = getArguments().getInt(ARG_LIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_itemlist_list, container, false);
        RecyclerView view = (RecyclerView)rootView.getRootView().findViewById(R.id.list);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyItemListRecyclerViewAdapter(getContext(), new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(Task item) {
                    if(getActivity() instanceof CompletedTasksActivity){
                        Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.taskOnlyDeletable), Toast.LENGTH_SHORT).show();
                    }else {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_list, TaskFragment.newInstance(item.getTaskId(), item.getTaskTitle(), item.getTaskDescription(), item.getTaskListId(), item.getTaskListId() + "", true), "first").addToBackStack("tag")
                                .commit();
                        hideFabShowToast();
                    }
                }
            });
            mAdapter.setHasStableIds(true);
            recyclerView.setAdapter(mAdapter);
            getLoaderManager().initLoader(0, getArguments(), this);
        }
        return rootView;
    }

    public void hideFabShowToast(){
        if(getActivity() instanceof LifeBacklogActivity){
            ((LifeBacklogActivity)getActivity()).fab.hide();
        }else if(getActivity() instanceof SprintCycleActivity){
            ((SprintCycleActivity)getActivity()).fab.hide();
        }else if(getActivity() instanceof ToDoListActivity){
            ((ToDoListActivity)getActivity()).fab.hide();
        }
        Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.taskEdited), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mListId) {
            case 1: //Display all active tasks for LifeBacklog
                return new CursorLoader(getActivity(),
                        TasksProvider.CONTENT_URI,
                        null, TaskTable.COLUMN_LIST + " in (1,2,3)", null,
                        TaskTable.COLUMN_ID
                );
            case 2: //Display all active tasks for sprint and the day
                return new CursorLoader(getActivity(),
                        TasksProvider.CONTENT_URI,
                        null, TaskTable.COLUMN_LIST + " in (2,3)", null,
                        TaskTable.COLUMN_ID
                );
            case 3: //Display all active tasks for the day
                return new CursorLoader(getActivity(),
                        TasksProvider.CONTENT_URI,
                        null, TaskTable.COLUMN_LIST + " = 3", null,
                        TaskTable.COLUMN_ID
                );
            case 4: //Display all completed tasks
                return new CursorLoader(getActivity(),
                        TasksProvider.CONTENT_URI,
                        null, TaskTable.COLUMN_LIST + " = 4", null,
                        TaskTable.COLUMN_ID
                );
            default:    //Display all tasks
                return new CursorLoader(getActivity(),
                        TasksProvider.CONTENT_URI,
                        null, null, null,
                        TaskTable.COLUMN_ID
                );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
        if(mAdapter.getItemCount()==0){
            RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.list);
            recyclerView.setVisibility(View.GONE);
            TextView textView = (TextView)getActivity().findViewById(R.id.emptyView);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Task item);
    }
}
