/**
 * File:     JobController.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.api.worker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.uniwuerzburg.zpd.ocr4all.application.communication.msa.api.domain.SystemJobResponse;
import de.uniwuerzburg.zpd.ocr4all.application.msa.api.util.ApiUtils;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SchedulerService;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SystemProcessJob;

/**
 * Defines job controllers for the api.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@RestController
@RequestMapping(path = JobController.contextPath, produces = CoreApiController.applicationJson)
public class JobController extends CoreApiController {
	/**
	 * The context path.
	 */
	public static final String contextPath = apiContextPathVersion_1_0 + "/job";

	/**
	 * The scheduler resourceService.
	 */
	private final SchedulerService service;

	/**
	 * Creates a job controller for the api.
	 * 
	 * @param service The scheduler service.
	 * @since 17
	 */
	public JobController(SchedulerService service) {
		super(JobController.class);

		this.service = service;
	}

	/**
	 * Returns the system job in the response body.
	 * 
	 * @param id The job id.
	 * @return The system job in the response body.
	 * @since 1.8
	 */
	@GetMapping(idPathVariable)
	public ResponseEntity<SystemJobResponse> getSystemJob(@PathVariable int id) {
		try {
			SystemProcessJob job = (SystemProcessJob) service.getJob(id);

			if (job == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

			logger.debug(
					"system job " + job.getId() + ": state " + job.getState().name() + ", key " + job.getKey() + ".");

			return ResponseEntity.ok().body(ApiUtils.getSystemJobResponse(job));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

}
