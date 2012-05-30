/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Short myExperiment pack metadata.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "pack")
public class PackHeader extends BaseResourceHeader {

    /** id. */
    private static final long serialVersionUID = 3146768347985843474L;


    @Override
    public URI getResourceUrl() {
        return URI.create(getUri().toString() + "&elements=internal-pack-items,id,title");
    }


    @Override
    public Class<Pack> getResourceClass() {
        return Pack.class;
    }

}
