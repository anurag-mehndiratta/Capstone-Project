package com.anurag.agilizeyourlife;

import android.content.DialogInterface;
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

public class LifeBacklogActivity extends AppCompatActivity implements ItemListFragment.OnListFragmentInteractionListener, TaskFragment.OnFragmentInteractionListener {

    private static String TAG = "LifeBacklogActivity";
    @BindView(R.id.fragment_list)
    FrameLayout fragmentList;
    @BindView(R.id.fragment_task)
    FrameLayout fragmentTask;
    @BindView(R.id.toolbar_life_backlog)
    Toolbar toolbar;
    @BindView(R.id.fab_add_task)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_backlog);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_list, ItemListFragment.newInstance(1, 1), "first")
                .commit();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && bundle.getBoolean("addFlag")){
            fab.hide();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_list, TaskFragment.newInstance(null,null,null,null,"1",false), "second").addToBackStack("tag")
                    .commit();
        }else{
            fab.show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskFragment taskFragment = (TaskFragment) getSupportFragmentManager().findFragmentByTag("second");
                if (taskFragment == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_list, TaskFragment.newInstance(null,null,null,null,"1",false), "second").addToBackStack("tag")
                            .commit();
                    Log.d(TAG, "FAB New");
                }
                fab.hide();
            }
        });
    }

    @Override
    public void onListFragmentInteraction(Task item) {
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }

    public void moveToSprint(Task task) {
        int taskId = task.getTaskId();
        confirmMoveToSprint(taskId);
        Log.d("Moved to sprint", taskId + "");
    }

    public void deleteFromList(Task task) {
        int taskId = task.getTaskId();
        confirmDelete(taskId);
        Log.d("Deleted from sprint", taskId + "");
    }

    public void editTask(Task item) {
        confirmEdit(item);
        Log.d("Task Edit Box opened", item.getTaskId() + "");
    }

    private void confirmMoveToSprint(final int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage(getResources().getString(R.string.moveTaskToSprint))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, String> contentValues = new HashMap<String, String>();
                        contentValues.put(TaskTable.COLUMN_LIST, "2");
                        Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + taskId);
                        getContentResolver().update(taskUri, AgilizeYourLifeUtils.getContentValuesFromMap(contentValues), null, null);
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.taskMovedToSprint), Toast.LENGTH_SHORT).show();
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
                        Uri taskUri = Uri.parse(TasksProvider.CONTENT_URI + "/" + taskId);
                        getContentResolver().delete(taskUri, null, null);
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
                                .replace(R.id.fragment_list, TaskFragment.newInstance(item.getTaskId(),item.getTaskTitle(),item.getTaskDescription(),item.getTaskListId(),item.getTaskListId()+"",true), "second").addToBackStack( "tag" )
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

    public FloatingActionButton getFab(){
        return fab;
    }
}
