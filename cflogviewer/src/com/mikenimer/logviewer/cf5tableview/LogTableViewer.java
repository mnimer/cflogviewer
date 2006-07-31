/*
 * Created on Feb 11, 2005
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.cf5tableview;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import com.mikenimer.logviewer.treeview.LogFile;
import com.mikenimer.logviewer.views.LogViewer;



/**
 * This class wraps the TableViewer and creates the column to define the table. 
 * @author mnimer
 */
public class LogTableViewer
{
    private TableViewer viewer;
    private LogViewer parentViewer;
    private TableContentProvider tableContentProvider;
    private TableViewLabelProvider tableViewLabelProvider;
    private Action action1;
    private Action action2;
    private Action doubleClickAction;
    private LogFile logFile;    
    
    /**
     * 
     */
    public LogTableViewer(LogViewer parentViewer)
    {
        this.parentViewer = parentViewer;
        this.tableContentProvider = new TableContentProvider();
        this.tableViewLabelProvider = new TableViewLabelProvider();
    }
    
    public TableViewer getViewer()
    {
        return viewer;
    }
    
    
    public void createPartControl(Composite parent)
    {
        // Table view used to display log data
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        viewer.setContentProvider(tableContentProvider);
        viewer.setLabelProvider(tableViewLabelProvider);
        //viewer.setSorter(new NameSorter());
        viewer.setInput(parentViewer.getViewSite());        
        
	        // Setup the CF5 Format table        
	        Table table = viewer.getTable();
	        table.setHeaderVisible(true);
	        // "Severity","ThreadID","Date","Time","Application","Message"
	        TableColumn column = new TableColumn(table, SWT.LEFT, 0);		
			column.setText("Severity");	
			column.setWidth(70);
			
			column = new TableColumn(table, SWT.LEFT, 1);		
			column.setText("ThreadID");		
			column.setWidth(70);
			
			column = new TableColumn(table, SWT.LEFT, 2);		
			column.setText("Date");
			column.setWidth(70);
			
			column = new TableColumn(table, SWT.LEFT, 3);		
			column.setText("Time");
			column.setWidth(70);
			
			column = new TableColumn(table, SWT.LEFT, 4);		
			column.setText("Application");
			column.setWidth(70);
			
			column = new TableColumn(table, SWT.LEFT, 5);		
			column.setText("Message");
			column.setWidth(500);
			
        

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();  
    }
    
    
	
	public void fileSelectionChange(LogFile file)
	{
	    this.logFile = file;
	    TableContentProvider cp = new TableContentProvider(viewer, file);

	    try
	    {
	        viewer.getTable().clearAll();
	        viewer.setContentProvider(cp);
	    }
	    catch(Exception ex)
	    {
	        //swallow assert check error
	    }
	    
        viewer.getTable().setTopIndex(cp.getRowCount());

	    //showMessage("{LogTableViewer}.fileSelectionChange() view file: " +file.getName());
	}
    
    

    // add log file folder
    private void makeActions()
    {
        action1 = new Action()
        {
            public void run()
            {                
            	viewer.getTable().clearAll();            
            }
        };        
        action1.setText("Clear Log");
        action1.setToolTipText("Clear all rows from view.");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        // delete log folder
        // ignore if log file is selected
        action2 = new Action()
        {
            public void run()
            {
                fileSelectionChange(logFile);
            }
        };
        action2.setText("Refresh Log");
        action2.setToolTipText("Reload log file.");
        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        // double click
        // if it's a logfile, load the log file.
        // If it's a log folder, do nothing.
        doubleClickAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ( (IStructuredSelection)selection ).getFirstElement();
                //showMessage("Double-click detected on " + obj.toString());
                
                if( obj instanceof LogFileRow )
                {
                    //LogRowDialog dialog = new LogRowDialog( parentViewer.getSite().getShell(), (LogFileRow)obj);
                    LogRowDialog dialog = new LogRowDialog( parentViewer.getSite().getShell(), ((TableContentProvider)viewer.getContentProvider()).getLogFileReader(), viewer.getTable().getSelectionIndex()-1);
                    dialog.open();
                }
                                
            }
        };
    }

    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                LogTableViewer.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        parentViewer.getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars()
    {
        IActionBars bars = parentViewer.getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
    }

    // toolbar menu.
    private void fillLocalPullDown(IMenuManager manager)
    {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
    }

    // right click menu
    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
        //drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void hookDoubleClickAction()
    {
        viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                doubleClickAction.run();
            }
        });
    }

    private void showMessage(String message)
    {
        MessageDialog.openInformation(viewer.getControl().getShell(), "CF LogViewer", message);
    }
    
}
