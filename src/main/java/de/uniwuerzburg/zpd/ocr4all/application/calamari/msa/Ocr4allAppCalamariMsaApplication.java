/**
 * File:     Ocr4allAppCalamariMsaApplication.java
 * Package:  de.uniwuerzburg.zpd.ocr4all.application.ocrd.msa
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     13.06.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import de.uniwuerzburg.zpd.ocr4all.application.msa.configuration.ConfigurationService;

/**
 * Triggers auto-configuration and component scanning and enables the Calamari
 * server.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
@SpringBootApplication
@ComponentScan("de.uniwuerzburg.zpd.ocr4all.application")
public class Ocr4allAppCalamariMsaApplication {
	/**
	 * The logger.
	 */
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(Ocr4allAppCalamariMsaApplication.class);

	/**
	 * The main method to start the Calamari server.
	 * 
	 * @param args The application arguments.
	 * @since 17
	 */
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Ocr4allAppCalamariMsaApplication.class, args);

		ConfigurationService configurationService = context.getBean(ConfigurationService.class);

		logger.info("started ocr4all-app-calamari-msa: port " + configurationService.getServerPort()
				+ ", active profiles '" + configurationService.getActiveProfilesCSV() + "', folders [data '"
				+ context.getEnvironment().getProperty("ocr4all.data.folder") + "', assemble '"
				+ context.getEnvironment().getProperty("ocr4all.assemble.folder") + "', projects '"
				+ context.getEnvironment().getProperty("ocr4all.projects.folder") + "'], processors [evaluation '"
				+ context.getEnvironment().getProperty("ocr4all.calamari.processors.evaluation") + "', recognition '"
				+ context.getEnvironment().getProperty("ocr4all.calamari.processors.recognition") + "', training '"
				+ context.getEnvironment().getProperty("ocr4all.calamari.processors.training") + "'].");
	}

}
