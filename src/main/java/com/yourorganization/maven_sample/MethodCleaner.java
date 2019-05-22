package com.yourorganization.maven_sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

public class MethodCleaner {
	
	private static final String RETURN = "return";
	private static final String SEP = " ";
	private static final char SEMICOLON = ';';
	private static final char SLASH = '/';
	private static final Object DEFAULT_OBJECT = null;
	private static final boolean DEFAULT_BOOLEAN = false;
	private static final String DEFAULT_CHAR = "'a'";
	private static final int DEFAULT_PRIMITIVE = 0;
	private static final String MAIN_STRING = "main";
	private static final String ARRAY_STRING_STRING = "String[]";
	private static final String JAVA_COMPILE = "javac";
	private static final String JAVA_RUN = "java";
	private static final String ENABLE_ASSERT = "-ea";
	private static final String CLASS_PATH = "-cp";
	private static final int EXIT_SUCCESS = 0;
	private static final String JAVA_EXTENSION = ".java";
	
	private static final Modifier PUBLIC_MODIFIER = new Modifier(Modifier.Keyword.PUBLIC);
	private static final Modifier STATIC_MODIFIER = new Modifier(Modifier.Keyword.STATIC);
	
	private static String getReturnStmt(Object obj) {
		return RETURN + SEP + obj + SEMICOLON;
	}
	
	private static String getReturnStmt(boolean b) {
		return RETURN + SEP + b + SEMICOLON;
	}
	
	private static String getReturnStmt(char c) {
		return RETURN + SEP + c + SEMICOLON;
	}
	
	private static String getReturnStmt(int x) {
		return RETURN + SEP + x + SEMICOLON;
	}
	
	private static String getDefaultReturnStmt(Type type) {
		String returnStmt = null;
		if (type.isReferenceType()) {
			returnStmt = getReturnStmt(DEFAULT_OBJECT);
		} else {
			Primitive primitive = type.asPrimitiveType().getType();
			if (primitive.equals(Primitive.BOOLEAN)) {
				returnStmt = getReturnStmt(DEFAULT_BOOLEAN);
			} else if (primitive.equals(Primitive.CHAR)) {
				returnStmt = getReturnStmt(DEFAULT_CHAR);
			} else {
				returnStmt = getReturnStmt(DEFAULT_PRIMITIVE);
			}
		}
		return returnStmt;
	}

	private static void showMethods(String sourceRootPath, String startPackage, String filename) {
		CompilationUnit cu = getCompilationUnit(sourceRootPath, startPackage, filename);
		List<MethodDeclaration> mds = getMethods(cu);
		mds.stream().forEach(md -> {
			System.out.println(md.getDeclarationAsString());
		});
	}
	
	private static List<MethodDeclaration> getMethods(CompilationUnit cu) {
		return cu.findAll(MethodDeclaration.class);
	}
	
	private static Path getPath(String path) {
		Path projectPath = CodeGenerationUtils.mavenModuleRoot(MethodCleaner.class);
		return projectPath.resolve(path);
	}
	
	private static SourceRoot getSourceRoot(String path) {
		Path rootPath = getPath(path);
		return new SourceRoot(rootPath);
	}

	private static CompilationUnit getCompilationUnit(SourceRoot sr, String startPackage, String filename) {
		return sr.parse(startPackage, filename);
	}
	
	private static CompilationUnit getCompilationUnit(String sourceRootPath, String startPackage, String filename) {
		SourceRoot sr = getSourceRoot(sourceRootPath);
		return getCompilationUnit(sr, startPackage, filename);
	}
	
	private static void cleanMethods(String sourceRootPath, String startPackage, String filename, String outputPath, boolean cleanVoid, boolean cleanPrimitive, boolean cleanObject) {
		SourceRoot sr = getSourceRoot(sourceRootPath);
		CompilationUnit cu = getCompilationUnit(sr, startPackage, filename);
		cleanMethods(cu, cleanVoid, cleanPrimitive, cleanObject);
		Path p = getPath(outputPath);
		sr.saveAll(p);
	}
	
	private static void cleanMethods(CompilationUnit cu, boolean cleanVoid, boolean cleanPrimitive, boolean cleanObject) {
		cu.findAll(MethodDeclaration.class).stream().forEach(md -> {
			Type type = md.getType();
			boolean addReturnStmt = (type.isPrimitiveType() && cleanPrimitive) || (type.isReferenceType() && cleanObject), 
					cleanBody = addReturnStmt || (type.isVoidType() && cleanVoid);
			if (md.getBody().isPresent()) {
				if (cleanBody) {
					md.createBody();
				}
				if (addReturnStmt) {
					md.getBody().get().addStatement(getDefaultReturnStmt(type));
				}
			}
		});
	}

	private static void cleanVoidMethods(String sourceRootPath, String startPackage, String filename, String outputPath) {
		cleanMethods(sourceRootPath, startPackage, filename, outputPath, true, false, false);
	}
	
	private static void cleanObjectMethods(String sourceRootPath, String startPackage, String filename, String outputPath) {
		cleanMethods(sourceRootPath, startPackage, filename, outputPath, false, false, true);
	}
	
	private static void cleanPrimitiveMethods(String sourceRootPath, String startPackage, String filename, String outputPath) {
		cleanMethods(sourceRootPath, startPackage, filename, outputPath, false, true, false);
	}
	
	private static boolean isMainMethod(MethodDeclaration md) {
		NodeList<Modifier> modifiers = md.getModifiers();
		NodeList<Parameter> parameters = md.getParameters();
		return modifiers != null && modifiers.contains(PUBLIC_MODIFIER) && modifiers.contains(STATIC_MODIFIER) 
				&& md.getType().isVoidType() && md.getNameAsString().equals(MAIN_STRING) 
				&& parameters != null && parameters.size() == 1 && parameters.get(0).getTypeAsString().equals(ARRAY_STRING_STRING);
	}
	
	private static void cleanMethodsIfTestsPass(String inputPath, String filename, String outputPath) {
		SourceRoot sr = getSourceRoot(inputPath);
		CompilationUnit cu = getCompilationUnit(sr, "", filename);
		Path p = getPath(outputPath);
		cu.findAll(MethodDeclaration.class).stream().forEach(md -> {
			if (!isMainMethod(md) && md.getBody().isPresent()) {
				Type type = md.getType();
				BlockStmt orig = md.getBody().get();
				md.createBody();
				if (!type.isVoidType()) {
					md.getBody().get().addStatement(getDefaultReturnStmt(type));
				}
				sr.saveAll(p);
				if (!compileAndRunSuccessfully(outputPath, filename)) { 
					md.setBody(orig);
				}
			}
		});
		sr.saveAll(p);
	}
	
	private static String getCompileCommand(String filePath) {
		return JAVA_COMPILE + SEP + filePath;
	}
	
	private static boolean compileSuccessfully(String path, String filename) {
		String filePath = getPath(path).resolve(filename).toString(), cmd = getCompileCommand(filePath);
		return runCommandSuccessfully(cmd);
	}
	
	private static String getRunCommand(String path, String file) {
		return String.join(SEP, JAVA_RUN, ENABLE_ASSERT, CLASS_PATH, path, file);
	}
	
	private static boolean runSuccessfully(String path, String filename) {
		String dirPath = getPath(path).toString();
		String str = filename.substring(0, filename.length() - JAVA_EXTENSION.length());
		String cmd = getRunCommand(dirPath, str);
		return runCommandSuccessfully(cmd);
	}
	
	private static boolean compileAndRunSuccessfully(String path, String filename) {
		return compileSuccessfully(path, filename) && runSuccessfully(path, filename);
	}
	
	private static boolean runCommandSuccessfully(String cmd) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			int exitVal = proc.waitFor();
			return exitVal == EXIT_SUCCESS;
		} catch (Throwable t) {
			return false;
		}
	}

	public static void main(String[] args) {
//		showMethods("src/main/resources", "", "Blabla.java");
//		cleanVoidMethods("src/main/resources", "", "Blabla.java", "output");
//		cleanObjectMethods("src/main/resources", "", "Blabla.java", "output");
//		cleanPrimitiveMethods("src/main/resources", "", "Blabla.java", "output");
//		cleanMethods("src/main/resources", "", "Blabla.java", "output", true, true, true);
		cleanMethodsIfTestsPass("src/main/resources", "Blabla.java", "output");
		cleanMethodsIfTestsPass("src/main/resources/backtracking", "BronKerbosh.java", "output");
	}
}
