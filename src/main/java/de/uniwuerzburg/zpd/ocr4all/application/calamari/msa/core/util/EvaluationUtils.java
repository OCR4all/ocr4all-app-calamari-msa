/**
 * File:     EvaluationUtils.java
 * Package:  de.itbaier
 * 
 * Author:   Herbert Baier (herbert.baier@uni-wuerzburg.de)
 * Date:     18.09.2024
 */
package de.uniwuerzburg.zpd.ocr4all.application.calamari.msa.core.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.uniwuerzburg.zpd.ocr4all.application.communication.action.EvaluationMeasure;

/**
 * Defines evaluation utilities.
 *
 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
 * @version 1.0
 * @since 17
 */
public class EvaluationUtils {
	/**
	 * Define parser contexts.
	 *
	 * @author <a href="mailto:herbert.baier@uni-wuerzburg.de">Herbert Baier</a>
	 * @version 1.0
	 * @since 17
	 */
	private enum ParserContext {
		summary("Got mean normalized label error rate of "), header("GT"), detail("{"), ready;

		/**
		 * The context pattern.
		 */
		private final String pattern;

		/**
		 * Creates a parser context.
		 * 
		 * @since 17
		 */
		private ParserContext() {
			this.pattern = null;
		}

		/**
		 * Creates a parser context.
		 * 
		 * @param pattern The context pattern.
		 * @since 17
		 */
		private ParserContext(String pattern) {
			this.pattern = pattern;
		}

		/**
		 * Returns true if the line matches the context pattern.
		 * 
		 * @param line The line.
		 * @return True if the line matches the context pattern.
		 * @since 17
		 */
		public boolean match(String line) {
			return pattern == null || line.startsWith(pattern);
		}
	}

	/**
	 * The compiled representation of the regular expression to parse a context
	 * summary.
	 */
	private static final Pattern patternContextSummary = Pattern
			.compile("([0-9]*\\.?[0-9]*)%" + "\\D+" + "(\\d+)" + "\\D+" + "(\\d+)" + "\\D+" + "(\\d+)");

	/**
	 * The compiled representation of the regular expression to parse a context
	 * detail.
	 */
	private static final Pattern patternContextDetail = Pattern.compile(
			"\\{([^\\}]*)\\}" + "\\s+" + "\\{([^\\}]*)\\}" + "\\s+" + "(\\d+)" + "\\s+" + "([0-9]*\\.?[0-9]*)%");

	/**
	 * Returns the evaluation measure.
	 * 
	 * @param lines The lines.
	 * @return The evaluation measure.
	 * @since 17
	 */
	private static EvaluationMeasure parse(List<String> lines) {
		EvaluationMeasure evaluation = null;

		ParserContext context = ParserContext.summary;

		for (String line : lines) {
			switch (context) {
			case summary:
				if (context.match(line)) {
					Matcher matcher = patternContextSummary.matcher(line);

					if (matcher.find())
						evaluation = new EvaluationMeasure(EvaluationMeasure.State.completed,
								Float.parseFloat(matcher.group(1)), Integer.parseInt(matcher.group(2)),
								Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
					else
						return new EvaluationMeasure(EvaluationMeasure.State.inconsistent,
								"parser error (summary): " + line);

					context = ParserContext.header;
				}

				break;
			case header:
				if (context.match(line))
					context = ParserContext.detail;

				break;
			case detail:
				if (context.match(line)) {
					Matcher matcher = patternContextDetail.matcher(line);

					if (matcher.find()) {
						System.out.println("\t1: '" + matcher.group(1) + "'");
						System.out.println("\t2: '" + matcher.group(2) + "'");
						System.out.println("\t3: '" + matcher.group(3) + "'");
						System.out.println("\t4: '" + matcher.group(4) + "'");
					} else {
						evaluation.setState(EvaluationMeasure.State.inconsistent);

						String message = evaluation.getMessage();
						evaluation.setMessage((message == null ? "" : message + System.lineSeparator())
								+ "parser error (detail): " + line);
					}
				} else
					context = ParserContext.ready;

				break;
			default:
				break;
			}

			if (ParserContext.ready.equals(context))
				break;
		}

		return evaluation == null
				? new EvaluationMeasure(EvaluationMeasure.State.inconsistent, "parser error: no summary available")
				: evaluation;
	}

	/**
	 * Returns the evaluation measure.
	 * 
	 * @param evaluation The evaluation to parse the measure.
	 * @return The evaluation measure.
	 * @since 17
	 */
	public static EvaluationMeasure parse(String evaluation) {
		return evaluation == null || evaluation.isBlank()
				? new EvaluationMeasure(EvaluationMeasure.State.inconsistent, "parser error: no summary available")
				: parse(evaluation.lines().collect(Collectors.toList()));
	}

}
