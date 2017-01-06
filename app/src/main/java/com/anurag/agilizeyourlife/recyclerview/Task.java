package com.anurag.agilizeyourlife.recyclerview;

public class Task {
    public static String TASK_ID = "TASK_ID";
    public static String TASK_TITLE = "TASK_TITLE";
    public static String TASK_DESCRIPTION = "TASK_DESCRIPTION";
    public static String TASK_LIST_ID = "TASK_LIST_ID";

    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private int taskListId;


    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

}
