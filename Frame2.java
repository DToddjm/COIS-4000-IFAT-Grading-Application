package com.grader;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Frame2 extends Frame1 {

	
	private JFrame frame2;
	public File file = super.file;//array of files in chosen folder
	public int questNum = 	10; //Inputed number of questions 
	public JCheckBox[][] checkBoxes = new JCheckBox[10][10];
	public Frame2(){
		
		initialize();
		
}
	private void initialize() {
		frame2 = new JFrame();
		frame2.setTitle("IF-AT Grader");
		frame2.setLayout(new BorderLayout());
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setSize(800, 500);
		
		

		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		panel2.setBackground(Color.LIGHT_GRAY);
		JLabel label1 = new JLabel("Choose the number of questions: ");
		label1.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
		panel2.add(label1);
		
		JTextField qNum = new JTextField("10");
		panel2.add(qNum);
		JLabel label2 = new JLabel("Choose the correct answers.");
		
		label2.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

	
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		panel1.setBackground(Color.LIGHT_GRAY);
		
		
		
		
		Button conButton = new Button("Confirm");
		
		JPanel panel3 = new JPanel(new GridLayout(questNum, 5, 10, 10));
	
		
		conButton.addActionListener(new ActionListener() {
			
			@Override
		public void actionPerformed(ActionEvent e) {
			
		questNum = Integer.parseInt(qNum.getText());
		 panel3.removeAll();
		 panel3.setLayout((new GridLayout(questNum, 5, 10, 10)));
		
		 checkBoxes = new JCheckBox[questNum][5];
	        for (int i = 0; i < questNum; i++) 
	        {
	        	
	        	for (int j = 0; j < 5; j++) 
		        {
		      
	        		if (j==0) {
	        			checkBoxes[i][j] = new JCheckBox("A");
	        		}
	        		if (j==1) {
	        			checkBoxes[i][j] = new JCheckBox("B");
	        		}
	        		if (j==2) {
	        			checkBoxes[i][j] = new JCheckBox("C");
	        		}
	        		if (j==3) {
	        			checkBoxes[i][j] = new JCheckBox("D");
	        		}
	        		if (j==4) {
	        			checkBoxes[i][j] = new JCheckBox("E");
	        		}
	        		 panel3.add(checkBoxes[i][j]);
	        }
	        }
	       
	        	
		        
		     
	

	       
	        
	    	panel2.add(label2);
	        System.out.println("action performed");
	        
Button nextButton = new Button("Continue");
			
			nextButton.addActionListener(new ActionListener() {
				
				@Override
			public void actionPerformed(ActionEvent e) {
					
					int[][] answers = new int[questNum][5]; 
					
					for (int i =0;i<questNum;i++) {
						for (int j =0;j<5;j++) {
							if (checkBoxes[i][j].isSelected()) {
								answers[i][j]=1;
							}
							else answers[i][j]=0;
						}
					}
		 
				}
				
			});
				
		 panel1.add(nextButton);
	        
	        panel3.revalidate();
	        panel3.repaint();

			}
			
	});
		
		 panel2.add(conButton);
		 
		 
		
			
		 
		 
		frame2.add(panel1, BorderLayout.SOUTH);
		frame2.add(panel2, BorderLayout.NORTH);
        frame2.add(panel3, BorderLayout.CENTER);
		  
			frame2.setLocationRelativeTo(null);

	}
		
	
	
	public void show() {
		frame2.setVisible(true);
	}
	
}