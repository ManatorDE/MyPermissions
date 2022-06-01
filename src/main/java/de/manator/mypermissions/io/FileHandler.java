package de.manator.mypermissions.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class FileHandler {

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

	public static void writeLines(List<String> lines, File f) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
			for(String s : lines) {
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
	
	public static LinkedList<String> getLines(File f) {
		BufferedReader br = null;
		LinkedList<String> lines = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(f));
			while(br.ready()) {
				lines.add(br.readLine());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return lines;
	}
	
	public static LinkedList<String> getUpperCaseLines(File f) {
		BufferedReader br = null;
		LinkedList<String> lines = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(f));
			while(br.ready()) {
				lines.add(br.readLine().toUpperCase());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return lines;
	}

}
