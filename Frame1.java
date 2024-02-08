package com.grader;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class Frame1 {
	private JFrame frame;
	public File[] allFiles;
	public Frame1(){
		initialize();
		
	}
	
	private void initialize() {
		
		
		frame = new JFrame();
		frame.setTitle("IF-AT Grader");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
	
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 5));
		
		panel1.setBackground(Color.LIGHT_GRAY);
		
		Button exitButton = new Button("Exit");
		
		exitButton.addActionListener(new CloseListener());
		
		Button contButton = new Button("Continue");
		
		contButton.addActionListener(new ContListener());
		

		panel1.add(exitButton);
		panel1.add(contButton);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		panel2.setBackground(Color.LIGHT_GRAY);
		JLabel welcLabel = new JLabel("Welcome to IF-AT Grader!");
		welcLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		
		Button pickFolderButton = new Button("Choose Folder");
		panel2.add(welcLabel);
		//panel2.add(pickFolderButton);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 50));
		panel3.setBackground(Color.LIGHT_GRAY);
		
		  final JLabel label = new JLabel();
		pickFolderButton.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	            JFileChooser fileChooser = new JFileChooser();
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int option = fileChooser.showOpenDialog(frame);
	            if(option == JFileChooser.APPROVE_OPTION){
	               File folder = fileChooser.getSelectedFile();
	                allFiles = folder.listFiles();
	               if (allFiles == null || allFiles.length == 0) {
	            	    throw new RuntimeException("No files present in the directory: " + folder.getAbsolutePath());
	            	  }
	               label.setText("Folder Selected: " + folder.getName());
	            }else{
	               label.setText("Open command canceled");
	            }
	         }
	      });
		
		
		panel3.add(pickFolderButton);
		panel3.add(label);
		
		frame.add(panel1, BorderLayout.SOUTH);
		frame.add(panel2, BorderLayout.NORTH);
		frame.add(panel3, BorderLayout.CENTER);

		frame.setLocationRelativeTo(null);
		
	}
	
	private class CloseListener implements ActionListener{
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        //DO SOMETHING
	        System.exit(0);
	    }
	}
	
	private class ContListener implements ActionListener{
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        //DO SOMETHING
	        Frame2 frame2 = new Frame2();
	        frame.setVisible(false);
	        frame2.show();
	    }
	}
	
	
	public void show() {
		frame.setVisible(true);
	}
}
