package com.anurag.agilizeyourlife;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anurag.agilizeyourlife.recyclerview.Task;
import com.anurag.agilizeyourlife.db.TasksProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompletedTasksActivity extends AppCompatActivity implements ItemListFragment.OnListFragmentInteractionListener, TaskFragment.OnFragmentInteractionListener{

    private static String TAG = "CompletedTasksActivity";
    @BindView(R.id.fragment_list)
    FrameLayout fragmentSprint;
    @BindView(R.id.toolbar_completed_tasks)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_list, ItemListFragment.newInstance(1, 4), "first")
                .commit();
    }

    public void deleteTaskPermanently(Task task) {
        int taskId = task.getTaskId();
        Log.d(TAG,"Delete from completed list"+ taskId);
        confirmDelete(taskId);
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

    @Override
    public void onListFragmentInteraction(Task item) {
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }
}
