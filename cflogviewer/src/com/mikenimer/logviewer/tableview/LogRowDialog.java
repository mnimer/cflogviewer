/*
 * Created on Feb 21, 2005
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.tableview;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.editors.text.JavaFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.mikenimer.logviewer.utils.ColumnDefinition;
import com.mikenimer.logviewer.utils.FileDefinition;
import com.mikenimer.logviewer.utils.MessageParser;



/**
 * 
 * @author mnimer
 */
public class LogRowDialog extends Dialog
{
	private Composite parent;
    private LogFileRow row;
    private LogFileReader reader = null;
    private int rowIndex = 0;
    
    Label[] labels;
    Label[] labelValues;
    
    StyledText extendedValue;
    Link extendedValueLink;
    FileDefinition fileDefinition;
   
    public LogRowDialog(Shell parentShell, LogFileRow obj)
    {
        //super(parentShell, "Information", null, obj.getMessage(), MessageDialog.INFORMATION, btnLabels, 0);
        super(parentShell);
        this.parent = parentShell;
        this.row = obj;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
    } 
    public LogRowDialog(Shell parentShell, LogFileReader reader, int rowIndex, FileDefinition fileDef)
    {
        //super(parentShell, "Information", null, obj.getMessage(), MessageDialog.INFORMATION, btnLabels, 0);
        super(parentShell);
        
        
        this.fileDefinition = fileDef;
        
        if( fileDefinition != null)
        {
	        // does extended data column exists
	        if( fileDefinition.extendedColumn != null )
	        {
	        	labels = new Label[fileDef.columns.size()];
	        	labelValues = new Label[fileDef.columns.size()];
	        }
	        else
	        {
	        	labels = new Label[fileDef.columns.size()];
	        	labelValues = new Label[fileDef.columns.size()];
	        }
        }
        else
        {
        	labels = new Label[1];
        	labelValues = new Label[1];
        }

	        
        this.parent = parentShell;
        this.reader = reader;
        this.rowIndex = rowIndex;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE );
    } 
    
    
    public void setRow(LogFileRow row)
    {
        	
    	if( fileDefinition != null )
    	{
	    	for(int i=0; i < fileDefinition.columns.size(); i++)
	        {
	    		ColumnDefinition c = (ColumnDefinition)fileDefinition.columns.get(i);
	        	if( fileDefinition.extendedColumn != null && fileDefinition.extendedColumn.equalsIgnoreCase(c.name) )
	        	{
	        		String msg = row.getColumnAt(i);
	        		MessageParser mparse = new MessageParser(msg, fileDefinition);
	        		
	        		extendedValueLink.setText(mparse.getText());
	        		extendedValueLink.addListener(SWT.Selection, new Listener() {
	        		      public void handleEvent(Event event) 
	        		      {
	        		    	  	IEditorPart part = null;
								//System.out.println("Selection: " + event.text);
								String[] file = event.text.split("\\|");
								if( file.length == 1 )
								{
									part = openFile(file[0], null);
								}
								else if( file.length == 2 )
								{
									part = openFile(file[0], new Integer(file[1]));
								}
								if( part == null )
								{
									MessageDialog.openError(parent.getShell(),"Log Viewer", "File not found, unable to open: " +file[0] );
									event.widget.getDisplay().getActiveShell().dispose();
								}
	        		      }
	        		    });
	                        
	
	        	}
	        	else
	        	{	
	        		labels[i].setText(c.name);	
		            labelValues[i].setText(row.getColumnAt(i));	    	
	        	}
	        }
	    	
    	}
    	else
    	{
    		labels[0].setText("");
			labelValues[0].setText(row.getColumnAt(0));	    	
    	}
    }
    
    
    /**
     * This is used to open a file in the eclipse editor.
     * @param filename
     * @param lineNumber
     * @return
     */
    private IEditorPart openFile(String filename, Integer lineNumber )
    {
    	IEditorDescriptor edDesc = null;
    	IEditorInput input = null;
    	IEditorPart part = null;
    	
    	filename = filename.replace('\\', '/');
    	try
		{
			edDesc = org.eclipse.ui.ide.IDE.getEditorDescriptor(filename);
		} catch (PartInitException pie) {
			edDesc = null;
		}

		File file = new File(filename);
		if (file.exists())
		{
			
			if (edDesc == null)
			{
				MessageDialog.openConfirm(parent.getShell(),"LogViewer", "No Description of this file"); 
				return null; // the user didn't want to open the file, no sense in retrieving it				
			}
			if (!edDesc.isInternal() && 
				!MessageDialog.openConfirm(parent.getShell(),"LogViewer", "There is no default editor installed that can be used to open this file, do you want to open it as a simple text file instead?")) 
			{
				return null; // the user didn't want to open the file, no sense in retrieving it
			}
			
			IFile workspaceFile= getWorkspaceFile(file);
			input = createEditorInput(file);

			if (input != null) 
			{
				try 
				{
					IMarker marker = null;
					if( workspaceFile != null )
					{
						marker = workspaceFile.createMarker(IMarker.LINE_NUMBER);
						if( lineNumber != null )
						{
							marker.setAttribute(IMarker.LINE_NUMBER, lineNumber.intValue());
						}
					}
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
					if (edDesc.isInternal()) 
					{
						//part = page.openEditor(input, edDesc.getId());
						part = org.eclipse.ui.ide.IDE.openEditor(page, input, edDesc.getId());
						
					} else {
						part = org.eclipse.ui.ide.IDE.openEditor(page, input, "org.eclipse.ui.DefaultTextEditor");
					}
					
					if( part != null && marker != null )
					{
						org.eclipse.ui.ide.IDE.gotoMarker(part, marker);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}			
		}
		return part;
    }
    
    private IEditorInput createEditorInput(File file) 
    {		
    	IFile workspaceFile= getWorkspaceFile(file);
		if (workspaceFile != null)
		{
			return new FileEditorInput(workspaceFile);
		}
		return new JavaFileEditorInput(file);
	}
    
    private IFile getWorkspaceFile(File file) 
    {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IPath location= Path.fromOSString(file.getAbsolutePath());
		IFile[] files= workspace.getRoot().findFilesForLocation(location);
		
		if (files == null || files.length == 0)
			return null;
		if (files.length == 1)
		{
			return files[0];
		}
		return null;
	}
	
    
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public Control createDialogArea(Composite parent) 
	{
  
        Composite panel = (Composite)super.createDialogArea(parent);
        panel.setLayout(new GridLayout());
        //panel.setBounds(0, 0, 0, 0);
        panel.getShell().setText("Log Row Data");

        
        GridLayout gridLayout = (GridLayout)panel.getLayout();

        final Composite composite = new Composite(panel, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        composite.setLayout(gridLayout_1);
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 0;
        gridData.heightHint = 0;
        composite.setLayoutData(gridData);

        
        // fieldset
        final Group fieldset_1 = new Group(composite, SWT.NONE | SWT.FILL);
        fieldset_1.setText("Log Details");
        final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData_1.heightHint = 92;
        gridData_1.widthHint = 432;
        gridData_1.verticalSpan = 2;
        fieldset_1.setLayoutData(gridData_1);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        fieldset_1.setLayout(gridLayout_2);        
        //final GridData gridData_5 = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
        //gridData_5.heightHint = 14;
        //gridData_5.widthHint = 60;        
        
        
        if( fileDefinition != null )
        {
	        for(int i=0; i < fileDefinition.columns.size(); i++)
	        {
	        	ColumnDefinition c = (ColumnDefinition)fileDefinition.columns.get(i);
	        	if( fileDefinition.extendedColumn != null && !fileDefinition.extendedColumn.equalsIgnoreCase(c.name) )
	        	{
	        		labels[i] = new Label(fieldset_1, SWT.NONE);
	        		labels[i].setText(c.header);
	            	labelValues[i] = new Label(fieldset_1, SWT.NONE);
	        	}
	        	else
	        	{
	        		labels[i] = new Label(fieldset_1, SWT.NONE);
	        		labels[i].setText(c.header);
	            	labelValues[i] = new Label(fieldset_1, SWT.NONE);
	        	}
	
	        }
        }
        else
        {
        	labels[0] = new Label(fieldset_1, SWT.NONE);
    		labels[0].setText("");
        	labelValues[0] = new Label(fieldset_1, SWT.NONE);
        }
    
        // prev/next buttons.
        final Button button = new Button(composite, SWT.ARROW | SWT.FLAT | SWT.UP );
        final GridData gridData_4 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_END);
        if( reader == null )
        {
            button.setEnabled(false);
        }
        gridData_4.heightHint = 35;
        gridData_4.widthHint = 35;
        button.setLayoutData(gridData_4);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setRow(((LogFileRow)reader.getRows()[(rowIndex > 0? --rowIndex: 0)]));
            }
        });
        button.setText("button");

        final Button button_1 = new Button(composite, SWT.ARROW | SWT.FLAT | SWT.DOWN);
        button_1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) 
            {                
                setRow(((LogFileRow)reader.getRows()[(rowIndex < reader.getRowCount()? ++rowIndex: reader.getRowCount())]));
            }
        });
        final GridData gridData_3 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_END );
        if( reader == null )
        {
            button.setEnabled(false);
        }
        gridData_3.heightHint = 38;
        gridData_3.widthHint = 35;
        button_1.setLayoutData(gridData_3);
        button_1.setText("button");

        
        // properties for extended data.
        if( fileDefinition != null && fileDefinition.extendedColumn != null )
        {
        	//extendedValue = new StyledText(composite, SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.H_SCROLL);
        	//extendedValue.setEnabled(true);
        	extendedValueLink = new Link(composite, SWT.NONE);
        	
        	
        	final GridData gridData_2 = new GridData(GridData.FILL_BOTH);
            gridData_2.heightHint = 200;
            gridData_2.horizontalSpan = 2;
            extendedValueLink.setLayoutData(gridData_2);

        }
      
        
        
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;
        
        gridLayout.numColumns = 1;

        // populate with the dialog with the correct row
        if( reader != null )
        {
            setRow(((LogFileRow)reader.getRows()[rowIndex]));
        }
        else
        {
            setRow( row );
        }
        return panel;

	}
	
	public void createButtonsForButtonBar(Composite parent)
	{
	    //super.createButtonsForButtonBar(parent);
	    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
    protected Point getInitialSize() {
        return new Point(500, 400);
    }
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Log Row Details");
    }
	
}
	
