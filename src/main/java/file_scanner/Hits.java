package file_scanner;

import java.nio.file.Path;

public class Hits {	
	
	public Path fileName, filePath;

	public Hits(Path fileName, Path filePath) {
		this.fileName = fileName;
		this.filePath = filePath;
	}
}
