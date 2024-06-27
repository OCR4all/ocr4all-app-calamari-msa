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
	 * Defines description types.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	private enum DescriptionType {
		evaluation, recognition, training;

		/**
		 * The folder.
		 */
		private static final String folder = "descriptions/";

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
	 * The evaluation model.
	 */
	private final Description evaluation;

	/**
	 * The evaluation argument mappings.
	 */
	private final Hashtable<String, List<String>> evaluationMappings;

	/**
	 * The recognition model.
	 */
	private final Description recognition;

	/**
	 * The recognition model mappings.
	 */
	private final Hashtable<String, List<String>> recognitionMappings;

	/**
	 * The training model.
	 */
	private final Description training;

	/**
	 * The training model mappings.
	 */
	private final Hashtable<String, List<String>> trainingMappings;

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
		evaluation = load(objectMapper, DescriptionType.evaluation);
		recognition = load(objectMapper, DescriptionType.recognition);
		training = load(objectMapper, DescriptionType.training);

		// Load the argument mappings
		evaluationMappings = getMappings(evaluation, DescriptionType.evaluation);
		recognitionMappings = getMappings(recognition, DescriptionType.recognition);
		trainingMappings = getMappings(training, DescriptionType.training);
	}

	/**
	 * Loads the description.
	 * 
	 * @param objectMapper The JSON object mapper.
	 * @param type         The description type.
	 * @return The model. Null on troubles.
	 * @since 17
	 */
	private static Description load(ObjectMapper objectMapper, DescriptionType type) {
		try {
			return objectMapper.readValue(type.getResourceAsStream(), Description.class);
		} catch (Exception e) {
			logger.error("can not load resource " + type.getResourceName() + " - " + e.getMessage());

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
	private static Hashtable<String, List<String>> getMappings(Description description, DescriptionType type) {
		Hashtable<String, List<String>> mapping = new Hashtable<>();

		if (description != null && description.getMappings() != null) {
			for (Description.Mapping map : description.getMappings())
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
	public Description getEvaluation() {
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
		return mapArguments(evaluationMappings, arguments);
	}

	/**
	 * Returns the recognition description.
	 *
	 * @return The recognition description. Null if not available.
	 * @since 17
	 */
	public Description getRecognition() {
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
		return mapArguments(recognitionMappings, arguments);
	}

	/**
	 * Returns the training description.
	 *
	 * @return The training description. Null if not available.
	 * @since 17
	 */
	public Description getTraining() {
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
		return mapArguments(trainingMappings, arguments);
	}

	/**
	 * Defines descriptions.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class Description {
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

}
