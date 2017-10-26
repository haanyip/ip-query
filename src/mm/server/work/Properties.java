package mm.server.work;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by baidu on 16/11/17.
 */
public class Properties {
    private static Logger logger = Logger.getLogger(Properties.class);
    private static java.util.Properties prop = new java.util.Properties();
    private static Properties instance = new Properties();

    private Properties() {
        if (prop.isEmpty()) {
            loadProperties();
        }
    }

    public static Properties getInstance() {
        return instance;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    private void loadProperties() {
        FileInputStream is = null;
        try {
            is = new FileInputStream("config.properties");
            prop.load(is);
            logger.info("Prop is loaded : ");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                logger.error(" Fail to close InputStream ");
            }
        }
    }
}
