package starter;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Iterator;

public class BaseDriver implements DriverSource {

    public WebDriver newDriver() {
        //Read serenity properties
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();

        //Check environment variable for the username
        //If not found get the username from the serenity.properties file
        String username = System.getenv("BROWSERSTACK_USERNAME");
        if (username == null) {
            username = (String) environmentVariables.getProperty("browserstack.user");
        }

        //Check environment variable for the access key
        //If not found get the access key from the serenity.properties file
        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = (String) environmentVariables.getProperty("browserstack.key");
        }

        //Create capabilities object
        DesiredCapabilities capabilities = new DesiredCapabilities();

        //Read all serenity properties and set capabilities
        Iterator it = environmentVariables.getKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();

            if (key.equals("browserstack.user") || key.equals("browserstack.key")
                    || key.equals("browserstack.server")) {
                continue;
            }

            //If browserstack.local = true in serenity properties, add the capability
            String localSerenityProperty = "browserstack.local";
            boolean is_local = environmentVariables.getProperty(localSerenityProperty) != null
                    && environmentVariables.getProperty(localSerenityProperty).equals("true");
            if (is_local){
                capabilities.setCapability("browserstack.local", "true");
            }

            //add other browserstack capabilities prefixed with "bstack_"
            if (key.startsWith("bstack_")) {
                capabilities.setCapability(key.replace("bstack_", ""), environmentVariables.getProperty(key));
            }
        }

        try {

            URL url = new URL("https://" + username + ":" + accessKey + "@"
                    + environmentVariables.getProperty("browserstack.server") + "/wd/hub");

            RemoteWebDriver rd =  new RemoteWebDriver(url, capabilities);
            return rd;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
    public boolean takesScreenshots() {
        return true;
    }
}