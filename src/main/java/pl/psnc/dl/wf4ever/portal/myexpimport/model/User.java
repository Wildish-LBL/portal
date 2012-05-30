/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * myExperiment user metadata.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "user")
public class User implements Serializable {

    /** id. */
    private static final long serialVersionUID = 4428655508723987304L;

    /** XML with metadata URI. */
    private String uri;

    /** Format-agnostic user URI. */
    private String resource;

    /** User id in myExperiment. */
    private int id;

    /** User openId. Might not be the same across different web applications (i.e. Google). */
    private String openId;

    /** User name. */
    private String name;

    /** User email. */
    private String email;

    /** User city. */
    private String city;

    /** User country. */
    private String country;

    /** User website. */
    private String website;

    /** User packs. */
    private List<PackHeader> packs = new ArrayList<PackHeader>();

    /** User workflows. */
    private List<WorkflowHeader> workflows = new ArrayList<WorkflowHeader>();

    /** User files. */
    private List<FileHeader> files = new ArrayList<FileHeader>();


    /**
     * Default constructor.
     */
    public User() {

    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    @XmlElement(name = "openid-url")
    public String getOpenId() {
        return openId;
    }


    public void setOpenId(String openId) {
        this.openId = openId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public String getWebsite() {
        return website;
    }


    public void setWebsite(String website) {
        this.website = website;
    }


    @XmlAttribute
    public String getUri() {
        return uri;
    }


    public void setUri(String uri) {
        this.uri = uri;
    }


    @XmlAttribute
    public String getResource() {
        return resource;
    }


    public void setResource(String resource) {
        this.resource = resource;
    }


    @XmlElementWrapper(name = "workflows")
    @XmlElement(name = "workflow")
    public List<WorkflowHeader> getWorkflows() {
        return workflows;
    }


    public void setWorkflows(List<WorkflowHeader> workflows) {
        this.workflows = workflows;
    }


    @XmlElementWrapper(name = "packs")
    @XmlElement(name = "pack")
    public List<PackHeader> getPacks() {
        return packs;
    }


    public void setPacks(List<PackHeader> packs) {
        this.packs = packs;
    }


    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    public List<FileHeader> getFiles() {
        return files;
    }


    public void setFiles(List<FileHeader> files) {
        this.files = files;
    }

}
