/**
 * File:     EvaluationController.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.api.DescriptionResponse;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.api.EvaluationRequest;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.ProcessorService;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.Configuration;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.ResourceService;
import de.uniwuerzburg.zpd.ocr4all.application.communication.action.EvaluationMeasure;
import jakarta.validation.Valid;

/**
 * Defines evaluation controllers for the api.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@RestController
@RequestMapping(path = EvaluationController.contextPath, produces = CoreApiController.applicationJson)
public class EvaluationController extends ProcessorApiController {
	/**
	 * The context path.
	 */
	public static final String contextPath = apiContextPathVersion_1_0 + "/evaluation";

	/**
	 * Creates an evaluation controller for the api.
	 * 
	 * @param service         The processor service.
	 * @param resourceService The resource service.
	 * @since 17
	 */
	public EvaluationController(ProcessorService service, ResourceService resourceService) {
		super(EvaluationController.class, service, resourceService);
	}

	/**
	 * Returns the description in the response body.
	 * 
	 * @return The description in the response body.
	 * @since 1.8
	 */
	@GetMapping(descriptionRequestMapping)
	public ResponseEntity<DescriptionResponse> description() {
		Configuration configuration = resourceService.getEvaluation();

		if (configuration == null)
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
		else
			return ResponseEntity.ok()
					.body(getDescription(service.getProcessor(ProcessorService.Type.evaluation), configuration));
	}

	/**
	 * Executes the process and returns the evaluation measure in the response body.
	 * 
	 * @param request The evaluation request.
	 * @return The evaluation measure in the response body.
	 * @since 1.8
	 */
	@PostMapping(executeRequestMapping)
	public ResponseEntity<EvaluationMeasure> execute(@RequestBody @Valid EvaluationRequest request) {
		try {
			logger.debug("execute process, arguments '" + request.getArguments() + "'.");

			return ResponseEntity.ok().body(service.evaluate(request.getFolder(), request.getArguments()));
		} catch (IllegalArgumentException ex) {
			log(ex);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			log(ex);

			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

}
