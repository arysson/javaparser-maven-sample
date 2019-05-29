package utils;

import static utils.Constants.*;
import static utils.PathUtils.getPathStr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class CommandUtils {
	private static String getCompileCommand(String dirPath, String filePath) {
		return String.join(SEP, JAVA_COMPILE, CLASS_PATH, dirPath, filePath);
	}
	
	private static boolean compileSuccessfully(String sourceRootPath, String startPackage, String filename) {
		String filePath = getPathStr(sourceRootPath, startPackage, filename), dirPath = getPathStr(sourceRootPath), cmd = getCompileCommand(dirPath, filePath);
		return runCommandSuccessfully(cmd);
	}
	
	private static String getRunCommand(String path, String file) {
		return String.join(SEP, JAVA_RUN, ENABLE_ASSERT, CLASS_PATH, path, file);
	}
	
	private static boolean runSuccessfully(String sourceRootPath, String startPackage, String filename) {
		String dirPath = getPathStr(sourceRootPath);
		String str = filename.substring(0, filename.length() - JAVA_EXTENSION.length());
		String filePath = startPackage + SLASH + str;
		String cmd = getRunCommand(dirPath, filePath);
		return runCommandSuccessfully(cmd);
	}
	
	public static boolean compileAndRunSuccessfully(String sourceRootPath, String startPackage, String filename) {
		return compileSuccessfully(sourceRootPath, startPackage, filename) && runSuccessfully(sourceRootPath, startPackage, filename);
	}
	
	private static boolean runCommandSuccessfully(String cmd) {
		String value = System.getenv(Constants.DEBUG);
		boolean DEBUG = value != null && !value.equals("0");
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			if (DEBUG) {
				System.out.println(cmd);
				log(proc.getErrorStream(), "ERROR");
				log(proc.getInputStream(), "OUTPUT");
				System.out.println();
			}
			int exitVal = proc.waitFor();
			return exitVal == EXIT_SUCCESS;
		} catch (Throwable t) {
			return false;
		}
	}
	
	private static void log(InputStream in, String name) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			System.out.println("<" + name + ">");
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("</" + name + ">");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
