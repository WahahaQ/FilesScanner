package file_scanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JTextArea;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FileScanner {

	public final String streamFileName = "search-log.txt";

	public String folderPath, keyword;
	public Long runTime, startTime, endTime;
	public JTextArea destination;
	private Path fileDirectory;
	private FileVisitor visitor;

	public String Search(String folderPath, String keyword) throws FileNotFoundException {

		// Folder path check
		if (folderPath == null || folderPath.isEmpty()) {
			throw new IllegalArgumentException();
		}

		// Create log file
		CreateStreamFile();
		PrintStream out = new PrintStream(new FileOutputStream(streamFileName));
		System.setOut(out); // Set standard output stream to log file

		this.folderPath = folderPath;
		this.keyword = keyword;

		try {
			// Start search
			fileDirectory = Paths.get(folderPath);
			visitor = new FileVisitor(keyword);
			startTime = System.currentTimeMillis();
			Files.walkFileTree(fileDirectory, visitor);
		} catch (Exception ex) {
		}
		
		try {
			// Show search results
			if (!keyword.isEmpty() && keyword != null) {
				System.out.println("\n\n___________");
				if (visitor.hit == null || visitor.hit.isEmpty()) {
					System.out.println("No files with \'" + keyword + "\' keyword were found\n");
				} else {
					System.out.println("Files has hits:\n" + visitor.hit);
				}
			}

			// Calculate end time and runtime
			endTime = System.currentTimeMillis();
			runTime = endTime - startTime;

			return "Search time: " + runTime.toString() + " millis";
		} catch (Exception ex) {
			return "exception";
		}

	}

	private boolean CreateStreamFile() {

		File logFile = new File(streamFileName);

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException ex) {
				return false;
			}
		}

		return true;
	}
}
