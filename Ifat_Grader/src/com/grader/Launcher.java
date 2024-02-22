package com.grader;

import javax.swing.SwingUtilities;

public class Launcher {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run(){
				
				Frame1 main = new Frame1();
				main.show();
				
		}
		});
	}

}
