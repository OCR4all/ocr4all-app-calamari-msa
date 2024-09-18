/**
 * File:     ProcessorService.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.core.Batch;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.core.BatchArgument;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.communication.core.ModelConfiguration;
import de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.configuration.ResourceService;
import de.uniwuerzburg.zpd.ocr4all.application.communication.action.EvaluationMeasure;
import de.uniwuerzburg.zpd.ocr4all.application.communication.msa.job.ThreadPool;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SchedulerService;
import de.uniwuerzburg.zpd.ocr4all.application.msa.job.SystemProcessJob;
import de.uniwuerzburg.zpd.ocr4all.application.persistence.assemble.Engine;
import de.uniwuerzburg.zpd.ocr4all.application.spi.util.SystemProcess;

/**
 * Defines processor services.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@Service
public class ProcessorService {
	/**
	 * The logger.
	 */
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProcessorService.class);

	/**
	 * True if add the process environment to the standard output.
	 */
	private static final boolean isAddEnvironmentStandardOutput = false;

	/**
	 * Defines types.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	public enum Type {
		evaluation, recognition, training
	}

	/**
	 * The ocr4all data folder.
	 */
	private final Path dataFolder;

	/**
	 * The ocr4all assemble folder.
	 */
	private final Path assembleFolder;

	/**
	 * The ocr4all projects folder.
	 */
	private final Path projectsFolder;

	/**
	 * The ocr4all temporary folder.
	 */
	private final Path temporaryFolder;

	/**
	 * True if discards the standard output.
	 */
	private final boolean isDiscardOutput;

	/**
	 * True if discards the standard error.
	 */
	private final boolean isDiscardError;

	/**
	 * The processors to be run on the time-consuming thread pool.
	 */
	private final Set<Type> timeConsuming = new HashSet<>();

	/**
	 * The processors.
	 */
	private final Hashtable<Type, String> processors = new Hashtable<>();

	/**
	 * The training dataset filename.
	 */
	private final String trainingDatasetFilename;

	/**
	 * The scheduler service.
	 */
	private final SchedulerService schedulerService;

	/**
	 * The resource service.
	 */
	protected final ResourceService resourceService;

	/**
	 * Creates a processor service.
	 * 
	 * @param dataFolder              The ocr4all data folder.
	 * @param assembleFolder          The ocr4all assemble folder.
	 * @param projectsFolder          The ocr4all projects folder.
	 * @param temporaryFolder         The ocr4all temporary folder.
	 * @param isDiscardOutput         True if discards the standard output.
	 * @param isDiscardError          True if discards the standard error.
	 * @param evaluationProcessor     The evaluation processor.
	 * @param recognitionProcessor    The recognition processor.
	 * @param trainingProcessor       The training processor.
	 * @param trainingDatasetFilename The training dataset filename.
	 * @param timeConsuming           The processors to be run on the time-consuming
	 *                                thread pool.
	 * @param schedulerService        The scheduler service.
	 * @param resourceService         The resource service.
	 * @since 17
	 */
	public ProcessorService(@Value("${ocr4all.data.folder}") String dataFolder,
			@Value("${ocr4all.assemble.folder}") String assembleFolder,
			@Value("${ocr4all.projects.folder}") String projectsFolder,
			@Value("${ocr4all.temporary.folder}") String temporaryFolder,
			@Value("${ocr4all.calamari.logging.discard.output}") boolean isDiscardOutput,
			@Value("${ocr4all.calamari.logging.discard.error}") boolean isDiscardError,
			@Value("${ocr4all.calamari.processors.evaluation.name}") String evaluationProcessor,
			@Value("${ocr4all.calamari.processors.recognition.name}") String recognitionProcessor,
			@Value("${ocr4all.calamari.processors.training.name}") String trainingProcessor,
			@Value("${ocr4all.calamari.processors.training.dataset-filename}") String trainingDatasetFilename,
			@Value("#{'${ocr4all.calamari.processors.time-consuming}'.split(',')}") List<String> timeConsuming,
			SchedulerService schedulerService, ResourceService resourceService) {
		super();

		this.dataFolder = Paths.get(dataFolder).normalize();
		this.assembleFolder = Paths.get(assembleFolder).normalize();
		this.projectsFolder = Paths.get(projectsFolder).normalize();
		this.temporaryFolder = Paths.get(temporaryFolder).normalize();

		this.isDiscardOutput = isDiscardOutput;
		this.isDiscardError = isDiscardError;

		for (String processor : timeConsuming)
			if (processor != null && !processor.isBlank())
				try {
					this.timeConsuming.add(Type.valueOf(processor.trim()));
				} catch (Exception e) {
					logger.warn("unknown time-consuming processor type '" + processor.trim() + "'.");
				}

		processors.put(Type.evaluation, evaluationProcessor);
		processors.put(Type.recognition, recognitionProcessor);
		processors.put(Type.training, trainingProcessor);

		this.trainingDatasetFilename = trainingDatasetFilename;

		this.schedulerService = schedulerService;
		this.resourceService = resourceService;
	}

	/**
	 * Returns the processor.
	 *
	 * @param type The processor type to return.
	 * @return The processor. Null if unknown.
	 * @since 17
	 */
	public String getProcessor(Type type) {
		return type == null ? null : processors.get(type);
	}

	/**
	 * Returns the evaluation.
	 * 
	 * @param arguments The arguments.
	 * @return The evaluation measure.
	 * @since 17
	 */
	public EvaluationMeasure evaluate(String folder, List<String> arguments) {
		try {
			SystemProcess process = new SystemProcess(temporaryFolder.resolve(folder), processors.get(Type.evaluation));

			process.execute(arguments);

			if (process.getExitValue() == 0) {
				return process.getStandardOutput();
			} else
				return new EvaluationMeasure(EvaluationMeasure.State.interrupted,
						"process exit code " + process.getExitValue() + ": " + process.getStandardError().trim());
		} catch (Exception e) {
			return new EvaluationMeasure(EvaluationMeasure.State.interrupted,
					e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Creates a job to perform the recognition process and starts it on the
	 * scheduler.
	 * 
	 * @param key       The job key.
	 * @param arguments The processor arguments.
	 * @param models    The models. Null or empty if not model is used.
	 * @param folder    The working directory of the job. It is relative to the
	 *                  project folder.
	 * @return The scheduled job.
	 * @throws IllegalArgumentException Throws on folder troubles.
	 * @since 17
	 */
	public SystemProcessJob startRecognition(String key, List<String> arguments, List<BatchArgument> models,
			String folder) throws IllegalArgumentException {
		if (folder == null || folder.isBlank())
			throw new IllegalArgumentException("the folder parameter is not defined");

		Path path = Paths.get(projectsFolder.toString(), folder.trim()).normalize();

		if (!path.startsWith(projectsFolder) || !Files.isDirectory(path))
			throw new IllegalArgumentException("the folder is not a valid directory");

		// Adds the model arguments
		addModelArguments(arguments, models);

		SystemProcessJob job = new SystemProcessJob(
				timeConsuming.contains(Type.recognition) ? ThreadPool.timeConsuming : ThreadPool.standard, key,
				new SystemProcess(path, processors.get(Type.recognition)), isAddEnvironmentStandardOutput,
				isDiscardOutput, isDiscardError, arguments);
		schedulerService.start(job);

		return job;
	}

	/**
	 * Adds the model arguments to arguments.
	 * 
	 * @param arguments The arguments.
	 * @param models    The model arguments.
	 * @since 17
	 */
	private void addModelArguments(List<String> arguments, List<BatchArgument> models) {
		if (models != null)
			for (BatchArgument argument : models) {
				List<String> batches = new ArrayList<>();

				if (argument != null && argument.getItems() != null && !argument.getItems().isEmpty()
						&& argument.getArgument() != null && !argument.getArgument().isBlank())
					for (Batch.Item item : argument.getItems())
						if (item.getId() != null && !item.getId().isBlank()) {
							String prefix = Paths.get(assembleFolder.toString(), item.getId().trim()).toString();
							for (String file : item.getFiles())
								if (file != null && !file.isBlank())
									batches.add(Paths.get(prefix, file.trim()).toString());
						}

				if (!batches.isEmpty()) {
					arguments.add(argument.getArgument().trim());
					arguments.addAll(batches);
				}
			}

	}

	/**
	 * Creates a job to perform the training process and starts it on the scheduler.
	 * 
	 * @param key                The job key.
	 * @param arguments          The processor arguments.
	 * @param modelId            The model id.
	 * @param dataset            The dataset.
	 * @param models             The models. Null or empty if not model is used.
	 * @param modelConfiguration The model configuration.
	 * @return The scheduled job.
	 * @throws IllegalArgumentException Throws on argument troubles.
	 * @throws IOException              Throws if an I/O exception of some sort has
	 *                                  occurred.
	 * @since 17
	 */
	public EngineJob startTraining(String key, List<String> arguments, String modelId, Batch dataset,
			List<BatchArgument> models, ModelConfiguration modelConfiguration)
			throws IllegalArgumentException, IOException {
		if (modelId == null || modelId.isBlank())
			throw new IllegalArgumentException("model id is mandatory and may not be empty");

		modelId = modelId.trim();

		final Path path = Paths.get(assembleFolder.toString(), modelId).normalize();
		if (!path.toString().startsWith(assembleFolder.toString()) || !Files.isDirectory(path))
			throw new IllegalArgumentException("invalid model id '" + modelId + "'");

		if (!Files.isDirectory(path))
			throw new IllegalArgumentException("unknown model id '" + modelId + "'");

		// The dataset
		StringBuffer buffer = new StringBuffer();
		if (dataset.getItems() != null)
			for (Batch.Item item : dataset.getItems())
				if (item.getId() != null && !item.getId().isBlank()) {
					String prefix = Paths.get(dataFolder.toString(), item.getId().trim()).toString();
					for (String file : item.getFiles())
						if (file != null && !file.isBlank())
							buffer.append(Paths.get(prefix, file.trim()).toString() + System.lineSeparator());
				}

		if (buffer.length() == 0)
			throw new IllegalArgumentException("dataset can not be empty");

		Files.write(Paths.get(path.toString(), modelConfiguration.getFolder(), trainingDatasetFilename),
				buffer.toString().getBytes());

		// Adds the reserved arguments
		arguments.addAll(Arrays.asList(resourceService.getTraining().getFramework().getArgument().getImages(),
				Paths.get(modelConfiguration.getFolder(), trainingDatasetFilename).toString(),
				resourceService.getTraining().getFramework().getArgument().getOutput(), path.toString(),
				resourceService.getTraining().getFramework().getArgument().getTrain(),
				resourceService.getTraining().getFramework().getArgument().getTrainValue()));

		// Adds the model arguments
		addModelArguments(arguments, models);

		SystemProcessJob job = new SystemProcessJob(
				timeConsuming.contains(Type.training) ? ThreadPool.timeConsuming : ThreadPool.standard, key,
				new SystemProcess(path, processors.get(Type.training)), isAddEnvironmentStandardOutput, isDiscardOutput,
				isDiscardError, arguments);
		schedulerService.start(job);

		return new EngineJob(job, new Engine(null, Engine.Method.processor, Engine.State.running, Engine.Type.Calamari,
				resourceService.getTraining().getFramework().getVersion(), processors.get(Type.training), arguments));
	}

}
