/**
 * File:     CoreApiController.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker;

/**
 * Defines core controllers for the api.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class CoreApiController {
	/**
	 * The api context path.
	 */
	public static final String apiContextPath = "/api";

	/**
	 * The api version 1.0 prefix path.
	 */
	public static final String apiContextPathVersion_1_0 = apiContextPath + "/v1.0";

	/**
	 * The application type json.
	 */
	public static final String applicationJson = "application/json";

	/**
	 * The description request mapping.
	 */
	public static final String descriptionRequestMapping = "/description";

	/**
	 * The execute request mapping.
	 */
	public static final String executeRequestMapping = "/execute";

	/**
	 * The id path variable.
	 */
	public static final String idPathVariable = "/{id}";

	/**
	 * The logger.
	 */
	protected final org.slf4j.Logger logger;

	/**
	 * Creates a core controller for the api.
	 *
	 * @param logger The logger class.
	 * @since 17
	 */
	public CoreApiController(Class<?> logger) {
		super();

		this.logger = org.slf4j.LoggerFactory.getLogger(logger);
	}

	/**
	 * Logs the exception.
	 *
	 * @param exception The exception to log.
	 * @since 1.8
	 */
	protected void log(Exception exception) {
		logger.error("throws exception " + exception.getClass().getName(), exception);
	}

}
