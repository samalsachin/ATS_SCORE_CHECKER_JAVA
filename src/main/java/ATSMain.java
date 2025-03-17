import Src.DatabaseConnector;
import Src.ResumeHandler;
import org.apache.tika.exception.TikaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ATSMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATSMain::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("ATS Resume Checker");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(30, 40, 60)); // Dark theme
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ATS Resume Checker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JLabel jobIdLabel = new JLabel("Enter Job ID:");
        jobIdLabel.setForeground(Color.WHITE);
        JTextField jobIdField = new JTextField(15);

        JButton fetchJobTitleButton = new JButton("Fetch Job Title");
        fetchJobTitleButton.setBackground(new Color(70, 130, 180));
        fetchJobTitleButton.setForeground(Color.WHITE);

        JLabel jobTitleLabel = new JLabel("Job Title: Not Selected");
        jobTitleLabel.setForeground(Color.WHITE);

        JButton uploadButton = new JButton("Upload Resume");
        uploadButton.setBackground(new Color(34, 177, 76));
        uploadButton.setForeground(Color.WHITE);

        JLabel previewLabel = new JLabel();
        previewLabel.setPreferredSize(new Dimension(100, 100));
        previewLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel scoreLabel = new JLabel("ATS Score: Not Available");
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(jobIdLabel, gbc);

        gbc.gridx = 1;
        frame.add(jobIdField, gbc);

        gbc.gridx = 2;
        frame.add(fetchJobTitleButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        frame.add(jobTitleLabel, gbc);

        gbc.gridy = 3;
        frame.add(uploadButton, gbc);

        gbc.gridy = 4;
        frame.add(previewLabel, gbc);

        gbc.gridy = 5;
        frame.add(scoreLabel, gbc);

        // Fetch Job Title
        fetchJobTitleButton.addActionListener(e -> {
            String jobIdText = jobIdField.getText();
            try {
                int jobId = Integer.parseInt(jobIdText);
                String jobTitle = getJobTitleById(jobId);
                if (jobTitle != null) {
                    jobTitleLabel.setText("Job Title: " + jobTitle);
                } else {
                    jobTitleLabel.setText("Job Title: Not Found!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Job ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Upload Resume
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon("resume-icon.png"); // Use a resume icon
                previewLabel.setIcon(icon);
                processResume(selectedFile, jobIdField.getText(), scoreLabel);
            }
        });

        frame.setVisible(true);
    }

    private static void processResume(File resumeFile, String jobIdText, JLabel scoreLabel) {
        try {
            int jobId = Integer.parseInt(jobIdText);

            SwingWorker<Double, Void> worker = new SwingWorker<>() {
                @Override
                protected Double doInBackground() throws Exception {
                    return ResumeHandler.processResume(resumeFile, jobId);
                }

                @Override
                protected void done() {
                    try {
                        double score = Math.round(get());
                        if (score > 0) {
                            scoreLabel.setText("ATS Score: " + score + "%");  // Display on label
                        } else {
                            scoreLabel.setText("ATS Score: Not Available");
                        }
                    } catch (Exception ex) {
                        scoreLabel.setText("Error processing resume.");
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException ex) {
            scoreLabel.setText("Invalid Job ID!");
        }
    }

    private static String getJobTitleById(int jobId) {
        String query = "SELECT job_title FROM job_descriptions WHERE id=?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("job_title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}