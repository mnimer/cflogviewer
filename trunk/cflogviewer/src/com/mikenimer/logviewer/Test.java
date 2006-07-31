package com.mikenimer.logviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Test extends Shell {

	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Test shell = new Test(display, SWT.SHELL_TRIM);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Test(Display display, int style) {
		super(display, style);
		createContents();
	}

	protected void createContents() {
		setText("SWT Application");
		setSize(500, 375);

		final SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setBounds(5, 3, 485, 343);

		final Button button = new Button(sashForm, SWT.PUSH);
		button.setText("button");
		button.setBounds(105, 110, 40, 25);

		final Button button_1 = new Button(sashForm, SWT.PUSH);
		button_1.setText("button");
		button_1.setBounds(95, 175, 15, 5);


	}

	protected void checkSubclass() {
	}

}
