import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Class for writing IFAT grader CSV output.
 * Contains methods to write:
 * - Result summary CSV 
 * - Detail CSVs (one per student)
 */
public class OutputCSV {

    /**
     * Write result summary CSV file containing high level 
     * student grade details.
     * 
     * @param cards List of all student IFAT card objects
     * @param file Output CSV file  
     */ 
    public static void writeResultCSV(List<StudentIFATCard> cards, File file) throws Exception {
        
        PrintWriter writer = new PrintWriter(file);
        
        // Header
        writer.println("Student No, Name, Course, Grade");
                
        // Row data
        for(StudentIFATCard card : cards) {
            writer.println(
                card.getStudentNo() + "," + 
                card.getStudentName() + "," +
                card.getCourse() + "," +
                card.getGrade()
           );
        }
        
        writer.close();
    }

    /**
     * Write individual detail CSVs for each student IFAT card.
     * One file per student named using student ID and name.
     * 
     * @param cards List of all student IFAT cards 
     * @param outputDir Directory to write CSV files
     */
    public static void writeDetailCSVs(List<StudentIFATCard> cards, File outputDir) throws Exception {
        // Ensure directory exists
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        for (StudentIFATCard card : cards) {
            // Name file using student metadata
            String filename = card.getStudentNo() + "_" + card.getStudentName() + ".csv";
            File detailCSV = new File(outputDir, filename);
            PrintWriter writer = new PrintWriter(detailCSV);

            // Header
            writer.println("Question,Attempts,Score,Image Path");

            // Row data
            for (int q = 0; q < card.getNumQuestions(); ++q) {
                StringBuilder attemptsBuilder = new StringBuilder();
                int[] cardArray = card.getCardArray()[q];
                for (int attempt : cardArray) {
                    attemptsBuilder.append(attempt == 0 ? " U" : " S"); // U for Unscratched, S for Scratched
                }
                String attempts = attemptsBuilder.toString();
                int p = q + 1;
                writer.println(p + "," + attempts + "," + card.getScores()[q] + "," + card.getFilePath());
            }

            writer.close();
        }
    }

}