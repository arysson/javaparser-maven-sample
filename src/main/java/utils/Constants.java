package utils;

import com.github.javaparser.ast.Modifier;

public final class Constants {
	public static final String RETURN = "return";
	public static final String SEP = " ";
	public static final char SEMICOLON = ';';
	public static final char SLASH = '/';
	public static final Object DEFAULT_OBJECT = null;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final String DEFAULT_CHAR = "'a'";
	public static final int DEFAULT_PRIMITIVE = 0;
	public static final String MAIN_STRING = "main";
	public static final String ARRAY_STRING_STRING = "String[]";
	public static final String JAVA_COMPILE = "javac";
	public static final String JAVA_RUN = "java";
	public static final String ENABLE_ASSERT = "-ea";
	public static final String CLASS_PATH = "-cp";
	public static final int EXIT_SUCCESS = 0;
	public static final String JAVA_EXTENSION = ".java";
	public static final String DEBUG = "DEBUG";
	
	public static final Modifier PUBLIC_MODIFIER = new Modifier(Modifier.Keyword.PUBLIC);
	public static final Modifier STATIC_MODIFIER = new Modifier(Modifier.Keyword.STATIC);
}
