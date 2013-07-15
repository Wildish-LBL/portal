package pl.psnc.dl.wf4ever.portal.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * Thread that creates a new RO based on a ZIP archive with folders and resources.
 * 
 * @author piotrekhol
 * 
 */
public class CreateROThread extends Thread {

    /**
     * Thread state set in the run() method.
     * 
     * @author piotrekhol
     * 
     */
    public enum State {
        /** Thread is running. */
        RUNNING,
        /** Thread finished successfully or not. */
        TERMINATED
    }


    /**
     * Indicators of the process progress.
     * 
     * @author piotrekhol
     * 
     */
    public static class ProgressModel implements Serializable {

        /** id. */
        private static final long serialVersionUID = 4410500942718651694L;

        /** Is the thread running or not. */
        private State threadState = State.RUNNING;

        /** Number of completed steps or null if unknown yet. */
        private Integer complete = null;

        /** Number of total steps or null if unknown yet. */
        private Integer total = null;

        /** The output for the user. */
        private StringBuilder outputStringBuilder = new StringBuilder();

        /** The URI of the created RO. */
        private URI roUri = null;


        public synchronized State getThreadState() {
            return threadState;
        }


        public synchronized void setThreadState(State threadState) {
            this.threadState = threadState;
        }


        public synchronized Integer getComplete() {
            return complete;
        }


        /**
         * Increment the complete steps counter.
         */
        public synchronized void incrementComplete() {
            this.complete++;
        }


        public synchronized Integer getTotal() {
            return total;
        }


        /**
         * Reset the step counter.
         * 
         * @param total
         *            total steps
         */
        public synchronized void setTotal(Integer total) {
            this.complete = 0;
            this.total = total;
        }


        public synchronized String getOutputString() {
            return outputStringBuilder.toString();
        }


        /**
         * Append text to the string builder.
         * 
         * @param text
         *            text to append
         */
        public synchronized void appendToOutput(String text) {
            outputStringBuilder.append(text);
        }


        public synchronized URI getRoUri() {
            return roUri;
        }


        public synchronized void setRoUri(URI roUri) {
            this.roUri = roUri;
        }
    }


    /** Logger. */
    private static final Logger LOG = Logger.getLogger(CreateROThread.class);

    /** ZIP archive input stream. */
    private InputStream zip;

    /** ZIP archive name. */
    private String zipName;

    /** ROSRS client. */
    private ROSRService rosrs;

    /** Progress indicators. */
    private ProgressModel progressModel;

    /** MIME type guessing utility. */
    private static MimetypesFileTypeMap mfm = new MimetypesFileTypeMap();

    static {
        try (InputStream mimeTypesIs = CreateROThread.class.getClassLoader().getResourceAsStream("mime.types")) {
            mfm = new MimetypesFileTypeMap(mimeTypesIs);
        } catch (IOException e) {
            LOG.error("Can't initialize mime types", e);
        }
    }


    /**
     * Constructor.
     * 
     * @param zip
     *            the ZIP input stream
     * @param zipName
     *            ZIP archive name
     * @param rosrs
     *            ROSRS client
     */
    public CreateROThread(InputStream zip, String zipName, ROSRService rosrs) {
        this.zip = zip;
        this.zipName = zipName;
        this.rosrs = rosrs;
        this.progressModel = new ProgressModel();
    }


    @Override
    public void run() {
        try {
            File temp = File.createTempFile(zipName, ".zip");
            FileOutputStream outputStream = new FileOutputStream(temp);
            log("Uploading the archive... ");
            IOUtils.copy(zip, outputStream);
            log("done.\n");
            Map<String, Folder> createdFolders = new HashMap<>();
            try (ZipFile zipFile = new ZipFile(temp)) {
                log("Found " + zipFile.size() + " entries.\n");
                progressModel.setTotal(zipFile.size());
                ResearchObject ro = createRO();
                progressModel.setRoUri(ro.getUri());
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    addEntry(ro, entry.getName(), zipFile.getInputStream(entry), createdFolders);
                }
            }
            log("FINISHED");
        } catch (IOException | ROSRSException | ROException e) {
            log("\nERROR: " + e.getLocalizedMessage());
            LOG.error("Error when creating an RO from ZIP", e);
        } finally {
            try {
                zip.close();
            } catch (IOException e) {
                log("\nERROR: " + e.getLocalizedMessage());
                LOG.error("Error when closing the ZIP file", e);
            }
            progressModel.setThreadState(State.TERMINATED);
        }
    }


    /**
     * Write a text to the output.
     * 
     * @param text
     *            the text
     */
    private void log(String text) {
        progressModel.appendToOutput(text);
    }


    /**
     * Add a new resource and all necessary folders.
     * 
     * @param ro
     *            the RO
     * @param name
     *            resource path
     * @param inputStream
     *            resource content
     * @param createdFolders
     *            already created folders
     * @throws ROSRSException
     *             when RODL complains
     * @throws ROException
     *             when RODL returns incorrect data
     */
    private void addEntry(ResearchObject ro, String name, InputStream inputStream, Map<String, Folder> createdFolders)
            throws ROSRSException, ROException {
        //TODO can we make it more general?
        Path path = Paths.get(name);
        if (name.endsWith("/") || path.getFileName().toString().startsWith(".")) {
            log("Skipping " + name + ".\n");
        } else {
            log("Adding " + name + "... ");
            String contentType = mfm.getContentType(name);
            Resource resource = ro.aggregate(name, inputStream, contentType);
            boolean parentExisted = false;
            while (path.getParent() != null && !parentExisted) {
                if (!createdFolders.containsKey(path.getParent().toString())) {
                    Folder f = ro.createFolder(path.getParent().toString());
                    f.load(false);
                    createdFolders.put(path.getParent().toString(), f);
                } else {
                    parentExisted = true;
                }
                Resource current = createdFolders.containsKey(path.toString()) ? createdFolders.get(path.toString())
                        : resource;
                createdFolders.get(path.getParent().toString()).addEntry(current, null);
                path = path.getParent();
            }
            log("done.\n");
        }
        progressModel.incrementComplete();
    }


    /**
     * Create a new empty RO.
     * 
     * @return the new RO
     * @throws ROSRSException
     *             when RODL complains
     */
    private ResearchObject createRO()
            throws ROSRSException {
        log("Creating the research object... ");
        String roId = zipName.endsWith(".zip") ? zipName.substring(0, zipName.length() - ".zip".length()) : zipName;
        ResearchObject ro = ResearchObject.create(rosrs, roId);
        log("done.\n");
        log("The RO is " + ro.getUri() + "\n");
        return ro;
    }


    public ProgressModel getProgressModel() {
        return progressModel;
    }
}
