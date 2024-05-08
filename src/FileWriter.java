import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class FileWriter {
    public void writeToFile(String data, String fileName) throws Exception {
        String filePath = "D:\\HOUSSEM\\2023-2024\\semestre_2\\systemes repartis\\tp3\\tp3_replication_de_donnees\\src\\fichiers\\" + fileName;
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(filePath), true);
            os.write(data.getBytes(), 0, data.length());
            os.write("\n".getBytes(), 0, "\n".length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
