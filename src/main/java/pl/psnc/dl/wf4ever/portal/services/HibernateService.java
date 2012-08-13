/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import pl.psnc.dl.wf4ever.portal.model.users.AuthCodeData;

/**
 * This service handles storing OAuth temporary authorization codes in database.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public final class HibernateService {

    /** session factory. */
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();


    /**
     * Private constructor.
     */
    private HibernateService() {
        // nope
    }


    /**
     * Creates a session factory from hibernate.cfg.xml.
     * 
     * @return the session factory
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }


    /**
     * Save an OAuth authorization code.
     * 
     * @param data
     *            authorization code
     */
    public static void storeCode(AuthCodeData data) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        session.saveOrUpdate(data);

        session.getTransaction().commit();
    }


    /**
     * Delete an OAuth authorization code.
     * 
     * @param data
     *            authorization code
     */
    public static void deleteCode(AuthCodeData data) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        session.delete(data);

        session.getTransaction().commit();
    }


    /**
     * Read OAuth authorization code details.
     * 
     * @param code
     *            the code value
     * @return authorization code data or null
     */
    public static AuthCodeData loadCode(String code) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        AuthCodeData data = (AuthCodeData) session.get(AuthCodeData.class, code);

        session.getTransaction().commit();

        return data;
    }
}
