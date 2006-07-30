/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.cf5tableview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * Label provider for table cell columns
 * @author mnimer
 */
public class TableViewLabelProvider extends LabelProvider implements ITableLabelProvider 
{
	public String getColumnText(Object obj, int index) 
	{
		if( obj instanceof LogFileRow )
		{
		    switch (index)
			{
			    case 0:
			        return ((LogFileRow)obj).getSeverity();
			    case 1:
			        return ((LogFileRow)obj).getThreadid();
			    case 2:
			        return ((LogFileRow)obj).getDate();
		        case 3:
		            return ((LogFileRow)obj).getTime();
		        case 4:
		            return ((LogFileRow)obj).getApplication();
		        case 5:
		            return ((LogFileRow)obj).getMessage();
			}
		}
		return "";
	}
	public Image getColumnImage(Object obj, int index) 
	{
		return null;//getImage(obj);
	}
	public Image getImage(Object obj) 
	{
		return PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
}    
   

