package com.yourorganization.maven_sample;

import java.nio.file.Path;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

public class MethodCleaner {
	
	static final String RETURN = "return";
	static final char SEP = ' ';
	static final char SEMICOLON = ';';
	static final Object DEFAULT_OBJECT = null;
	static final boolean DEFAULT_BOOLEAN = false;
	static final String DEFAULT_CHAR = "'a'";
	static final int DEFAULT_PRIMITIVE = 0;
	
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
			if (cleanBody) {
				md.createBody();
			}
			if (addReturnStmt) {
				md.getBody().get().addStatement(getDefaultReturnStmt(type));
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
	
	

	public static void main(String[] args) {
//		showMethods("src/main/resources", "", "Blabla.java");
//		cleanVoidMethods("src/main/resources", "", "Blabla.java", "output");
//		cleanObjectMethods("src/main/resources", "", "Blabla.java", "output");
//		cleanPrimitiveMethods("src/main/resources", "", "Blabla.java", "output");
		cleanMethods("src/main/resources", "", "Blabla.java", "output", true, true, true);
	}
}
