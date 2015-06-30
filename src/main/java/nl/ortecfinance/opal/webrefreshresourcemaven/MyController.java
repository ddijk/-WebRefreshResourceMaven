package nl.ortecfinance.opal.webrefreshresourcemaven;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.apache.log4j.Logger;

@ManagedBean
public class MyController {

    private static final Logger LOGGER = Logger.getLogger(MyController.class);

    String aap = "blah";

    public String getAap() {
        return aap;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Init");
    }
}
