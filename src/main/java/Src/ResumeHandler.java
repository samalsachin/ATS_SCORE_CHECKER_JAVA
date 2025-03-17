package Src;

import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResumeHandler {
    public static double processResume(File resumeFile, int jobId) throws IOException, SQLException, TikaException, TikaException {
        String extractedText = ResumeParser.parseResume(resumeFile);

        try (Connection conn = DatabaseConnector.connect()) {
            // Fetch Job Description from DB
            String query = "SELECT description FROM job_descriptions WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, jobId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String jobDesc = rs.getString("description");
                        return ScoreCalculator.calculateScore(extractedText, jobDesc);
                    }
                }
            }
        }
        return 0;
    }
}


