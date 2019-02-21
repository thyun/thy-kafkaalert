package com.skp.kafkaalert.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.event.LogEvent;
import com.skp.kafkaalert.output.OutputFile;
import com.skp.kafkaalert.output.OutputProcessor;
import com.skp.kafkaalert.output.OutputQueue;
import com.skp.util.FileHelper;
import com.skp.util.FileHelper.LineReadCallback;

public class OutputProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(OutputProcessorTest.class);

	@Before
	public void setUp() {
	}

	@Test
	public void testOutputFile() throws IOException, ParseException, InterruptedException {
		// Get config
//		String input = FileHelper.getFile("process-nxlog.conf");
		Config config = Config.createFromResource("process.conf", "regex.conf");

	    // Create OutputProcessor
	    OutputProcessor oprocess = new OutputProcessor(config);
	    oprocess.init();

	    // Get OutputFile
	    OutputFile outputFile = (OutputFile) oprocess.getOutputPluginList().get(0);

	    // Generate sample data
	    generateSampleJson(outputFile.getOutputQueue());

	    // Process
	    outputFile.process();
	}

	// TODO generate 함수 합치기 - GeneralConsumerTest 클래스와
	static long offset;
	public static void generateSampleJson(OutputQueue outputQueue) {
	    offset = 0;
		FileHelper.processFileFromResource("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				try {
					List<LogEvent> elist1 = createLogEventList(produceJson("web01", line));
					List<LogEvent> elist2 = createLogEventList(produceJson("web02", line));
					outputQueue.put(elist1);
					outputQueue.put(elist2);
				} catch (InterruptedException e) {
					logger.error(e.toString());
				}
			}
		});
	}

	protected static List<LogEvent> createLogEventList(String value) {
		ArrayList<LogEvent> elist = new ArrayList<>();
		elist.add(LogEvent.parse(value));
		return elist;
	}

	private static String produceJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("hostname", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	}

}