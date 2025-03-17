package Src;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ResumeParser {


    public static String parseResume(File file) throws IOException, TikaException, TikaException {
        Tika tika = new Tika();
        try (FileInputStream fis = new FileInputStream(file)) {
            return tika.parseToString(fis);
        }
    }
}

