package de.manator.mypermissions.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A class used to manage the file writing and reading
 * 
 * @author ManatorDE
 */
public class FileHandler {

	/**
	 * Writes the given line into the given file
	 * 
	 * @param line The line to write into the file
	 * @param f    The file to be written into
	 */
	public static void writeLine(String line, File f) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
			pw.println(line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * Writes multiple lines into a file
	 * 
	 * @param lines The lines to be written into the file
	 * @param f     The file to be written into
	 */
	public static void writeLines(List<String> lines, File f) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
			for (String s : lines) {
				pw.println(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * Gets the lines written in a file
	 * 
	 * @param f The file to read
	 * @return A LinkedList with all lines of the read file
	 */
	public static LinkedList<String> getLines(File f) {
		BufferedReader br = null;
		LinkedList<String> lines = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(f));
			while (br.ready()) {
				lines.add(br.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	/**
	 * Gets the lines written in a file in UPPER CASE
	 * 
	 * @param f The file to read
	 * @return A LinkedList with all lines of the read file in UPPER CASE
	 */
	public static LinkedList<String> getUpperCaseLines(File f) {
		BufferedReader br = null;
		LinkedList<String> lines = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(f));
			while (br.ready()) {
				lines.add(br.readLine().toUpperCase());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return lines;
	}
	
	/**
	 * A method used to extract all files from a zip file into a driectory
	 * @param zipPath The path to the zip file (https URLs supported)
	 * @param dest The destination directory
	 */
	public static void unzipFile(String zipPath, File dest) {
		try {
			URL latest = new URL(zipPath);
			byte[] buffer = new byte[1024];
			ZipInputStream zis = new ZipInputStream(latest.openStream());
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
			     File newFile = newFile(dest, zipEntry);
			     if (zipEntry.isDirectory()) {
			         if (!newFile.isDirectory() && !newFile.mkdirs()) {
			             throw new IOException("Failed to create directory " + newFile);
			         }
			     } else {
			         File parent = newFile.getParentFile();
			         if (!parent.isDirectory() && !parent.mkdirs()) {
			             throw new IOException("Failed to create directory " + parent);
			         }
			         FileOutputStream fos = new FileOutputStream(newFile);
			         int len;
			         while ((len = zis.read(buffer)) > 0) {
			             fos.write(buffer, 0, len);
			         }
			         fos.close();
			     }
			 zipEntry = zis.getNextEntry();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used to create a File from a ZipEntry
	 * @param destinationDir
	 * @param zipEntry
	 * @return The File from the ZipEntry
	 * @throws IOException
	 */
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}
	
	/**
	 * Deletes  
	 * @param f
	 */
	public static void removeRecursive(File f) {
		if(f.exists() && f.isDirectory()) {
			if(f.listFiles().length > 0) {
				for(File f2 : f.listFiles()) {
					removeRecursive(f2);
				}
			}
			f.delete();
		} else if(f.exists()) {
			f.delete();
		}
	}
	
}
