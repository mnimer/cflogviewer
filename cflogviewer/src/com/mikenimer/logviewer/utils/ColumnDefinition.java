package com.mikenimer.logviewer.utils;

import org.w3c.dom.NamedNodeMap;

public class ColumnDefinition 
{
	public String name;
	public String header;
	public String type;
	public boolean quoted = false;
	public boolean bracketed = false;
	public int width = 100;
	
	public ColumnDefinition(NamedNodeMap attr)
	{
		if( attr.getNamedItem("name") != null)
		{
			this.name = attr.getNamedItem("name").getNodeValue();
		}
		if( attr.getNamedItem("header") != null)
		{
			this.header = attr.getNamedItem("header").getNodeValue();
		}
		if( attr.getNamedItem("quoted") != null)
		{
			this.quoted = new Boolean(attr.getNamedItem("quoted").getNodeValue()).booleanValue();
		}
		if( attr.getNamedItem("bracketed") != null)
		{
			this.bracketed = new Boolean(attr.getNamedItem("bracketed").getNodeValue()).booleanValue();
		}
		if( attr.getNamedItem("datatype") != null)
		{
			this.type = attr.getNamedItem("datatype").getNodeValue();
		}
		if( attr.getNamedItem("width") != null)
		{
			this.width = new Integer( attr.getNamedItem("width").getNodeValue() ).intValue();
		}
		
	}
}
