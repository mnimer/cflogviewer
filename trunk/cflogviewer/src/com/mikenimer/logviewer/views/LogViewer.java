package com.mikenimer.logviewer.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.mikenimer.logviewer.cf5tableview.LogTableViewer;
import com.mikenimer.logviewer.treeview.LogFile;
import com.mikenimer.logviewer.treeview.LogTreeViewer;


public class LogViewer extends ViewPart 
{
    private LogTreeViewer logTreeViewer;
    private LogTableViewer logTableViewer;
    
	public void createPartControl(Composite parent) 
	{
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
 		
 		// create tdir tree, assign to grid layout
	    logTreeViewer = new LogTreeViewer(this);
	    logTreeViewer.createPartControl(sashForm);

	    // create table, assign to grid layout
	    logTableViewer = new LogTableViewer(this);
	    logTableViewer.createPartControl(sashForm);
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	    //logTreeViewer.getControl().setFocus();
	}
	
    private void showMessage(String message)
    {
        MessageDialog.openInformation( this.getViewSite().getShell(), "CF LogViewer", message);
         
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