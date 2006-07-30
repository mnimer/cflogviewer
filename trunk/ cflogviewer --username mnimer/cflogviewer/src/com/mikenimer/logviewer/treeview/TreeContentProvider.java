/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.treeview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.mikenimer.logviewer.LogViewerPlugin;

/**
 * 
 * @author mnimer
 */
public class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    private LogFolder invisibleRoot;
    private LogTreeViewer parentViewer;
    private ArrayList logFolders = new ArrayList();
    
    private static final String FOLDERS = "folders";
    private static final String FOLDER = "folder";

	/**
	 * The constructor.
	 */
	public TreeContentProvider(LogTreeViewer parentViewer) 
	{
	    this.parentViewer = parentViewer;
	}
	
	void addFolder(String folder)
	{
	    System.out.println("add folder:" +folder);
	    // add log folder from the content provider
	    LogFolder file = new LogFolder(folder);
	    logFolders.add(file);
	    saveLogFolders();
	    refresh();
	    
	    //System.out.println("save file:" +getLogViewerFile().getAbsolutePath());
	}
	
	void deleteFolder(LogFolder logFolder)
	{
	    logFolders.remove(logFolder);
	    saveLogFolders();
	    refresh();
	}
	
	void deleteFile(LogFile logFile)
	{
	    //logFolders.remove(logFile);
	    refresh();
	}	
	
	void refresh()
	{
	    parentViewer.getTreeViewer().getTree().removeAll();
	    logFolders = new ArrayList();
	    initialize();
	    parentViewer.getTreeViewer().refresh( );
	}
	
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object parent)
    {
        if ( parent.equals(parentViewer.getLogViewer().getViewSite()) )
        {
            if ( invisibleRoot == null ) initialize();
            return getChildren(invisibleRoot);
        }
        
        return getChildren(parent);
    }

    public Object getParent(Object child)
    {
        if ( child instanceof LogFile )
        {
            return ( (LogFile)child ).getParent();
        }
        return null;
    }

    public Object[] getChildren(Object parent)
    {
        if ( parent instanceof LogFolder )
        {
            return ( (LogFolder)parent ).getChildren();
        }
        return new Object[0];
    }

    public boolean hasChildren(Object parent)
    {
        if ( parent instanceof LogFolder ) return ( (LogFolder)parent ).hasChildren();
        return false;
    }

    /*
     * We will set up a dummy model to initialize tree heararchy. In a real
     * code, you will connect to a real model and expose its hierarchy.
     */
    private void initialize()
    {
            
        invisibleRoot = new LogFolder("Root");       
        
        if( logFolders.size() == 0 )
        {
            loadLogFolders();
        }
        
        for(int i=0; i < logFolders.size(); i++ )
        {
            if( logFolders.get(i) instanceof LogFolder )
            {
                invisibleRoot.addChild( ((LogFolder)logFolders.get(i)) );
            }            
        }
       
    }
    
    private File getLogViewerFile()
    {
        return LogViewerPlugin.getDefault().getStateLocation().append("logviewer.xml").toFile();
    }
    
    private void loadLogFolders()
    {
        FileReader reader = null;
        try
        {
            reader = new FileReader( getLogViewerFile() );
            loadLogFolders( XMLMemento.createReadRoot(reader) );
        }
        catch( FileNotFoundException ex)
        {
            // ignore
            System.out.print("no file exists");
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if( reader != null )
                {
                    reader.close();
                }
            }
            catch(java.io.IOException ex)
            {
                ex.printStackTrace();
            }
        }

    }
    
    private void loadLogFolders(XMLMemento memento)
    {
        IMemento[] children = memento.getChildren(FOLDER);
        for( int i=0; i < children.length; i++)
        {
            LogFolder folder = new LogFolder( children[i].getTextData() );
            if( folder != null )
            {
                logFolders.add(folder);
            }
        }
    }
    
    
    private void saveLogFolders()
    {
        XMLMemento memento = XMLMemento.createWriteRoot(FOLDERS);
        saveLogFolders(memento);
        
        FileWriter writer = null;
        try
        {
            writer = new FileWriter(getLogViewerFile());
            memento.save(writer);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if( writer != null )
                {
                    writer.close();
                }                
            }   
            catch( IOException ex ){ }
        }
    }
    
    private void saveLogFolders(XMLMemento memento)
    {
        for(int i=0; i < logFolders.size(); i++)
        {
            if( ((LogFolder)logFolders.get(i)).isValidDir() )
            {
                IMemento child = memento.createChild(FOLDER);
                child.putTextData( ((LogFolder)logFolders.get(i)).getName() );
            }
        }
        
        
    }
    
    
}

