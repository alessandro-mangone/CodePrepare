package com.vektor.parsing;

//Comment
//Comment <b>
public class Palette {
	public static final String comColor = "#969896";
	public static final String kwdColor = "#b294bb";
	public static final String punColor = "#c6c8c6";
	public static final String strColor = "#b5bd68";
	public static final String typColor ="#81a2be";
	public static final String plnColor ="#c5c8c6";
	public static final String litColor ="#de935f";
	public static final String boldStart = "<b>";
	public static final String boldEnd = "</b>";
	public static final String italicStart ="<i>";
	public static final String italicEnd ="</i>";
	public static final String fontcolorStart ="<font color=\"[%D]\">";
	public static final String fontcolorEnd ="</font>";
	private static String boldText(String text){
		StringBuilder sb = new StringBuilder()
		.append(boldStart)
		.append(text)
		.append(boldEnd);
		return sb.toString();
	}
	private static String italicText(String text){
		StringBuilder sb = new StringBuilder()
		.append(italicStart)
		.append(text)
		.append(italicEnd);
		return sb.toString();
	}
	private static String fontColorize(String text, String tag){
		StringBuilder sb = new StringBuilder()
		.append(fontcolorStart.replace("[%D]", getColorByTag(tag)))
		.append(text)
		.append(fontcolorEnd);
		return sb.toString();
	}
	public static String getColorByTag(String tag){
		if(tag.equalsIgnoreCase("COM")) return comColor;
		else if(tag.equalsIgnoreCase("KWD")) return kwdColor;
		else if(tag.equalsIgnoreCase("PUN")) return punColor;
		else if(tag.equalsIgnoreCase("STR")) return strColor;
		else if(tag.equalsIgnoreCase("TYP")) return typColor;
		else if(tag.equalsIgnoreCase("PLN")) return plnColor;
		else if(tag.equalsIgnoreCase("LIT")) return litColor;
		else return plnColor;
	}
	public static String smartText(String text, String tag) {
		
		//text = StringEscapeUtils.escapeHtml4(text);
		text = text.replaceAll("\"", "&quot;");
		
		//System.out.println("Tag "+tag+" TEXT "+text);
		if(tag.equalsIgnoreCase("COM")) return italicText(text);
		else if(tag.equalsIgnoreCase("KWD")) return boldText(text);
		else if(tag.equalsIgnoreCase("PUN")) return text;
		else if(tag.equalsIgnoreCase("STR")) return text;
		else if(tag.equalsIgnoreCase("TYP")) return boldText(text);
		else if(tag.equalsIgnoreCase("PLN")) return text;
		else if(tag.equalsIgnoreCase("LIT")) return text;
		else return "";
	}
	
}
