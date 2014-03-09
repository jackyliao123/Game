package game;

import java.io.*;

public class NativeLoader {
	public static void loadLwjgl() throws IOException {
		String path = "/" + System.mapLibraryName("lwjgl");
		String[] parts = path.split("/");
		String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
		if (filename != null) {
			parts = filename.split("\\.", 2);
		}
		File temp = new File(System.getProperty("java.io.tmpdir") + path);
		temp.deleteOnExit();
		temp.createNewFile();
		if (!temp.exists()) {
			throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
		}
		byte[] buffer = new byte[1024];
		int readBytes;
		InputStream is = NativeLoader.class.getResourceAsStream(path);
		if (is == null) {
			throw new FileNotFoundException("File " + path + " was not found inside JAR.");
		}
		OutputStream os = new FileOutputStream(temp);
		try {
			while ((readBytes = is.read(buffer)) != -1) {
				os.write(buffer, 0, readBytes);
			}
		}
		finally {
			os.close();
			is.close();
		}
		System.setProperty("org.lwjgl.librarypath", System.getProperty("java.io.tmpdir"));
	}
}
