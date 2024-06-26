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
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.ResourceService;
import de.uniwuerzburg.zpd.ocr4all.application.communication.msa.api.domain.JobResponse;
import de.uniwuerzburg.zpd.ocr4all.application.msa.api.util.ApiUtils;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SystemProcessJob;
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
public class EvaluationController extends CoreApiController {
	/**
	 * The context path.
	 */
	public static final String contextPath = apiContextPathVersion_1_0 + "/evaluation";

	/**
	 * The processor service.
	 */
	private final ProcessorService service;

	/**
	 * The resource service.
	 */
	private final ResourceService resourceService;

	/**
	 * Creates an evaluation controller for the api.
	 * 
	 * @param service         The processor service.
	 * @param resourceService The resource service.
	 * @since 17
	 */
	public EvaluationController(ProcessorService service, ResourceService resourceService) {
		super(EvaluationController.class);

		this.service = service;
		this.resourceService = resourceService;
	}

	/**
	 * Returns the description in the response body.
	 * 
	 * @return The description in the response body.
	 * @since 1.8
	 */
	@GetMapping(descriptionRequestMapping)
	public ResponseEntity<DescriptionResponse> description() {
		DescriptionResponse description = resourceService.getEvaluation();

		if (description == null)
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
		else
			return ResponseEntity.ok().body(description);
	}

	/**
	 * Executes the process and returns the job in the response body.
	 * 
	 * @param request The evaluation request.
	 * @return The job in the response body.
	 * @since 1.8
	 */
	@PostMapping(executeRequestMapping)
	public ResponseEntity<JobResponse> execute(@RequestBody @Valid EvaluationRequest request) {
		try {
			logger.debug("execute process: key " + request.getKey() + ", arguments '" + request.getArguments() + "'.");

			final SystemProcessJob job = service.startEvaluation(request.getKey(),
					resourceService.mapEvaluationArguments(request.getArguments()), request.getCollection());

			logger.debug("running job " + job.getId() + ", key " + job.getKey() + ".");

			return ResponseEntity.ok().body(ApiUtils.getJobResponse(job));
		} catch (IllegalArgumentException ex) {
			log(ex);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			log(ex);

			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

}
