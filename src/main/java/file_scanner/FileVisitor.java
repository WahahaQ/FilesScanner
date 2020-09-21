package file_scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.util.Calendar;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class FileVisitor extends SimpleFileVisitor<Path> {

	public Hits[] searchHit, temp;
	public String hit = "", visited = "";
	public long runTime, startTime, endTime;
	private String keyword;

	public FileVisitor(String keyword) {
		this.keyword = keyword;
		runTime = startTime = endTime = 0;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path directory, IOException ex) throws IOException {
		startTime = System.currentTimeMillis();
		System.out.println("\nJust visited: " + directory);
		visited = visited + "Visited:" + directory + "\n";
		endTime = System.currentTimeMillis();
		runTime = runTime + (endTime - startTime);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes fileAttributes) throws IOException {
		startTime = System.currentTimeMillis();
		
		try {
			Configuration cfg = new Configuration();
			cfg.configure("hibernate.cfg.xml");
			SessionFactory factory = cfg.buildSessionFactory();
			Session session = factory.openSession();
			Transaction transaction = session.beginTransaction();

			// Add path to database
			entities.Path pathEntity = new entities.Path();
			pathEntity.setAbsolutePath(directory.toAbsolutePath().toString());
			pathEntity.setRootDirectory(directory.getRoot().toString());
			session.persist(pathEntity);

			// Add directory to database
			entities.Directory directoryEntity = new entities.Directory();
			directoryEntity.setName(directory.getFileName().toString());
			directoryEntity.setPath(pathEntity);
			directoryEntity.setSize(directory.toFile().length());
			directoryEntity.setModificationDate(new Date(directory.toFile().lastModified()));
			session.persist(directoryEntity);

			// Add directory to visited directory table
			entities.VisitedDirectory visitedDirectoryEntity = new entities.VisitedDirectory();
			visitedDirectoryEntity.setDirectory(directoryEntity);
			visitedDirectoryEntity.setVisitDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
			session.persist(visitedDirectoryEntity);

			transaction.commit();
			session.close();
		} catch (Exception ex) {
		}
		
		System.out.println("About to visit: " + directory);
		visited = visited + "Going inside:" + directory + "\n";
		endTime = System.currentTimeMillis();
		runTime = runTime + (endTime - startTime);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes fileAttributes) throws IOException {
		startTime = System.currentTimeMillis();
		String everything = "", fileName = file.getFileName().toString();

		if (fileName.substring(fileName.length() - 4, fileName.length()).equalsIgnoreCase(".txt")) {
			visited = visited + "Read:" + file + "\n";
			System.out.println(file);
			everything = "";
			File asd = new File(file.toAbsolutePath().toString());
			BufferedReader br = new BufferedReader(new FileReader(asd));
			try {
				StringBuilder sb = new StringBuilder();
				String line = "";
				line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				everything = sb.toString();
			} finally {
				if (everything.contains(keyword) || containsIgnoreCase(keyword, fileName)) {
					Hits newHit = new Hits(file.getFileName(), file);
					int x;
					if (searchHit == null) {
						searchHit = new Hits[1];
						searchHit[0] = newHit;
					} else {
						temp = searchHit;
						searchHit = new Hits[temp.length + 1];
						for (x = 0; x < temp.length; x++) {
							searchHit[x] = temp[x];
						}
						searchHit[x] = newHit;
					}
					System.out.println("Has hit " + file.getFileName());

					hit = hit + file + "\n";
					br.close();

					try {
						Configuration cfg = new Configuration();
						cfg.configure("hibernate.cfg.xml");
						SessionFactory factory = cfg.buildSessionFactory();
						Session session = factory.openSession();
						Transaction transaction = session.beginTransaction();

						// Add path to database
						entities.Path pathEntity = new entities.Path();
						pathEntity.setAbsolutePath(file.toAbsolutePath().toString());
						pathEntity.setRootDirectory(file.getRoot().toString());
						session.persist(pathEntity);

						// Add file to database
						entities.File fileEntity = new entities.File();
						fileEntity.setName(file.getFileName().toString());
						fileEntity.setPath(pathEntity);
						fileEntity.setSize(file.toFile().length());
						fileEntity.setModificationDate(new Date(file.toFile().lastModified()));
						session.persist(fileEntity);

						// Add log to database
						entities.SearchLog searchingLogEntity = new entities.SearchLog();
						searchingLogEntity.setFile(fileEntity);
						searchingLogEntity.setKeyword(keyword);
						searchingLogEntity
								.setSearchingDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
						session.persist(searchingLogEntity);

						transaction.commit();
						session.close();
					} catch (Exception ex) {
					}
				}
			}
		} else if (file.toFile().isDirectory()){

			visited = visited + "Visited:" + file.getFileName() + "\n";
		}

		endTime = System.currentTimeMillis();
		runTime = runTime + (endTime - startTime);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException ex) throws IOException {
		startTime = System.currentTimeMillis();
		System.err.println(ex.getMessage());
		endTime = System.currentTimeMillis();
		runTime = runTime + (endTime - startTime);
		return FileVisitResult.CONTINUE;
	}

	private boolean containsIgnoreCase(String str, String searchStr) {
		if (str == null || searchStr == null)
			return false;

		final int length = searchStr.length();
		if (length == 0)
			return true;

		for (int i = str.length() - length; i >= 0; i--) {
			if (str.regionMatches(true, i, searchStr, 0, length))
				return true;
		}
		return false;
	}
}
