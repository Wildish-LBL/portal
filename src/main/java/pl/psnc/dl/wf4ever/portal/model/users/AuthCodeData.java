package pl.psnc.dl.wf4ever.portal.model.users;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OAuth 2.0 authorization code.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@Entity
@Table(name = "tempAuthCodeData")
public class AuthCodeData {

    /** the code. */
    private String code;

    /** OAuth redirection URI. */
    private String providedRedirectURI;

    /** user id. */
    private String userId;

    /** client application id. */
    private String clientId;

    /** authorization code creation date. */
    private Date created;


    /**
     * Constructor.
     * 
     * @param code
     *            the code
     * @param providedRedirectURI
     *            OAuth redirection URI
     * @param userId
     *            user id
     * @param clientId
     *            client application id
     */
    public AuthCodeData(String code, String providedRedirectURI, String userId, String clientId) {
        this.code = code;
        this.providedRedirectURI = providedRedirectURI;
        this.userId = userId;
        this.clientId = clientId;
        this.created = new Date();
    }


    @Id
    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public String getProvidedRedirectURI() {
        return providedRedirectURI;
    }


    public void setProvidedRedirectURI(String providedRedirectURI) {
        this.providedRedirectURI = providedRedirectURI;
    }


    @Basic
    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Basic
    public String getClientId() {
        return clientId;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    @Basic
    public Date getCreated() {
        return created;
    }


    public void setCreated(Date created) {
        this.created = created;
    }

}
