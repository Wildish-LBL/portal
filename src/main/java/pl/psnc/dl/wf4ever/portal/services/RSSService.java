package pl.psnc.dl.wf4ever.portal.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * The service runs a background thread that periodically creates/updates RSS feeds.
 * 
 * @author piotrekhol
 * 
 */
public class RSSService {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(RSSService.class);

    /** Interval between feed updates, in ms. */
    public static final long INTERVAL = 15 * 60 * 1000;

    /** Interval between getting user URI and resolving its name, in ms. */
    public static final long USER_RESOLUTION_INTERVAL = 60 * 1000;

    /** The background thread. */
    private final UpdatingThread updatingThread;

    /** File name of recent ROs feed. */
    public static final String RECENT_ROS_FILENAME = "feed.xml";


    /**
     * Constructor.
     * 
     * @param basePath
     *            application path
     * @param sparqlEndpoint
     *            SPARQL endpoint URI
     * @param rodl
     *            RODL URI, for creator resolution
     */
    public RSSService(String basePath, URI sparqlEndpoint, URI rodl) {
        updatingThread = new UpdatingThread();
        updatingThread.setSparqlEndpoint(sparqlEndpoint);
        updatingThread.setRodl(rodl);
        updatingThread.setBasePath(basePath);
    }


    /**
     * Starts the background feed updating thread. Can be called only once.
     * 
     */
    public void start() {
        if (updatingThread.isAlive()) {
            throw new IllegalStateException("RSS updating thread has already been started");
        }
        updatingThread.start();
    }


    /**
     * Feed update thread.
     * 
     * @author piotrekhol
     * 
     */
    private class UpdatingThread extends Thread {

        /** SPARQL endpoint. */
        private URI sparqlEndpoint;

        /** RODL URI. */
        private URI rodl;

        /** Base path of the webapp. */
        private String basePath;


        /**
         * Constructor.
         */
        public UpdatingThread() {
            setDaemon(true);
        }


        @Override
        public void run() {
            try {
                do {
                    try {
                        SyndFeed feed = generateRecentROsFeed();
                        sleep(USER_RESOLUTION_INTERVAL);
                        saveFeed(feed, RECENT_ROS_FILENAME);
                    } catch (IOException | FeedException e) {
                        LOG.error("Could not generate RSS", e);
                    }
                    sleep(INTERVAL - USER_RESOLUTION_INTERVAL);
                } while (!isInterrupted());
            } catch (InterruptedException e) {
                LOG.warn("RSS updating thread interrupted", e);
            }
        }


        /**
         * Generate a feed of 5 most recent ROs.
         * 
         * @return the ATOM feed
         * @throws IOException
         *             when could not connect to SPARQL endpoint
         */
        @SuppressWarnings("unchecked")
        private SyndFeed generateRecentROsFeed()
                throws IOException {
            //FIXME it shouldn't be hardcoded
            ROSRService rosrs = new ROSRService(getRodl().resolve("ROs/"), null);
            List<ResearchObject> ros = RODLUtilities.getMostRecentROs(getSparqlEndpoint(), rosrs, 5);

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("atom_1.0");
            feed.setTitle("5 most recent Research Objects in RODL");
            feed.setPublishedDate(new Date());

            for (ResearchObject ro : ros) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(ro.getName());
                entry.setLink(ro.getUri().toString());
                if (ro.getCreated() != null) {
                    entry.setUpdatedDate(ro.getCreated().toDate());
                }
                SyndContent description = new SyndContentImpl();
                description.setValue(ro.getName());
                description.setType("text");
                entry.setDescription(description);
                if (ro.getAuthor() != null) {
                    entry.setAuthor(ro.getAuthor().getName());
                }
                feed.getEntries().add(entry);
            }
            LOG.debug("Generated recent ROs feed");
            return feed;
        }


        /**
         * Saves a feed to a file.
         * 
         * @param feed
         *            feed
         * @param filename
         *            feed filename
         * @throws IOException
         *             when there are problems with saving the file
         * @throws FeedException
         *             when the feed cannot be saved
         */
        private void saveFeed(SyndFeed feed, String filename)
                throws IOException, FeedException {
            final Writer writer = new FileWriter(getBasePath() + "/" + filename);
            final SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
            LOG.info("Saved recent ROs feed under: " + basePath + "/" + filename);
        }


        public URI getSparqlEndpoint() {
            return sparqlEndpoint;
        }


        public void setSparqlEndpoint(URI sparqlEndpoint) {
            this.sparqlEndpoint = sparqlEndpoint;
        }


        public URI getRodl() {
            return rodl;
        }


        public void setRodl(URI rodl) {
            this.rodl = rodl;
        }


        public String getBasePath() {
            return basePath;
        }


        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

}
