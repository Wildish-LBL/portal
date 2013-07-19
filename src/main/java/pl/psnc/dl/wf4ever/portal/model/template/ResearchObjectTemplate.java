package pl.psnc.dl.wf4ever.portal.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * A template for a Research Object. Currently it defines the initial folder structure.
 * 
 * @author piotrekhol
 * 
 */
public class ResearchObjectTemplate implements Serializable {

    /** id. */
    private static final long serialVersionUID = -1006576456091117809L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ResearchObjectTemplate.class);

    /** An unmodifiable list of templates read from the configuration file. */
    public static final List<ResearchObjectTemplate> VALUES = load("template.properties");

    /** Short template title. */
    private final String title;

    /** Template description. */
    private final String description;

    /** An unmodifiable set of folders that will be created. */
    private final Set<? extends FolderStub> folderStubs;


    /**
     * Constructor.
     * 
     * @param title
     *            Short title
     * @param description
     *            Description
     * @param folders
     *            A collection of folders that will be created
     */
    public ResearchObjectTemplate(String title, String description, Set<? extends FolderStub> folders) {
        this.title = title;
        this.description = description;
        this.folderStubs = Collections.unmodifiableSet(folders);
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public Collection<? extends FolderStub> getFolders() {
        return folderStubs;
    }


    /**
     * Load the templates.
     * 
     * @param foldersPropertiesFilename
     *            filename of properties file
     * @return a set of templates
     */
    static List<ResearchObjectTemplate> load(String foldersPropertiesFilename) {
        PropertiesConfiguration props;
        try {
            props = new PropertiesConfiguration(foldersPropertiesFilename);
            List<Object> templateNames = props.getList("templates");
            List<ResearchObjectTemplate> templates2 = new ArrayList<>();
            for (Object templateName : templateNames) {
                templates2.add(loadTemplate(props, templateName.toString()));
            }
            return Collections.unmodifiableList(templates2);
        } catch (ConfigurationException e) {
            LOGGER.error("Cannot initialize the folder templates", e);
            return Collections.emptyList();
        }
    }


    /**
     * Create a template based on the configuration file.
     * 
     * @param props
     *            the configuration file
     * @param templateName
     *            template name in the file
     * @return a template
     */
    private static ResearchObjectTemplate loadTemplate(PropertiesConfiguration props, String templateName) {
        String title = props.getString(templateName + ".title", templateName);
        String description = props.getString(templateName + ".description", "No description");
        List<Object> folderNames = props.getList(templateName + ".folders");
        Map<String, FolderStub> stubs = new HashMap<>();
        Map<String, Set<FolderStub>> entries = new HashMap<>();
        for (Object folderName : folderNames) {
            String folderName2 = folderName.toString();
            entries.put(folderName2, new HashSet<FolderStub>());
            stubs.put(folderName2, new FolderStub(folderName2, entries.get(folderName2)));
        }
        for (String folderName : stubs.keySet()) {
            List<Object> entryNames = props.getList(templateName + "." + folderName + ".entries");
            for (Object entryName : entryNames) {
                String entryName2 = entryName.toString();
                if (!stubs.containsKey(entryName2)) {
                    LOGGER.warn("Folder " + folderName + " has an entry to an unknown folder " + entryName);
                    continue;
                }
                entries.get(folderName).add(stubs.get(entryName2));
            }
        }
        return new ResearchObjectTemplate(title, description, new HashSet<>(stubs.values()));
    }


    /**
     * Create a new research object according to the template.
     * 
     * @param rosrs
     *            the ROSR service
     * @param roIdentifier
     *            RO identifier
     * @return an RO
     * @throws ROSRSException
     *             when ROSR service returned an unexpected response
     * @throws ROException
     *             when ROSR service returned incorrect data
     */
    public ResearchObject create(ROSRService rosrs, String roIdentifier)
            throws ROSRSException, ROException {
        ResearchObject ro = ResearchObject.create(rosrs, roIdentifier);
        Map<FolderStub, Folder> folders = new HashMap<>();
        for (FolderStub stub : folderStubs) {
            folders.put(stub, ro.createFolder(stub.getPath()));
        }
        for (FolderStub stub : folderStubs) {
            folders.get(stub).load(false);
            for (FolderStub subfolder : stub.getSubfolders()) {
                folders.get(stub).addEntry(folders.get(subfolder), null);
            }
        }
        return ro;
    }

}
