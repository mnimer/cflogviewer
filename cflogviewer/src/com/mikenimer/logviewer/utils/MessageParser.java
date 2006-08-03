/*
 * Created on Feb 24, 2005
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author mnimer
 */
public class MessageParser
{
    private String message;
    private static String URLPATTERN = "urlpattern";
	private static String LINENUMBERPATTERN = "linenumberpattern";
	
    
    public MessageParser(String message, FileDefinition fileDef)
    {
        this.message = message;
        if( fileDef.patterns.size() > 0 )
        {
        	parseURL(fileDef);
        }
    }
    
    public String getText()
    {
    	return this.message;    
    }
    
    
    private void parseURL(FileDefinition fileDef)
    {
        StringBuffer sb = new StringBuffer();
    	ArrayList patterns = fileDef.patterns;
    	String restOfLine = message;
    	
    	for( int i=0; i<patterns.size(); i++)
    	{
    		HashMap pattern = (HashMap)patterns.get(i); 
    		if( pattern.containsKey(URLPATTERN) )
    		{
	    		Pattern url = (Pattern)pattern.get(URLPATTERN);
	    		Pattern line = (Pattern)pattern.get(LINENUMBERPATTERN);
	    		Matcher m1 = url.matcher(this.message);
	    		Matcher m2 = null;
	    		if( pattern.containsKey(LINENUMBERPATTERN) )
	    		{
	    			m2 = line.matcher(this.message);
	    		}
	    		
	    		
	    		int start = 0;
	    		String urlString;
	    		while( m1.find() )
	    		{
	    			sb.append( message.substring(start, m1.start()) );
	    			if( m2 != null && m2.find(m1.end()) )
	    			{
	    				urlString = message.substring(m1.start(), m1.end());
	    				String lineNumber = m2.group(m2.groupCount());
	    				sb.append("<a href=\"" +urlString +"|" +lineNumber +"\">" +urlString +"</a>");
	    				restOfLine = message.substring(m1.end(), message.length());
	    			}
	    			else
	    			{
	    				urlString = message.substring(m1.start(), m1.end());
	    				sb.append("<a href=\"" +urlString +"\">" +urlString +"</a>");
	    				restOfLine = message.substring(m1.end(), message.length());
	    			}
	    			start = m1.end();
	    		}
				sb.append(restOfLine);
	    		this.message = sb.toString();
    		}
    	}
    }
}
