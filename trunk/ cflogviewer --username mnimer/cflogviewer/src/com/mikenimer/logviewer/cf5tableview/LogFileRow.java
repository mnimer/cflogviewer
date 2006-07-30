/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.cf5tableview;

/**
 * Single object which represents single row in CF5 log file. 
 * @author mnimer
 */
public class LogFileRow
{
    //  "Severity","ThreadID","Date","Time","Application","Message"
    private String severity = null;
    private String threadid = null;
    private String date = null;
    private String time = null;
    private String application = null;
    private String message = null;
    
    public LogFileRow(String s, String tid, String d, String t, String a, String m)
    {
        this.severity = s;
        this.threadid = tid;
        this.date = d;
        this.time = t;
        this.application = a;
        this.message = m;
    }
    
    
    /**
     * @return Returns the applciation name.
     */
    public String getApplication()
    {
        return application;
    }
    /**
     * @return Returns the date.
     */
    public String getDate()
    {
        return date;
    }
    /**
     * @return Returns the message.
     */
    public String getMessage()
    {
        return message;
    }
    /**
     * @return Returns the severity.
     */
    public String getSeverity()
    {
        return severity;
    }
    /**
     * @return Returns the threadid.
     */
    public String getThreadid()
    {
        return threadid;
    }
    /**
     * @return Returns the time.
     */
    public String getTime()
    {
        return time;
    }
}
