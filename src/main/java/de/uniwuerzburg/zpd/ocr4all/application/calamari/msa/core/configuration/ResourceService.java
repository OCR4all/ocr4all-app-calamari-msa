/**
 * File:     ResourceService.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     17.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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

}
