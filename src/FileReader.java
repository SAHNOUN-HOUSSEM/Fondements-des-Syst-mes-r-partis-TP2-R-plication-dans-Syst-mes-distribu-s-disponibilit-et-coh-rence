import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
public class FileReader {
    public String readLastLine(String fileName) {
        String filePath = "D:\\HOUSSEM\\2023-2024\\semestre_2\\systemes repartis\\tp3\\tp3_replication_de_donnees\\src\\fichiers\\" + fileName;
        String lastLine = "";
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastLine;
    }
}
