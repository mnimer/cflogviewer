package com.mikenimer.logviewer.tableview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.util.Iterator;


import com.mikenimer.logviewer.treeview.LogFile;
import com.mikenimer.logviewer.utils.ColumnDefinition;
import com.mikenimer.logviewer.utils.FileDefinition;
import com.mikenimer.logviewer.utils.LogFilesConfig;

/**
 * Read a specific number of rows from a log file. Used to determine the log file format.
 * @author mnimer
 *
 */
public class LogFileTypeParser 
{	
    
    public FileDefinition readFileRow(LogFile file)
    {
    	LineNumberReader lnReader = null;
    	File logFile = file.getFile();
    	
    	if( logFile.canRead() )
        {
    		FileReader reader = null;
    		int counter = 0;
            try
            {
                reader = new FileReader(logFile);
                lnReader = new LineNumberReader(reader);
                try
                {               
	                while( lnReader.ready())
	                {			            
	                    while (true)
	                    {
	                        String row = lnReader.readLine();
		                    if( row == null )
		                    {
		                    	return null;
		                    }
		                    else if( row.startsWith("#") )
		                    {		                    	
		                    	FileDefinition f1 = parseRowType(row);
		                    	if( f1 != null )
		                    	{
		                    		return f1;
		                    	}
		                    	counter--; // reset row counter, since we ignore comment lines
		                    	continue;
		                    }
		                    else
		                    {
		                    	return parseRowType(row);
		                    }
	                    }                   
	                }
	                
	                try
	                {
	                    lnReader.close();
	                    reader.close();                        
	                }
	                catch(IOException ex)
	                {
	                    return null;
	                }
                }
                catch (IOException ex)
                {
                	return null;
                }
	            
            }
            catch (FileNotFoundException ex)
            {
                return null;
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
                    return null;
                }
            }
        }
    	return null;    	
    }
    
    
    private FileDefinition parseRowType(String row)
    {
    	
		for( int x=0; x<LogFilesConfig.logFiles.size(); x++)
		{
			FileDefinition f = ((FileDefinition)LogFilesConfig.logFiles.get(x));
			
			// check first row comment, if set. 
			if( row.startsWith("#") )
			{
				if( f.firstlinecomment != null && row.startsWith(f.firstlinecomment) )
				{
					return f;
				}
			}
			// not a comment row. so we will parse the row according to the FileDefinition, and see if it matches. 
			else
			{
				if( f.delimPattern != null )
				{
					if( f.delimPattern.matcher(row).matches() )
					{
						return f;
					}
					continue;				
				}
				else
				{
				
					String[] cols = row.split(f.delimiter);
					// quick checks
					if( cols == null ) continue;				
					if( cols.length != f.columns.size() ) continue;
					
					// compare columns
					for( int i=0; i < cols.length; i++)
			    	{
						ColumnDefinition c = (ColumnDefinition)f.columns.get(i);
						
			    		String col = cols[i];
			    		col = (col.startsWith("\"") && col.endsWith("\""))? col.substring(1, col.length()-1): col;
			    		col = (col.startsWith("\'") && col.endsWith("\'"))? col.substring(1, col.length()-1): col;
			    		
			    		// compare column names
			    		if( f.firstrowheader )
			    		{
				    		if( col.equals(c.name) )
			    			{
				    			continue;
			    			}
				    		else
				    		{
				    			break;
				    		}
			    		}
			    		// compare data types
			    		else
			    		{
			    			Class cl = guessType(col);
			    			if( cl != null)
			    			{
			    				if( cl.getName().equals(c.type) )
			    				{
			    					continue;
			    				}
			    				else
			    				{
			    					break;
			    				}
			    			}
			    			else
			    			{
			    				break;		    			
			    			}
			    		}
			    	}
					return f;			
				}
			}
		}
		return null;
    }
    
    private Class guessType(String s)
    {
    	try
		{
			return new Integer(s).getClass();
		}catch(Exception ex){}
		try
		{
			return new Double(s).getClass();
		}catch(Exception ex){}
		try
		{
			if( s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false") )
			{    				
				return Boolean.class;
			}
		}catch(Exception ex){}
		try
		{
			return DateFormat.getDateInstance().parse(s).getClass();
		}catch(Exception ex){}
		
		return null;
    }
            	
}
