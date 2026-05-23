package com.csw.listener;

import com.csw.db.Database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Database.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Database.shutdown();
    }
}
