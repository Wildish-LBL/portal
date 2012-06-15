/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model.users;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity that represents an OAuth 2.0 access token received from RODL.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "access-token")
public class AccessToken implements Serializable {

    /** id. */
    private static final long serialVersionUID = 8724845005623981779L;

    /** the access token. */
    private String token;

    /** RODL. */
    private OAuthClient client;

    /** Token creation date. */
    private Date created;

    /** Token last used date. */
    private Date lastUsed;


    @Id
    @XmlElement
    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    @ManyToOne
    @JoinColumn(nullable = false)
    @XmlElement
    public OAuthClient getClient() {
        return client;
    }


    public void setClient(OAuthClient client) {
        this.client = client;
    }


    @Basic
    @XmlElement
    public Date getCreated() {
        return created;
    }


    public void setCreated(Date created) {
        this.created = created;
    }


    @Basic
    @XmlElement
    public Date getLastUsed() {
        return lastUsed;
    }


    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

}
