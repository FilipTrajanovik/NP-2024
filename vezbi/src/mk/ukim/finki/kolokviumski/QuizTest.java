package mk.ukim.finki.kolokviumski;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class InvalidOperationException extends Exception{
    public InvalidOperationException(String message){
        super(message);
    }
}

abstract class Question{
    private String questionText;
    private double questionPoints;
    private String questionAnswer;

    public Question(String questionText, double questionPoints, String questionAnswer) {
        this.questionText = questionText;
        this.questionPoints = questionPoints;
        this.questionAnswer = questionAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public double getQuestionPoints() {
        return questionPoints;
    }

    public void setQuestionPoints(double questionPoints) {
        this.questionPoints = questionPoints;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }
}

class TF extends Question{

    public TF(String questionText, double questionPoints, String questionAnswer) {
        super(questionText, questionPoints, questionAnswer);
    }

    @Override
    public String toString() {
        return String.format("True/False Question: %s Points: %d Answer: %s", getQuestionText(), (int)getQuestionPoints(), getQuestionAnswer());
    }
}

class MC extends Question{

    public MC(String questionText, double questionPoints, String questionAnswer) {
        super(questionText, questionPoints, questionAnswer);
    }

    @Override
    public String toString() {
        return String.format("Multiple Choice Question: %s Points %d Answer: %s", getQuestionText(), (int)getQuestionPoints(), getQuestionAnswer());
    }
}

class Quiz{

    List<Question> questions;

    public Quiz() {
        this.questions = new ArrayList<>();
    }
    public void addQuestion(String questionData) throws InvalidOperationException {
        //MC;Question2;4;E
        //TF;Question3;2;false

        String []parts=questionData.split(";");
        String type=parts[0];
        String questionText=parts[1];
        double questionPoints=Double.parseDouble(parts[2]);
        String questionAnswer=parts[3];
        if(type.equals("MC"))
        {
            char answerToCheck=questionAnswer.charAt(0);
            if(!(answerToCheck>='A' && answerToCheck<='E'))
            {
                throw new InvalidOperationException(String.format("%s is not allowed option for this question", answerToCheck));
            }
            questions.add(new MC(questionText, questionPoints, questionAnswer));
        }else{
            questions.add(new TF(questionText, questionPoints, questionAnswer));
        }

    }
    public void printQuiz(OutputStream os){
        PrintWriter pw=new PrintWriter(os);
        questions.sort(Comparator.comparing(Question::getQuestionPoints).reversed());
        questions.forEach(question -> pw.println(question.toString()));
        pw.flush();
    }
    public void answerQuiz (List<String> answers, OutputStream os) throws InvalidOperationException {
        PrintWriter pw=new PrintWriter(os);
        double totalPoints=0;
        if(answers.size() != questions.size())
        {
            throw new InvalidOperationException("Answers and questions must be of same length!");
        }
        for(int i=0;i<questions.size();i++)
        {
            double currentPoints=0;
            String questionAnswer=questions.get(i).getQuestionAnswer();
            String answer=answers.get(i);
            if(answer.equals(questionAnswer))
            {
                currentPoints=questions.get(i).getQuestionPoints();
            }else{
                if(questions.get(i) instanceof TF)
                {
                    currentPoints=0;
                }else{
                    currentPoints=-(questions.get(i).getQuestionPoints()*0.2);
                }
            }
            totalPoints+=currentPoints;
            pw.println(String.format("%d. %.2f", i+1, currentPoints));
        }
        pw.println(String.format("Total points: %.2f", totalPoints));
        pw.flush();
    }
}

public class QuizTest {
    public static void main(String[] args)  {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i=0;i<questions;i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<String> answers = new ArrayList<>();

        int answersCount =  Integer.parseInt(sc.nextLine());

        for (int i=0;i<answersCount;i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase==1) {
            quiz.printQuiz(System.out);
        } else if (testCase==2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
