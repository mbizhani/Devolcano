package org.devocative.devolcano;

import org.apache.commons.io.FileUtils;
import org.jmeld.ui.JMeldPanel;
import org.jmeld.ui.util.LookAndFeelManager;
import org.jmeld.util.prefs.WindowPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MergeCode implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(MergeCode.class);

	private static final String DIFF_RESOLVE_FILE = "/dlava/diffResolve.txt";

	public static void merge(File baseDir) throws Exception {
		File file = new File(baseDir.getCanonicalPath() + DIFF_RESOLVE_FILE);

		int retry = 1;
		while (retry < 4) {
			if (file.exists()) {
				break;
			} else {
				file = new File(baseDir.getParentFile().getCanonicalPath() + DIFF_RESOLVE_FILE);
				retry++;
			}
		}

		if (!file.exists()) {
			throw new RuntimeException("Diff Resolve file not exist: " + DIFF_RESOLVE_FILE);
		}

		logger.info("Diff Resolve file: {}", file.getCanonicalPath());

		List<String> mergeItems = FileUtils.readLines(file);

		Map<String, String> files = new LinkedHashMap<>();
		for (String mergeItem : mergeItems) {
			String[] split = mergeItem.split("[|]");

			String current = split[0];
			String generated = split[1];

			logger.info("Main={} | Generated={}", current, generated);

			files.put(current, generated);
		}

		if (!files.isEmpty()) {
			SwingUtilities.invokeLater(new MergeCode(files));
			logger.info("Merge Finished!");
		} else {
			logger.info("No file for merge!");
		}
	}

	private Map<String, String> files;

	private MergeCode(Map<String, String> files) {
		this.files = files;
	}

	@Override
	public void run() {
		LookAndFeelManager.getInstance().install();

		JMeldPanel jmeldPanel = new JMeldPanel();
		jmeldPanel.SHOW_TOOLBAR_OPTION.disable();

		JFrame frame = new JFrame("JMeld");
		frame.add(jmeldPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		new WindowPreference(frame.getTitle(), frame);
		frame.addWindowListener(jmeldPanel.getWindowListener());
		frame.setVisible(true);
		frame.toFront();

		for (Map.Entry<String, String> entry : files.entrySet()) {
			jmeldPanel.openComparison(entry.getKey(), entry.getValue());
		}
	}
}
