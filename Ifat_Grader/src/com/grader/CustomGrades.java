package com.grader;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CustomGrades extends Frame1{
	
	private JFrame frame;
	public double try1 = 	1; 
	public double try2 = 	0.66; 
	public double try3 = 	0.33; 
	public double try4 = 	0; 
	public double try5 = 	0; 
	public CustomGrades(){
		
		initialize();
		
}
	private void initialize() {
		
		frame = new JFrame();
		frame.setTitle("Custom Grades");
		frame.setLayout(new BorderLayout(10,10));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 200);
		 JPanel panel = new JPanel(new GridLayout(6, 3, 10, 10));
		
			JLabel label = new JLabel("1st try");
			JLabel label2 = new JLabel("2nd try");
			JLabel label3 = new JLabel("3rd try");
			JLabel label4 = new JLabel("4th try");
			JLabel label5 = new JLabel("5th try");
			

			
		
			
			 JTextField try1F = new JTextField("100");
			 JTextField try2F = new JTextField("66");
			 JTextField try3F = new JTextField("33");
			 JTextField try4F = new JTextField("0");
			 JTextField try5F = new JTextField("0");
			 
	
			JLabel per1 = new JLabel("%");
			JLabel per2 = new JLabel("%");
			JLabel per3 = new JLabel("%");
			JLabel per4 = new JLabel("%");
			JLabel per5 = new JLabel("%");
			
			panel.add(label);
			panel.add(try1F);
			panel.add(per1);
			
			panel.add(label2);
			panel.add(try2F);
			panel.add(per2);
			
			panel.add(label3);
			panel.add(try3F);
			panel.add(per3);
			
			
			panel.add(label4);
			panel.add(try4F);
			panel.add(per4);
			
			panel.add(label5);
			panel.add(try5F);
			panel.add(per5);
			
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
			
				 
				 
				 Button conButton = new Button("Confirm");
		
				 conButton.addActionListener(new ActionListener() {
						
						@Override
					public void actionPerformed(ActionEvent e) {
							
							try1 = Integer.parseInt(try1F.getText())*0.01;
							try2 = Integer.parseInt(try2F.getText())*0.01;
							try3 = Integer.parseInt(try3F.getText())*0.01;
							try4 = Integer.parseInt(try4F.getText())*0.01;
							try5 = Integer.parseInt(try5F.getText())*0.01;
							
							 frame.setVisible(false);
							 
							 System.out.println(try1);
								System.out.println(try2);
								System.out.println(try3);
								System.out.println(try4);
								System.out.println(try5);
						}
				 });
			
			panel.add(conButton);
			frame.add(panel);
		
			
		 
		
	}
	public void show() {
		frame.setVisible(true);
	}

}
