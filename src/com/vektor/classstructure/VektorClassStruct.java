package com.vektor.classstructure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vektor.classstructure.VektorSerialization.classDocument;
import com.vektor.classstructure.VektorSerialization.classField;
import com.vektor.classstructure.VektorSerialization.classMethod;
import com.vektor.classstructure.VektorSerialization.classStructure;
import com.vektor.classstructure.VektorSerialization.fs;
import com.vektor.classstructure.VektorSerialization.fsElement;
import com.vektor.classstructure.VektorSerialization.sourceCode;
import com.vektor.parsing.VektorParser;

public class VektorClassStruct {
	private static IWorkspace workspace = ResourcesPlugin.getWorkspace();
	private static IWorkspaceRoot root = workspace.getRoot();
	private static IProject[] projects = root.getProjects();
	private final static String path = System.getProperty("user.home")
			+ File.separator + "Documents" + File.separator + "PreparedCode";
	private final static String jsonIdentifier = "-code.json";
	private static int maxLevel = 0;

	public static void scan() throws BadLocationException {
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
		long start = System.currentTimeMillis();
		for (IProject project : projects) {
			try {
				printProjectInfo(project);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("All projects updated in "
				+ (System.currentTimeMillis() - start) + " ms.");
		start = System.currentTimeMillis();
		updateFs(new File(path));
		System.out.println("FS JSON objects created in "
				+ (System.currentTimeMillis() - start) + " ms.");
	}

	private static void updateFs(File f) {
		File[] list = f.listFiles();
		ArrayList<fsElement> files = new ArrayList<fsElement>();
		for (File file : list) {
			if (file.isDirectory()) {
				files.add(new fsElement(file.getName(), true, file
						.getAbsolutePath().substring(path.length()+1).concat(File.separator)));
				updateFs(file);
			} else if (file.getName().endsWith(jsonIdentifier)
					&& new File(file.getParent(), file.getName().replace(
							jsonIdentifier, "")
							+ "-structure.json").exists()) {
				files.add(new fsElement(file.getName().replace(jsonIdentifier, ""), false, file
						.getParent().substring(path.length()+1).concat(File.separator)));
			}
		}
		writeToFile(new fs(files), new File(f, "dir.json"));
	}

	private static void printProjectInfo(IProject project)
			throws CoreException, JavaModelException, BadLocationException {
		String prjDir = path + File.separator + project.getName();
		File currentDir = new File(prjDir);
		if (!currentDir.exists())
			currentDir.mkdirs();
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			printPackageInfos(javaProject, prjDir);
		}
	}

	private static void printPackageInfos(IJavaProject javaProject,
			String projectDir) throws JavaModelException, BadLocationException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				String pkgDir = projectDir
						+ File.separator
						+ mypackage.getElementName().replace(".",
								File.separator);
				File f = new File(pkgDir);
				if (!f.exists())
					f.mkdirs();
				printICompilationUnitInfo(mypackage, pkgDir);

			}

		}
	}

	private static void printICompilationUnitInfo(IPackageFragment mypackage,
			String packageDir) throws JavaModelException, BadLocationException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit, packageDir);
		}
	}

	private static classDocument printCompilationUnitDetails(
			ICompilationUnit unit, String packageDir)
			throws JavaModelException, BadLocationException {
		Document doc = new Document(unit.getSource());
		classDocument classDoc = new classDocument("OK",
				doc.getNumberOfLines(), unit.getElementName(), printStructure(
						unit, doc), maxLevel);
		maxLevel = 0;
		String unitName = unit.getElementName();
		String jsonStructureDir = packageDir + File.separator
				+ unitName.substring(0, unitName.length() - 5)
				+ "-structure.json";
		writeToFile(classDoc, new File(jsonStructureDir));
		String jsonCodeDir = packageDir + File.separator
				+ unitName.substring(0, unitName.length() - 5) + "-code.json";
		writeToFile(VektorParser.getSrcCode(unitName, doc.get()), new File(
				jsonCodeDir));
		return classDoc;
	}

	private static ArrayList<classStructure> printStructure(
			ICompilationUnit unit, Document doc) throws JavaModelException,
			BadLocationException {
		maxLevel = 1;
		IType[] types = unit.getTypes();
		ArrayList<classStructure> classes = new ArrayList<classStructure>();
		for (IType type : types) {
			classes.add(new classStructure(type.getElementName(), type
					.getFlags(), getSubTypes(type, doc, maxLevel),
					printIFieldDetails(type, doc), printIMethodDetails(type,
							doc)));
		}
		return classes;
	}

	private static ArrayList<classStructure> getSubTypes(IType type,
			Document doc, int level) throws JavaModelException,
			BadLocationException {

		ArrayList<classStructure> subtypes = new ArrayList<classStructure>();
		IType[] tps = type.getTypes();
		level++;
		if (level > maxLevel)
			maxLevel = level;
		for (IType tp : tps) {
			subtypes.add(new classStructure(tp.getElementName(), tp.getFlags(),
					getSubTypes(tp, doc, level), printIFieldDetails(tp, doc),
					printIMethodDetails(tp, doc)));
		}
		return subtypes;
	}

	private static ArrayList<classField> printIFieldDetails(IType type,
			Document doc) throws JavaModelException, BadLocationException {
		IField[] fields = type.getFields();
		ArrayList<classField> fs = new ArrayList<classField>();
		for (IField field : fields) {
			int from = 1 + doc.getLineOfOffset(field.getSourceRange()
					.getOffset());
			int to = 1 + doc.getLineOfOffset(field.getSourceRange().getOffset()
					+ field.getSourceRange().getLength() - 1);
			fs.add(new classField(field.getElementName(), field.getFlags(),
					from, to, Signature.toString(field.getTypeSignature())));
		}
		return fs;
	}

	private static ArrayList<classMethod> printIMethodDetails(IType type,
			Document doc) throws JavaModelException, BadLocationException {
		ArrayList<classMethod> ms = new ArrayList<classMethod>();
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {
			int from = 1 + doc.getLineOfOffset(method.getSourceRange()
					.getOffset());
			int to = 1 + doc.getLineOfOffset(method.getSourceRange()
					.getOffset() + method.getSourceRange().getLength() - 1);
			ms.add(new classMethod(method.getElementName(), method.getFlags(),
					from, to, Signature.toString(method.getReturnType())));
		}
		return ms;
	}

	private static void writeToFile(Object obj, File src) {
		try {
			File parent = new File(src.getParent());
			if (!parent.exists())
				parent.mkdirs();
			FileWriter writer = new FileWriter(src);
			writer.append(new Gson().toJson(obj));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}