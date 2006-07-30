/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.utils;

import java.util.HashMap;
import java.util.Vector;

/**
 * 
 * @author mnimer
 */
public class MessageParser
{
    private Vector urls = new Vector();    
    private String message;
    
    public MessageParser(String message)
    {
        this.message = message;
        parseURL();
    }
    
    public Vector getUrls()
    {
        return urls;
    }
    
    private void parseURL()
    {
        char[] chars = message.toCharArray();
        int pos = message.indexOf(".cfm ");
        
        
        while( pos != -1 )
        {
            int start = 0;
            int end = pos + 4;
	        while (pos > 0)
	        {
	            if( chars[pos] == ' ' || chars[pos] == '[' )
	            {
	                start = pos;
	                break;
	            }
	            pos--;
	        }
	        
	        
	        if( start > 0 && pos != -1 )
	        {
	            System.out.println("url:" +message.substring(start, end));
	            HashMap range = new HashMap();
	            range.put("start", new Integer(start));
	            range.put("end", new Integer(end - start));
	            urls.add(range);
	        }
	        pos = message.indexOf(".cfm", end);	        
        }
        
    }
}
