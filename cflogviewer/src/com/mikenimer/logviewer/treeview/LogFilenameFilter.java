/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.treeview;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * File name filter for .log files
 * @author mnimer
 */
public class LogFilenameFilter implements FilenameFilter
{


    private ArrayList filters = new ArrayList();
    
    
    /**
     * 
     */
    public LogFilenameFilter(String fileExt)
    {
        String[] ext = fileExt.split(",");
        for( int i=0; i < ext.length; i++)
        {
            filters.add(ext[i]);
        }
    }
    
    
    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String fileName)
    {
        Iterator itr = filters.iterator();
        while( itr.hasNext() )
        {
            String extension = ((String)itr.next()).toLowerCase();
            if( fileName.toLowerCase().endsWith(extension) )
            {
                return true;
            }
        }
        return false;
    }

}
