FROM scottysinclair/java8

ENV BATCH_HOME /opt/stock-application-batch

RUN mkdir $BATCH_HOME

COPY target/stock-application-batch-*.jar $BATCH_HOME

#echo "java -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=y" \


RUN echo "log4j.rootLogger=INFO, stdout" > $BATCH_HOME/log4j.properties && \
echo "log4j.appender.stdout=org.apache.log4j.ConsoleAppender" >> $BATCH_HOME/log4j.properties && \
echo "log4j.appender.stdout.Target=System.out" >> $BATCH_HOME/log4j.properties && \
echo "log4j.appender.stdout.layout=org.apache.log4j.PatternLayout" >> $BATCH_HOME/log4j.properties && \
echo "log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n" >> $BATCH_HOME/log4j.properties && \
echo "log4j.logger.org.arquillian=DEBUG, stdout" >> $BATCH_HOME/log4j.properties && \
\
\
echo "#!/bin/bash" > $BATCH_HOME/run_integration.sh && \
echo "cd /opt/stock-application-batch" >> $BATCH_HOME/run_integration.sh && \
echo "java -cp stock-application-batch-*.jar \
-Dlog4j.configuration=file:log4j.properties \
com.acme.spring.hibernate.batch.integration.Application" >> $BATCH_HOME/run_integration.sh && \
\
\
chmod uga+x $BATCH_HOME/run_integration.sh && \
\
\
echo "new.jdbcurl=jdbc:postgresql://db:5432/test_db" > $BATCH_HOME/application.properties && \
echo "new.user=test_user" >> $BATCH_HOME/application.properties && \
echo "new.password=password" >> $BATCH_HOME/application.properties && \
echo "new.driver=org.postgresql.Driver" >> $BATCH_HOME/application.properties && \
echo "old.jdbcurl=jdbc:db2://olddb:50000/test" >> $BATCH_HOME/application.properties && \
echo "old.user=db2inst1" >> $BATCH_HOME/application.properties && \
echo "old.password=password" >> $BATCH_HOME/application.properties && \
echo "old.driver=com.ibm.db2.jcc.DB2Driver" >> $BATCH_HOME/application.properties

#Expose a port for remote debugging the integration batch.
EXPOSE 8000

#the echo to the console use used by the arquillian await stratgey.
ENTRYPOINT echo "batch container up" && tail -f /dev/null

