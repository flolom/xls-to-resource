package com.hotcocoacup.mobiletools.xlstoresouces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hotcocoacup.mobiletools.xlstoresouces.model.Entry;
import com.hotcocoacup.mobiletools.xlstoresouces.model.KeyValuePair;

public class XlsToResources {

	public static final String VERSION = "1.0.0";
	public static final String LOGGER_NAME = "XlsToResources";

	private static Logger logger = Logger.getLogger(LOGGER_NAME);
	private static Options options = new Options();

	public static void main(String[] args) {

		// Setting up the logger
		logger.setLevel(Level.INFO);
		logger.setUseParentHandlers(false);

		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);

		// Parsing the user inputs
		options.addOption("h", "help", false, "Print the help.");
		options.addOption("v", "version", false, "Print the current version.");
		options.addOption("c", "config", true, "The configuration file");
		options.addOption("a", "android", true,
				"The android resouce filename to export");
		options.addOption("i", "ios", true,
				"The iOS resource filename to export");

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Failed to parse command line properties",
					e);
			help();
			return;
		}

		// user asked for help...
		if (cmd.hasOption('h')) {
			help();
			return;
		}
		
		// user asked for version
		if (cmd.hasOption('v')) {
			printVersion();
			return;
		}

		// extracting the configuration filename
		String configFileName;
		if (cmd.hasOption('c')) {
			configFileName = cmd.getOptionValue('c');
		} else {
			logger.severe("You must input the configurationFilename");
			help();
			return;
		}

		logger.info("Reading configuration file " + configFileName);
		File file = new File(configFileName);
		Gson gson = new Gson();

		List<Entry> entries;
		try {
			entries = gson.fromJson(new FileReader(file),
					new TypeToken<List<Entry>>() {
					}.getType());
		} catch (JsonIOException e) {
			logger.log(Level.SEVERE, "Cannot parse the configuration file", e);
			return;
		} catch (JsonSyntaxException e) {
			logger.log(Level.SEVERE, "Cannot parse the configuration file", e);
			return;
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "The configuration file does not exist", e);
			return;
		}

		logger.log(Level.INFO, entries.size()
				+ " entry(ies) found in the configuration file.");

		Map<String, List<KeyValuePair>> map = new HashMap<String, List<KeyValuePair>>();

		int entryCount = 1;
		for (Entry entry : entries) {
			Workbook workbook;

			logger.log(Level.INFO, "Entry #" + entryCount + ": Reading "
					+ entry.getXlsFile() + " ...");

			// parsing the excel file.
			try {
				if (entry.getXlsFile() == null) {
					logger.log(Level.SEVERE, "You must specify an XLS/XLSX file name. Ignoring the entry.");
					continue;
				}
				
				workbook = WorkbookFactory.create(new File(entry.getXlsFile()));
			} catch (InvalidFormatException e) {
				logger.log(Level.SEVERE,
						"Invalid file format. Ignoring this entry.", e);
				continue;
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"IO error while reading the file. Ignoring the entry.",
						e);
				continue;
			}

			// invalid sheet number
			if (entry.getSheet() < 0
					|| entry.getSheet() > workbook.getNumberOfSheets()) {
				logger.log(Level.SEVERE,
						"Sheet index not valid. Ignoring this entry.");
				continue;
			}

			Sheet sheet = workbook.getSheetAt(entry.getSheet());

			int rowEnd;
			if (entry.getRowEnd() == -1) {

				// default rowEnd : read all the rows
				rowEnd = sheet.getLastRowNum();
			} else {

				if (entry.getRowEnd() < 0
						|| entry.getRowEnd() < entry.getRowStart()) {
					logger.log(Level.SEVERE,
							"Invalid row end. Ignoring this entry.");
					continue;
				} else {
					rowEnd = Math.min(sheet.getLastRowNum(),
							entry.getRowEnd() - 1);
				}
			}

			// processing all the rows of the file
			for (int i = entry.getRowStart() - 1; i <= rowEnd; i++) {

				Row row = sheet.getRow(i);

				logger.log(Level.FINEST, " processing row: " + i + "...");
				
				if (row == null) {
					logger.log(Level.WARNING, " row: " + i + " is null");
					continue;
				}

				Cell keyCell = row.getCell(entry.getColumnKey() - 1);
				Cell valueCell = row.getCell(entry.getColumnValue() - 1);

				if (keyCell == null) {
					logger.log(Level.WARNING,
							"Key column " + entry.getColumnKey() + " (row "
									+ (i + 1)
									+ ") does not exist. Skipping row.");
					continue;
				}

				if (valueCell == null) {
					logger.log(Level.WARNING,
							"Value colum " + entry.getColumnValue() + " (row "
									+ (i + 1)
									+ ") does not exist. Skipping row.");
					continue;
				}

				String key = keyCell.getStringCellValue();
				String value = valueCell.getStringCellValue();

				String groupBy = "";
				if (entry.getGroupBy() != -1) {
					Cell groupByCell = row.getCell(entry.getGroupBy() - 1);

					if (groupByCell != null) {
						groupBy = groupByCell.getStringCellValue();
					} else {
						logger.log(
								Level.WARNING,
								"GroupBy column "
										+ entry.getGroupBy()
										+ " (row "
										+ (i + 1)
										+ ") does not exist. GroupBy set to default.");
					}
				}

				KeyValuePair keyValue = new KeyValuePair(key, value);

				add(map, groupBy, keyValue);
			}

			logger.log(Level.INFO, "Entry #" + entryCount
					+ ": Parsed with success.");

			entryCount++;
		}

		if (cmd.hasOption('a')) {
			
			String androidFileName = cmd.getOptionValue('a');
			logger.log(Level.INFO, "Exporting as android resource: " + androidFileName);
			
			FileOutputStream outputAndroidStream;
			try {
				outputAndroidStream = new FileOutputStream(androidFileName);
				Processor processorAndroid = new AndroidProcessor();
				processorAndroid.process(outputAndroidStream, map);
				logger.log(Level.INFO, "Exported with success");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Export failed...", e);
			}

		}
		
		if (cmd.hasOption('i')) {
			String iosFileName = cmd.getOptionValue('i');
			logger.log(Level.INFO, "Exporting as ios resource: " + iosFileName);
			
			FileOutputStream outputIosStream;
			try {
				outputIosStream = new FileOutputStream(iosFileName);
				Processor processorIos = new IosProcessor();
				processorIos.process(outputIosStream, map);
				logger.log(Level.INFO, "Exported with success");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Export failed...", e);
			}
			
		}
		
		logger.log(Level.INFO, "End of execution");
	}

	private static void add(Map<String, List<KeyValuePair>> map,
			String groupBy, KeyValuePair keyValue) {

		List<KeyValuePair> list;
		if (!map.containsKey(groupBy)) {
			list = new ArrayList<KeyValuePair>();
			map.put(groupBy, list);
		} else {
			list = map.get(groupBy);
		}

		list.add(keyValue);
	}

	private static void help() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Main", options);

		System.out.println("\nFormat of the Configuration file:");
		System.out.println("[");
		System.out.println("     {");
		System.out.println("         \"fileName\": (string) \"xls or xlsx file containing the wording. Mandatory.\",");
		System.out.println("         \"sheet\": (int) \"index of the sheet concerned. 0=first sheet. Default=0\", ");
		System.out.println("         \"rowStart\": (int) \"index of the starting row. 1=first row. Default=1\", ");
		System.out.println("         \"rowEnd\": (int) \"index of the last row. 1=first row. -1=all rows. Default=-1\", ");
		System.out.println("         \"columnKey\": (int) \"index of the column containing the key. 1=column A. Default=1\", ");
		System.out.println("         \"columnValue\": (int) \"index of the column containing the value. 1=column A. Default=2\", ");
		System.out.println("         \"groupBy\": (int) \"index of the column containing the group value. 1=column A. -1=Do not group. Default=-1\", ");
		System.out.println("     }, ...");
		System.out.println("]");
		System.out.println("");
		System.out.println("Example of how to use:");
		System.out.println("java -jar xlsToResource.jar -c config.json -a string.xml -i sample.strings");
		
		System.exit(0);

	}
	
	private static void printVersion() {
		System.out.println("V" + VERSION);
	}

}
