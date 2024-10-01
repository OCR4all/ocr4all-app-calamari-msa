/**
 * File:     Evaluation.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     28.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

/**
 * Defines evaluations.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class Evaluation extends Configuration {
	/**
	 * The framework.
	 */
	@NotNull
	private Framework framework;

	/**
	 * Returns the framework.
	 *
	 * @return The framework.
	 * @since 17
	 */
	public Framework getFramework() {
		return framework;
	}

	/**
	 * Set the framework.
	 *
	 * @param framework The framework to set.
	 * @since 17
	 */
	public void setFramework(Framework framework) {
		this.framework = framework;
	}

	/**
	 * Defines frameworks.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public static class Framework {
		/**
		 * The required arguments.
		 */
		@NotNull
		@JsonProperty("required-arguments")
		private List<String> requiredArguments;

		/**
		 * Returns the required arguments.
		 *
		 * @return The required arguments.
		 * @since 17
		 */
		public List<String> getRequiredArguments() {
			return requiredArguments;
		}

		/**
		 * Set the required arguments.
		 *
		 * @param requiredArguments The required arguments to set.
		 * @since 17
		 */
		public void setRequiredArguments(List<String> requiredArguments) {
			this.requiredArguments = requiredArguments;
		}

	}
}
