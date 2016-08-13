package cz.GravelCZLP.Breakpoint.language;


public interface Translateable
{
	public String getDefaultTranslation();
	public String getYamlPath();
	public void setTranslation(Translation translation);
	public Translation getTranslation();
}
