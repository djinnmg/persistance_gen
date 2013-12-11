package matthew.main;

import com.peterphi.std.util.jaxb.JAXBSerialiser;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.ObjectFactory;
import matthew.velocity.codegen.Templater;
import matthew.velocity.codegen.VelocityMarshaller;
import matthew.velocity.types.VelocityEntitiesType;
import matthew.velocity.types.VelocityEntityType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;

public class TemplateProcessor
{
	private final String packagePath;
	private final String outputDir;
	private final String projectName;

	public TemplateProcessor(final String packagePath, final String outputDir, final String projectName)
	{
		this.packagePath = packagePath;
		this.projectName = projectName.replace(" ", "");
		this.outputDir = outputDir;
	}

	public void Call()
	{
		JAXBSerialiser serialiser = JAXBSerialiser.getInstance(ObjectFactory.class);

		// load xml file
		// deserialise
		EntitiesType entitiesType = (
				(JAXBElement<EntitiesType>) serialiser
						.deserialise(getClass().getResourceAsStream("/DataModel/DataModel.xml"))).getValue();


		VelocityMarshaller velocityMarshaller = new VelocityMarshaller(entitiesType);
		VelocityEntitiesType entities = velocityMarshaller.marshall(packagePath);

		createPom();

		for (VelocityEntityType entity : entities.getEntities())
		{
			createEntities(entity);

			createDaos(entity, entities);

			createDaoImpls(entity, entities);

			createJaxbTypes(entity);
		}
		createMarshaller(entities);

		createUnmarshaller(entities);

	}

	private void createPom()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/PomTemplate.vm");

		entityTemplater.put("groupId", projectName);
		entityTemplater.put("artifactId", projectName);

		outputToFile("pom.xml", entityTemplater.process());

	}

	private void createEntities(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/EntityTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("at", "@");

		outputToFile(getPackageFilePath("/hibernate/entities/" + entity.getName() + ".java"),
		             entityTemplater.process());
	}

	private void createDaos(final VelocityEntityType entity, final VelocityEntitiesType entityTypeList)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("entityTypeList", entityTypeList);

		outputToFile(getPackageFilePath("/hibernate/dao/" + entity.getName() + "Dao.java"), entityTemplater.process());
	}

	private void createDaoImpls(final VelocityEntityType entity, final VelocityEntitiesType entityTypeList)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("entityTypeList", entityTypeList);

		outputToFile(getPackageFilePath("/hibernate/dao/impl/" + entity.getName() + "DaoImpl.java"),
		             entityTemplater.process());
	}

	private void createJaxbTypes(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/JaxbTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/types/" + entity.getName() + "Type.java"), entityTemplater.process());
	}

	private void createMarshaller(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/MarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/serialisation/Marshaller.java"), entityTemplater.process());
	}

	private void createUnmarshaller(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/UnmarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/serialisation/Unmarshaller.java"), entityTemplater.process());
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

	private String getPackageFilePath(final String filePath)
	{
		return "src/main/java/" + packagePath.replace(".", "/") + filePath;
	}

	private void outputToFile(final String filePath, final String fileContents)
	{
		try
		{
			File file = new File(outputDir + "/" + projectName + "/" + filePath);

			if (file.exists())
			{
				file.delete();
			}

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
