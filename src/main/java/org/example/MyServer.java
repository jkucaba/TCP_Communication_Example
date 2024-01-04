package org.example;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyServer {
    private static final int PORT = 1234;
    private static final int MAX_STUDENTS = 250;
    private static final int TIMEOUT = 9000; // 30 sekund

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Serwer uruchomiony. Oczekiwanie na klientow...");

            int liczbaStudentow = 0;

            while (liczbaStudentow < MAX_STUDENTS) {
                Socket clientSocket = serverSocket.accept();

                liczbaStudentow++;
                System.out.println("Nowy klient polaczony. Numer studenta: " + liczbaStudentow);

                // Utwórz nowy wątek obsługujący klienta
                Thread clientThread = new Thread(new ObslugaKlienta(clientSocket, liczbaStudentow));
                clientThread.start();
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ObslugaKlienta implements Runnable {
        private Socket clientSocket;
        private int numerStudenta;

        public ObslugaKlienta(Socket clientSocket, int numerStudenta) {
            this.clientSocket = clientSocket;
            this.numerStudenta = numerStudenta;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Wczytaj pytania z pliku bazaPytan.txt
                BufferedReader questionsReader = new BufferedReader(new FileReader("bazaPytan.txt"));
                String pytanie;
                int numerPytania = 1;
                ArrayList<String> listaOdpowiedzi = new ArrayList<>();
                // Wysyłaj pytania do klienta i odbieraj odpowiedzi
                for(int i=0; i<3; i++) {
                    pytanie = questionsReader.readLine();
                    writer.println("Pytanie " + numerPytania + ": " + pytanie);
                    String odpowiedz = reader.readLine();
                    listaOdpowiedzi.add(odpowiedz);
                    numerPytania++;
                }
                zapiszOdpowiedzi(numerStudenta, listaOdpowiedzi);
                questionsReader.close();

                // Oblicz wynik i wyślij do klienta
                int wynik = obliczWynik(numerStudenta, listaOdpowiedzi);
                writer.println("Twoj wynik: " + wynik);

                reader.close();
                writer.close();
                clientSocket.close();
                System.out.println("Klient " + numerStudenta + " rozlaczony.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void zapiszOdpowiedzi(int numerStudenta, ArrayList<String> odpowiedzi) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("bazaOdpowiedzi.txt", true));
                writer.write("Student " + numerStudenta + ", : " + odpowiedzi);
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int obliczWynik(int numerStudenta, ArrayList<String> odpowiedzi) {
            try {
                BufferedReader poprawneOdpowiedziReader = new BufferedReader(new FileReader("bazaOdpowiedziDoTestu.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("wyniki.txt", true));

                String poprawnaOdpowiedz;
                int wynik = 0;
                int numerPytania = 0;

                while ((poprawnaOdpowiedz = poprawneOdpowiedziReader.readLine()) != null) {
                    String[] czesciPoprawnejOdpowiedzi = poprawnaOdpowiedz.split(":");

                    if (czesciPoprawnejOdpowiedzi.length == 2) {
                        String odpowiedzStudenta = odpowiedzi.get(numerPytania);
                        String poprawnaOdpowiedzTrim = czesciPoprawnejOdpowiedzi[1].trim();

                        if (odpowiedzStudenta.equalsIgnoreCase(poprawnaOdpowiedzTrim)) {
                            wynik++;
                        }
                    }

                    numerPytania++;
                }

                writer.write("Student " + numerStudenta + " wynik: " + wynik);
                writer.newLine();
                writer.close();

                poprawneOdpowiedziReader.close();

                return wynik;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}