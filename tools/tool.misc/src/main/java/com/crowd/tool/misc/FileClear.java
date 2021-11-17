package com.crowd.tool.misc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Random;

public class FileClear {

	public final static void main(String[] args) throws Throwable {
		String path = "/Users/fly/deleted/";
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				process(file);
			}
		}
	}

	private final static void process(File file) throws Throwable {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				process(f);
			}
		} else {
			//
			System.out.println(file.getAbsolutePath() + "：第1遍写入");
			clearContent(file);
			System.out.println(file.getAbsolutePath() + "：第2遍写入");
			clearContent(file);
			System.out.println(file.getAbsolutePath() + "：第3遍写入");
			clearContent(file);
		}
		//
		file.delete();
		System.out.println(file.getAbsolutePath() + "：删除");
	}

	private final static void clearContent(File file) throws Throwable {
		long fileSize = file.length();
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		while (raf.getFilePointer() < fileSize) {
			byte[] buffer = new byte[(int) (Math.min(64 * 1024, fileSize - raf.getFilePointer()))];
			new Random().nextBytes(buffer);
			raf.write(buffer);
		}
		raf.close();
	}

}
