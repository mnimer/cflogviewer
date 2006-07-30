/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import com.mikenimer.logviewer.cf5tableview.LogTableViewer;
import com.mikenimer.logviewer.treeview.LogFile;
import com.mikenimer.logviewer.treeview.LogTreeViewer;


public class LogViewer extends ViewPart 
{
    private LogTreeViewer logTreeViewer;
    private LogTableViewer logTableViewer;
    
	public void createPartControl(Composite parent) {

	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = true;
 		parent.setLayout(gridLayout);
 		
 		GridData treeData = new GridData();
 		treeData.horizontalSpan = 1;
        treeData.horizontalAlignment = GridData.FILL;        
        treeData.verticalAlignment = GridData.FILL;
        treeData.grabExcessVerticalSpace = true;
        
        GridData tableData = new GridData();
        tableData.horizontalSpan = 2;
        tableData.horizontalAlignment = GridData.FILL;        
        tableData.verticalAlignment = GridData.FILL;
        tableData.grabExcessVerticalSpace = true;
	    
        // create tdir tree, assign to grid layout
        
	    logTreeViewer = new LogTreeViewer(this);
	    logTreeViewer.createPartControl(parent);
	    TreeViewer treeView = logTreeViewer.getTreeViewer();
	    //logTreeViewer.createPartControl(parent);
	    treeView.getTree().setLayoutData(treeData);
	    
	    
	    // create table, assign to grid layout
	    logTableViewer = new LogTableViewer(this);
	    logTableViewer.createPartControl(parent);
	    TableViewer tableView = logTableViewer.getViewer();
	    tableView.getTable().setLayoutData(tableData);
	    
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