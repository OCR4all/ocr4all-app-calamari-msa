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
	 * Defines model types.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	private enum ModelType {
		evaluation, recognition, training;

		/**
		 * The folder.
		 */
		private static final String folder = "models/";

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
	 * Defines argument mapping types.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	private enum ArgumentMappingType {
		evaluation, recognition, training;

		/**
		 * The folder.
		 */
		private static final String folder = ModelType.folder;

		/**
		 * The suffix.
		 */
		private static final String suffix = "-mapping" + ModelType.suffix;

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
		 * @return An input stream for reading the resource. Null if the resource could
		 *         not be found.
		 * @since 17
		 */
		public InputStream getResourceAsStream() {
			return ResourceService.getResourceAsStream(getResourceName());
		}
	}

	/**
	 * The evaluation model.
	 */
	private final Model evaluation;

	/**
	 * The evaluation argument mappings.
	 */
	private final Hashtable<String, List<String>> evaluationMappings;

	/**
	 * The recognition model.
	 */
	private final Model recognition;

	/**
	 * The recognition model mappings.
	 */
	private final Hashtable<String, List<String>> recognitionMappings;

	/**
	 * The training model.
	 */
	private final Model training;

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
		evaluation = load(objectMapper, ModelType.evaluation);
		recognition = load(objectMapper, ModelType.recognition);
		training = load(objectMapper, ModelType.training);

		// Load the argument mappings
		evaluationMappings = load(objectMapper, ArgumentMappingType.evaluation);
		recognitionMappings = load(objectMapper, ArgumentMappingType.recognition);
		trainingMappings = load(objectMapper, ArgumentMappingType.training);
	}

	/**
	 * Loads the model.
	 * 
	 * @param objectMapper The JSON object mapper.
	 * @param type         The model type.
	 * @return The model. Null on troubles.
	 * @since 17
	 */
	private static Model load(ObjectMapper objectMapper, ModelType type) {
		try {
			return objectMapper.readValue(type.getResourceAsStream(), Model.class);
		} catch (Exception e) {
			logger.error("can not load resource " + type.getResourceName() + " - " + e.getMessage());

			return null;
		}

	}

	/**
	 * Loads the model mapping.
	 * 
	 * @param objectMapper The JSON object mapper.
	 * @param type         The model mapping type.
	 * @return The model. Null if not available.
	 * @since 17
	 */
	private static Hashtable<String, List<String>> load(ObjectMapper objectMapper, ArgumentMappingType type) {
		Hashtable<String, List<String>> mapping = new Hashtable<>();

		InputStream inputStream = type.getResourceAsStream();
		if (inputStream != null)
			try {
				ModelMapping model = objectMapper.readValue(type.getResourceAsStream(), ModelMapping.class);

				for (ModelMapping.Mapping map : model.getMappings())
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

			} catch (Exception e) {
				logger.error("can not load resource " + type.getResourceName() + " - " + e.getMessage());
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
	 * Returns the evaluation model.
	 *
	 * @return The evaluation model. Null if not available.
	 * @since 17
	 */
	public Model getEvaluationModel() {
		return evaluation;
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
	 * Returns the recognition model.
	 *
	 * @return The recognition model. Null if not available.
	 * @since 17
	 */
	public Model getRecognitionModel() {
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
	 * Returns the training model.
	 *
	 * @return The training model. Null if not available.
	 * @since 17
	 */
	public Model getTrainingModel() {
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
	 * Defines model mappings.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class ModelMapping implements Serializable {
		/**
		 * The serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The mappings.
		 */
		private List<Mapping> mappings;

		/**
		 * Returns the mappings.
		 *
		 * @return The mappings.
		 * @since 17
		 */
		public List<Mapping> getMappings() {
			return mappings;
		}

		/**
		 * Set the mappings.
		 *
		 * @param mappings The mappings to set.
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
