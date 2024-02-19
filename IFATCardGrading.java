
package ifatcardgrading;

/**
 *
 * @author YahMa
 */
public class IFATCardGrading {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        StudentIFATCard test = new StudentIFATCard();
        int[] answerKey = new int[test.getNumQuestions()];
        answerKey[0] = 4;
        answerKey[1] = 0;
        answerKey[2] = 1;
        answerKey[3] = 4;
        answerKey[4] = 3;
        answerKey[5] = 2;
        answerKey[6] = 2;
        answerKey[7] = 0;
        answerKey[8] = 3;
        answerKey[9] = 0;
        
        int[][] mtx = new int[test.getNumQuestions()][test.getNumBoxes()];
        for (int i = 0; i < mtx.length; i++) {
            for (int j = 0; j < mtx[j].length; j++) {
                mtx[i][j] = 0;
            }
        }
        mtx[0][4] = 1;
        mtx[1][0] = 1;
        mtx[2][1] = 1;
        mtx[3][4] = 1;
        mtx[4][3] = 1;
        mtx[4][4] = 1;
        mtx[5][2] = 1;
        mtx[6][2] = 1;
        mtx[7][0] = 1;
        mtx[8][2] = 1;
        mtx[8][3] = 1;
        mtx[9][0] = 1;
        
        test.setCardArray(mtx);
        test.printArray(test.getCardArray());
        test.calcScore(answerKey);
        System.out.println(test.getGrade());
        
    }
    
}
