/**
 * File:     RecognitionController.java
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

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.api.RecognitionRequest;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.model.Model;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.ProcessorService;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.ResourceService;
import de.uniwuerzburg.zpd.ocr4all.application.communication.msa.api.domain.JobResponse;
import de.uniwuerzburg.zpd.ocr4all.application.msa.api.util.ApiUtils;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SystemProcessJob;
import jakarta.validation.Valid;

/**
 * Defines recognition controllers for the api.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@RestController
@RequestMapping(path = RecognitionController.contextPath, produces = CoreApiController.applicationJson)
public class RecognitionController extends CoreApiController {
	/**
	 * The context path.
	 */
	public static final String contextPath = apiContextPathVersion_1_0 + "/recognition";

	/**
	 * The processor service.
	 */
	private final ProcessorService service;

	/**
	 * The resource service.
	 */
	private final ResourceService resourceService;

	/**
	 * Creates a recognition controller for the api.
	 * 
	 * @param service         The processor service.
	 * @param resourceService The resource service.
	 * @since 17
	 */
	public RecognitionController(ProcessorService service, ResourceService resourceService) {
		super(RecognitionController.class);

		this.service = service;
		this.resourceService = resourceService;
	}

	/**
	 * Returns the model in the response body.
	 * 
	 * @return The model in the response body.
	 * @since 1.8
	 */
	@GetMapping(modelRequestMapping)
	public ResponseEntity<Model> model() {
		Model model = resourceService.getRecognitionModel();

		if (model == null)
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
		else
			return ResponseEntity.ok().body(model);
	}

	/**
	 * Executes the process and returns the job in the response body.
	 * 
	 * @param request The recognition request.
	 * @return The job in the response body.
	 * @since 1.8
	 */
	@PostMapping(executeRequestMapping)
	public ResponseEntity<JobResponse> execute(@RequestBody @Valid RecognitionRequest request) {
		try {
			logger.debug("execute process: key " + request.getKey() + ", arguments '" + request.getArguments() + "'.");

			final SystemProcessJob job = service.startRecognition(request.getKey(),
					resourceService.mapEvaluationArguments(request.getArguments()), request.getFolder(),
					request.getInput(), request.getOutput());

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
