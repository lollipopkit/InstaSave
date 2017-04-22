package fjy.ins;

import java.util.regex.*;

public class Praser
{
	public static String RegexString(String targetStr, String patternStr)
	{
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		while(matcher.find()){
			return matcher.group();
		}
		return "Nothing Found!";
	}
}
