/**
 * @author Mike Nimer (mnimer@gmail.com)
 * Copyright (c) 2005 Mike Nimer
 */
package com.mikenimer.logviewer.treeview;

import java.io.File;

import org.eclipse.core.runtime.IAdaptable;

/**
 * TreeNode for individual log file
 * @author mnimer
 */
public class LogFile implements IAdaptable {
	private String name;
	private File file;
	private LogFolder parent;
	
	public LogFile(String name) {
		this.name = name;
	}
	public LogFile(File file) {
		this.name = file.getName();
		this.file = file;
	}
	public void setName(String s) {
		this.name = s;
	}
	public String getName() {
		return name;
	}
	public File getFile()
	{
	    return this.file;
	}
	public void setParent(LogFolder parent) {
		this.parent = parent;
	}
	public LogFolder getParent() {
		return parent;
	}
	public String toString() {
	    if( file != null )
	    {
	        return file.getAbsoluteFile().toString();
	    }
		return getName();
	}
	public Object getAdapter(Class key) {
		return null;
	}
}