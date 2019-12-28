import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		JCodeModel codeModel = new JCodeModel();

//		URL source = Main.class.getResource("/schema/address.schema.json");

		GenerationConfig config = new DefaultGenerationConfig()
		{
			@Override
			public boolean isGenerateBuilders()
			{ // set config option by overriding method
				return true;
			}
		};


//		SchemaMapper mappeAddress = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()),
//				new SchemaGenerator());
//		mappeAddress.generate(codeModel, "", "", Main.class.getResource("/schema/Address.json"));
//		codeModel.build(Files.createTempDirectory("required").toFile());
//
//		SchemaMapper mapperPerson = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()),
//				new SchemaGenerator());
//		mapperPerson.generate(codeModel, "", "", Main.class.getResource("/schema/Person.json"));
//		codeModel.build(Files.createTempDirectory("required").toFile());
//
//		SchemaMapper mapperProfessor = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()),
//				new SchemaGenerator());
//		mapperProfessor.generate(codeModel, "", "", Main.class.getResource("/schema/Professor.json"));
//		codeModel.build(Files.createTempDirectory("required").toFile());
//
//		SchemaMapper mapperStudent = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()),
//				new SchemaGenerator());
//		mapperStudent.generate(codeModel, "", "", Main.class.getResource("/schema/Student.json"));
//		codeModel.build(Files.createTempDirectory("required").toFile());
	}
}
