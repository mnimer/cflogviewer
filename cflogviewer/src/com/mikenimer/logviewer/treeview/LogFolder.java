/*
 * Created on Feb 9, 2005
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.treeview;

import java.io.File;
import java.util.ArrayList;



/**
 * Tree node for log folder
 * @author mnimer
 */
public class LogFolder extends LogFile {
	
    private File dir = null;
    private ArrayList children;
    
    
	public LogFolder(String name) {
		super(name);
		try
		{
		    dir = new File(name);
		    setName(dir.getAbsolutePath());
		    children = new ArrayList();
		    
		    getLogFiles(dir);
		}
		catch(Exception ex)
		{
		    setName(name);
		    // do nothing, this is the invisible root.
		}
	}
	
	public void addChild(LogFile child) {
		children.add(child);
		child.setParent(this);
	}
	
	public void removeChild(LogFile child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public LogFile [] getChildren() {
		return (LogFile [])children.toArray(new LogFile[children.size()]);
	}
	
	public boolean hasChildren() {
		return children.size()>0;
	}
	
    /**
     * recurse the folder and add each file as a child 
     *
     */
    private void getLogFiles(File folder)
    {
        File[] files = folder.listFiles(new LogFilenameFilter(".log"));
        for(int i=0; i < files.length; i++)
        {
            addChild(new LogFile(files[i]));
        }        
    }
	
	public String getName()
	{
	    if( dir == null )
	    {
	        return super.getName();
	    }
	    return dir.getAbsolutePath();
	}
	
	public boolean isValidDir()
	{
	    if( dir == null )
	    {
	        return false;
	    }
	    return true;
	}
}
