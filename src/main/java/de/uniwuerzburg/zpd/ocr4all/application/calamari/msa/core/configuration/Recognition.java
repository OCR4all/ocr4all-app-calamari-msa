/**
 * File:     Recognition.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     28.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Defines recognitions.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class Recognition extends Configuration {
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
		 * The argument.
		 */
		@NotNull
		private Framework argument;

		/**
		 * Returns the argument.
		 *
		 * @return The argument.
		 * @since 17
		 */
		public Framework getArgument() {
			return argument;
		}

		/**
		 * Set the argument.
		 *
		 * @param argument The argument to set.
		 * @since 17
		 */
		public void setArgument(Framework argument) {
			this.argument = argument;
		}

		/**
		 * Defines arguments.
		 *
		 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
		 * @version 1.0
		 * @since 17
		 */
		public static class Argument {

			/**
			 * The images.
			 */
			@NotBlank
			private String images;

			/**
			 * The output folder.
			 */
			@NotBlank
			private String output;

			/**
			 * Returns the images.
			 *
			 * @return The images.
			 * @since 17
			 */
			public String getImages() {
				return images;
			}

			/**
			 * Set the images.
			 *
			 * @param images The images to set.
			 * @since 17
			 */
			public void setImages(String images) {
				this.images = images;
			}

			/**
			 * Returns the output folder.
			 *
			 * @return The output folder.
			 * @since 17
			 */
			public String getOutput() {
				return output;
			}

			/**
			 * Set the output folder.
			 *
			 * @param output The output folder to set.
			 * @since 17
			 */
			public void setOutput(String output) {
				this.output = output;
			}

		}
	}

}
