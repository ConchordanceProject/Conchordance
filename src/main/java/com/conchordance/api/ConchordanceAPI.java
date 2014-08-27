package com.conchordance.api;

import com.conchordance.Conchordance;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.conchordance.api.resources.ChordResource;
import com.conchordance.api.resources.FretboardResource;
import com.conchordance.api.resources.InstrumentResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class ConchordanceAPI extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new ConchordanceAPI().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
    }

    @Override
    public void run(Configuration config, Environment environment) {
        Conchordance conchordance = new Conchordance();

        // resources
        environment.jersey().register(new ChordResource(conchordance));
        environment.jersey().register(new FretboardResource(conchordance));
        environment.jersey().register(new InstrumentResource());

        // CORS support
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedHeaders", "Authorization,Content-Type,X-Api-Key,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,POST,PUT,DELETE,OPTIONS");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");
    }
}
