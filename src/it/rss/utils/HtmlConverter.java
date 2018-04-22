package it.rss.utils;

public class HtmlConverter
{	
	/***
	 * Removes all escape characters from a string
	 * @param string - Any string that may contain the &# combo for escape characters
	 * @return - A new string with the unicode replacements
	 */
	public static String removeEscapeChar(String string)	{
		int lastIndex = 0;
		while (string.contains("&#"))
		{
			//Get the escape character index
			int startIndex = string.indexOf("&#", lastIndex);
			int endIndex = string.indexOf(";", startIndex);

			//and rip the sucker out of the string
			String escapeChar = string.substring(startIndex, endIndex);

			//Get the unicode representation and replace all occurrences in the string
			String replacementChar = convertEscapeChar(escapeChar);
			string = string.replaceAll(escapeChar + ";", replacementChar);			
			lastIndex = endIndex;
		}
		return string;
	}
	
	/***
	 * Given an escape character (&#<digits>), return the represented value
	 * @param escapeCharacter - String with the format &#<digits>
	 * @return - the represented value.
	 * Luck for us, java uses the same unicode conversion as HTML.
	 * Basically this rips out the number and converts it into the character it maps to
	 */
	private static String convertEscapeChar(String escapeCharacter)	{
		String charCode = escapeCharacter.substring(escapeCharacter.indexOf("#")+1);
		return "" + (char) Integer.parseInt(charCode);
	}

}
