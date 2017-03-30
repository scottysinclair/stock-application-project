package com.acme.spring.hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for executing the integration job.</br>
 *</br>
 * Expects the system environment variable DOCKER_SERVER_URI to be set.
 *
 * @author scott
 *
 */
public class IntegrationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationHelper.class);

    private static final String BATCH_CONTAINER_NAME = "batch";
    private static final String INTEGRATION_SCRIPT = "/opt/stock-application-batch/run_integration.sh";

    /**
     * Executes the integration batch job using docker client to execute the script
     * on the batch docker container.
     */
    public static void executeIntegration() {

       final String serverUri = System.getenv("DOCKER_SERVER_URI");
       if (serverUri == null || serverUri.isEmpty()) {
            throw new IllegalStateException("DOCKER_SERVER_URI environment variable is missing");
       }

       /**
        * JBoss uses the Reseteasy JAX-RS implementation which prevents the Java DockerClient from
        * executing within the same JVM, therefore we launch a separate Java process.</br>
        */
        ProcessBuilder builder = new ProcessBuilder(
                "/opt/dockerclient/exec.sh",
                serverUri,
                BATCH_CONTAINER_NAME,
                INTEGRATION_SCRIPT
            )
            .redirectErrorStream(true);

        try {
        Process p = builder.start();

        try (BufferedReader bin = new BufferedReader( new InputStreamReader( p.getInputStream() ) )){
              bin.lines().forEach(IntegrationHelper::logLine);
            }


        int returnCode = p.waitFor();
        if (returnCode != 0){
          throw new IllegalStateException("Docker client process did not terminate successfully");
        }
        } catch (InterruptedException e) {
        throw new IllegalStateException("Docker client execution was interrupted");
        }
        catch(IOException x) {
        throw new IllegalStateException("Error executing process", x);
        }
    }

    private static void logLine(String line) {
        LOG.info(line);
    }

}
