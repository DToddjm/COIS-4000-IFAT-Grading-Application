
import java.util.stream.*;
public class Grading {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// test variables (will plug variables from user/image detection in final program) 
		
		int[][] corAns = { {1, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 1, 0, 0}};//will get from user
		int[][] sdtAns = { {1, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}};//will get from image detection
		
		int qNum = 3;//from user
		
		double try1 = 1;//question grading from user
		double try2 = 0.66;
		double try3 = 0.33;
		double try4 = 0;
		double try5 = 0;
		
		//actual grading algorithm starts here
		double qGrade[] = new double[qNum];
		
		for(int i = 0; i<qNum; i++) {
			
			int errNum = 0;//number of wrong scratches
			
			for(int j = 0; j<5; j++) {
				
				if (sdtAns[i][j]!=corAns[i][j]) {
					errNum++;
				}
				
			}
			if (errNum == 0) { //applying grading from user to each question
				qGrade[i] = try1;
			}
			if (errNum == 1) {
				qGrade[i] = try2;
			}
			if (errNum == 2) {
				qGrade[i] = try3;
			}
			if (errNum == 3) {
				qGrade[i] = try4;
			}
			if (errNum == 4) {
				qGrade[i] = try5;
			}
		}
		
		double totalGrade = ((DoubleStream.of(qGrade).sum())/qNum) * 100;//the total grade
		
		
		//testing
		System.out.println("The total grade I'm supposed to get: "+Math.round((1.66/3)*100)+"%");
		
		System.out.println("The total grade I get (hope it works): "+Math.round(totalGrade)+"%");
		
		
		
	}

}
