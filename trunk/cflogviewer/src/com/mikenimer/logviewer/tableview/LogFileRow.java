/*
 * Created on Dec 26, 2004
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.tableview;

/**
 * Single object which represents single row in CF5 log file. 
 * @author mnimer
 */
public class LogFileRow
{
    private String[] row;
    private boolean completeRow = true;
    public LogFileRow(String[] row, boolean crow)
    {
    	this.row = row;
    	this.completeRow = crow;
    }
    
    public String[] getRow()
    {
    	return row;
    }
    
    public boolean isCompleteRow()
    {
    	return completeRow;
    }
    
    public String getColumnAt(int indx)
    {
    	return row[indx];
    }
}
