package nl.ortecfinance.opal.webrefreshresourcemaven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 * This class makes it possible to override resource value in 'messages_xx_yy.properties' by providing alternative
 * values in 'custom_xx_yy.properties'.
 *
 */
public class DynamicResourceBundle extends ResourceBundle {

    protected static final String BASE_NAME = "Xmessages";
    protected static final Control DB_CONTROL = new DBControl();
    private static final Logger LOGGER = Logger.getLogger(DynamicResourceBundle.class);

    private Map<String, String> messages;
    private Locale locale;

    public DynamicResourceBundle() {
        LOGGER.error("Constr. DynamicResourceBundle");
        setParent(ResourceBundle.getBundle(BASE_NAME,
                FacesContext.getCurrentInstance().getViewRoot().getLocale(), DB_CONTROL));
    }

    protected DynamicResourceBundle(Map<String, String> messages, Locale locale) {
        this.locale = locale;
        LOGGER.error("Setting messages " + messages.size());
        this.messages = messages;
    }

    @Override
    protected Object handleGetObject(String key) {
        if (messages == null) {
            LOGGER.info("messages is null, locale=" + locale);
        } else {
            LOGGER.info("Size of messages: " + messages.size() + ", locale=" + locale);
        }

        return messages != null ? messages.get(key) : parent.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return messages != null ? Collections.enumeration(messages.keySet()) : parent.getKeys();
    }

    protected static class DBControl extends Control {

        private static final Logger LOGGER = Logger.getLogger(DBControl.class);

        public DBControl() {
            LOGGER.error("DBControl constr");
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            LOGGER.error("Locale is " + locale + ", time is " + new Date() + ", baseName=" + baseName + ",format=" + format + ", reload=" + reload);
            //  String language = locale.getLanguage();
            Map<String, String> msgMap = new HashMap<>();//getItSomehow(baseName, language); // Do your JPA thing. The baseName can be used as @NamedQuery name.
            preLoadFromProperties(msgMap, locale);
            overwrite(msgMap, locale);

            return new DynamicResourceBundle(msgMap, locale);
        }

        @Override
        public List<Locale> getCandidateLocales(String baseName, Locale locale) {
            if (baseName == null) {
                throw new NullPointerException();
            }

            LOGGER.info("Locale " + locale);

            return Arrays.asList(
                    locale,
                    Locale.FRANCE,
                    Locale.US,
                    Locale.GERMANY,
                    Locale.ROOT);
        }

        private void preLoadFromProperties(Map<String, String> msgMap, Locale locale) {

            ResourceBundle rb = ResourceBundle.getBundle(BASE_NAME, locale);
            Map<String, String> items = printBundle(rb);
            msgMap.putAll(items);
        }

        private void overwrite(Map<String, String> msgMap, Locale locale) throws FileNotFoundException, IOException {
            String fileName;
            if (locale == null || locale.toString().length() == 0) {
                fileName = "/custom.properties";
            } else {
                fileName = "/custom_" + locale + ".properties";
            }

            File file = new File(getApplicationConfigDirectory() + fileName);

            InputStream inputStream = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(inputStream);
            for (Map.Entry<Object, Object> entrySet : prop.entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();

                msgMap.put((String) key, (String) value);
            }
        }

    }

    public static String getApplicationConfigDirectory() {
        String sServerName = System.getenv("SERVER_NAME");
        if (sServerName == null) {
            sServerName = System.getProperty("weblogic.Name");
        }

        return System.getProperty("user.dir") + File.separator + "servers" + File.separator + sServerName + File.separator + "config";
    }

    public static Map<String, String> printBundle(ResourceBundle bundle) {
        Enumeration<String> keys = bundle.getKeys();
        Map<String, String> items = new HashMap<>();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            // LOGGER.error("" + key + ", value=" + bundle.getString(key));
            items.put(key, bundle.getString(key));
        }
        return items;
    }

}
