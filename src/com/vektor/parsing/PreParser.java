package com.vektor.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import prettify.PrettifyParser;
import syntaxhighlight.ParseResult;

public class PreParser {

	private static PrettifyParser parser = new PrettifyParser();

	public static List<String> parseLinesFromFile(String filepath)
			throws IOException {
		return parseLinesFromFile(new File(filepath));
	}

	public static List<String> parseLinesFromFile(File f) throws IOException {
		List<String> flines = Files.readLines(f, Charsets.UTF_8);
		StringBuilder sb = new StringBuilder();
		for (String fline : flines) {
			sb.append(fline);
		}
		// System.out.println(sb.toString());

		// String source = parseLineFromString(sb.toString());
		// return Arrays.asList(source.split("\n"));
		return parseLineFromString(sb.toString());
	}

	public static ArrayList<String> parseLineFromString(String source) {
		ArrayList<ParseResult> codelines = (ArrayList<ParseResult>) parser
				.parse("java", source);
		ArrayList<String> lines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (ParseResult codepart : codelines) {
			String tag = codepart.getStyleKeysString();
			String content = source
					.substring(codepart.getOffset(),
							(codepart.getOffset() + codepart.getLength()))
					.replace("&", "&amp;").replace("<", "&lt;")
					.replace(">", "&gt;");

			String startTag = "<" + getExtendedTag(tag, content) + ">";
			String endTag = "</" + tag + ">";
			
			sb.append(startTag + content + endTag);
			
		}
		lines.add(sb.toString());
		// sb.append("\n");
		// return sb.toString();
		return lines;
	}

	public static String getExtendedTag(String tag, String text) {
		if (tag.equalsIgnoreCase("KWD")) {
			return tag + getExtendedKwdTag(tag, text);
		} else if (tag.equalsIgnoreCase("PUN")) {
			return tag + getExtendedPunTag(tag, text);
		} else
			return tag;

	}

	private static String getExtendedPunTag(String tag, String text) {
		String cls = "";
		if (text.contains(";"))
			cls += " endcommand";
		if (text.contains("{"))
			cls += " startblock";
		if (text.contains("}"))
			cls += " endblock ";

		return (!cls.equals("") ? " class=\"" + cls
				+ "\"".replace(" \\s+", "\\s") : "");
	}

	private static String getExtendedKwdTag(String tag, String text) {
		String cls = "";
		// Split contains 8 elements.
		if (text.contains("break"))
			cls += " flow break";
		if (text.contains("continue"))
			cls += " flow continue";
		if (text.contains("do"))
			cls += " flow do";
		if (text.contains("else"))
			cls += " flow else";
		if (text.contains("for"))
			cls += " flow for";
		if (text.contains("if"))
			cls += " flow if";
		if (text.contains("return"))
			cls += " flow return";
		if (text.contains("while"))
			cls += " flow while";
		// Split contains 25 elements.
		// if (text.contains("auto")) cls +=" c auto";
		if (text.contains("case"))
			cls += " flow case";
		if (text.contains("char"))
			cls += " type char";
		if (text.contains("const"))
			cls += " c const";
		if (text.contains("default"))
			cls += " c default";
		if (text.contains("double"))
			cls += " type double";
		if (text.contains("enum"))
			cls += " type enum";
		// if (text.contains("extern")) cls +=" c extern";
		if (text.contains("float"))
			cls += " type float";
		if (text.contains("goto"))
			cls += " flow goto";
		// if (text.contains("inline")) cls +=" c inline";
		if (text.contains("int"))
			cls += " type int";
		if (text.contains("long"))
			cls += " type long";
		// if (text.contains("register")) cls +=" c register";
		if (text.contains("short"))
			cls += " c short";
		// if (text.contains("signed")) cls +=" c signed";
		if (text.contains("sizeof"))
			cls += " c sizeof";
		if (text.contains("static"))
			cls += " modifier static";
		// if (text.contains("struct")) cls +=" c struct";
		if (text.contains("switch"))
			cls += " flow switch";
		// if (text.contains("typedef")) cls +=" c typedef";
		// if (text.contains("union")) cls +=" c union";
		// if (text.contains("unsigned")) cls +=" c unsigned";
		if (text.contains("void"))
			cls += " type void";
		if (text.contains("volatile"))
			cls += " modifier volatile";
		// Split contains 15 elements.
		if (text.contains("catch"))
			cls += " debug catch";
		if (text.contains("class"))
			cls += " type class";
		// if (text.contains("delete")) cls +=" common delete";
		if (text.contains("false"))
			cls += " boolean false";
		// if (text.contains("import")) cls +=" java import";
		if (text.contains("new"))
			cls += " def new";
		// if (text.contains("operator")) cls +=" common operator";
		if (text.contains("private"))
			cls += " access private";
		if (text.contains("protected"))
			cls += " access protected";
		if (text.contains("public"))
			cls += " access public";
		if (text.contains("this"))
			cls += " java this";
		if (text.contains("throw"))
			cls += " debug throw";
		if (text.contains("true"))
			cls += " boolean true";
		if (text.contains("try"))
			cls += " debug try";
		// if (text.contains("typeof")) cls +=" common typeof";
		// Split contains 19 elements.
		if (text.contains("abstract"))
			cls += " modifier abstract";
		if (text.contains("assert"))
			cls += " debug assert";
		if (text.contains("boolean"))
			cls += " type boolean";
		if (text.contains("byte"))
			cls += " type byte";
		if (text.contains("extends"))
			cls += " class extends";
		if (text.contains("final"))
			cls += " modifier final";
		if (text.contains("finally"))
			cls += " debug finally";
		if (text.contains("implements"))
			cls += " class implements";
		if (text.contains("import"))
			cls += " class import";
		if (text.contains("instanceof"))
			cls += " java instanceof";
		if (text.contains("interface"))
			cls += " class interface";
		if (text.contains("null"))
			cls += " null";
		if (text.contains("native"))
			cls += " modifier native";
		if (text.contains("package"))
			cls += " class package";
		if (text.contains("strictfp"))
			cls += " java strictfp";
		if (text.contains("super"))
			cls += " method super";
		if (text.contains("synchronized"))
			cls += " modifier synchronized";
		if (text.contains("throws"))
			cls += " debug throws";
		if (text.contains("transient"))
			cls += " modifier transient";
		return (!cls.equals("") ? " class=\"" + cls
				+ "\"".replace("\\s+", "\\s") : "");

	}
}
