package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

public class FileTask extends Task<Void> {
    private final SimpleStringProperty fileName;
    private File file;

    FileTask(File f) {
        file = f;
        fileName = new SimpleStringProperty(f.getName());
        System.out.println("mam plik: " + f.getName());

        updateMessage("Waiting...");
    }

    public String getFileName() {
        return fileName.get();
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 1);

        updateMessage("Sending...");
        try (Socket socket = new Socket("localhost", 27013);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             BufferedInputStream input = new BufferedInputStream(
                     new FileInputStream(file))) {

            byte[] buffer = new byte[4096];
            int nRead;
            int nSent = 0;

            output.writeUTF(file.getName());

            while (true) {
                nRead = input.read(buffer, 0, buffer.length);
                if (nRead <= 0)
                    break;
                nSent += nRead;
                output.write(buffer, 0, nRead);
                output.flush();
                updateProgress(nSent, file.length());
            }
        }

        updateProgress(1, 1);
        updateMessage("Uploaded.");
        return null;
    }
}
