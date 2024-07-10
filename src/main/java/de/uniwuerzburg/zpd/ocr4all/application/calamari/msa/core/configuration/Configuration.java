/**
 * File:     Configuration.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     28.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.model.Model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Defines configurations.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class Configuration {
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
