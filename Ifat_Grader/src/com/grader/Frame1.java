package com.grader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import com.ironsoftware.ironpdf.PdfDocument;
import java.awt.image.BufferedImage;

public class Frame1 {
    private JFrame frame;
    protected File file;
    private File imageFolder;
    private volatile boolean conversionInProgress = false;

    public Frame1() {
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

        JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 50));
        panel3.setBackground(Color.LIGHT_GRAY);

        final JLabel label = new JLabel();
        pickFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!conversionInProgress) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.addChoosableFileFilter(new ImageFilter());
                    fileChooser.setAcceptAllFileFilterUsed(false);

                    int option = fileChooser.showOpenDialog(frame);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        file = fileChooser.getSelectedFile();
                        label.setText("File Selected: " + file.getName());

                        // Start a new conversion thread
                        new Thread(() -> convertPDFToImages(file)).start();
                    } else {
                        label.setText("Open command canceled");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Conversion in progress. Please wait.");
                }
            }
        });

        panel3.add(pickFolderButton);
        panel3.add(label);

        frame.add(panel1, BorderLayout.SOUTH);
        frame.add(panel2, BorderLayout.NORTH);
        frame.add(panel3, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);

        // Set up the image folder
        imageFolder = new File("image");
        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }

        // Ensure cleanup when the application is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopConversionThread();
                deleteImagesFolder();
            }
        });
    }

    private void convertPDFToImages(File pdfFile) {
        try {
            conversionInProgress = true;
            PdfDocument pdfDocument = PdfDocument.fromFile(pdfFile.toPath());
            List<BufferedImage> extractedImages = pdfDocument.toBufferedImages();
            int pageIndex = 1;

            for (BufferedImage extractedImage : extractedImages) {
                // Check if the thread is interrupted
                if (Thread.interrupted()) {
                    conversionInProgress = false;
                    return;
                }

                String fileName = Paths.get(imageFolder.getAbsolutePath(), "image" + pageIndex++ + ".png").toString();
                ImageIO.write(extractedImage, "PNG", new File(fileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conversionInProgress = false;
        }
    }

    private class CloseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Ensure the conversion thread is stopped before cleanup
            stopConversionThread();

            // Delete the images folder and exit
            deleteImagesFolder();
            System.exit(0);
        }
    }

    private class ContListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Frame2 frame2 = new Frame2();
            frame.setVisible(false);
            frame2.show();
        }
    }

    private void stopConversionThread() {
        // Interrupt the conversion thread if running
        if (conversionInProgress) {
            Thread.currentThread().getThreadGroup().interrupt();
        }
    }

    private void deleteImagesFolder() {
        if (imageFolder.exists() && imageFolder.isDirectory()) {
            File[] images = imageFolder.listFiles();
            if (images != null) {
                for (File image : images) {
                    image.delete();
                }
            }
            imageFolder.delete();
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
            return extension != null && extension.equals(PDF);
        }

        @Override
        public String getDescription() {
            return "PDF Only";
        }

        String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }
    }
}
