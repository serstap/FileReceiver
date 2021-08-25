package ru.avalon.j130.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    /**
     * Стандартный порт, на котором принимается файл.
     */
    public static final int SERVER_PORT = 34567;
    /**
     * Стандартный размер буфера.
     */
    public static final int BUFFER_SIZE = 4096;
    /**
     * Стандартное расширение, которое добавляется к имени принятого файла.
     */
    private static final String SUFFIX = "-copy";

    /**
     * Стартовый метод приложения.
     * @param args массив аргументов командной строки.
     */
    public static void main(String[] args) {
        System.out.println("File receiver started...");
        new Main().run();
        System.out.println("File receiver finished.");
    }

    /**
     * Метод обеспечивает установку соединения с клиентом и принятие файла.
     */
    private void run() {
        try (ServerSocket ss = new ServerSocket(SERVER_PORT);
             Socket s = ss.accept();
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream()) {
            byte[] buf = new byte[BUFFER_SIZE];
            // Имя файла приходит в виде сериализованной строки
            int n = in.read(buf);
            File file = createFile(new String(buf, 0, n));
            try (FileOutputStream fos = new FileOutputStream(file)) {
                while (true) {
                    n = in.read(buf);
                    // В конце файла/потока метод read() возвращает -1 (EOF).
                    if (n < 0) {
                        break;
                    }
                    fos.write(buf, 0, n);
                }
            }
            out.write("Transfer file finished.".getBytes());
        } catch (IOException e) {
            System.err.println("Error #1: " + e.getMessage());
        }
    }

    /**
     * Метод создаёт файл-копию присланного файла.
     *
     * @param fileName имя файла
     * @return ссылка на созданный файл-копию.
     * @throws IOException выбрасывается в случае общей ошибки ввода/вывода.
     */
    private File createFile(String fileName) throws IOException {
        fileName = fileName.trim();
        if (fileName.isEmpty()) {
            fileName = "default_name.txt";
        }
        else {
            //test.txt -> test-copy.txt
            String[] arr = fileName.split("[.]");
            fileName = arr[0] + SUFFIX + "." + arr[1];
        }
        return new File(fileName);
    }
}
