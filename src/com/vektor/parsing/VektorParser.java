package com.vektor.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vektor.classstructure.VektorSerialization.codeLine;
import com.vektor.classstructure.VektorSerialization.sourceCode;

public class VektorParser {

	private static String[] tags = { "KWD", "COM", "PLN", "PUN", "TYP", "STR",
			"LIT" };
	// private static Gson bigGson = new Gson();
	private static int openblocks = 0;
	private static int closedblocks = 0;
	private static int indent = 0;

	private static codeLine postParse(String source) {
		// System.out.println(source);
		// String src = ("<html><body>" + source + "</body></html>");
		// source = source.replace("\n", "<br />");
		// source = source.replace("\r", "");
		// source = source.replace("\t", "&nbsp;");
		// source = source.trim();
		int open = 0;
		int close = 0;
		boolean block = false;
		// int ind = indent;
		Document doc = Jsoup.parse(source, "", Parser.xmlParser());
		String tag = "PUN";
		// for (String tag : tags) {
		Elements els = doc.select(tag);
		for (int i = 0; i < els.size(); i++) {
			Element curr = els.get(i);
			// System.out.println("TXT "+curr.text());
			// curr.html(Palette.smartText(curr.text().trim(), curr.tagName()));
			// System.out.println("HTML "+curr.html());
			// curr.text(curr.text().replaceAll("\\s+",""));
			String cclass = curr.attr("class");
			if (cclass.contains("startblock")) {
				open++;
				block = true;
			}
			if (cclass.contains("endblock")) {
				open--;
				block = true;
			}

			// curr./*attr("color",Palette.getColorByTag(tag)).*/attr(
			// "class",
			// ((null != cclass && !"".equals(cclass)) ? " "
			// + curr.attr("class") : ""));

		}

		// }
		if (open > 0)
			indent++;
		else if (open < 0)
			indent--;
		// System.out.println("LVL:"+indent+" LINE:"+source);
		openblocks += open;
		// return
		// "<span>"+((block&&(open>0))?indent-1:indent)+"</span>"+doc.select("body").html();
		// System.out.println(indent);
		return new codeLine((block && (open > 0)) ? indent - 1 : indent, source);

	}

	public static ArrayList<codeLine> parseFile(String filepath)
			throws IOException {
		return parseFile(new File(filepath));
	}

	public static ArrayList<codeLine> parseFile(File file) throws IOException {
		List<String> lines = PreParser.parseLinesFromFile(file);
		openblocks = 0;
		closedblocks = 0;
		indent = 0;
		ArrayList<codeLine> pps = new ArrayList<codeLine>();
		for (String line : lines) {
			pps.add(postParse(line));
			// System.out.println(line +" "+ postParse(line));
		}
		return pps;
	}

	public static ArrayList<codeLine> parseSourceString(String code) {
		// Scanner srcScanner = new Scanner(code);
		// String [] lines = PreParser.parseLineFromString(code).split("\n");
		ArrayList<codeLine> pps = new ArrayList<codeLine>();
		for (String line : PreParser.parseLineFromString(code)) {
			pps.add(postParse(line));
		}
		// ArrayList<codeLine> pps = new ArrayList<codeLine>();
		// while (srcScanner.hasNextLine()) {
		// pps.add(postParse(PreParser.parseLineFromString(srcScanner
		// .nextLine())));
		// }
		return pps;
	}

	private static ArrayList<codeLine> parseString(String code) {
		ArrayList<codeLine> postparsedCode = new ArrayList<codeLine>();
		for (String line : PreParser.parseLineFromString(code)) {
			postparsedCode.add(postParse(line));
		}
		openblocks = 0;
		closedblocks = 0;
		indent = 0;

		return postparsedCode;
	}

	public static void printParsedCode(String filepath) throws IOException {
		printParsedCode(new File(filepath));
	}

	public static void printParsedCode(File file) throws IOException {
		List<codeLine> lines = parseFile(file);
		for (int i = 0; i < lines.size(); i++) {
			System.out.println(lines.get(i));
		}
	}

	public static String getCodeJson(String filepath) throws IOException {
		return getCodeJson(new File(filepath));
	}

	public static String getCodeJson(File file) throws IOException {
		ArrayList<codeLine> code = parseFile(file);
		sourceCode src = new sourceCode("OK", file.getName(), 1, code.size(),
				code);
		return new Gson().toJson(src);
	}

	public static sourceCode getSrcCode(String fname, String src) {
		ArrayList<codeLine> code = parseSourceString(src);
		return new sourceCode("OK", fname, 1, code.get(0).getCode()
				.split("\\r?\\n").length, code);
	}

	public static String getCodeJson(String filepath, int from, int to)
			throws IOException {
		return getCodeJson(new File(filepath), from, to);
	}

	public static String getCodeJson(File file, int from, int to)
			throws IOException {

		ArrayList<codeLine> code = parseFile(file);
		ArrayList<codeLine> sel = new ArrayList<codeLine>();
		if (from < 1 || from > code.size()) {
			return new Gson().toJson(new sourceCode(
					"Errore: range linee non valido!", "", -1, -1, null));
		}
		for (int i = (from - 1); i < (to); i++) {
			// jsonCode.addProperty("l" + (i + 1), code.get(i));
			sel.add(code.get(i));
		}
		return new Gson().toJson(new sourceCode("OK", file.getName(), from, to,
				sel));
	}

}
