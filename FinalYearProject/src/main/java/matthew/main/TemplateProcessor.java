package matthew.main;

import com.peterphi.std.util.jaxb.JAXBSerialiser;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.ObjectFactory;
import matthew.velocity.codegen.Templater;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;

public class TemplateProcessor
{
	private final String packagePath;

	public TemplateProcessor(final String packagePath)
	{
		this.packagePath = packagePath;
	}

	public void Call()
	{
		JAXBSerialiser serialiser = JAXBSerialiser.getInstance(ObjectFactory.class);

		// load xml file
		// deserialise
		EntitiesType entities = (
				(JAXBElement<EntitiesType>) serialiser
						.deserialise(getClass().getResourceAsStream("/DataModel/DataModel.xml"))).getValue();

		for (EntityType entity : entities.getEntity())
		{
			createEntities(entity);

			createDaos(entity, entities);

			createDaoImpls(entity, entities);

			createJaxbTypes(entity);
		}
		createMarshaller(entities);

		createUnmarshaller(entities);


	}

	private void createEntities(final EntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/EntityTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile("/hibernate/entities/" + entity.getName() + ".java", entityTemplater.process());
	}

	private void createDaos(final EntityType entity, final EntitiesType entityTypeList)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("entityTypeList", entityTypeList);

		outputToFile("/hibernate/dao/" + entity.getName() + "Dao.java", entityTemplater.process());
	}

	private void createDaoImpls(final EntityType entity, final EntitiesType entityTypeList)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("entityTypeList", entityTypeList);

		outputToFile("/hibernate/dao/impl/" + entity.getName() + "DaoImpl.java", entityTemplater.process());
	}

	private void createJaxbTypes(final EntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/JaxbTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile("/jaxb/types/" + entity.getName() + "Type.java", entityTemplater.process());
	}

	private void createMarshaller(final EntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/MarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile("/jaxb/serialisation/Marshaller.java", entityTemplater.process());
	}

	private void createUnmarshaller(final EntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/UnmarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile("/jaxb/serialisation/Unmarshaller.java", entityTemplater.process());
	}

	private Templater getTemplater(final String templatePath)
	{
		try
		{
			String entityTemplate =
					IOUtils.toString(getClass().getResourceAsStream(templatePath),
					                 "UTF-8"); // entity template

			return new Templater(entityTemplate);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void outputToFile(final String filePath, final String fileContents)
	{
		try
		{
			File file = new File("src/main/java/" + packagePath.replace(".", "/") + filePath);

			file.getParentFile().mkdirs();


			System.out.println("Trying to output file to " + file.getAbsolutePath());

			FileUtils.writeStringToFile(file, fileContents);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}


}
