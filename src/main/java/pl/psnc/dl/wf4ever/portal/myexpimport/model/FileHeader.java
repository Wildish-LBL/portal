/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Short myExperiment file metadata.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "file")
public class FileHeader extends BaseResourceHeader {

    /** id. */
    private static final long serialVersionUID = 1547898914095065327L;


    @Override
    public URI getResourceUrl() {
        return URI.create(getUri().toString() + "&elements=filename,content,content-type,id,title");
    }


    @Override
    public Class<File> getResourceClass() {
        return File.class;
    }

}
