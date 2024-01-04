package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class QuestionThread implements Runnable {
    private PrintWriter writer;
    private BufferedReader reader;
    private String question;
    private ArrayList<String> answerList;
    private int questionNumber;

    public QuestionThread(PrintWriter writer, BufferedReader reader, String question, ArrayList<String> answerList, int questionNumber) {
        this.writer = writer;
        this.reader = reader;
        this.question = question;
        this.answerList = answerList;
        this.questionNumber = questionNumber;
    }

    @Override
    public void run() {
        try {
            String answer = reader.readLine();
            answerList.add(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}