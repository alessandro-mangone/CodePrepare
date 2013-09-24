package com.vektor.classstructure;

import java.util.ArrayList;

import org.eclipse.jdt.core.Flags;

import com.google.gson.annotations.SerializedName;

public class VektorSerialization {

	public static class sourceCode {
		@SerializedName("status")
		private String status;
		@SerializedName("file")
		private String file;
		@SerializedName("from")
		private int from;
		@SerializedName("to")
		private int to;
		@SerializedName("code")
		private ArrayList<codeLine> code = new ArrayList<codeLine>();

		public sourceCode(String status, String file, int from, int to,
				ArrayList<codeLine> code) {
			this.status = status;
			this.file = file;
			this.from = from;
			this.to = to;
			this.code.addAll(code);
		}
	}

	public static class codeLine {
		@SerializedName("depth")
		private int depth;
		@SerializedName("code")
		private String code;

		public codeLine(int depth, String code) {
			this.depth = depth;
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public static class parsedLine {
		private int depth;
		private String code;

		public parsedLine(int depth, String code) {
			this.depth = depth;
			this.code = code;
		}

		public int getDepth() {
			return depth;
		}

		public String getCode() {
			return code;
		}
	}

	public static class classDocument {
		@SerializedName("status")
		private String status;
		@SerializedName("lines")
		private int lines;
		@SerializedName("name")
		private String name;
		@SerializedName("types")
		private ArrayList<classStructure> classes = new ArrayList<classStructure>();
		@SerializedName("nestlevel")
		private int nestlevel;

		public classDocument(String status, int lines, String name,
				ArrayList<classStructure> classes,int nestlevel) {
			this.lines = lines;
			this.status = status;
			this.name = name;
			this.classes = classes;
			this.nestlevel=nestlevel;
		}

		public void addClass(classStructure classs) {
			classes.add(classs);
		}
	}

	public static class classStructure {
		@SerializedName("name")
		private String name;
		@SerializedName("interface")
		private boolean isInterface;
		@SerializedName("access")
		private String access;
		@SerializedName("static")
		private boolean isStatic;
		@SerializedName("final")
		private boolean isFinal;
		@SerializedName("abstract")
		private boolean isAbstract;
		@SerializedName("types")
		private ArrayList<classStructure> types = new ArrayList<classStructure>();
		@SerializedName("fields")
		private ArrayList<classField> fields = new ArrayList<classField>();
		@SerializedName("methods")
		private ArrayList<classMethod> methods = new ArrayList<classMethod>();

		public classStructure(String name, int flags,
				ArrayList<classStructure> types, ArrayList<classField> fields,
				ArrayList<classMethod> methods) {
			this.name = name;
			this.isInterface = isInterface(flags);
			this.access = getAccessFlags(flags);
			this.isFinal = isFinal(flags);
			this.isStatic = isStatic(flags);
			this.isAbstract = isAbstract(flags);
			this.types = types;
			this.fields.addAll(fields);
			this.methods.addAll(methods);
		}

		public void addType(classStructure classs) {
			types.add(classs);
		}
	}

	public static class classField {
		@SerializedName("name")
		private String name;
		@SerializedName("access")
		private String access;
		@SerializedName("final")
		private boolean isFinal;
		@SerializedName("static")
		private boolean isStatic;
		@SerializedName("transient")
		private boolean isTransient;
		@SerializedName("volatile")
		private boolean isVolatile;
		@SerializedName("lineStart")
		private int lineStart;
		@SerializedName("lineEnd")
		private int lineEnd;

		public classField(String name, int flags, int lineStart, int lineEnd) {
			this.name = name;
			this.access = getAccessFlags(flags);
			this.isFinal = isFinal(flags);
			this.isStatic = isStatic(flags);
			this.isTransient = isTransient(flags);
			this.isVolatile = isVolatile(flags);
			this.lineStart = lineStart;
			this.lineEnd = lineEnd;
		}

		public String toString() {
			return name + " [" + lineStart + "," + lineEnd + "]";
		}
	}

	public static class classMethod {
		@SerializedName("name")
		private String name;
		@SerializedName("access")
		private String access;
		@SerializedName("abstract")
		private boolean isAbstract;
		@SerializedName("final")
		private boolean isFinal;
		@SerializedName("native")
		private boolean isNative;
		@SerializedName("static")
		private boolean isStatic;
		@SerializedName("synchronized")
		private boolean isSynchronized;
		@SerializedName("lineStart")
		private int lineStart;
		@SerializedName("lineEnd")
		private int lineEnd;

		public classMethod(String name, int flags, int lineStart, int lineEnd) {
			this.access = getAccessFlags(flags);
			this.isAbstract = isAbstract(flags);
			this.isFinal = isFinal(flags);
			this.isNative = isNative(flags);
			this.isStatic = isStatic(flags);
			this.isSynchronized = isSynchronized(flags);
			this.name = name;
			this.lineStart = lineStart;
			this.lineEnd = lineEnd;
		}

		public String toString() {
			return name + " [" + lineStart + "," + lineEnd + "]";
		}
	}

	private static String getAccessFlags(int flags) {
		if (Flags.isPublic(flags))
			return "public";
		else if (Flags.isProtected(flags))
			return "protected";
		else if (Flags.isPrivate(flags))
			return "private";
		else
			return "";
	}

	private static boolean isStatic(int flags) {
		return Flags.isStatic(flags);
	}

	private static boolean isAbstract(int flags) {
		return Flags.isAbstract(flags);
	}

	private static boolean isFinal(int flags) {
		return Flags.isFinal(flags);
	}

	private static boolean isInterface(int flags) {
		return Flags.isInterface(flags);
	}

	private static boolean isSynchronized(int flags) {
		return Flags.isSynchronized(flags);
	}

	private static boolean isNative(int flags) {
		return Flags.isNative(flags);
	}

	private static boolean isTransient(int flags) {
		return Flags.isTransient(flags);
	}

	private static boolean isVolatile(int flags) {
		return Flags.isVolatile(flags);
	}
}
