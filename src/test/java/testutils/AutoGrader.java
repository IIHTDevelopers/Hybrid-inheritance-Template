package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code implements hybrid inheritance correctly
	public boolean testHybridInheritance(String filePath) throws IOException {
		System.out.println("Starting testHybridInheritance with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean animalClassFound = new AtomicBoolean(false);
		AtomicBoolean dogClassFound = new AtomicBoolean(false);
		AtomicBoolean flyableInterfaceFound = new AtomicBoolean(false);
		AtomicBoolean runnableInterfaceFound = new AtomicBoolean(false);
		AtomicBoolean dogExtendsAnimal = new AtomicBoolean(false);
		AtomicBoolean dogImplementsFlyable = new AtomicBoolean(false);
		AtomicBoolean dogImplementsRunnable = new AtomicBoolean(false);
		AtomicBoolean speakMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean flyMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean runMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean methodsExecuted = new AtomicBoolean(false);

		// Check for class implementation and inheritance (Dog extends Animal, Dog
		// implements Flyable and Runnable)
		System.out.println("------ Inheritance and Interface Implementation Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found.");
					animalClassFound.set(true);
				}

				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found.");
					dogClassFound.set(true);
					// Check if Dog extends Animal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						dogExtendsAnimal.set(true);
						System.out.println("Dog extends 'Animal'.");
					} else {
						System.out.println("Error: 'Dog' does not extend 'Animal'.");
					}

					// Check if Dog implements Flyable and Runnable interfaces
					classDecl.getImplementedTypes().forEach(impl -> {
						if (impl.getNameAsString().equals("Flyable")) {
							dogImplementsFlyable.set(true);
							System.out.println("Dog implements 'Flyable'.");
						}
						if (impl.getNameAsString().equals("Runnable")) {
							dogImplementsRunnable.set(true);
							System.out.println("Dog implements 'Runnable'.");
						}
					});
				}

				if (classDecl.getNameAsString().equals("Flyable")) {
					System.out.println("Interface 'Flyable' found.");
					flyableInterfaceFound.set(true);
				}

				if (classDecl.getNameAsString().equals("Runnable")) {
					System.out.println("Interface 'Runnable' found.");
					runnableInterfaceFound.set(true);
				}
			}
		}

		// Ensure all classes and interfaces are found
		if (!animalClassFound.get() || !dogClassFound.get() || !flyableInterfaceFound.get()
				|| !runnableInterfaceFound.get()) {
			System.out
					.println("Error: One or more classes (Animal, Dog, Flyable, Runnable) or interfaces are missing.");
			return false; // Early exit if class or interface creation is missing
		}

		// Ensure Dog extends Animal, implements Flyable and Runnable
		if (!dogExtendsAnimal.get()) {
			System.out.println("Error: 'Dog' must extend 'Animal'.");
			return false;
		}
		if (!dogImplementsFlyable.get()) {
			System.out.println("Error: 'Dog' must implement 'Flyable'.");
			return false;
		}
		if (!dogImplementsRunnable.get()) {
			System.out.println("Error: 'Dog' must implement 'Runnable'.");
			return false;
		}

		// Check for method overriding (speak, fly, and run methods)
		System.out.println("------ Method Override Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("speak") && method.getParentNode().get().toString().contains("Dog")) {
				speakMethodImplemented.set(true);
				System.out.println("Method 'speak' overridden in 'Dog' class.");
			}
			if (method.getNameAsString().equals("fly") && method.getParentNode().get().toString().contains("Dog")) {
				flyMethodImplemented.set(true);
				System.out.println("Method 'fly' implemented in 'Dog' class.");
			}
			if (method.getNameAsString().equals("run") && method.getParentNode().get().toString().contains("Dog")) {
				runMethodImplemented.set(true);
				System.out.println("Method 'run' implemented in 'Dog' class.");
			}
		}

		if (!speakMethodImplemented.get() || !flyMethodImplemented.get() || !runMethodImplemented.get()) {
			System.out.println(
					"Error: One or more methods ('speak', 'fly', 'run') not implemented correctly in 'Dog' class.");
			return false;
		}

		// Check if methods are executed in main
		System.out.println("------ Method Execution Check in Main ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("speak") || callExpr.getNameAsString().equals("fly")
								|| callExpr.getNameAsString().equals("run")) {
							methodsExecuted.set(true);
							System.out.println("Methods 'speak', 'fly', and 'run' are executed in the main method.");
						}
					});
				}
			}
		}

		if (!methodsExecuted.get()) {
			System.out.println("Error: Methods 'speak', 'fly', and 'run' not executed in the main method.");
			return false;
		}

		// If inheritance, method overriding, and method execution are correct
		System.out.println("Test passed: Hybrid inheritance is correctly implemented.");
		return true;
	}
}
