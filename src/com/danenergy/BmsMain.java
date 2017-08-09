package com.danenergy;


import com.danenergy.common.ResourcesGuiceModule;
import com.danenergy.inject.MainLogicGuiceModule;
import com.danenergy.logic.MainLogic;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;

public class BmsMain {
    //logging
    final static Logger logger = Logger.getLogger(BmsMain.class);

    public static void main(String[] args)
    {
        logger.info("Application staring");

        Injector guice = Guice.createInjector(new MainLogicGuiceModule(),new ResourcesGuiceModule());
        MainLogic logic = guice.getInstance(MainLogic.class);
        logic.start();

        try
        {
            logger.info("Application running, press key to stop");
            while(true){}
        }
        catch (Exception e)
        {
            logger.error("Error in main loop",e);
        }
        finally {
            logger.info("Stopping Application");

            logic.stop();

            logger.info("Application stopped");
        }
    }
}
