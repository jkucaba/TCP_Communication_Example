package org.example;

import java.io.*;
import java.net.*;

public class TestClient {
    private static final String ADRES_SERWERA = "127.0.0.1";
    private static final int PORT = 1234;
    private static final int TIMEOUT = 9000; // 30 sekund

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(ADRES_SERWERA, PORT);
            socket.setSoTimeout(TIMEOUT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String pytanie;
            int numerPytania = 1;

            // Odbieraj pytania i wysyłaj odpowiedzi
            for(int i=0; i<3; i++) {
                pytanie = reader.readLine();
                System.out.println(pytanie);
                String odpowiedz="";
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < TIMEOUT) {
                    if (userInputReader.ready()) {
                        odpowiedz = userInputReader.readLine();
                        break;
                    }
                }
                if (odpowiedz.equals("")) {
                    System.out.println("Czas sie skonczyl. Nastepne pytanie...");
                }
                writer.println(odpowiedz);
                numerPytania++;
            }

            // Odbierz wynik
            String wynik = reader.readLine();
            System.out.println(wynik);

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}