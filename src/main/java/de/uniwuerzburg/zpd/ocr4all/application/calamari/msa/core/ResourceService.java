/**
 * File:     ResourceService.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     17.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.model.Model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Defines resource services.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@Service
@ApplicationScope
public class ResourceService {
	/**
	 * The logger.
	 */
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceService.class);

	/**
	 * Defines configuration types.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	private enum Type {
		evaluation, recognition, training;

		/**
		 * The folder.
		 */
		private static final String folder = "configurations/";

		/**
		 * The suffix.
		 */
		private static final String suffix = ".json";

		/**
		 * Returns the resource name.
		 * 
		 * @return The resource name.
		 * @since 1.8
		 */
		public String getResourceName() {
			return folder + this.name() + suffix;
		}

		/**
		 * Returns an input stream for reading the resource.
		 * 
		 * @return An input stream for reading the resource.
		 * @throws IllegalArgumentException Throws if the resource could not be found.
		 * @since 17
		 */
		public InputStream getResourceAsStream() throws IllegalArgumentException {
			InputStream inputStream = ResourceService.getResourceAsStream(getResourceName());

			if (inputStream == null)
				throw new IllegalArgumentException("resource not found: " + getResourceName());
			else
				return inputStream;
		}
	}

	/**
	 * The evaluation configuration.
	 */
	private final Evaluation evaluation;

	/**
	 * The recognition configuration.
	 */
	private final Recognition recognition;

	/**
	 * The training configuration.
	 */
	private final Training training;

	/**
	 * The model mappings.
	 */
	private final Hashtable<Type, Hashtable<String, List<String>>> mappings = new Hashtable<>();

	/**
	 * Creates a resource service.
	 * 
	 * @since 17
	 */
	public ResourceService() {
		super();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// Load the models
		evaluation = load(Evaluation.class, objectMapper, Type.evaluation);
		recognition = load(Recognition.class, objectMapper, Type.recognition);
		training = load(Training.class, objectMapper, Type.training);

		// Load the argument mappings
		mappings.put(Type.evaluation, getMappings(evaluation, Type.evaluation));
		mappings.put(Type.recognition, getMappings(recognition, Type.recognition));
		mappings.put(Type.training, getMappings(training, Type.training));
	}

	/**
	 * Loads the configuration.
	 * 
	 * @param clazz        The configuration class type.
	 * @param objectMapper The JSON object mapper.
	 * @param type         The configuration type.
	 * @return The model. Null on troubles.
	 * @since 17
	 */
	private static <T extends Configuration> T load(Class<T> clazz, ObjectMapper objectMapper, Type type) {
		try {
			return objectMapper.readValue(type.getResourceAsStream(), clazz);
		} catch (Exception e) {
			logger.error("can not load resource " + clazz.getName().toLowerCase() + " - " + e.getMessage());

			return null;
		}
	}

	/**
	 * Returns the model mappings.
	 * 
	 * @param description The description.
	 * @param type        The description type.
	 * @return The model mappings.
	 * @since 17
	 */
	private static Hashtable<String, List<String>> getMappings(Configuration description, Type type) {
		Hashtable<String, List<String>> mapping = new Hashtable<>();

		if (description != null && description.getMappings() != null) {
			for (Configuration.Mapping map : description.getMappings())
				if (map.getArgument() != null) {
					if (mapping.containsKey(map.getArgument()))
						logger.warn("ambiguous argument '" + map.getArgument() + "' on resource "
								+ type.getResourceName() + ".");
					else {
						List<String> values = new ArrayList<>();
						if (map.getValues() != null)
							for (String value : map.getValues())
								if (value != null)
									values.add(value);

						mapping.put(map.getArgument(), values);
					}
				}

		}

		return mapping;
	}

	/**
	 * Returns an input stream for reading the specified resource.
	 * 
	 * @param name The resource name.
	 * @return An input stream for reading the specified resource. Null if the
	 *         resource could not be found.
	 * @since 1.8
	 */
	private static InputStream getResourceAsStream(String name) {
		return ResourceService.class.getClassLoader().getResourceAsStream(name);
	}

	/**
	 * Maps the arguments.
	 *
	 * @param map       The mapping.
	 * @param arguments The arguments to map.
	 * @return The mapped arguments.
	 * @since 17
	 */
	private List<String> mapArguments(Hashtable<String, List<String>> map, List<String> arguments) {
		if (arguments == null || map.isEmpty())
			return arguments;
		else {
			List<String> values = new ArrayList<>();
			for (String argument : arguments)
				if (argument != null && map.containsKey(argument))
					values.addAll(map.get(argument));
				else
					values.add(argument);

			return values;
		}
	}

	/**
	 * Returns the evaluation description.
	 *
	 * @return The evaluation description. Null if not available.
	 * @since 17
	 */
	public Configuration getEvaluation() {
		return evaluation;
	}

	/**
	 * Maps the evaluation arguments.
	 *
	 * @param arguments The arguments to map.
	 * @return The mapped arguments.
	 * @since 17
	 */
	public List<String> mapEvaluationArguments(List<String> arguments) {
		return mapArguments(mappings.get(Type.evaluation), arguments);
	}

	/**
	 * Returns the recognition description.
	 *
	 * @return The recognition description. Null if not available.
	 * @since 17
	 */
	public Configuration getRecognition() {
		return recognition;
	}

	/**
	 * Maps the recognition arguments.
	 *
	 * @param arguments The arguments to map.
	 * @return The mapped arguments.
	 * @since 17
	 */
	public List<String> mapRecognitionArguments(List<String> arguments) {
		return mapArguments(mappings.get(Type.recognition), arguments);
	}

	/**
	 * Returns the training description.
	 *
	 * @return The training description. Null if not available.
	 * @since 17
	 */
	public Configuration getTraining() {
		return training;
	}

	/**
	 * Maps the training arguments.
	 *
	 * @param arguments The arguments to map.
	 * @return The mapped arguments.
	 * @since 17
	 */
	public List<String> mapTrainingArguments(List<String> arguments) {
		return mapArguments(mappings.get(Type.training), arguments);
	}

	/**
	 * Defines configurations.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class Configuration {
		/**
		 * The service provider description.
		 */
		@NotBlank
		private String description;

		/**
		 * The service provider categories.
		 */
		private List<String> categories = new ArrayList<>();

		/**
		 * The service provider steps.
		 */
		private List<String> steps = new ArrayList<>();

		/**
		 * The model.
		 */
		@NotNull
		private Model model;

		/**
		 * The model mappings.
		 */
		private List<Mapping> mappings;

		/**
		 * Returns the description.
		 *
		 * @return The description.
		 * @since 17
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Set the description.
		 *
		 * @param description The description to set.
		 * @since 17
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * Returns the categories.
		 *
		 * @return The categories.
		 * @since 17
		 */
		public List<String> getCategories() {
			return categories;
		}

		/**
		 * Set the categories.
		 *
		 * @param categories The categories to set.
		 * @since 17
		 */
		public void setCategories(List<String> categories) {
			this.categories = categories;
		}

		/**
		 * Returns the steps.
		 *
		 * @return The steps.
		 * @since 17
		 */
		public List<String> getSteps() {
			return steps;
		}

		/**
		 * Set the steps.
		 *
		 * @param steps The steps to set.
		 * @since 17
		 */
		public void setSteps(List<String> steps) {
			this.steps = steps;
		}

		/**
		 * Returns the model.
		 *
		 * @return The model.
		 * @since 17
		 */
		public Model getModel() {
			return model;
		}

		/**
		 * Set the model.
		 *
		 * @param model The model to set.
		 * @since 17
		 */
		public void setModel(Model model) {
			this.model = model;
		}

		/**
		 * Returns the model mappings.
		 *
		 * @return The model mappings.
		 * @since 17
		 */
		public List<Mapping> getMappings() {
			return mappings;
		}

		/**
		 * Set the model mappings.
		 *
		 * @param mappings The model mappings to set.
		 * @since 17
		 */
		public void setMappings(List<Mapping> mappings) {
			this.mappings = mappings;
		}

		/**
		 * Defines model mappings.
		 *
		 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
		 * @version 1.0
		 * @since 17
		 */
		public static class Mapping implements Serializable {
			/**
			 * The serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * The argument.
			 */
			private String argument;

			/**
			 * The values.
			 */
			private List<String> values;

			/**
			 * Returns the argument.
			 *
			 * @return The argument.
			 * @since 17
			 */
			public String getArgument() {
				return argument;
			}

			/**
			 * Set the argument.
			 *
			 * @param argument The argument to set.
			 * @since 17
			 */
			public void setArgument(String argument) {
				this.argument = argument;
			}

			/**
			 * Returns the values.
			 *
			 * @return The values.
			 * @since 17
			 */
			public List<String> getValues() {
				return values;
			}

			/**
			 * Set the values.
			 *
			 * @param values The values to set.
			 * @since 17
			 */
			public void setValues(List<String> values) {
				this.values = values;
			}

		}
	}

	/**
	 * Defines evaluations.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class Evaluation extends Configuration {
	}

	/**
	 * Defines recognitions.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class Recognition extends Configuration {
		/**
		 * The dataset arguments.
		 */
		@NotBlank
		@JsonProperty("dataset-arguments")
		private DatasetArguments datasetArguments;

		/**
		 * Returns the dataset arguments.
		 *
		 * @return The dataset arguments.
		 * @since 17
		 */
		public DatasetArguments getDatasetArguments() {
			return datasetArguments;
		}

		/**
		 * Set the dataset arguments.
		 *
		 * @param datasetArguments The dataset arguments to set.
		 * @since 17
		 */
		public void setDatasetArguments(DatasetArguments datasetArguments) {
			this.datasetArguments = datasetArguments;
		}

		/**
		 * Defines dataset arguments.
		 *
		 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
		 * @version 1.0
		 * @since 17
		 */
		public static class DatasetArguments extends Configuration {
			/**
			 * The xml file name.
			 */
			@NotBlank
			@JsonProperty("xml-file")
			private String xmlFile;

			/**
			 * The image file name.
			 */
			@NotBlank
			@JsonProperty("image-file")
			private String imageFile;

			/**
			 * Returns the xml file name.
			 *
			 * @return The xml file name.
			 * @since 17
			 */
			public String getXmlFile() {
				return xmlFile;
			}

			/**
			 * Set the xml file name.
			 *
			 * @param xmlFile The xml file name to set.
			 * @since 17
			 */
			public void setXmlFile(String xmlFile) {
				this.xmlFile = xmlFile;
			}

			/**
			 * Returns the image file name.
			 *
			 * @return The image file name.
			 * @since 17
			 */
			public String getImageFile() {
				return imageFile;
			}

			/**
			 * Set the image file name.
			 *
			 * @param imageFile The image file name to set.
			 * @since 17
			 */
			public void setImageFile(String imageFile) {
				this.imageFile = imageFile;
			}

		}

	}

	/**
	 * Defines trainings.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class Training extends Configuration {
	}

}
