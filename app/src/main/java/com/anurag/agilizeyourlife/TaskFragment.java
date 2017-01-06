package com.anurag.agilizeyourlife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anurag.agilizeyourlife.analytics.AnalyticsApplication;
import com.anurag.agilizeyourlife.utils.AgilizeYourLifeUtils;
import com.anurag.agilizeyourlife.db.TaskTable;
import com.anurag.agilizeyourlife.db.TasksProvider;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.name;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";
    private static final String ARG_TASK_ID = AgilizeYourLifeUtils.TASK_ID;
    private static final String ARG_TASK_TITLE = AgilizeYourLifeUtils.TASK_TITLE;
    private static final String ARG_TASK_DESC = AgilizeYourLifeUtils.TASK_DESC;
    private static final String ARG_TASK_LIST_ID = AgilizeYourLifeUtils.TASK_LIST_ID;
    private static final String TARGET_LIST = AgilizeYourLifeUtils.TARGET_LIST;
    private static final String ARG_EDIT_FLAG = AgilizeYourLifeUtils.EDIT_FLAG;
    private Tracker mTracker;

    private int mTaskId;
    private int mTaskListId;
    private String mTaskTitle;
    private String mTaskDescription;
    private String mTargetList;
    private boolean mEditFlag;

    private OnFragmentInteractionListener mListener;

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param taskId Task Id
     * @param taskTitle Task Title
     * @param taskDescription Task Description
     * @param taskListId Task List Id
     * @param editFlag Edit Flag
     * @return A new instance of fragment TaskFragment.
     */
    public static TaskFragment newInstance(Integer taskId, String taskTitle, String taskDescription, Integer taskListId, String targetList, boolean editFlag) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        if(taskId != null){
        args.putInt(ARG_TASK_ID, taskId);}
        args.putString(ARG_TASK_TITLE, taskTitle);
        args.putString(ARG_TASK_DESC, taskDescription);
        if(taskListId != null){
        args.putInt(ARG_TASK_LIST_ID, taskListId);}
        args.putString(TARGET_LIST, targetList);
        args.putBoolean(ARG_EDIT_FLAG, editFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTaskId = getArguments().getInt(ARG_TASK_ID);
            mTaskTitle = getArguments().getString(ARG_TASK_TITLE);
            mTaskDescription = getArguments().getString(ARG_TASK_DESC);
            mTaskListId = getArguments().getInt(ARG_TASK_LIST_ID);
            mTargetList = getArguments().getString(TARGET_LIST);
            mEditFlag = getArguments().getBoolean(ARG_EDIT_FLAG);
        }
    }

    public boolean validate(EditText editTitle, EditText editDesc){
        boolean noError = true;
        if( editTitle.getText().toString().length() == 0 ) {
            editTitle.setError(getResources().getString(R.string.title_error));
            noError = false;
        }else if( editTitle.getText().toString().length() > 25 ) {
            editTitle.setError(getResources().getString(R.string.title_length));
            noError = false;
        }
        if( editDesc.getText().toString().length() == 0 ) {
            editDesc.setError(getResources().getString(R.string.desc_error));
            noError = false;
        }
        return noError;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        Button saveButton = (Button)view.findViewById(R.id.save);
        Button delButton = (Button)view.findViewById(R.id.delete);
        final EditText editTitle = (EditText) view.findViewById(R.id.title);
        final EditText editDesc = (EditText) view.findViewById(R.id.description);

        if (mEditFlag) {
            editTitle.setText(mTaskTitle);
            editDesc.setText(mTaskDescription);
            saveButton.setText(getResources().getString(R.string.updateButton));
            saveButton.setContentDescription(getResources().getString(R.string.updateButton));
        }else {
            saveButton.setText(getResources().getString(R.string.saveButton));
            saveButton.setContentDescription(getResources().getString(R.string.saveButton));
            delButton.setVisibility(View.GONE);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            boolean updatable = false;
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if(validate(editTitle, editDesc)){
                if (mEditFlag) {
                    Log.d(TAG,"Update action");
                    Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + mTaskId);
                    int result = getActivity().getContentResolver().update(taskUri, getDataFromUI(editTitle,editDesc), null, null);
                    if(result == 0){
                        Toast.makeText(getActivity().getBaseContext(), getActivity().getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity().getBaseContext(), getActivity().getResources().getString(R.string.taskUpdated), Toast.LENGTH_SHORT).show();
                    }
                } else {
                        Log.d(TAG,"Save action");
                        Uri uri = getActivity().getContentResolver().insert(TasksProvider.CONTENT_URI, getDataFromUI(editTitle,editDesc));
                        if (uri == null) {
                            Toast.makeText(getActivity().getBaseContext(), getActivity().getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                        } else {
                            String uriString = uri.toString();
                            Log.d("URI", uriString);
                            uriString = uriString.split("/")[1];

                            Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + uriString);
                            String[] projection = {TaskTable.COLUMN_ID, TaskTable.COLUMN_TITLE,
                                    TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_LIST};
                            Cursor cursor = getActivity().getContentResolver().query(taskUri, projection, null, null,
                                    null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COLUMN_TITLE));
                                String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COLUMN_DESCRIPTION));
                                editTitle.setText(title);
                                editDesc.setText(description);

                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Task Added")
                                        .setAction("Successful")
                                        .setValue(1)
                                        .build());
                                Toast.makeText(getActivity().getBaseContext(), getActivity().getResources().getString(R.string.taskAdded), Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                        }
                    }
                    if(getActivity() instanceof LifeBacklogActivity){
                        LifeBacklogActivity activity = (LifeBacklogActivity) getActivity();
                        activity.fab.show();
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Delete action");
                Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + mTaskId);
                int result = getActivity().getContentResolver().delete(taskUri, null, null);
                if(result == 0){
                    Toast.makeText(getActivity().getBaseContext(), getActivity().getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity().getBaseContext(), getActivity().getResources().getString(R.string.taskDeleted), Toast.LENGTH_SHORT).show();
                }
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }

    public ContentValues getDataFromUI(EditText editTitle, EditText editDesc){
        String t = editTitle.getText().toString();
        String d = editDesc.getText().toString();
        Map<String, String> contentValues = new HashMap<String, String>();
        contentValues.put(TaskTable.COLUMN_TITLE, t);
        contentValues.put(TaskTable.COLUMN_DESCRIPTION, d);
        contentValues.put(TaskTable.COLUMN_LIST, mTargetList);
        return AgilizeYourLifeUtils.getContentValuesFromMap(contentValues);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
