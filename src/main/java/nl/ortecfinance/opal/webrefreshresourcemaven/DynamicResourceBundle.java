package nl.ortecfinance.opal.webrefreshresourcemaven;

import app.owf.constants.ApplicationConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 * This class makes it possible to override resource value in 'messages_xx_yy.properties' by providing alternative
 * values in 'custom_xx_yy.properties'.
 *
 */
public class DynamicResourceBundle extends ResourceBundle {

    protected static final String BASE_NAME = "Xmessages";

    private static final String DUMMY_BUNDLE = "unused_but_necessary";
    protected static final Control DB_CONTROL = new CustomControl();

    private Map<String, String> messages;

    public DynamicResourceBundle() {
        setParent(ResourceBundle.getBundle(DUMMY_BUNDLE,
                FacesContext.getCurrentInstance().getViewRoot().getLocale(), DB_CONTROL));
    }

    protected DynamicResourceBundle(Map<String, String> messages, Locale locale) {
        this.messages = messages;
    }

    @Override
    protected Object handleGetObject(String key) {
        return messages != null ? messages.get(key) : parent.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return messages != null ? Collections.enumeration(messages.keySet()) : parent.getKeys();
    }

    protected static class CustomControl extends Control {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {

            Map<String, String> msgMap = new HashMap<>();
            loadInAppProperties(msgMap, locale);//load texts from property files within OPAL project
            overwrite(msgMap, locale);//load texts from property files in server config folder (overwrite OPAL project texts with same key)

            return new DynamicResourceBundle(msgMap, locale);
        }

        private void loadInAppProperties(Map<String, String> msgMap, Locale locale) {
            msgMap.putAll(getProperties(ResourceBundle.getBundle(BASE_NAME, locale)));
        }

        private void overwrite(Map<String, String> msgMap, Locale locale) throws FileNotFoundException, IOException {
            if (locale != null && locale.toString().length() != 0) {
                File file = new File(ApplicationConfig.getApplicationConfigDirectory() + "/" + BASE_NAME + "_" + locale.toString() + ".properties");
                if (file.exists()) {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(file));
                    for (Map.Entry<Object, Object> entrySet : prop.entrySet()) {
                        msgMap.put((String) entrySet.getKey(), (String) entrySet.getValue());
                    }
                }
            }
        }
    }

    public static Map<String, String> getProperties(ResourceBundle bundle) {
        Enumeration<String> keys = bundle.getKeys();
        Map<String, String> items = new HashMap<>();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            items.put(key, bundle.getString(key));
        }
        return items;
    }

}
