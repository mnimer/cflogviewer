/*
 * Created on Dec 26, 2004
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.tableview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;

import org.eclipse.jface.viewers.TableViewer;

import com.mikenimer.logviewer.utils.ColumnDefinition;
import com.mikenimer.logviewer.utils.FileDefinition;


/**
 * 
 * @author mnimer
 */
public class LogFileReader
{
    private File logFile = null;
    private ArrayList rows = new ArrayList();
    private ArrayList tempRows = new ArrayList();
    private LineNumberReader lnReader = null;
    private TableViewer viewer = null;
    private final int sleepms = 10;
    private FileDefinition fileDef;
    private Thread runningThread;
    private boolean stopThread = false;
    private static int rowCounter = 0;
    
    
    public LogFileReader(FileDefinition fileDef, TableViewer viewer, File file)
    {
        this.logFile = file;
        this.viewer = viewer;
        this.fileDef = fileDef;
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
        runningThread = new Thread()
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
		                	if( fileDef != null && fileDef.firstrowheader )
		                	{
		                		lnReader.readLine(); // reader header line.
		                	}
			                while( lnReader.ready() && !stopThread )
			                {			                    
			                    
			                    while (true && !stopThread)
			                    {
			                        String row = lnReader.readLine();
				                    if( row != null && !row.startsWith("#") )
				                    {
					                    LogFileRow logRow = parseRow(row);
					                    if( fileDef != null )
					                    {
						                    // if the next row isn't a complete row (next line of a stacktrace) 
						                    // and we have an extendedColumn defined for this file type.
						                    // we will back up a row, and add this line to the end of the extended data column.
						                    if( !logRow.isCompleteRow() && fileDef.extendedColumn != null && tempRows.size() > 0 )
						                    {
						                    	synchronized (rows)
						                    	{
							                    	int currentTempRow = tempRows.size()-1;
							                    	int currentRow = rows.size()-1;
							                    	LogFileRow temprow = (LogFileRow)tempRows.remove(currentTempRow);
							                    	rows.remove(currentTempRow); // remove here, to keep in sync with tempRows
							                    	String[] thisrow = temprow.getRow();
							                    	thisrow[fileDef.extendedColumnIndex] = thisrow[fileDef.extendedColumnIndex] + logRow.getColumnAt(fileDef.extendedColumnIndex);
							                    	
							                    	LogFileRow temprow2 = new LogFileRow(thisrow, true);
							                    	rows.add(currentRow, temprow2 );
							                    	tempRows.add(currentTempRow, temprow2 );
						                    	}
					                    	}
						                    else if( logRow.isCompleteRow() )
						                    {
						                    	rows.add( logRow );
						                    	tempRows.add( logRow );
						                    }
					                    }
					                    else
					                    {
					                    	rows.add( logRow );
					                    	tempRows.add( logRow );
					                    }
					                    
					                    // only send rows back to the client 10 at a time.					                    
				                    	while( tempRows.size() > 1 )
				                		{
				                			//System.out.println("{logFileReader} add Row:" +logRow +" to view:" +view);
				                			view.getControl().getDisplay().syncExec(new DisplayThread(view, (LogFileRow)tempRows.remove(0)));
				                		}
					                					                    
					                     
				                    }
				                    else
				                    {
				                        try
				                        {
				                        	// before we sleep the thread, we'll output any tempRows that are left.
				                        	while( tempRows.size() > 0 )
					                		{
					                			//System.out.println("{logFileReader} add Row:" +logRow +" to view:" +view);
					                			view.getControl().getDisplay().syncExec(new DisplayThread(view, (LogFileRow)tempRows.remove(0)));
					                		}
				                        	
				                        	
				                            counter++;
				                            Thread.sleep(sleepms);
				                        }catch(Exception ex)
				                        {
				                            break;
				                        }
				                    }
				                    
			                    }
			                    break;
			                }
			                
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
        
        runningThread.start();
    }
    
    
    private LogFileRow parseRow(String row) //todo: parse based on FileDefinition
    {
    	boolean completeRow = true;
    	 
    	if( fileDef == null ) 
		{
    		String[] str = new String[1];
    		str[0] = row;
    		return new LogFileRow(str, false);
		}
    	
    	String[] cols;
    	
    	if( fileDef.delimPattern != null )
		{
    		cols = new String[fileDef.columns.size()];
    		for(int j=1; j<=cols.length; j++)
    		{
    			
    			Matcher m = fileDef.delimPattern.matcher(row); 
    			//if the row doesn't match the pattern, a 2nd line of an error for instance, we will put the row in the extendedColumn instead.
				if( m.matches() )
				{
					ColumnDefinition c = ((ColumnDefinition)fileDef.columns.get(j-1));
					if( c.bracketed || c.quoted )
		    		{
		    			String str = m.group(j)!=null?m.group(j).toString():"";
		    			cols[j-1] = str.length()>1?str.substring(1, str.length()-1):str;    			
		    		}
	    			else
	    			{
	    				cols[j-1] = m.group(j)!=null?m.group(j).toString():"";
	    			}
					
				}
				else
				{
					completeRow = false;
					if( fileDef.extendedColumn != null ) 
		    		{
		    			for(int i=0; i < fileDef.columns.size(); i++)
		    	    	{
		    				ColumnDefinition c = ((ColumnDefinition)fileDef.columns.get(i));
		    	    		if( c.name.equals(fileDef.extendedColumn) )
		    	    		{
		    	    			cols[i] = row;
		    	    		}
		    	    		else
		    	    		{
		    	    			cols[i] = null;
		    	    		}
		    	    	}    			
		    		}
		    		else
		    		{
		    			for(int i=0; i < fileDef.columns.size(); i++)
		    	    	{    	    		
		    	    		if( i==0 ) //put the value in the first column.
		    	    		{
		    	    			cols[i] = row;
		    	    		}
		    	    		else
		    	    		{
		    	    			cols[i] = null;
		    	    		}
		    	    	}    	
		    		}
				}
    			/*
    			fileDef.delimPattern = Pattern.compile("([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)\\s([^ ]*)");
    			fileDef.delimPattern.matcher(row).matches();
    			fileDef.delimPattern.matcher(row).group();
    			*/
    		}
		
		}
		else
		{
    	
	    	// break columns up.
	    	cols = new String[fileDef.columns.size()];
	    	String[] tempCols = row.split(fileDef.delimiter, fileDef.columns.size());//(fileDef.delimiter);
	    	
	    	if( tempCols.length < fileDef.columns.size() )
	    	{
    			completeRow = false;
	    		if( fileDef.extendedColumn != null ) 
	    		{
	    			for(int i=0; i < fileDef.columns.size(); i++)
	    	    	{
	    	    		ColumnDefinition c = ((ColumnDefinition)fileDef.columns.get(i));
	    	    		if( c.name.equals(fileDef.extendedColumn) )
	    	    		{
	    	    			if( c.bracketed || c.quoted )
	    		    		{
	    		    			String str = row;
	    		    			cols[i] = str.substring(1, str.length()-1);    			
	    		    		}
	    	    			else
	    	    			{
	    	    				cols[i] = row;
	    	    			}
	    	    		}
	    	    		else
	    	    		{
	    	    			cols[i] = null;
	    	    		}
	    	    	}    			
	    		}
	    		else
	    		{
	    			for(int i=0; i < fileDef.columns.size(); i++)
	    	    	{    	    		
	    	    		if( i==0 ) //put the value in the first column.
	    	    		{
	    	    			ColumnDefinition c = ((ColumnDefinition)fileDef.columns.get(i));
	    	    			if( c.bracketed || c.quoted )
	    		    		{
	    		    			String str = row;
	    		    			cols[i] = str.substring(1, str.length()-1);    			
	    		    		}
	    	    			else
	    	    			{
	    	    				cols[i] = row;
	    	    			}
	    	    		}
	    	    		else
	    	    		{
	    	    			cols[i] = null;
	    	    		}
	    	    	}    	
	    		}
	    	}
	    	else
	    	{
		    	for(int i=0; i < fileDef.columns.size(); i++)
		    	{
		    		ColumnDefinition c = ((ColumnDefinition)fileDef.columns.get(i));
		    		if( ( c.bracketed || c.quoted ) && tempCols[i].length() > 1 )
		    		{
		    			String str = tempCols[i];
		    			cols[i] = str.substring(1, str.length()-1);    			
		    		}		    		
		    		else
		    		{
		    			cols[i] = tempCols[i];
		    		}
		    	}
	    	}		    
		}
        return new LogFileRow(cols, completeRow);        
    }
    
    public void dispose()
    {
    	stopThread = true;
		runningThread.interrupt();
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
			view.getTable().setItemCount(view.getTable().getItemCount());
			view.add(row);
			// move scroll to bottom (newest row)
			viewer.getTable().setTopIndex(view.getTable().getItemCount());
		}
    }
}
