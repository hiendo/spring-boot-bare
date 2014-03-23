package com.github.hiendo.bare.db.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

// http://www.datastax.com/documentation/developer/java-driver/2.0/java-driver/quick_start/qsSimpleClientAddSession_t.html
@Test
public class TimeSeriesRepositoryTests {

    private TimeSeriesRepository timeSeriesRepository;
    protected String keyspace = "tsa";

    protected Session session;
    protected Cluster cluster;

    @BeforeClass
    public void setupDbBeforeClass() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        cluster = new Cluster.Builder().addContactPoints("localhost").withPort(9142).build();

        CQLDataLoader dataLoader = new CQLDataLoader("localhost", 9142);
        dataLoader.load(new ClassPathCQLDataSet("schema.cql"));
        session = cluster.connect(keyspace);

        timeSeriesRepository = new TimeSeriesRepository(session);
    }

    @AfterClass
    public void cleanupDbAfterClass() {
        cluster.shutdown();
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Test
    public void canInsertTimeSeriesData() throws Exception {
        TimeSeriesRepository timeSeriesRepository = new TimeSeriesRepository(session);
        long now = new Date().getTime();

        timeSeriesRepository.saveTime("topic", 2.54);
        timeSeriesRepository.saveTime("topic", 2.55);

        DataPoints dataPoints = timeSeriesRepository.getAllDataPointsForTopic("topic");
        assertThat("Data points", dataPoints, notNullValue());
        assertThat("Data points size", dataPoints.size(), is(2));
        assertThat("Data points time", dataPoints.getTimeAt(0), allOf(greaterThan(now - 100), lessThan(now + 100)));
        assertThat("Data points time", dataPoints.getTimeAt(1), allOf(greaterThan(now - 100), lessThan(now + 100)));
        assertThat("Data points value", dataPoints.getValueAt(0), closeTo(2.54, .01));
        assertThat("Data points value", dataPoints.getValueAt(1), closeTo(2.55, .01));
    }

    @Test
    public void canGetTimeSeriesForTopicWithNoData() throws Exception {
        DataPoints dataPoints = timeSeriesRepository.getAllDataPointsForTopic("non.existing.topic");

        assertThat("Data points", dataPoints, notNullValue());
        assertThat("Data points size", dataPoints.size(), is(0));
    }
}
