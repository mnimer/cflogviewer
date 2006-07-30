/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.cf5tableview;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.mikenimer.logviewer.treeview.LogFile;


/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 * 
 * Created on Dec 24, 2004
 * @author mnimer
*/
public class TableContentProvider implements IStructuredContentProvider
{
    private File logFile = null;
    private TableViewer viewer = null;
    private LogFileReader logFileReader = null;
        
    public TableContentProvider()
    {
        
    }
    
    public TableContentProvider(TableViewer viewer, LogFile file)
    {
        this.viewer = viewer;
        if( file != null )
        {
            this.logFile = file.getFile().getAbsoluteFile();
        }
    }
    

    public void setLogFile(String file)
    {
        setLogFile(new File(file));
    }
    
    public void setLogFile(File file)
    {
        this.logFile = file;
    }
    
    
    public void dispose()
    {
    }
    
    public void refresh()
    {
        if( viewer != null )
        {
            viewer.getTable().clearAll();
        }
        logFileReader = new LogFileReader(this.viewer, logFile);
    }
    
    public LogFileReader getLogFileReader()
    {
        return logFileReader;
    }
    
    
    public int getRowCount()
    {
        if( logFileReader == null )
        {
            return 0;
        }
        return logFileReader.getRowCount();
    }

    public Object[] getElements(Object parent) 
    {
        if( logFileReader == null && logFile != null )
        {
            refresh();
            return logFileReader.getRows();
        }
        return new Object[0];
    }
	
    public void inputChanged(Viewer v, Object oldInput, Object newInput) 
    {
        //System.out.println("{TableContextProvider}.itemChanged()" +v +":" +oldInput +":" +newInput);
	}
    
    
        

	
}