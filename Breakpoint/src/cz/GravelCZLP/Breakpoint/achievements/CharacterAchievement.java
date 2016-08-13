package cz.GravelCZLP.Breakpoint.achievements;

import cz.GravelCZLP.Breakpoint.game.CharacterType;

public class CharacterAchievement extends Achievement
{
	private final CharacterType characterType;
	
	public CharacterAchievement(AchievementType type, CharacterType characterType, boolean achieved)
	{
		super(type, achieved);
		
		this.characterType = characterType;
	}
	
	@Override
	public String getName()
	{
		return super.getName() + '_' + characterType.name();
	}

	public CharacterType getCharacterType()
	{
		return characterType;
	}
}
