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

import com.anurag.agilizeyourlife.recyclerview.Task;
import com.anurag.agilizeyourlife.utils.AgilizeYourLifeUtils;
import com.anurag.agilizeyourlife.db.TaskTable;
import com.anurag.agilizeyourlife.db.TasksProvider;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SprintCycleActivity extends AppCompatActivity implements ItemListFragment.OnListFragmentInteractionListener, TaskFragment.OnFragmentInteractionListener  {

    private static String TAG = "SprintCycleActivity";
    @BindView(R.id.fragment_list)
    FrameLayout fragmentSprint;
    @BindView(R.id.toolbar_sprint_cycle)
    Toolbar toolbar;
    @BindView(R.id.fab_sprint_add)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_cycle);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_list, ItemListFragment.newInstance(1,2), "first")
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

    public void moveToToDoList(Task task) {
        int taskId = task.getTaskId();
        Log.d(TAG, "Move task to to do list"+ taskId);
        confirmMoveToDo(taskId);
    }

    public void deleteFromList(Task task) {
        int taskId = task.getTaskId();
        Log.d(TAG, "Delete from sprint list"+ taskId);
        confirmDelete(taskId);
    }

    public void editTask(Task item) {
        Log.d(TAG, "Edit task sprint list" + item.getTaskTitle());
        confirmEdit(item);
    }

    private void confirmMoveToDo(final int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage(getResources().getString(R.string.moveTaskToToDoList))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, String> contentValues = new HashMap<String, String>();
                        contentValues.put(TaskTable.COLUMN_LIST, "3");
                        Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + taskId);
                        getContentResolver().update(taskUri, AgilizeYourLifeUtils.getContentValuesFromMap(contentValues), null, null);
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.taskMovedToToDoList), Toast.LENGTH_SHORT).show();
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
                        contentValues.put(TaskTable.COLUMN_LIST, "1");
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
