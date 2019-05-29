package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import utils.CommandUtils;

import static utils.Constants.*;
import static utils.PathUtils.*;

public class MethodCleaner {	
	
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
	
	private static void cleanMethods(String sourceRootPath, String startPackage, String filename, String outputPath, 
			boolean cleanVoid, boolean cleanPrimitive, boolean cleanObject) {
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
		if (!md.getParentNode().isPresent()) {
			return false;
		}
		TypeDeclaration parent = (TypeDeclaration)md.getParentNode().get();
		return modifiers != null && modifiers.contains(PUBLIC_MODIFIER) && modifiers.contains(STATIC_MODIFIER) 
				&& md.getType().isVoidType() && md.getNameAsString().equals(MAIN_STRING) 
				&& parameters != null && parameters.size() == 1 && parameters.get(0).getTypeAsString().equals(ARRAY_STRING_STRING)
				&& parent.getModifiers().contains(PUBLIC_MODIFIER);
	}
	
	private static void cleanMethodsIfTestsPass(String sourceRootPath, String testPackage, String testFile, String outputPath) throws IOException {
		SourceRoot sr = getSourceRoot(sourceRootPath);
		CompilationUnit cuTest = getCompilationUnit(sr, testPackage, testFile);
		Path p = getPath(outputPath);
		sr.tryToParse();
		for (CompilationUnit cu: sr.getCompilationUnits()) {
			if (!cu.equals(cuTest)) {
				cu.findAll(MethodDeclaration.class).stream().forEach(md -> {
					if (md.getBody().isPresent()) {
						Type type = md.getType();
						BlockStmt orig = md.getBody().get();
						md.createBody();
						if (!type.isVoidType()) {
							md.getBody().get().addStatement(getDefaultReturnStmt(type));
						}
						sr.saveAll(p);
						if (!CommandUtils.compileAndRunSuccessfully(outputPath, testPackage, testFile)) { 
							md.setBody(orig);
						}
					}
				});
			}
		}
		sr.saveAll(p);
	}
	
	private static void removeMethodsIfTestsPass(String sourceRootPath, String testPackage, String testFile, String outputPath) throws IOException {
		SourceRoot sr = getSourceRoot(sourceRootPath);
		CompilationUnit cuTest = getCompilationUnit(sr, testPackage, testFile);
		Path p = getPath(outputPath);
		sr.tryToParse();
		for (CompilationUnit cu: sr.getCompilationUnits()) {
			if (!cu.equals(cuTest)) {
				cu.findAll(MethodDeclaration.class).stream().forEach(md -> {
					if (md.getParentNode().isPresent()) {
						NodeWithMembers<MethodDeclaration> parentNode = (NodeWithMembers<MethodDeclaration>)md.getParentNode().get();
						md.remove();
						sr.saveAll(p);
						if (!CommandUtils.compileAndRunSuccessfully(outputPath, testPackage, testFile)) {
							parentNode.addMember(md);
						}
					}
				});
			}
		}
		sr.saveAll(p);
	}

	public static void main(String[] args) throws IOException {
//		cleanMethodsIfTestsPass("src/main/resources", "test", "ArraysTests.java", "output");
		removeMethodsIfTestsPass("src/main/resources", "test", "ArraysTests.java", "output");
	}
}
