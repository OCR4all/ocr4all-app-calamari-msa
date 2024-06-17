/**
 * File:     ResourceService.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     17.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core;

import java.io.InputStream;

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
	 * @since 1.8
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
			return ResourceService.getResourceAsStream(getResourceName());
		}
	}

	/**
	 * The evaluation model.
	 */
	private final Model evaluation;

	/**
	 * The recognition model.
	 */
	private final Model recognition;

	/**
	 * The training model.
	 */
	private final Model training;

	/**
	 * Creates a resource service.
	 * 
	 * @since 17
	 */
	public ResourceService() {
		super();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		evaluation = load(objectMapper, ModelType.evaluation);
		recognition = load(objectMapper, ModelType.recognition);
		training = load(objectMapper, ModelType.training);
	}

	/**
	 * Loads the model.
	 * 
	 * @param objectMapper The JSON object mapper.
	 * @param type         The model type.
	 * @return The model.
	 * @since 17
	 */
	private Model load(ObjectMapper objectMapper, ModelType type) {
		try {
			return objectMapper.readValue(type.getResourceAsStream(), Model.class);
		} catch (Exception e) {
			logger.error("can not load resource " + type.getResourceName() + " - " + e.getMessage());

			return null;
		}

	}

	/**
	 * Returns an input stream for reading the specified resource.
	 * 
	 * @param name The resource name.
	 * @return An input stream for reading the specified resource.
	 * @throws IllegalArgumentException Throws if the resource could not be found.
	 * @since 1.8
	 */
	private static InputStream getResourceAsStream(String name) throws IllegalArgumentException {
		ClassLoader classLoader = ResourceService.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(name);

		if (inputStream == null) {
			throw new IllegalArgumentException("resource not found: " + name);
		} else {
			return inputStream;
		}
	}

	/**
	 * Returns the evaluation model.
	 *
	 * @return The evaluation model.
	 * @since 17
	 */
	public Model getEvaluation() {
		return evaluation;
	}

	/**
	 * Returns the recognition model.
	 *
	 * @return The recognition model.
	 * @since 17
	 */
	public Model getRecognition() {
		return recognition;
	}

	/**
	 * Returns the training model.
	 *
	 * @return The training model.
	 * @since 17
	 */
	public Model getTraining() {
		return training;
	}

}
