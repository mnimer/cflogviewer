package com.mikenimer.logviewer.utils;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class LineNumberMarker implements IMarker 
{
	private Integer linenumber;
	
	public void delete() throws CoreException {
		// TODO Auto-generated method stub

	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getAttribute(String attributeName) throws CoreException {
		// TODO Auto-generated method stub
		if( attributeName.equals(IMarker.LINE_NUMBER) )
		{
			return linenumber;
		}
		return null;
	}

	public int getAttribute(String attributeName, int defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAttribute(String attributeName, String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) {
		// TODO Auto-generated method stub
		return false;
	}

	public Map getAttributes() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getAttributes(String[] attributeNames) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getCreationTime() throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSubtypeOf(String superType) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAttribute(String attributeName, int value)
			throws CoreException {
		if( attributeName.equals(IMarker.LINE_NUMBER) )
		{
			linenumber = new Integer(value);
		}

	}

	public void setAttribute(String attributeName, Object value)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	public void setAttribute(String attributeName, boolean value)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	public void setAttributes(String[] attributeNames, Object[] values)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	public void setAttributes(Map attributes) throws CoreException {
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

}
