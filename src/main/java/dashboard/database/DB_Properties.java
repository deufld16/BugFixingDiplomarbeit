package dashboard.database;

import general.bl.GlobalParamter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Class to load the .properties file for the database
 * @author Florian Deutschmann
 */
public class DB_Properties {

    private static final Properties PROPS = new Properties();

    static{
        try {
            PROPS.load(new FileInputStream(Paths.get(GlobalParamter.getInstance().getDashboardResPath().toString(), "database.properties").toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return PROPS.getProperty(key);
    }
}
