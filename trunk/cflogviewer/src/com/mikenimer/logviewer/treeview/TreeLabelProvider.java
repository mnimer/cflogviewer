/*
 * Created on Feb 9, 2005
 * Copyright 2005, Macromedia Inc.
 */
package com.mikenimer.logviewer.treeview;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;



/**
 * 
 * @author mnimer
 */
public class TreeLabelProvider extends LabelProvider
{

    public String getText(Object obj)
    {
        if( obj instanceof LogFile )
        {
            return ((LogFile)obj).getName();
        }
        return obj.toString();
    }

    public Image getImage(Object obj)
    {
        // file icon
        String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
        if ( obj instanceof LogFolder )
        {
            // folder icon
            imageKey = ISharedImages.IMG_OBJ_FOLDER;
        }
        
        return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }

}