package com.mikenimer.logviewer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileDefinition 
{
	public String id = String.valueOf(new java.util.Date().getTime()); //dynamic string, in case id is not defined in xml
	public String name;	
	public boolean firstrowheader = true;
	public String delimiter = ",";
	public Pattern delimPattern;
	public ArrayList columns = new ArrayList();
	public String extendedColumn;
	public int extendedColumnIndex = 0;
	public int firstrow = 1;
	public String firstlinecomment;	
	public ArrayList patterns = new ArrayList();
	
	
	private static String FIRSTROWHEADER = "firstrowheader";
	private static String DELIMITER = "delimiter";
	private static String DELIMITERPATTERN = "delimiterpattern";
	private static String EXTENDEDCOLUMN = "extendedcolumn";
	private static String FIRSTROW = "firstrow";
	private static String FIRSTLINECOMMENT = "firstlinecomment";
	private static String COLUMNS = "columns";
	private static String COLUMN = "column";
	private static String URLPATTERNS = "urlpatterns";
	private static String PATTERNS = "patterns";
	private static String URLPATTERN = "urlpattern";
	private static String LINENUMBERPATTERN = "linenumberpattern";
	
	public FileDefinition(Node xmlNode)
	{
		NamedNodeMap attr = xmlNode.getAttributes();
		if( attr.getNamedItem("id") != null )
		{
			this.id = attr.getNamedItem("id").getNodeValue();
		}
		if( attr.getNamedItem("name") != null )
		{
			this.name = attr.getNamedItem("name").getNodeValue();
		}
		
		NodeList childs = xmlNode.getChildNodes();
		for( int i=0; i < childs.getLength(); i++ )
		{
			Node item = childs.item(i);
			if( item.getNodeType() == Node.ELEMENT_NODE  )
			{
				if( item.getNodeName().equalsIgnoreCase(FIRSTROWHEADER) )
				{
					this.firstrowheader = new Boolean( getTextValue(item) ).booleanValue();
				}
				if( item.getNodeName().equalsIgnoreCase(DELIMITER) )
				{
					this.delimiter = getTextValue(item);
				}
				if( item.getNodeName().equalsIgnoreCase(DELIMITERPATTERN) )
				{
					this.delimPattern = Pattern.compile(getTextValue(item));
				}
				if( item.getNodeName().equalsIgnoreCase(EXTENDEDCOLUMN) )
				{
					this.extendedColumn = getTextValue(item);
				}
				if( item.getNodeName().equalsIgnoreCase(FIRSTLINECOMMENT) )
				{
					this.firstlinecomment = getTextValue(item);
				}
				if( item.getNodeName().equalsIgnoreCase(FIRSTROW) )
				{
					this.firstrow = new Integer( getTextValue(item) ).intValue();
				}
				if( item.getNodeName().equalsIgnoreCase(URLPATTERNS) )
				{
					this.patterns.addAll( getUrlPatterns(item.getChildNodes() ) );
				}
				if( item.getNodeName().equalsIgnoreCase(COLUMNS) )
				{
					this.columns.addAll( getColumns(item.getChildNodes() ) );
				}
				
			}			
		}
		
		
		// now we are done parsing the xml, find the index of the extendedColumn
		if( this.extendedColumn != null )
		{
			for(int i=0; i<this.columns.size();i++)
			{
				ColumnDefinition c = (ColumnDefinition)this.columns.get(i);
				if( this.extendedColumn.equals(c.name) )
				{
					this.extendedColumnIndex = i;
					break;
				}
			}
		}
	}
	
	private ArrayList getColumns(NodeList cols)
	{
		ArrayList al = new ArrayList();
		
		for( int i=0; i < cols.getLength(); i++ )
		{
			Node item = cols.item(i);
			if( item.getNodeType() == Node.ELEMENT_NODE  )
			{
				if( item.getNodeName().equalsIgnoreCase(COLUMN) )
				{
					al.add( new ColumnDefinition(item.getAttributes()) );
				}
			}			
		}
		
		return al;
	}
	
	
	private ArrayList getUrlPatterns(NodeList ptrns)
	{
		ArrayList al = new ArrayList();
		
		for( int i=0; i < ptrns.getLength(); i++ )
		{
			Node item = ptrns.item(i);
			if( item.getNodeType() == Node.ELEMENT_NODE  )
			{
				if( item.getNodeName().equalsIgnoreCase(PATTERNS) )
				{
					HashMap pattern = new HashMap();
					NodeList children = item.getChildNodes();
					for( int j=0; j < children.getLength(); j++ )
					{
						Node citem = children.item(j);
						if( citem.getNodeType() == Node.ELEMENT_NODE  )
						{
							if( citem.getNodeName().equalsIgnoreCase(URLPATTERN) )
							{
								try
								{
									pattern.put(URLPATTERN, Pattern.compile(getTextValue(citem)) );
								}catch(Exception ex){}
							}
							if( citem.getNodeName().equalsIgnoreCase(LINENUMBERPATTERN) )
							{
								try
								{
									pattern.put(LINENUMBERPATTERN, Pattern.compile(getTextValue(citem)) );
								}catch(Exception ex){}
							}
						}			
					}
					al.add(pattern);
				}
			}			
		}
		
		return al;
	}
	
	
	private String getTextValue(Node xmlNode)
	{
		NodeList childs = xmlNode.getChildNodes();
		for( int i=0; i < childs.getLength(); i++ )
		{
			Node item = childs.item(i);
			if( item.getNodeType() == Node.TEXT_NODE  )
			{
				return item.getNodeValue();
			}
		}	
		return null;
	}
}
