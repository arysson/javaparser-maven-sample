package utils;

import java.nio.file.Path;

import com.github.javaparser.utils.CodeGenerationUtils;

import core.MethodCleaner;

public final class PathUtils {
	
	private static final Path BASE = CodeGenerationUtils.mavenModuleRoot(PathUtils.class); 
	
	public static Path getPath(String... paths) {
		if (paths == null) {
			return null;
		}
		Path path = BASE;
		for (String p: paths) {
			path = path.resolve(p);
		}
		return path;
	}
	
	public static String getPathStr(String... paths) {
		Path path = getPath(paths);
		return path == null ? null : path.toString();
	}
}
