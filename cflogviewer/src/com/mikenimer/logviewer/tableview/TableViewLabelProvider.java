package com.mikenimer.logviewer.tableview;

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
			return ((LogFileRow)obj).getColumnAt(index);
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
   

