/**
 * File:     TrainingController.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.api.DescriptionResponse;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.api.TrainingRequest;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.ProcessorService;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.Configuration;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.ResourceService;
import de.uniwuerzburg.zpd.ocr4all.application.communication.msa.api.domain.JobResponse;
import de.uniwuerzburg.zpd.ocr4all.application.msa.api.util.ApiUtils;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SystemProcessJob;
import jakarta.validation.Valid;

/**
 * Defines training controllers for the api.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@RestController
@RequestMapping(path = TrainingController.contextPath, produces = CoreApiController.applicationJson)
public class TrainingController extends ProcessorApiController {
	/**
	 * The context path.
	 */
	public static final String contextPath = apiContextPathVersion_1_0 + "/training";

	/**
	 * Creates a training controller for the api.
	 * 
	 * @param service         The processor service.
	 * @param resourceService The resource service.
	 * @since 17
	 */
	public TrainingController(ProcessorService service, ResourceService resourceService) {
		super(TrainingController.class, service, resourceService);
	}

	/**
	 * Returns the description in the response body.
	 * 
	 * @return The description in the response body.
	 * @since 1.8
	 */
	@GetMapping(descriptionRequestMapping)
	public ResponseEntity<DescriptionResponse> description() {
		Configuration configuration = resourceService.getTraining();

		if (configuration == null)
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
		else
			return ResponseEntity.ok()
					.body(getDescription(service.getProcessor(ProcessorService.Type.evaluation), configuration));
	}

	/**
	 * Executes the process and returns the job in the response body.
	 * 
	 * @param request The training request.
	 * @return The job in the response body.
	 * @since 1.8
	 */
	@PostMapping(executeRequestMapping)
	public ResponseEntity<JobResponse> execute(@RequestBody @Valid TrainingRequest request) {
		try {
			logger.debug("execute process: key " + request.getKey() + ", model id '" + request.getModelId()
					+ "', arguments '" + request.getArguments() + "'.");

			final SystemProcessJob job = service.startTraining(request.getKey(),
					resourceService.mapEvaluationArguments(request.getArguments()), request.getModelId(),
					request.getDataset(), request.getModelConfiguration(), request.getUser());

			logger.debug("running job " + job.getId() + ", key " + job.getKey() + ".");

			return ResponseEntity.ok().body(ApiUtils.getJobResponse(job));
		} catch (IllegalArgumentException | IOException ex) {
			log(ex);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			log(ex);

			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

}
