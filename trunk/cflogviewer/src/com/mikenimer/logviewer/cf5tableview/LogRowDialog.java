/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.cf5tableview;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import com.mikenimer.logviewer.utils.MessageParser;


/**
 * 
 * @author mnimer
 */
public class LogRowDialog extends Dialog
{
    //private final static String[] btnLabels = {"Ok"};
    private StyledText styledText;
    private Composite parent;
    private LogFileRow row;
    private LogFileReader reader = null;
    private int rowIndex = 0;
    
    Label appValue;
    Label severityValue;
    Label dateValue;
    Label timeValue;
    StyledText messageValue;
   
    public LogRowDialog(Shell parentShell, LogFileRow obj)
    {
        //super(parentShell, "Information", null, obj.getMessage(), MessageDialog.INFORMATION, btnLabels, 0);
        super(parentShell);
        this.parent = parentShell;
        this.row = obj;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
    } 
    public LogRowDialog(Shell parentShell, LogFileReader reader, int rowIndex)
    {
        //super(parentShell, "Information", null, obj.getMessage(), MessageDialog.INFORMATION, btnLabels, 0);
        super(parentShell);
        this.parent = parentShell;
        this.reader = reader;
        this.rowIndex = rowIndex;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
    } 
    
    public void setRow(LogFileRow row)
    {
        appValue.setText(row.getApplication());
        severityValue.setText(row.getSeverity());
        dateValue.setText(row.getDate());
        timeValue.setText(row.getTime());
        
        String msg = row.getMessage();
        messageValue.setText(msg);
        
        MessageParser mparse = new MessageParser(msg);
        Vector urls = mparse.getUrls();
        
        for(int i=0; i < urls.size(); i++)
        {
            HashMap map = ((HashMap)urls.get(i));
            StyleRange range = new StyleRange();
            range.start = ((Integer)map.get("start")).intValue();
            range.length = ((Integer)map.get("end")).intValue();
            range.fontStyle = SWT.BOLD;
            messageValue.setStyleRange(range);
        }
        
    }
    
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public Control createDialogArea(Composite parent) {
  
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

        final Label applbl = new Label(fieldset_1, SWT.NONE);
        final GridData gridData_5 = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
        gridData_5.heightHint = 14;
        gridData_5.widthHint = 60;
        applbl.setLayoutData(gridData_5);
        applbl.setText("Application");

        appValue = new Label(fieldset_1, SWT.NONE);
    	
        
    	final GridData gridData_6 = new GridData(GridData.FILL_HORIZONTAL);
        appValue.setLayoutData(gridData_6);

        final Label sevlbl = new Label(fieldset_1, SWT.NONE);
        sevlbl.setText("Severity");

        severityValue = new Label(fieldset_1, SWT.NONE);
        
        
        final Label label_2 = new Label(fieldset_1, SWT.NONE);
        label_2.setText("Date");

        dateValue = new Label(fieldset_1, SWT.NONE);
        
        final Label label_3 = new Label(fieldset_1, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("Time");

        timeValue = new Label(fieldset_1, SWT.NONE);
        

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

        messageValue = new StyledText(composite, SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.H_SCROLL);
        messageValue.setEnabled(true);
        
        
        final GridData gridData_2 = new GridData(GridData.FILL_BOTH);
        gridData_2.heightHint = 200;
        gridData_2.horizontalSpan = 2;
        messageValue.setLayoutData(gridData_2);
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
	
