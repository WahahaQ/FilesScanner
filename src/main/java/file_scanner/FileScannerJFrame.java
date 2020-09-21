package file_scanner;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import file_scanner.FileScanner;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;

public class FileScannerJFrame extends JFrame {

	private static FileScanner fileScanner;
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField pathTextField;
	private JTextField keywordTextFiled;
	
	public static void main(String[] args) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(bos));
		fileScanner = new FileScanner();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileScannerJFrame frame = new FileScannerJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FileScannerJFrame() {

		// Frame settings
		setResizable(false);
		setTitle("File Scanner");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 490, 310);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// Create layered pane
		JLayeredPane layeredPane = new JLayeredPane();
		contentPane.add(layeredPane, BorderLayout.CENTER);

		// Create path label
		JLabel pathLabel = new JLabel("Folder Path:");
		pathLabel.setBounds(10, 11, 73, 14);
		layeredPane.add(pathLabel);
		pathTextField = new JTextField();
		pathTextField.setBounds(95, 8, 255, 20);
		layeredPane.add(pathTextField);
		pathTextField.setColumns(10);

		// Create keyword label
		JLabel keywordLabel = new JLabel("Keyword:");
		keywordLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		keywordLabel.setBounds(10, 42, 73, 14);
		layeredPane.add(keywordLabel);
		keywordTextFiled = new JTextField();
		keywordTextFiled.setColumns(10);
		keywordTextFiled.setBounds(73, 39, 240, 20);
		layeredPane.add(keywordTextFiled);

		// Create run time label
		final JLabel runTimeLabel = new JLabel("Search time:");
		runTimeLabel.setBounds(10, 234, 454, 26);
		layeredPane.add(runTimeLabel);

		// Create main text area
		final JTextArea textArea = new JTextArea("", 5, 50);
		textArea.setLineWrap(true);
		textArea.setBounds(10, 70, 399, 153);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 70, 454, 153);
		layeredPane.add(scrollPane);

		// Create 'browse' button
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = chooser.showOpenDialog(contentPane);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					pathTextField.setText(chooser.getSelectedFile().toString());
				}
			}
		});
		browseButton.setBounds(360, 8, 104, 20);
		layeredPane.add(browseButton);

		// Create 'search' button
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				searchButton.setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Runnable runnable = new Runnable() {
					public void run() { 
						File logFile = new File(fileScanner.streamFileName);

						if (pathTextField.getText() != null && !pathTextField.getText().isEmpty()) {
							try {
								if (logFile.getTotalSpace() != 0) {
									FileWriter fwOb = new FileWriter(logFile.getName(), false);
									PrintWriter pwOb = new PrintWriter(fwOb, false);

									pwOb.flush();
									pwOb.close();
									fwOb.close();
								}

								runTimeLabel.setText(
										fileScanner.Search(pathTextField.getText(), keywordTextFiled.getText()));
								String content = readFile(logFile.getName(), Charset.defaultCharset());
								textArea.setText(content);
								textArea.setCaretPosition(textArea.getDocument().getLength());
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							searchButton.setEnabled(true);
						}
					}
				};
				
				Thread thread = new Thread(runnable);
				thread.start();

			}
		});
		searchButton.setBounds(324, 39, 140, 20);
		layeredPane.add(searchButton);
	}

	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
