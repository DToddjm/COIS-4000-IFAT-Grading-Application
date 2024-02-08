package com.grader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Frame2 extends Frame1 {

	
	private JFrame frame2;
	public File[] allFiles = super.allFiles;
	public Frame2(){
		
		initialize();
		
}
	private void initialize() {
		frame2 = new JFrame();
		frame2.setTitle("IF-AT Grader");
		frame2.setLayout(new BorderLayout());
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setSize(800, 500);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		
		panel1.setBackground(Color.LIGHT_GRAY);
		
		JLabel welcLabel = new JLabel("Test Settings");
		welcLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		
		panel1.add(welcLabel);
		frame2.add(panel1, BorderLayout.NORTH);
	}
	
	public void show() {
		frame2.setVisible(true);
	}
	
}