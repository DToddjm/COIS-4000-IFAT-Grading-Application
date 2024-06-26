
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Core;
import static org.opencv.core.Core.inRange;
import org.opencv.core.CvType;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import java.awt.Point;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.Collections;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;

/**
 *
 * @author Davian Todd
 */
public class IFATController {
    private Hashtable<String,Integer> controllerConfig;
    // set default controller settings
    
    public IFATController(){
        // Image detection preprocessing default settings
        controllerConfig.put("roiX", 35);
        controllerConfig.put("roiY", 110);
        controllerConfig.put("width", 250);
        controllerConfig.put("height", 350);
        controllerConfig.put("lower", 115);
        controllerConfig.put("upper", 180);
        // Image detection algorithm default settings
        controllerConfig.put("horThresh", 35);
        controllerConfig.put("verThresh", 21);
        controllerConfig.put("partialThresh",715);
        // Image alignment algorith default settings
        controllerConfig.put("maxFeatures", 500);
        controllerConfig.put("keepPercent", 20);
    }
    public IFATController(Hashtable<String,Integer> settings){
        controllerConfig = settings;
    }

    // method to load all scanned cards from a folder
    public Hashtable<String, StudentIFATCard> loadCards(String folder) throws Exception {
        // create a hashtable to store a list of IFAT cards
        // the key will be the filename (for now) and the value is the IFAT card obj
        Hashtable<String, StudentIFATCard> studentCardTable = new Hashtable<>();
        try {
            // image codecs obj required for generating the Mat obj
            Imgcodecs imgcdx = new Imgcodecs();
            // create a file obj with the folder path
            File path = new File(folder);
            // array of files that stores all of the files in the folder
            File[] allFiles = path.listFiles();
            // array that stores the actual image for display
            BufferedImage[] allImages = new BufferedImage[allFiles.length];
            // loop through all the files in the folder
            for (int i = 0; i < allFiles.length; i++) {
                // store the filepath of the image
                String filepath = allFiles[i].getPath();
                // store the filename of the image
                String filename = allFiles[i].getName();
                // read each image file
                allImages[i] = ImageIO.read(allFiles[i]);
                // read each image file and create a Mat obj
                Mat cardMtx = imgcdx.imread(filepath);
                // create the IFAT card object
                StudentIFATCard card = new StudentIFATCard(filename, filepath, allImages[i], cardMtx);
                // add the card to the hash table
                studentCardTable.put(filename, card);
            }
        } catch (Exception e) {
            // handle exceptions with a popup window here
        }
        return studentCardTable;
    }

    // image detection method to be invoked
    // includes all of the preprocessing needed for the image detection algorithm
    public int[][] detect(StudentIFATCard card) {
        
        int X = controllerConfig.get("roiX");
        int Y = controllerConfig.get("roiY");
        int width = controllerConfig.get("width");
        int height = controllerConfig.get("height");
        int maskLower = controllerConfig.get("lower");
        int maskUpper = controllerConfig.get("upper");
        
        // set the bounds of the rectangle to define the roi
        Rect rect = new Rect(X, Y, width, height);
        // get the roi based on the rectangle
        Mat roi = new Mat(card.getMatObj(), rect);
        // set the roi for the student IFAT card
        card.setROI(roi);
        // create a grayscale Mat obj
        Mat gray = new Mat();
        // convert the roi to grayscale
        Imgproc.cvtColor(roi, gray, Imgproc.COLOR_RGB2GRAY);
        // create upper/lower bound Scalars for the grayscale image
        Scalar low = new Scalar(maskLower);
        Scalar high = new Scalar(maskUpper);
        // Mat obj that stores the grayscale mask
        Mat mask = new Mat();
        // mask the pixels within the scalar range
        inRange(gray, low, high, mask);
        // set the inrange pixels to black
        gray.setTo(new Scalar(0), mask);
        // store the Mat obj as a 2D int matrix
        int[][] cardmtx = matTo2DArray(gray);
        // invoke image detection algorithm
        int[][] cardArray = mapToCard(cardmtx, card.getNumBoxes(), card.getNumQuestions());
        return cardArray;
    }

    // Image detection and card matrix mapping algorithm
    private int[][] mapToCard(int[][] mtx, int boxes, int questions) {
        // initialize card array to the size of the IFAT card questions and boxes
        int[][] cardArray = new int[questions][boxes];

        // get the number of rows/cols from the 2D pixel array
        // the number of rows is equivalent to the height (in pixels) of the ROI
        int numRows = mtx.length;
        // the number of cols is equivalent to the width (in pixels) of the ROI
        int numCols = mtx[boxes].length;

        // set the threshold values
        int horThresh = controllerConfig.get("horThresh"); // width of the pixel density rectangle
        int verThresh = controllerConfig.get("verThresh"); // height of the pixel density rectangle
        int pdrArea = horThresh * verThresh; // maximum area of the pixel density rectangle
        // indicates the min number of pixels before a box is considered partially scratched
        int partialThresh = controllerConfig.get("partialThresh"); 

        // row/col index for top left corner(point) of the pixel density rectangle
        int rIndx;
        int cIndx;

        // stores the pixel value used for comparisons
        int pix;

        // point array that stores the coordinates for the point at the top left corner of the pixel density rectangle
        Point[] boxPnts = new Point[boxes * questions];
        // counter for the boxpoints array
        int k = 0;

        // traverse the 2D pixel array
        for (int i = 0; i < mtx.length; i++) {
            for (int j = 0; j < mtx[j].length; j++) {
                // assign the pixel value to pix
                pix = mtx[i][j];
                // if pix is 0, store the starting (origin) coordinates
                if (pix == 0) {
                    rIndx = i; // top left x coord
                    cIndx = j; // top left y coord

                    int csum = 0; // stores the sum of pixels going down a col based on the vertical threshold
                    int rsum = 0; // stores the sum of pixels going across a row based on the horizontal threshold
                    int area = 0; // stores the area of the masked region of the pixel density rectangle

                    // look ahead (down) by vertical threshold
                    if ((rIndx + verThresh) < mtx.length) { // ensures no index out of bounds while going down
                        for (int cx = rIndx; cx < (rIndx + verThresh); cx++) {
                            // sum each pixel value while keeping the column index static
                            csum += mtx[cx][cIndx];
                        }
                    }
                    // look ahead (across) by horizontal threshold
                    if ((cIndx + horThresh) < (mtx[j].length)) { // ensures no index out of bounds while going across
                        for (int cy = cIndx; cy < (cIndx + horThresh); cy++) {
                            // sum each pixel value while keeping the row index static
                            rsum += mtx[rIndx][cy];
                        }
                    }
                    // Potential box is found based on the pixel density rectangle
                    if ((csum == 0) && (rsum == 0)) {
                        // stores the number of masked pixels within the pixel denstity rectangle
                        int numPix = 0;
                        // traverse the pixel density rectangle
                        if ((rIndx + verThresh) < mtx.length) {
                            for (int cx = rIndx; cx < (rIndx + verThresh); cx++) {
                                if ((cIndx + horThresh) < mtx[j].length) {
                                    for (int cy = cIndx; cy < (cIndx + horThresh); cy++) {
                                        // count the number of masked pixels within the pixel density rectangle
                                        if (mtx[cx][cy] == 0) {
                                            numPix++;
                                        }
                                    }
                                }
                            }
                        }
                        // if the number of masked pixels exceeds the partial threshold (unscratched box detected)
                        if (numPix > partialThresh) {
                            // traverse the pixel density rectangle
                            if ((rIndx + verThresh) < mtx.length) {
                                for (int cx = rIndx; cx < (rIndx + verThresh); cx++) {
                                    if ((cIndx + horThresh) < mtx[j].length) {
                                        for (int cy = cIndx; cy < (cIndx + horThresh); cy++) {
                                            // set each masked pixel to 1
                                            mtx[cx][cy] = 1;
                                            // calculate the area of the pdr by summing the 1's in the pdr
                                            area += mtx[cx][cy];
                                        }
                                        // if the area of the masked region matches the precalculated area of the pdr
                                        if (area == pdrArea) {
                                            // create a new point with the row index as the x coord and col index as the y coord
                                            // store the point in the boxpoints array
                                            boxPnts[k] = new Point(rIndx, cIndx);
                                            k++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } // end of 2D pixel array traversal
        // point mapping process
        // set each value in the card array to 1 (scratched)
        for (int i = 0; i < cardArray.length; i++) {
            for (int j = 0; j < cardArray[j].length; j++) {
                cardArray[i][j] = 1;
            }
        }
        // number of unscratched boxes
        int numUnscratched = 0;
        // pointers that store the indicies for the card array
        int xPtr = 0;
        int yPtr = 0;
        // cardArray mapping algorithm
        for (int i = 0; i < boxPnts.length; i++) {
            // set inital lower x/y bounds
            int lowerX = 0;
            int lowerY = 0;
            // set initial upper x/y bounds
            int upperX = (numRows / questions) * 1;
            int upperY = (numCols / boxes) * 1;

            if (boxPnts[i] != null) {
                // stores whether the x/y indicies have been found
                boolean xfound = false;
                boolean yfound = false;
                int j = 1; //counter variable for the while loop
                // while loop that traverses the rows of the cardArray
                while (!xfound && j < (cardArray.length) + 1) {
                    // if statement that checks if the x coord is within the bounds
                    if (boxPnts[i].x > lowerX && boxPnts[i].x < upperX) {
                        // set the xPtr to the required index
                        xPtr = j - 1;
                        // set xfound to true to end the while loop
                        xfound = true;
                        // if the x coord is not in bounds then raise the upper and lower bounds
                    } else {
                        lowerX = upperX;
                        upperX = (numRows / questions) * (j + 1);
                    }
                    j++;
                }
                j = 1; // reset while loop counter
                // while loop that traverses the cols of the cardArray
                while (!yfound && j < (cardArray[j].length) + 1) {
                    // if statement that checks if the y coord is within the bounds
                    if ((boxPnts[i].y > lowerY) && boxPnts[i].y < upperY) {
                        // set the yPtr to the required index
                        yPtr = j - 1;
                        // set xfound to true to end the while loop
                        yfound = true;
                        // if the y coord is not in bounds then raise the upper and lower bounds
                    } else {
                        lowerY = upperY;
                        upperY = (numCols / boxes) * (j + 1);
                    }
                    j++;
                }
                // set the value at the x/yPtr index to 0 (unscratched) in the cardArray
                cardArray[xPtr][yPtr] = 0;
                // increment the number of unscratched
                numUnscratched++;
            }
        }
        return cardArray;
    }

    // static helper method to convert a Mat obj to a buffered image for display
    public BufferedImage convertToBuff(Mat mtx) throws IOException {
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mtx, matOfByte);

        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();

        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
    }
    
    private static BufferedImage convertTo3ByteBGRType(BufferedImage image) {
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(image, 0, 0, null);
        return convertedImage;
    }

    public Mat convertToMat(BufferedImage img) {
        img = convertTo3ByteBGRType(img);
        byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    // static helper method to convert a Mat obj to a 2D array of integers
    private static int[][] matTo2DArray(Mat mtx) {
        // get rows/cols from mat obj
        int numRows = mtx.rows();
        int numCols = mtx.cols();
        int channels = mtx.channels();
        int[][] intMtx = new int[numRows][numCols];

        // copy mat obj to 2d int array
        intMtx = new int[numRows][numCols];
        byte[] data = new byte[(int) mtx.total() * mtx.channels()];
        mtx.get(0, 0, data);
        int width = mtx.width();
        int height = mtx.height();
        int numpix = (int) mtx.total();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                for (int i = 0; i < channels; i++) {
                    intMtx[r][c] = data[r * (width * channels) + c * channels + i] & 0xff;
                }
            }
        }
        return intMtx;
    }
    public Mat alignImages(Mat img, Mat template){
        // convert input image and template to grayscale
        int maxFeatures = controllerConfig.get("maxFeatures");
        double keepPercent = (double) controllerConfig.get("keepPercent") / 100;
        Mat imgGray = new Mat();
        Mat tempGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(template, tempGray, Imgproc.COLOR_RGB2GRAY);
        
        // use ORB to detect keypoints and extract features
        ORB orb = ORB.create(maxFeatures);
        MatOfKeyPoint kpsA = new MatOfKeyPoint();
        MatOfKeyPoint kpsB = new MatOfKeyPoint();
        Mat descA = new Mat();
        Mat descB = new Mat();
        
        orb.detectAndCompute(imgGray, new Mat(), kpsA, descA);
        orb.detectAndCompute(tempGray, new Mat(), kpsB, descB);
        
        // match the features
        int method = DescriptorMatcher.BRUTEFORCE_HAMMING;
        MatOfDMatch matches = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(method);
        matcher.match(descA, descB, matches);
        
        // sort the matches by distance
        List<DMatch> listMatch = matches.toList();
        Collections.sort(listMatch, new DMatchComparator());
        
        // keep only the top matches
        int keep = (int)(listMatch.size() * keepPercent);
        matches.fromList(listMatch.subList(0, keep));
        List<DMatch> goodMatches = listMatch.subList(0, keep);
        
        // loop over the top matches
        List<org.opencv.core.Point> imgPoints = new ArrayList<>();
        List<org.opencv.core.Point> tempPoints = new ArrayList<>();
        List<KeyPoint> kplA = kpsA.toList();
        List<KeyPoint> kplB = kpsB.toList();
        
        for(int i = 0; i< keep;i++){
            // get the keypoints from the good matches
            imgPoints.add(kplA.get(goodMatches.get(i).queryIdx).pt);
            tempPoints.add(kplB.get(goodMatches.get(i).trainIdx).pt);
        }
        MatOfPoint2f imgMat = new MatOfPoint2f();
        MatOfPoint2f tempMat = new MatOfPoint2f();
        imgMat.fromList(imgPoints);
        tempMat.fromList(tempPoints);
        // compute the homography matrix
        Mat homogMat = Calib3d.estimateAffinePartial2D(imgMat, tempMat);
        Mat alignedImage = new Mat();
        org.opencv.core.Size dsize = template.size();
        Imgproc.warpAffine(img, alignedImage, homogMat, dsize);
        
        return alignedImage;
    }
    class DMatchComparator implements java.util.Comparator<DMatch>{
        public int compare(DMatch a, DMatch b){
            return (int)(a.distance - b.distance);
        }
    }
}
