/**
 * File:     EngineJob.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     10.07.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core;

import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SystemProcessJob;
import de.uniwuerzburg.zpd.ocr4all.application.persistence.assemble.Engine;

/**
 * EngineJob is an immutable class that defines engine jobs.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class EngineJob {

	/**
	 * The job.
	 */
	private final SystemProcessJob job;

	/**
	 * The engine.
	 */
	private final Engine engine;

	/**
	 * Creates an engine job.
	 * 
	 * @since 17
	 */
	public EngineJob(SystemProcessJob job, Engine engine) {
		super();

		this.job = job;
		this.engine = engine;
	}

	/**
	 * Returns the job.
	 *
	 * @return The job.
	 * @since 17
	 */
	public SystemProcessJob getJob() {
		return job;
	}

	/**
	 * Returns the engine.
	 *
	 * @return The engine.
	 * @since 17
	 */
	public Engine getEngine() {
		return engine;
	}

}
