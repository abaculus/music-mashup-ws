package se.panok.music.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

	public static String readText(final String filePath) throws IOException,
			URISyntaxException {
		return new String(Files.readAllBytes(Paths.get(FileUtil.class
				.getResource(filePath).toURI())), Charset.defaultCharset());
	}
}
