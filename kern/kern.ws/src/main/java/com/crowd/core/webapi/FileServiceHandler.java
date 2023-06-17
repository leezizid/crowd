package com.crowd.core.webapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class FileServiceHandler {

	
	private final static String workDir = System.getProperty("user.dir") + File.separator + "Data";
	
	public final static void save(String domain, String name, String content) throws Throwable {
		File dir = new File(workDir + File.separator + domain);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		writeTextFile(new File(dir, name), content);
	}

	public final static String load(String domain, String name) throws Throwable {
		File dir = new File(workDir + File.separator + domain);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return readTextFile(new File(dir, name));
	}

	public static String readTextFile(File file) throws Throwable {
		if (!file.exists()) {
			return "";
		}
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			byte[] buffer = new byte[(int) file.length()];
			raf.readFully(buffer);
			return new String(buffer, "UTF-8");
		} finally {
			raf.close();
		}
	}

	public static void writeTextFile(File file, String content) throws Throwable {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			fos.write(content.getBytes("UTF-8"));
		} finally {
			fos.close();
		}
	}

	
	
	public final static void delete(String domain, String name) throws Throwable {
		File dir = new File(workDir + File.separator + domain);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, name);
		if(file.exists()) {
			file.delete();
		}
	}

}
