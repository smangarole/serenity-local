package starter;

import com.browserstack.local.Local;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;

import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    static Local bsLocal;

    @Before
    public static void setUp() throws Exception {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();

        //Check environment variable for the access key
        //If not found get the access key from the serenity.properties file
        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = (String) environmentVariables.getProperty("browserstack.key");
        }

        //Check browserstack.local value from serenity.properties file
        //If value is true. set is_local to true and start local testing
        String key = "browserstack.local";
        boolean is_local = environmentVariables.getProperty(key) != null
                && environmentVariables.getProperty(key).equals("true");

        //Start local testing using the language bindings
        if (is_local) {
            System.out.println("Check Local");
            bsLocal = new Local();
            Map<String, String> bsLocalArgs = new HashMap<String, String>();
            bsLocalArgs.put("key", accessKey);
            bsLocal.start(bsLocalArgs);
        }
    }

    @After
    public static void tearDown() throws Exception {
        if (bsLocal != null) {
            bsLocal.stop();
        }
    }
}
