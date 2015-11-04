package org.yaoqiang.bpmn.editor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.yaoqiang.graph.util.Constants;

/**
 * Utils
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class Utils {

	public static int getDaysDiff(long t1, long t2) {
		return getDaysDiff(t1, t2, true);
	}

	public static int getDaysDiff(long t1, long t2, boolean checkOverflow) {
		if (t1 > t2) {
			long tmp = t1;
			t1 = t2;
			t2 = tmp;
		}
		Calendar calendar = Calendar.getInstance();
		synchronized (calendar) {
			calendar.setTimeInMillis(t1);
			int delta = 0;
			while (calendar.getTimeInMillis() < t2) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				delta++;
			}
			if (checkOverflow && (calendar.getTimeInMillis() > t2)) {
				delta--;
			}
			return delta;
		}
	}

	public static int getDayOfWeek(long date) {
		Calendar calendar = Calendar.getInstance();
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			return (calendar.get(Calendar.DAY_OF_WEEK));
		}
	}

	public static long startOfDayInMillis(long date) {
		Calendar calendar = Calendar.getInstance();
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			return calendar.getTimeInMillis();
		}
	}

	public static ImageIcon getImageIcon(String imageName) {
		return new ImageIcon(Utils.class.getClassLoader().getResource(imageName));
	}

	public static void saveToConfigureFile(String key, String value) {
		Properties props = loadProperties(Constants.YAOQIANG_CONF_FILE);
		props.put(key, value);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(Constants.YAOQIANG_USER_HOME + File.separator + Constants.YAOQIANG_CONF_FILE));
			props.store(out, "Yaoqiang BPMN Editor Configuration");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void removeConfigure(String key) {
		Properties props = loadProperties(Constants.YAOQIANG_CONF_FILE);
		props.remove(key);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(Constants.YAOQIANG_USER_HOME + File.separator + Constants.YAOQIANG_CONF_FILE));
			props.store(out, "Yaoqiang BPMN Editor Configuration");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void saveConfigureFile() {
		String filename = Constants.YAOQIANG_USER_HOME + File.separator + Constants.YAOQIANG_CONF_FILE;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(filename));
			Constants.SETTINGS.store(out, "Yaoqiang BPMN Editor Configuration");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static Properties loadProperties(String propertyFile) {
		File file = new File(Constants.YAOQIANG_USER_HOME + File.separator + propertyFile);

		Properties props = new Properties();
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				props.load(fis);
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return props;
	}

	public static void saveConfigure(String key, String value, String propertyFile, String comment) {
		Properties props = loadProperties(propertyFile);
		props.put(key, value);

		FileOutputStream out = null;
		try {
			File configFile = new File(Constants.YAOQIANG_USER_HOME + File.separator + propertyFile);
			out = new FileOutputStream(configFile);
			props.store(out, comment);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void removeConfigure(String key, String propertyFile, String comment) {
		Properties props = loadProperties(propertyFile);
		props.remove(key);

		FileOutputStream out = null;
		try {
			File configFile = new File(Constants.YAOQIANG_USER_HOME + File.separator + propertyFile);
			out = new FileOutputStream(configFile);
			props.store(out, comment);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
