package org.firesoa.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	// InputStream 转换成byte[]
	private static final int BUFFER_SIZE = 1024;

	public static byte[] inputStream2ByteArray(InputStream is)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[BUFFER_SIZE];
		int len = 0;

		while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
			baos.write(b, 0, len);
		}

		baos.flush();

		byte[] bytes = baos.toByteArray();

		return bytes;
	}

	public static String inputStream2String(InputStream in, String charset)
			throws IOException {
		byte[] bytes = inputStream2ByteArray(in);

		return new String(bytes, charset);
	}

	public static String findXmlCharset(String processXml) {
		String encoding = "UTF-8";
		Pattern encodingPattern = Pattern.compile(
				"<\\?xml.*\\s+encoding\\s*=\\s*[\"']?([\\w\\-_]+)[\"'\\s?><]",
				Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
		Matcher m = encodingPattern.matcher(processXml);
		if (m.find()) {
			encoding = m.group(1).trim();
		}
		return encoding;
	}

	public static String findXmlCharset(InputStream inStream) throws IOException{
		String encoding = "UTF-8";
		Pattern encodingPattern = Pattern.compile(
				"<\\?xml.*\\s+encoding\\s*=\\s*[\"']?([\\w\\-_]+)[\"'\\s?><]",
				Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

		InputStream stream = inStream;
		
		if (!stream.markSupported()) {
			return encoding;
		}
		byte[] buf = new byte[70];
		stream.mark(100);
		int len = stream.read(buf);
		if (len > 0) {
			String xmlHead = new String(buf, 0, len);
			Matcher m = encodingPattern.matcher(xmlHead);
			if (m.find()) {
				encoding = m.group(1).trim();
			}
		}
		stream.reset();
		return encoding;
	}
	
	public static String newUUID(){
		String original = UUID.randomUUID().toString();
		String myUUID = "fid_"+original.replaceAll("-", "_");
		return myUUID;
	}
}
