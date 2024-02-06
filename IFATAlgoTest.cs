/* This is the IFAT grading algorithm test that I created a couple weeks ago
 * I havent added Sergei's suggestion for adding in the answer sheet yet
 * Also this class is not object oriented it just uses static methods for testing puposes
 * This is what I will be using to create the actual grading algorithm
 * this code doesnt use the image detection library so you can run it
 */
public class IFATGrade
{
    public static void Main(string[] args)
    {
        int rows; // number of questions on the IFAT card/assesment
        int cols; // number of boxes per question on the IFAT card/assessment
        int marks; // the amount of marks each question is worth
        

        rows = 10; cols = 4;
        marks = 4;
       
        int[,] cardArray = new int[rows, cols]; // this is the 2D array (like a table) that stores the representation of the card

        // initialize the 2D array w/ 0s (grey/unscratched boxes)
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                cardArray[i, j] = 0;

        // populate card with 1s (scratched boxes)
        cardArray[0, 1] = 1;
        cardArray[1, 0] = 1;
        cardArray[1, 3] = 1;
        cardArray[2, 2] = 1;
        cardArray[3, 0] = 1;
        cardArray[4, 1] = 1;
        cardArray[4, 2] = 1;
        cardArray[4, 3] = 1;
        cardArray[5, 3] = 1;
        cardArray[6, 0] = 1;
        cardArray[6, 1] = 1;
        cardArray[6, 2] = 1;
        cardArray[6, 3] = 1;
        cardArray[7, 3] = 1;
        cardArray[8, 2] = 1;
        cardArray[9, 1] = 1;

        
        int[] scores = calcScore(cardArray, marks); // this is the method that calculates the score for each question
        
        PrintCard(cardArray, marks); // print the card

        int[,] card1 = GenerateCard(10, 4); // this generates a random card and populates it randomly with 1's and 0's

        PrintCard(card1 , marks); // print the random card
    }
    // scoring algorithm
    // this algorithm takes the 2D card array as input as well as the marks and returns an array with the score of each question

    public static int[] calcScore(int[,] card, int marks)
    {
        int score = 0; // stores the score for a question
        int sum; // sum of all the scratched boxes (1's) in one row of the 2D array
        int[] scores = new int[card.GetLength(0)]; // create an array to store the scores of each question
        // for loop to calculate the score for each question
        for (int i = 0; i < card.GetLength(0); i++) // loops through the outer array (rows/questions)
        {
            sum = 0; // set the sum to 0 each time for each row
            for (int j = 0; j < card.GetLength(1); j++) // loops through the inner array (cols/boxes)
            {
                sum = sum + card[i, j]; // take the sum of all 1's and 0's for each row
            }
            if (sum == 0) // if the sum is 0 (they didnt scratch any boxes for that question)
            {
                score = 0; // then the score for that question is 0
            }
            else if (sum == 1) // if the sum is 1 (they only scratched the correct box)
            {
                score = marks; // then they get full marks for the question
            }
            else // if they scratched more than one box
            {
                score = (marks - sum); // then subtract the sum of each row to find the total amount of marks they should get
            }
            scores[i] = score; // add the score to the scores array
        }
        return scores; // return the scores array
    }
    // printing algorithm
    // also calculates tht final grade of the assessment
    public static void PrintCard(int[,] card, int marks)
    {
        int totalScore = 0; // stores the total score of the assessment
        int[] scores = calcScore(card, marks); // array for the score of each question
        foreach (int score in scores) // loop to calculate the total score
            totalScore += score;
        int totalMarks = card.GetLength(0) * card.GetLength(1); // calculate the total marks for the assessment
        float grade = ((float)totalScore / (float)totalMarks) * 100; // calculate the final grade for the assessment
        int numScratched = 0; // stores the number of scratched boxes
        // for loop to print output
        for (int i = 0;i < card.GetLength(0);i++)
        {
            Console.Write("Question " + (i + 1) + " : "); // print each question with the question number

            for (int j = 0;j < card.GetLength(1);j++) // for loop to calculate the the number of scratched boxes
            {
                if (card[i,j] == 1)
                    numScratched++;
                Console.Write(string.Format("{0} ", card[i, j])); // print a 0 or 1 for each box
            }
            Console.Write("Score " + scores[i] + " Percentage " + ((float)scores[i] / 4) * 100 + "%"); // calculate the score and print the percentage for each question
            Console.Write("\n" + "\n");
        }
        Console.WriteLine("Total Score " + totalScore + "/" + totalMarks); // print the total score
        Console.WriteLine("Assessment grade " + grade + "%"); // print the final grade
        // print the number of scratched/unscratched boxes
        Console.WriteLine("Total scratched: " + numScratched + " Total not scratched: " + (totalMarks - numScratched));
    }
    // this method generates a random card randomly populates it with 1's and 0's
    public static int[,] GenerateCard(int questions, int boxes)
    {
        Random rnd = new Random();
        int[,] card = new int[questions, boxes];
        for (int i = 0; i < questions; i++)
            for (int j = 0; j < boxes; j++)
            {
                if (rnd.NextDouble() < 0.75)
                    card[i, j] = 0;
                else 
                    card[i, j] = 1;

            }
        return card;
    }

}