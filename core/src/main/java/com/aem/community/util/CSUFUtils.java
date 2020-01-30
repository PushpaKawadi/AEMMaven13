package com.aem.community.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CSUFUtils {
	
	private CSUFUtils() {		
	}
	
	public static File copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			int read;
			byte[] bytes = new byte[inputStream.available()];
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
		return file;
	}
	
	public static void main(String[] args) {
		String imagePath = "/content/dam/csuf/CSUF_Mailer_logo.gif";		
		String [] imagePathArray = imagePath.split("\\.");		
		String tempPath = imagePathArray[0];
		int lastSlashIndex = tempPath.lastIndexOf("/");
		String imageName = tempPath.substring(lastSlashIndex+1, tempPath.length());
		String imageExtension = imagePathArray[1];
		System.out.println("imageName: ".concat(imageName));
		System.out.println("imageExtension: ".concat(imageExtension));
	}
}
