package pl.psnc.dl.wf4ever.portal.model.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

/**
 * Test.
 * 
 * @author piotrekhol
 * 
 */
public class ResearchObjectTemplateTest {

    /**
     * See name.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void shouldLoadAllTemplates() {
        List<ResearchObjectTemplate> templates = ResearchObjectTemplate.load("testtemplates.properties");
        assertThat(templates, hasSize(1));
        ResearchObjectTemplate template = templates.get(0);
        assertThat(template.getTitle(), equalTo("A test title"));
        assertThat(template.getDescription(), equalTo("This is a test description. It can go many lines."));
        assertThat(template.getFolders(), hasSize(4));
        // cast to List of Objects
        Collection<?> tmp = (Collection<?>) template.getFolders();
        Collection<Object> folders = (Collection<Object>) tmp;
        assertThat(
            folders,
            hasItem(allOf(hasProperty("path", equalTo("folder1")),
                hasProperty("subfolders", contains(hasProperty("path", equalTo("folder1/folder1a")))))));
        assertThat(folders,
            hasItem(allOf(hasProperty("path", equalTo("folder1/folder1a")), hasProperty("subfolders", empty()))));
        assertThat(
            folders,
            hasItem(allOf(
                hasProperty("path", equalTo("folder 2")),
                hasProperty(
                    "subfolders",
                    containsInAnyOrder(hasProperty("path", equalTo("folder1/folder1a")),
                        hasProperty("path", equalTo("folder3")))))));
        assertThat(folders, hasItem(allOf(hasProperty("path", equalTo("folder3")), hasProperty("subfolders", empty()))));
    }
}
