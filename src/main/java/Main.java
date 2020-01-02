import dataLayer.configReader.Conf;
import dataLayer.configReader.Reader;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Conf configuration = Reader.read();
	}
}
