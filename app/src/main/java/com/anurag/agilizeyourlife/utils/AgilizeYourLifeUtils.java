package com.anurag.agilizeyourlife.utils;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.anurag.agilizeyourlife.recyclerview.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anura on 11/29/2016.
 */

public class AgilizeYourLifeUtils {

    public static String TASK_ID = "taskId";
    public static String TASK_TITLE = "taskTitle";
    public static String TASK_DESC = "taskDescription";
    public static String TASK_LIST_ID = "taskListId";
    public static String TARGET_LIST = "targetList";
    public static String EDIT_FLAG = "editFlag";
    /**This method is used to save a task in database
     *
     * @return
     */
    public static boolean addTask(ContentValues contentValues, Uri uri){
                
        return false;
    }

    /**
     * This method is used to edit a task in database
     * @return
     */
    public static boolean editTask(){
        return false;
    }

    /**
     * This method is used to delete a task from database
     * @return
     */
    public static boolean deleteTask(){
        return false;
    }

    /** This method is used to fetch all the tasks corresponding
     * to a particular Uri
     *
     * @param uri
     * @return
     */
    public static List getTasks(Uri uri){
        List taskList = new ArrayList();
        return taskList;
    }

    public static ContentValues getContentValuesFromMap(Map<String,String> contentValues) {
        ContentValues values = new ContentValues();
        for(String s: contentValues.keySet()) {
            values.put(s, contentValues.get(s));
        }
        return values;
    }

    public static Bundle convertTaskToBundle(Task item){
        Bundle bundle = new Bundle();
        bundle.putInt(TASK_ID, item.getTaskId());
        bundle.putString(TASK_TITLE, item.getTaskTitle());
        bundle.putString(TASK_DESC, item.getTaskDescription());
        bundle.putInt(TASK_LIST_ID, item.getTaskListId());
        return bundle;
    }
}
