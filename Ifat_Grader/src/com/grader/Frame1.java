package com.grader;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.ironsoftware.ironpdf.*;
import java.awt.image.BufferedImage;


public class Frame1 {
	private JFrame frame;
	public File file;
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
		
		Button pickFolderButton = new Button("Choose PDF");
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
		            fileChooser.addChoosableFileFilter(new ImageFilter());
		            fileChooser.setAcceptAllFileFilterUsed(false);

		            int option = fileChooser.showOpenDialog(frame);
		            if(option == JFileChooser.APPROVE_OPTION){
		                file = fileChooser.getSelectedFile();
		               label.setText("File Selected: " + file.getName());

					   try {
                        convertPDFToImages(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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

	 private void convertPDFToImages(File pdfFile) throws IOException {
        PdfDocument pdfDocument = PdfDocument.fromFile(Paths.get(pdfFile.getAbsolutePath()));
        List<BufferedImage> extractedImages = pdfDocument.toBufferedImages();
        int pageIndex = 1;
        for (BufferedImage extractedImage : extractedImages) {
            String fileName = "image" + pageIndex++ + ".png"; // Change to PNG format as mentioned in the IronPDF example
            ImageIO.write(extractedImage, "PNG", new File(fileName));
        }
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
	
	class ImageFilter extends FileFilter {
		   public final static String PDF = "pdf";

		   @Override
		   public boolean accept(File f) {
		      if (f.isDirectory()) {
		         return true;
		      }

		      String extension = getExtension(f);
		      if (extension != null) {
		         if (extension.equals(PDF)) {
		            return true;
		         } else {
		            return false;
		         }
		      }
		      return false;
		   }
	

		   @Override
		   public String getDescription() {
		      return "PDF Only";
		   }

		   String getExtension(File f) {
		      String ext = null;
		      String s = f.getName();
		      int i = s.lastIndexOf('.');
		   
		      if (i > 0 &&  i < s.length() - 1) {
		         ext = s.substring(i+1).toLowerCase();
		      }
		      return ext;
		   } 
		   
	}
}
