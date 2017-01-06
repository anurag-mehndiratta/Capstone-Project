package com.anurag.agilizeyourlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anurag.agilizeyourlife.analytics.AnalyticsApplication;
import com.anurag.agilizeyourlife.recyclerview.Task;
import com.anurag.agilizeyourlife.utils.AgilizeYourLifeUtils;
import com.anurag.agilizeyourlife.db.TaskTable;
import com.anurag.agilizeyourlife.db.TasksProvider;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.name;

public class ToDoListActivity extends AppCompatActivity implements ItemListFragment.OnListFragmentInteractionListener, TaskFragment.OnFragmentInteractionListener {

    private static final String TAG = "ToDoListActivity";
    @BindView(R.id.fragment_list)
    FrameLayout fragmentSprint;
    @BindView(R.id.toolbar_to_do_list)
    Toolbar toolbar;
    private Tracker mTracker;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_list, ItemListFragment.newInstance(1, 3), "first")
                .commit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.fabUserPromt),Toast.LENGTH_SHORT);
                Intent intent = new Intent(getBaseContext(), LifeBacklogActivity.class);
                intent.putExtra("addFlag",true);
                startActivity(intent);
            }
        });
    }

    public void markCompleted(Task task) {
        int taskId = task.getTaskId();
        Log.d(TAG,"Mark Task Ccompleted"+ taskId);
        confirmCompleted(taskId);
    }

    public void deleteFromList(Task task) {
        int taskId = task.getTaskId();
        Log.d(TAG, "Delete from ToDo List"+ taskId);
        confirmDelete(taskId);
    }

    public void editTask(Task item) {
        Log.d(TAG, "Edit task from ToDo List"+ item.getTaskTitle());
        confirmEdit(item);
    }

    private void confirmCompleted(final int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage(getResources().getString(R.string.markTaskCompleted))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, String> contentValues = new HashMap<String, String>();
                        contentValues.put(TaskTable.COLUMN_LIST, "4");
                        Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + taskId);
                        getContentResolver().update(taskUri, AgilizeYourLifeUtils.getContentValuesFromMap(contentValues), null, null);
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.taskMarkedCompleted), Toast.LENGTH_SHORT).show();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Task Completed")
                                .setAction("Successful")
                                .setValue(1)
                                .build());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void confirmDelete(final int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage(getResources().getString(R.string.deleteTask))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, String> contentValues = new HashMap<String, String>();
                        contentValues.put(TaskTable.COLUMN_LIST, "2");
                        Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + taskId);
                        getContentResolver().update(taskUri, AgilizeYourLifeUtils.getContentValuesFromMap(contentValues), null, null);
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.taskDeleted), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void confirmEdit(final Task item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage(getResources().getString(R.string.editTask))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        fab.hide();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_list, TaskFragment.newInstance(item.getTaskId(),item.getTaskTitle(),item.getTaskDescription(),item.getTaskListId(),item.getTaskListId()+"",true), "second").addToBackStack("tag")
                                .commit();
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.taskEdited), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fab.show();
    }

    @Override
    public void onListFragmentInteraction(Task item) {
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }
}
