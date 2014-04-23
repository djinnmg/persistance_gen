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
import java.io.InputStream;

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

	public void call()
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
		createWebXML();
		createServiceProperties();
		createHibernateProperties();
		createLoggingProperties();

		for (VelocityEntityType entity : entities.getEntities())
		{
			createEntities(entity);
			createDaos(entity, entities);
			createDaoImpls(entity, entities);
			createJaxbTypes(entity);
			createRESTServices(entity);
			createRESTServiceImpls(entity);
			createThymeleafCreateEntityPage(entity);
			createThymeleafEntitiesPage(entity);
			createThymeleafEntityPage(entity);
			createThymeleafUpdateEntityPage(entity);
		}

		createMarshaller(entities);
		createUnmarshaller(entities);
		createRESTModule(entities);
		createHibernateModule(entities);
		createGuiceSetup();
		createDateHelper();

		createIndexRESTService();
		createIndexRESTServiceImpl();
		createThymeleafIndexPage(entities);
	}

	private void createPom()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/PomTemplate.vm");

		entityTemplater.put("groupId", projectName);
		entityTemplater.put("artifactId", projectName);

		outputToFile("pom.xml", entityTemplater.process());
	}


	private void createWebXML()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/WebXMLTemplate.vm");

		entityTemplater.put("displayName", projectName);

		outputToFile("src/main/webapp/WEB-INF/web.xml", entityTemplater.process());
	}


	private void createServiceProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ServicePropertiesTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile("src/main/resources/service.properties", entityTemplater.process());
	}

	private void createHibernateProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/HibernatePropertiesTemplate.vm");

		outputToFile("src/main/resources/hibernate.properties", entityTemplater.process());
	}

	private void createLoggingProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/Log4jPropertiesTemplate.vm");

		outputToFile("src/main/resources/log4j.properties", entityTemplater.process());
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

	private void createRESTServices(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/" + entity.getName() + "Service.java"),
		             entityTemplater.process());
	}


	private void createRESTServiceImpls(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/" + entity.getName() + "ServiceImpl.java"),
		             entityTemplater.process());
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


	private void createHibernateModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceHibernateModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/DBModule.java"), entityTemplater.process());
	}

	private void createRESTModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceRESTModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/RESTModule.java"), entityTemplater.process());
	}

	private void createGuiceSetup()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceSetupTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/setup/Setup.java"), entityTemplater.process());
	}

	private void createDateHelper()
	{
		Templater templater = getTemplater("/VelocityTemplates/DateHelperTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/util/DateHelper.java"), templater.process());
	}

	private void createIndexRESTService()
	{
		Templater templater = getTemplater("/VelocityTemplates/RESTIndexServiceTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/IndexService.java"), templater.process());
	}

	private void createIndexRESTServiceImpl()
	{
		Templater templater = getTemplater("/VelocityTemplates/RESTIndexServiceImplTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/IndexServiceImpl.java"), templater.process());
	}

	private void createThymeleafIndexPage(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafIndexTemplate.vm");

		entityTemplater.put("entities", entities);

		outputToFile("src/main/webapp/WEB-INF/template/Index.html", entityTemplater.process());
	}

	private void createThymeleafCreateEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafCreateEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/Create" + entity.getName() + "Template.html",
		             entityTemplater.process());
	}

	private void createThymeleafEntitiesPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafEntitiesTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/" + entity.getName() + "ListTemplate.html",
		             entityTemplater.process());
	}

	private void createThymeleafEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/" + entity.getName() + "Template.html",
		             entityTemplater.process());
	}

	private void createThymeleafUpdateEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafUpdateEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/Update" + entity.getName() + "Template.html",
		             entityTemplater.process());
	}

	private Templater getTemplater(final String templatePath)
	{
		try
		{
			// TODO check file exists before trying to open
			InputStream is = getClass().getResourceAsStream(templatePath);

			if (is == null)
			{
				throw new RuntimeException("Could not read file at resources" + templatePath);
			}

			String entityTemplate = IOUtils.toString(is, "UTF-8");

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
