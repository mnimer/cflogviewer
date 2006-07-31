/*
 * Created on Feb 9, 2005
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.treeview;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;


import com.mikenimer.logviewer.views.LogViewer;


/**
 * wrapper class to wrap the TreeViewer, used to display the log files. 
 * @author mnimer
 */
public class LogTreeViewer
{
    private TreeViewer viewer;
    private TreeContentProvider treeContentProvider;
    private TreeLabelProvider treeLabelProvider;
    private DrillDownAdapter drillDownAdapter;
    private Action action1; // add log folder
    private Action action2; // delete log folder
    private Action action3; // delete file
    private Action doubleClickAction;
    private LogViewer parentViewer;

    /**
     * The constructor.
     */
    public LogTreeViewer(LogViewer parentViewer)
    {
        this.parentViewer = parentViewer;
        this.treeContentProvider = new TreeContentProvider(this);
        this.treeLabelProvider = new TreeLabelProvider();
    }
    
    public LogViewer getLogViewer()
    {
        return parentViewer;
    }
    public TreeViewer getTreeViewer()
    {
        return viewer;
    }
  
  

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        //drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider( treeContentProvider );
        viewer.setLabelProvider( treeLabelProvider );
        viewer.setInput(parentViewer.getViewSite());
        
        viewer.getTree().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if( e.keyCode == SWT.F5 )
                {
                    treeContentProvider.refresh();
                }
            }
        });
        
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    // add log file folder
    private void makeActions()
    {
        action1 = new Action()
        {
            public void run()
            {                
                DirectoryDialog dialog = new DirectoryDialog(parentViewer.getViewSite().getShell());
                String dir = dialog.open();
                if( dir != null )
                {
                    // add Folder to contentProvider
                    System.out.println(dir);
                    treeContentProvider.addFolder(dir);
                }                
            }
        };        
        action1.setText("Add Log Folder");
        action1.setToolTipText("Add Log Folder");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        // delete log folder
        // ignore if log file is selected
        action2 = new Action()
        {
            public void run()
            {
                StructuredSelection selection = (StructuredSelection)viewer.getSelection();
                Iterator itr = selection.iterator();
            	while( itr.hasNext() )
                {
            	    Object obj = itr.next();
	                if( obj instanceof LogFolder )
	                {
	                    treeContentProvider.deleteFolder( (LogFolder)obj );
	                }
	                else
	                {
	                    showMessage("Folder can not be deleted.");
	                }
                }
            }
        };
        action2.setText("Delete Log Folder");
        action2.setToolTipText("Delete Log Folder");
        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        
        // delete log folder
        // ignore if log file is selected
        action3 = new Action()
        {
            public void run()
            {
                StructuredSelection selection = (StructuredSelection)viewer.getSelection();
                Iterator itr = selection.iterator();
            	while( itr.hasNext() )
                {
            	    Object obj = itr.next();
	                if( obj instanceof LogFile )
	                {
	                    try
	                    {
	                        ((LogFile)obj).getFile().delete();
	                        treeContentProvider.deleteFile((LogFile)obj);
	                    }
	                    catch(Exception ex)
	                    {
	                        showMessage("File can not be deleted.");
	                    }
	                }
	                else
	                {
	                    showMessage("File can not be deleted.");
	                }
                }
            }
        };
        action3.setText("Delete Log File");
        action3.setToolTipText("Delete Log File");
        action3.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        
        
        
        // double click
        // if it's a logfile, load the log file.
        // If it's a log folder, do nothing.
        doubleClickAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ( (IStructuredSelection)selection ).getFirstElement();
                if( obj instanceof LogFile )
                {
                    parentViewer.fileSelectionChange((LogFile)obj);
                }
                //showMessage("Double-click detected on " + obj.toString());
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
                LogTreeViewer.this.fillContextMenu(manager);
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
        manager.add(action3);
        manager.add(new Separator());
    }

    // right click menu
    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(action1);
        manager.add(action2);
        manager.add(action3);
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
        MessageDialog.openInformation(viewer.getControl().getShell(), "CF LogViewer2", message);
    }

}