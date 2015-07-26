package com.hy.xp.app.task;

import java.io.Serializable;

/**
 * 浠伙拷?锟斤拷??锟�?锟�?
 * 
 * @author root
 */
public class TaskAttribute implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String FILENAME = "tb_filename";
    private String FileName;
    public static final String TASKDECILNEMIN = "tb_taskdecilnemin";
    public static final String TASKDECILNERATIO = "tb_TaskDecilneRatio";
    public static final String TASKDECLINEFLAG = "tb_TaskDeclineFlag";
    public static final String TASKDESC = "tb_TaskDesc";
    public static final String TASKNAME = "tb_TaskName";
    public static final String TASKNEWDATA = "tb_TaskNewdata";
    public static final String TASKNEXTDAYFLAG = "tb_TaskNextDayFlag";
    public static final String TASKNEXTDAYVISITDECILNEMIN = "tb_TaskNextDayVisitDecilneMin";
    public static final String TASKNEXTDAYVISITDECILNERATIO = "tb_TaskNextDayVisitDecilneRatio";
    public static final String TASKNEXTDAYVISITDECLINEFLAG = "tb_TaskNextDayVisitDeclineFlag";
    public static final String TASKNEXTDAYVISITINTERVAL = "tb_TaskNextDayVisitInterval";
    public static final String TASKNEXTDAYVISITINTERVALCOUNT = "tb_TaskNextDayVisitIntervalCount";
    public static final String TASKNEXTDAYVISITINTERVALRETURNRATIO = "tb_TaskNextDayVisitIntervalReturnRatio";
    public static final String TASKNEXTDAYVISITSTAYWAY = "tb_TaskNextDayVisitStayWay";
    public static final String TASKNEXTMONTHVISITDECILNEMIN = "tb_TaskNextMonthVisitDecilneMin";
    public static final String TASKNEXTMONTHVISITDECILNERATIO = "tb_TaskNextMonthVisitDecilneRatio";
    public static final String TASKNEXTMONTHVISITDECLINEFLAG = "tb_TaskNextMonthVisitDeclineFlag";
    public static final String TASKNEXTMONTHVISITINTERVALRETURNRATIO = "tb_TaskNextMonthVisitIntervalReturnRatio";
    public static final String TASKNEXTMONTHVISITSTAYWAY = "tb_TaskNextMonthVisitStayWay";
    public static final String TASKNEXTWEEKVISITDECILNEMIN = "tb_TaskNextWeekVisitDecilneMin";
    public static final String TASKNEXTWEEKVISITDECILNERATIO = "tb_TaskNextWeekVisitDecilneRatio";
    public static final String TASKNEXTWEEKVISITDECLINEFLAG = "tb_TaskNextWeekVisitDeclineFlag";
    public static final String TASKNEXTWEEKVISITINTERVALRETURNRATIO = "tb_TaskNextWeekVisitIntervalReturnRatio";
    public static final String TASKNEXTWEEKVISITSTAYWAY = "tb_TaskNextWeekVisitStayWay";
    public static final String TASKNUMBER = "tb_TaskNumber";
    public static final String TASKRETURNRATIO = "tb_TaskReturnratio";
    public static final String TASKSECONDACTIVEF = "tb_TaskSecondActiveF";
    public static final String TASKSECONDACTIVES = "tb_TaskSecondActiveS";
    public static final String TASKSTAYWAY = "tb_taskstayway";
    private int TaskDecilneMin;
    private double TaskDecilneRatio;
    private boolean TaskDeclineFlag;
    private String TaskDesc;
    private String TaskName;
    private int TaskNewdata;
    private boolean TaskNextDayFlag;
    private int TaskNextDayVisitDecilneMin;
    private double TaskNextDayVisitDecilneRatio;
    private boolean TaskNextDayVisitDeclineFlag;
    private int TaskNextDayVisitInterval;
    private int TaskNextDayVisitIntervalCount;
    private double TaskNextDayVisitIntervalReturnRatio;
    private int TaskNextDayVisitStayWay;
    private int TaskNextMonthVisitDecilneMin;
    private double TaskNextMonthVisitDecilneRatio;
    private boolean TaskNextMonthVisitDeclineFlag;
    private double TaskNextMonthVisitIntervalReturnRatio;
    private int TaskNextMonthVisitStayWay;
    private int TaskNextWeekVisitDecilneMin;
    private double TaskNextWeekVisitDecilneRatio;
    private boolean TaskNextWeekVisitDeclineFlag;
    private double TaskNextWeekVisitIntervalReturnRatio;
    private int TaskNextWeekVisitStayWay;
    private int TaskNumber;
    private double TaskReturnratio;
    private int TaskSecondActiveF;
    private int TaskSecondActiveS;
    private int TaskStayWay;
    
    public static final String table_name = "tb_task_attribute";
    
    public static String getFilename() {
        return "tb_filename";
    }
    
    public String getTaskName() {
        return TaskName;
    }
    
    public String getTaskDesc() {
        return TaskDesc;
    }
    
    public int getTaskNewdata() {
        return TaskNewdata;
    }
    
    public int getTaskNumber() {
        return TaskNumber;
    }
    
    public double getTaskReturnratio() {
        return TaskReturnratio;
    }
    
    public boolean isTaskDeclineFlag() {
        return TaskDeclineFlag;
    }
    
    public double getTaskDecilneRatio() {
        return TaskDecilneRatio;
    }
    
    public int getTaskDecilneMin() {
        return TaskDecilneMin;
    }
    
    public boolean isTaskNextDayFlag() {
        return TaskNextDayFlag;
    }
    
    public int getTaskNextDayVisitInterval() {
        return TaskNextDayVisitInterval;
    }
    
    public double getTaskNextDayVisitIntervalReturnRatio() {
        return TaskNextDayVisitIntervalReturnRatio;
    }
    
    public int getTaskNextDayVisitIntervalCount() {
        return TaskNextDayVisitIntervalCount;
    }
    
    public boolean isTaskNextDayVisitDeclineFlag() {
        return TaskNextDayVisitDeclineFlag;
    }
    
    public double getTaskNextDayVisitDecilneRatio() {
        return TaskNextDayVisitDecilneRatio;
    }
    
    public int getTaskNextDayVisitDecilneMin() {
        return TaskNextDayVisitDecilneMin;
    }
    
    public int getTaskStayWay() {
        return TaskStayWay;
    }
    
    public void setTaskStayWay(int taskStayWay) {
        TaskStayWay = taskStayWay;
    }
    
    public int getTaskNextDayVisitStayWay() {
        return TaskNextDayVisitStayWay;
    }
    
    public void setTaskNextDayVisitStayWay(int taskNextDayVisitStayWay) {
        TaskNextDayVisitStayWay = taskNextDayVisitStayWay;
    }
    
    public double getTaskNextWeekVisitIntervalReturnRatio() {
        return TaskNextWeekVisitIntervalReturnRatio;
    }
    
    public void setTaskNextWeekVisitIntervalReturnRatio(double taskNextWeekVisitIntervalReturnRatio) {
        TaskNextWeekVisitIntervalReturnRatio = taskNextWeekVisitIntervalReturnRatio;
    }
    
    public boolean isTaskNextWeekVisitDeclineFlag() {
        return TaskNextWeekVisitDeclineFlag;
    }
    
    public void setTaskNextWeekVisitDeclineFlag(boolean taskNextWeekVisitDeclineFlag) {
        TaskNextWeekVisitDeclineFlag = taskNextWeekVisitDeclineFlag;
    }
    
    public double getTaskNextWeekVisitDecilneRatio() {
        return TaskNextWeekVisitDecilneRatio;
    }
    
    public void setTaskNextWeekVisitDecilneRatio(double taskNextWeekVisitDecilneRatio) {
        TaskNextWeekVisitDecilneRatio = taskNextWeekVisitDecilneRatio;
    }
    
    public int getTaskNextWeekVisitDecilneMin() {
        return TaskNextWeekVisitDecilneMin;
    }
    
    public void setTaskNextWeekVisitDecilneMin(int taskNextWeekVisitDecilneMin) {
        TaskNextWeekVisitDecilneMin = taskNextWeekVisitDecilneMin;
    }
    
    public int getTaskNextWeekVisitStayWay() {
        return TaskNextWeekVisitStayWay;
    }
    
    public void setTaskNextWeekVisitStayWay(int taskNextWeekVisitStayWay) {
        TaskNextWeekVisitStayWay = taskNextWeekVisitStayWay;
    }
    
    public double getTaskNextMonthVisitIntervalReturnRatio() {
        return TaskNextMonthVisitIntervalReturnRatio;
    }
    
    public void setTaskNextMonthVisitIntervalReturnRatio(double taskNextMonthVisitIntervalReturnRatio) {
        TaskNextMonthVisitIntervalReturnRatio = taskNextMonthVisitIntervalReturnRatio;
    }
    
    public boolean isTaskNextMonthVisitDeclineFlag() {
        return TaskNextMonthVisitDeclineFlag;
    }
    
    public void setTaskNextMonthVisitDeclineFlag(boolean taskNextMonthVisitDeclineFlag) {
        TaskNextMonthVisitDeclineFlag = taskNextMonthVisitDeclineFlag;
    }
    
    public double getTaskNextMonthVisitDecilneRatio() {
        return TaskNextMonthVisitDecilneRatio;
    }
    
    public void setTaskNextMonthVisitDecilneRatio(double taskNextMonthVisitDecilneRatio) {
        TaskNextMonthVisitDecilneRatio = taskNextMonthVisitDecilneRatio;
    }
    
    public int getTaskNextMonthVisitDecilneMin() {
        return TaskNextMonthVisitDecilneMin;
    }
    
    public void setTaskNextMonthVisitDecilneMin(int taskNextMonthVisitDecilneMin) {
        TaskNextMonthVisitDecilneMin = taskNextMonthVisitDecilneMin;
    }
    
    public int getTaskNextMonthVisitStayWay() {
        return TaskNextMonthVisitStayWay;
    }
    
    public void setTaskNextMonthVisitStayWay(int taskNextMonthVisitStayWay) {
        TaskNextMonthVisitStayWay = taskNextMonthVisitStayWay;
    }
    
    public int getTaskSecondActiveF() {
        return TaskSecondActiveF;
    }
    
    public void setTaskSecondActiveF(int taskSecondActiveF) {
        TaskSecondActiveF = taskSecondActiveF;
    }
    
    public int getTaskSecondActiveS() {
        return TaskSecondActiveS;
    }
    
    public void setTaskSecondActiveS(int taskSecondActiveS) {
        TaskSecondActiveS = taskSecondActiveS;
    }
    
    public void setTaskName(String taskName) {
        TaskName = taskName;
    }
    
    public void setTaskDesc(String taskDesc) {
        TaskDesc = taskDesc;
    }
    
    public void setTaskNewdata(int taskNewdata) {
        TaskNewdata = taskNewdata;
    }
    
    public void setTaskNumber(int taskNumber) {
        TaskNumber = taskNumber;
    }
    
    public void setTaskReturnratio(double taskReturnratio) {
        TaskReturnratio = taskReturnratio;
    }
    
    public void setTaskDeclineFlag(boolean taskDeclineFlag) {
        TaskDeclineFlag = taskDeclineFlag;
    }
    
    public void setTaskDecilneRatio(double taskDecilneRatio) {
        TaskDecilneRatio = taskDecilneRatio;
    }
    
    public void setTaskDecilneMin(int taskDecilneMin) {
        TaskDecilneMin = taskDecilneMin;
    }
    
    public void setTaskNextDayFlag(boolean taskNextDayFlag) {
        TaskNextDayFlag = taskNextDayFlag;
    }
    
    public void setTaskNextDayVisitInterval(int taskNextDayVisitInterval) {
        TaskNextDayVisitInterval = taskNextDayVisitInterval;
    }
    
    public void setTaskNextDayVisitIntervalReturnRatio(double taskNextDayVisitIntervalReturnRatio) {
        TaskNextDayVisitIntervalReturnRatio = taskNextDayVisitIntervalReturnRatio;
    }
    
    public void setTaskNextDayVisitIntervalCount(int taskNextDayVisitIntervalCount) {
        TaskNextDayVisitIntervalCount = taskNextDayVisitIntervalCount;
    }
    
    public void setTaskNextDayVisitDeclineFlag(boolean taskNextDayVisitDeclineFlag) {
        TaskNextDayVisitDeclineFlag = taskNextDayVisitDeclineFlag;
    }
    
    public void setTaskNextDayVisitDecilneRatio(double taskNextDayVisitDecilneRatio) {
        TaskNextDayVisitDecilneRatio = taskNextDayVisitDecilneRatio;
    }
    
    public void setTaskNextDayVisitDecilneMin(int taskNextDayVisitDecilneMin) {
        TaskNextDayVisitDecilneMin = taskNextDayVisitDecilneMin;
    }

	@Override
	public String toString()
	{
		return "TaskAttribute [TaskName=" + TaskName + ", TaskDesc=" + TaskDesc + ", TaskNewdata=" + TaskNewdata + ", TaskNumber=" + TaskNumber + ", TaskReturnratio=" + TaskReturnratio
				+ ", TaskDeclineFlag=" + TaskDeclineFlag + ", TaskDecilneRatio=" + TaskDecilneRatio + ", TaskDecilneMin=" + TaskDecilneMin + ", TaskNextDayFlag=" + TaskNextDayFlag
				+ ", TaskNextDayVisitInterval=" + TaskNextDayVisitInterval + ", TaskNextDayVisitIntervalReturnRatio=" + TaskNextDayVisitIntervalReturnRatio + ", TaskNextDayVisitIntervalCount="
				+ TaskNextDayVisitIntervalCount + ", TaskNextDayVisitDeclineFlag=" + TaskNextDayVisitDeclineFlag + ", TaskNextDayVisitDecilneRatio=" + TaskNextDayVisitDecilneRatio
				+ ", TaskNextDayVisitDecilneMin=" + TaskNextDayVisitDecilneMin + ", FileName=" + FileName + "]";
	}

}
