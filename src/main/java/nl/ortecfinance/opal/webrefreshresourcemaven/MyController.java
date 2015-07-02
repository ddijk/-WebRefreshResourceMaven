package nl.ortecfinance.opal.webrefreshresourcemaven;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

@ManagedBean
public class MyController {

    private static final Logger LOGGER = Logger.getLogger(MyController.class);

    String locale;

    String browserLocale;

    String aap = "blah";

    public String getAap() {
        return aap;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Init, default locale is  " + Locale.getDefault());
    }

    public String getLocale() {
        locale = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
        return locale;
    }

    public String getBrowserLocale() {
        browserLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale().toString();
        return browserLocale;
    }

    public void clearCache() {
        ResourceBundle.clearCache();

        LOGGER.info("clear cache ...");
    }

}
