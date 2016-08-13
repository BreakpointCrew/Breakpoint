package cz.GravelCZLP.Breakpoint.achievements;

import cz.GravelCZLP.Breakpoint.language.Translateable;
import cz.GravelCZLP.Breakpoint.language.Translation;


public class AchievementTranslation extends Translation
{
	private final String name, desc;
	
	public AchievementTranslation(Translateable key, String name, String desc)
	{
		super(key);
		this.name = name;
		this.desc = desc.replace("\\n", "\n");
	}
	
	public String getName(Object... values)
	{
		String filled = name;
		
		for(int i = 0; i < values.length; i++)
			filled = filled.replace("{" + (i + 1) + "}", values[i].toString());
		
		return filled;
	}

	public String getDesc(Object... values)
	{
		String filled = desc;
		
		for(int i = 0; i < values.length; i++)
			filled = filled.replace("{" + (i + 1) + "}", values[i].toString());
		
		return filled;
	}

	public static enum TranslationPart
	{
		NAME(".name"), DESCRIPTION(".desc");
		
		private final String suffix;
		
		private TranslationPart(String suffix)
		{
			this.suffix = suffix;
		}

		public String getSuffix()
		{
			return suffix;
		}
	}
}
