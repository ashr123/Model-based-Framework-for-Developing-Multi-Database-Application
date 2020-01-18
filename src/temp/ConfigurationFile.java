package Temp;

import Temp.ConfigurationLine;

import java.util.ArrayList;

public class ConfigurationFile
{
	final ArrayList<ConfigurationLine> configurationLines;

	public ConfigurationFile(ArrayList<ConfigurationLine> configurationLines)
	{
		this.configurationLines = configurationLines;
	}

	public ArrayList<ConfigurationLine> getConfigurationLines()
	{
		return configurationLines;
	}
}
