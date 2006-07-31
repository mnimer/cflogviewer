/*
 * Created on Dec 26, 2004
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.cf5tableview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;

import org.eclipse.jface.viewers.TableViewer;


/**
 * 
 * @author mnimer
 */
public class LogFileReader
{
    private File logFile = null;
    private Vector rows = new Vector();
    private LineNumberReader lnReader = null;
    private TableViewer viewer = null;
    private final int sleepms = 1000;
    
    public LogFileReader(TableViewer viewer, File file)
    {
        this.logFile = file;
        this.viewer = viewer;
        readFile();
    }
    
    public Object[] getRows()
    {
        return rows.toArray();
    }
    
    public int getRowCount()
    {
        return rows.size() - 1;
    }
    
    private void readFile()
    {
        //make this final so the Thread can see it.
        final TableViewer view = this.viewer;
        
        // parse the file in it's own thread, instead of in the main
        // eclipse UI Thread. This way we can keep a file monitor 
        // thread running
        Thread thread = new Thread()
        {
            int counter = 0;
            public void run()
            {
	            if( logFile.canRead() )
		        {
		            FileReader reader = null;
		            try
		            {
		                reader = new FileReader(logFile);
		                lnReader = new LineNumberReader(reader);
             
		                
		                try
		                {
			                lnReader.readLine(); // reader header line.                
			                while( lnReader.ready() )
			                {			                    
			                    
			                    while (true)
			                    {
			                        String row = lnReader.readLine();
				                    if( row != null )
				                    {
					                    LogFileRow logRow = parseRow(row);
					                    rows.add( logRow );
					                    
					                    //System.out.println("{logFileReader} add Row:" +logRow +" to view:" +view);
					                    view.getControl().getDisplay().syncExec(new DisplayThread(view, logRow));
					                        
				                    }
				                    else
				                    {
				                        try
				                        {
				                            counter++;
				                            Thread.sleep(sleepms);
				                        }catch(Exception ex)
				                        {
				                            break;
				                        }
				                    }
				                    
			                    }   
			                }
		                }
		                catch (IOException ex)
		                {
		                }
			            
		            }
		            catch (FileNotFoundException ex)
		            {
		                //swallow
		            }
		            
		            finally
		            {
		                try
		                {
		                    lnReader.close();
		                    reader.close();                        
		                }
		                catch(IOException ex)
		                {
		                    //ex.printStackTrace();
		                }
		            }
		        }
            }
        };
        
        thread.start();
    }
    
    private LogFileRow parseRow(String row)
    {
        // parse CF5 formated log file
        int sep1 = row.indexOf(",", 0);
        int sep2 = row.indexOf(",", sep1 + 1);
        int sep3 = row.indexOf(",", sep2 + 1);
        int sep4 = row.indexOf(",", sep3 + 1);
        int sep5 = row.indexOf(",", sep4 + 1);
        
    
        String str = row.substring(0, sep1);
        String severity = (str.startsWith("\"") && str.endsWith("\""))? row.substring(1, sep1-1): row.substring(0, sep1);
        
        str = row.substring(sep1 + 1, sep2);
        String threadid = (str.startsWith("\"") && str.endsWith("\""))? row.substring(sep1 + 2, sep2-1): row.substring(sep1+1, sep2);//
        
        str = row.substring(sep2 + 1, sep3);
        String date = (str.startsWith("\"") && str.endsWith("\""))? row.substring(sep2+2, sep3-1): row.substring(sep2+1, sep3);//
        
        str = row.substring(sep3 + 1, sep4);
        String time = (str.startsWith("\"") && str.endsWith("\""))? row.substring(sep3+2, sep4-1): row.substring(sep3+1, sep4);//
        
        str = row.substring(sep4 + 1, sep5);
        String application = (str.startsWith("\"") && str.endsWith("\""))? row.substring(sep4+2, sep5-1): row.substring(sep4+1, sep5);//
        
        str = row.substring(sep5 + 1, row.length());
        String message = (str.startsWith("\"") && str.endsWith("\""))? row.substring(sep5+2, row.length()-1): row.substring(sep4+1, row.length()-1);//
        
        return new LogFileRow(severity, threadid, date, time, application, message);        
    }

    
    /**
     * Add single log file row to the table. 
     * 
     * The runnable thread will get access to the main eclipse UI thread 
     * to add the new row.
     * 
     * @author mnimer
     */
    private class DisplayThread implements Runnable
    {
        final TableViewer view;
        final LogFileRow row;
        public DisplayThread(TableViewer view, LogFileRow row)
        {
            this.view = view;
            this.row = row;
            //System.out.println("{logFileReader} add Row:" +row +" to view:" +view);
        }
        
        
        public void run() {
			if (view.getTable().isDisposed()) return;
			view.getTable().setItemCount(rows.size());
			view.add(row);
			// move scroll to bottom (newest row)
			viewer.getTable().setTopIndex(rows.size());
		}
    }
}
