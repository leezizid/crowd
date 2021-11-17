package com.crowd.tool.misc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Random;

public class DiskClear {

	private final static int FILE_SIZE = 1024 * 1024 * 100;

	public final static void main(String[] args) throws Throwable {
		String path = System.getProperty("workDir");
		if (path == null || path.trim().length() == 0) {
			throw new IllegalArgumentException("workDir need");
		}
		long freeSpace = File.listRoots()[0].getFreeSpace() / FILE_SIZE;
		File workDir = new File(path);
		for (int i = 0; i < freeSpace; i++) {
			System.out.println("正在写入第" + (i + 1) + "/" + freeSpace);
			createAndWriteFile(workDir);
		}
		for (File file : workDir.listFiles()) {
			file.delete();
		}
	}

	private final static void createAndWriteFile(File workDir) throws Throwable {
		File file = new File(workDir, System.currentTimeMillis() + ".dat");
		file.createNewFile();
		writeContent(file);
	}

	private final static void writeContent(File file) throws Throwable {
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		while (raf.getFilePointer() < FILE_SIZE) {
			byte[] buffer = new byte[(int) (Math.min(64 * 1024, FILE_SIZE - raf.getFilePointer()))];
			new Random().nextBytes(buffer);
			raf.write(buffer);
		}
		raf.close();
	}
}
