package com.mikenimer.logviewer.views;



import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.mikenimer.logviewer.tableview.LogTableViewer;
import com.mikenimer.logviewer.treeview.LogFile;
import com.mikenimer.logviewer.treeview.LogTreeViewer;
import com.mikenimer.logviewer.utils.LogFilesConfig;


public class LogViewer extends ViewPart 
{
    private LogTreeViewer logTreeViewer;
    private LogTableViewer logTableViewer;
    public static LogFilesConfig logFileConfig;
    
    public LogViewer()
    {
    	// todo: Parse XML file.
    	String file = "C:\\MyDocuments\\web\\skunkworks\\mnimer\\eclipse plugins\\ColdFusion Log Viewer\\logfiles.xml";
    	logFileConfig = new LogFilesConfig(file);
    }
    
	public void createPartControl(Composite parent) 
	{
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
 		
 		// create tdir tree, assign to grid layout
	    logTreeViewer = new LogTreeViewer(this);
	    logTreeViewer.createPartControl(sashForm);
	    
	    
	    // create table, assign to grid layout
	    logTableViewer = new LogTableViewer(this);
	    logTableViewer.createPartControl(sashForm);
	    
	    sashForm.setWeights(new int[] {1,3});
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	    //logTreeViewer.getControl().setFocus();
	}

	public void fileSelectionChange(LogFile file)
	{
	    // redirect into LogTableViewer
	    logTableViewer.fileSelectionChange(file);
	    //showMessage("{LogViewer}.fileSelectionChange() view file: " +file.getName());
	}
	

    public LogTreeViewer getTreeViewer()
    {
    	return logTreeViewer;
    }
    

    public LogTableViewer getTableViewer()
    {
        return logTableViewer;
    }
}