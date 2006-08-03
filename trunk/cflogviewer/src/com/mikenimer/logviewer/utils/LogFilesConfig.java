package com.mikenimer.logviewer.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LogFilesConfig 
{
	public static ArrayList logFiles = new ArrayList();
	private File configFile;
	private static String LOGFILE = "logfile";
	
	public LogFilesConfig(String file)
	{
		configFile = new File(file);
		Document xmldoc = parseXML(configFile);
		buildFileDefinitions(xmldoc);
		
	}
	
	
	
	private Document parseXML(File file)
	{
		try
		{
			DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(file);
	        return doc;
		}
		catch(ParserConfigurationException ex)
		{
			//swallow
		}
		catch(SAXException ex)
		{
			//swallow
		}
		catch(IOException ex)
		{
			//swallow
		}
		return null;		
	}
	
	private void buildFileDefinitions(Document xml)
	{
		NodeList logfiles = xml.getElementsByTagName(LOGFILE);
		for( int i=0; i < logfiles.getLength(); i++)
		{
			Node node = logfiles.item(i);
			FileDefinition fd = new FileDefinition(node);
			LogFilesConfig.logFiles.add(fd);			
		}
	}
	
	
	
	
}
