package matthew.main;

public class Main
{

	public static void main(String[] args)
	{
		TemplateProcessor templateProcessor = new TemplateProcessor("matthew.generation", "/home/matthew/Code", "Test2", "/DataModel/DataModel2.xml");

		templateProcessor.call();
	}
}

