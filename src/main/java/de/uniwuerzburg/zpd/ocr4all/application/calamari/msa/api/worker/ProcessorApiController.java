/**
 * File:     CoreApiController.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker;

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.api.DescriptionResponse;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.ProcessorService;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.Configuration;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.ResourceService;

/**
 * Defines core controllers for the api.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class ProcessorApiController extends CoreApiController {
	/**
	 * The processor service.
	 */
	protected final ProcessorService service;

	/**
	 * The resource service.
	 */
	protected final ResourceService resourceService;

	/**
	 * Creates a core controller for the api.
	 *
	 * @param logger          The logger class.
	 * @param service         The processor service.
	 * @param resourceService The resource service.
	 * @since 17
	 */
	public ProcessorApiController(Class<?> logger, ProcessorService service, ResourceService resourceService) {
		super(logger);

		this.service = service;
		this.resourceService = resourceService;
	}

	/**
	 * Returns the description response for the api.
	 * 
	 * @param identifier    The identifier.
	 * @param configuration The processor configuration.
	 * @return The description response for the api.
	 * @since 17
	 */
	protected DescriptionResponse getDescription(String identifier, Configuration configuration) {
		return new DescriptionResponse(identifier, configuration.getDescription(), configuration.getCategories(),
				configuration.getSteps(), configuration.getModel());
	}

}
