package pl.psnc.dl.wf4ever.portal.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.model.CreateFromZipProgressModel;

/**
 * Thread that creates a new RO based on a ZIP archive with folders and resources.
 * 
 * @author piotrekhol
 * 
 */
public class CreateROThread implements Runnable {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(CreateROThread.class);

    /** ZIP archive input stream. */
    private InputStream zip;

    /** ZIP archive name. */
    private String zipName;

    /** ROSRS client. */
    private ROSRService rosrs;

    /** Progress indicators. */
    private final IModel<CreateFromZipProgressModel> progressModel;

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
     *            ROSRS
     * @param progressModel
     *            Model of the object in which the progress will be stored
     */
    public CreateROThread(InputStream zip, String zipName, ROSRService rosrs,
            IModel<CreateFromZipProgressModel> progressModel) {
        this.zip = zip;
        this.zipName = zipName;
        this.rosrs = rosrs;
        this.progressModel = progressModel;
    }


    @Override
    public void run() {
        try {
            progressModel.getObject().setStartTime(DateTime.now());
            File temp = File.createTempFile(zipName, ".zip");
            FileOutputStream outputStream = new FileOutputStream(temp);
            log("Uploading the archive... ");
            IOUtils.copy(zip, outputStream);
            log("done.\n");
            Map<String, Folder> createdFolders = new HashMap<>();
            try (ZipFile zipFile = new ZipFile(temp)) {
                log("Found " + zipFile.size() + " entries.\n");
                progressModel.getObject().setTotal(zipFile.size());
                ResearchObject ro = createRO();
                progressModel.getObject().setRoUri(ro.getUri());
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    addEntry(ro, entry.getName(), zipFile.getInputStream(entry), createdFolders);
                    updateEstimatedTime();
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
            progressModel.getObject().setThreadState(CreateFromZipProgressModel.State.TERMINATED);
        }
    }


    /**
     * Update the estimated time based on time elapsed and number of entries parsed.
     */
    private void updateEstimatedTime() {
        if (progressModel.getObject().getComplete() == 0) {
            return;
        }
        long oneTaskMillis = progressModel.getObject().getTimeElapsed().getMillis()
                / progressModel.getObject().getComplete();
        long allTasksMillis = oneTaskMillis * progressModel.getObject().getTotal();
        progressModel.getObject().setEndTime(progressModel.getObject().getStartTime().plus(allTasksMillis));
    }


    /**
     * Write a text to the output.
     * 
     * @param text
     *            the text
     */
    private void log(String text) {
        progressModel.getObject().appendToOutput(text);
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
                    f.load();
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
        progressModel.getObject().incrementComplete();
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

}
