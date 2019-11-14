package dataloader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FzReader {
	private static int DEFAULT_BUFFER_SIZE = 1024;

	public static String readFile(String filePath) {
		try {
			File file = new java.io.File(filePath);
			FileInputStream inputStream = new FileInputStream(file);
			String result = readStream(inputStream);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String readStream(InputStream in) throws IOException {
		if (in == null)
			return "";

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int cnt = 0;
		while ((cnt = in.read(buffer)) > -1) {
			baos.write(buffer, 0, cnt);
		}
		baos.flush();

		in.close();
		baos.close();
		return baos.toString();
	}
}