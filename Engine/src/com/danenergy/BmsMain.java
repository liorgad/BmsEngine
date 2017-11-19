package com.danenergy;


import com.danenergy.common.ResourcesGuiceModule;
import com.danenergy.common.Utilities;
import com.danenergy.inject.MainLogicGuiceModule;
import com.danenergy.logic.MainLogic;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

public class BmsMain {
    //logging
    //final static Logger logger = Logger.getLogger(BmsMain.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();


    public static void main(String[] args)
    {
        //System.setProperty("log4j.configurationFile","resources/log4j.properties");

        try
        {
            logger.info("Application staring");

            Injector guice = Guice.createInjector(new MainLogicGuiceModule(),new ResourcesGuiceModule());
            MainLogic logic = guice.getInstance(MainLogic.class);

            String key =logic.configuration.getKey();

            if(StringUtils.isEmpty(key))
            {
                System.out.println("License missing please contact support");
                System.exit(1);
            }

            boolean isAuthorised = Utilities.CheckLicense(key);

            if(!isAuthorised)
            {
                System.out.println("License mismatch please contact support");
                System.exit(1);
            }

            logic.start();


            //logger.info("Application running, press key to stop");
            //while(true){}
        }
        catch (Exception e)
        {
            logger.error("Error in main loop",e);
        }
        finally {
            //logger.info("Stopping Application");

            //logic.stop();

            //logger.info("Application stopped");
        }
    }
}
