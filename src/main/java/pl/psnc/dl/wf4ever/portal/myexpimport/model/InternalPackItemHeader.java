/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;

/**
 * Short metadata of myExperiment internal pack item, as in pack metadata.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class InternalPackItemHeader extends BaseResourceHeader {

    /** id. */
    private static final long serialVersionUID = 1547898914095065327L;


    @Override
    public URI getResourceUrl() {
        return URI.create(getUri().toString() + "&elements=id,item");
    }


    @Override
    public Class<InternalPackItem> getResourceClass() {
        return InternalPackItem.class;
    }

}
