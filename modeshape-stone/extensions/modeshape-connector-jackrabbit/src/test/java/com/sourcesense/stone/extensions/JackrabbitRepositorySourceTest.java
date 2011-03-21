package com.sourcesense.stone.extensions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.lang.reflect.Field;
import net.jcip.annotations.ThreadSafe;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.connector.jcr.JcrRepositoryConnection;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositorySourceCapabilities;
import org.modeshape.graph.connector.RepositorySourceException;

public class JackrabbitRepositorySourceTest {

    private JackrabbitRepositorySource repositorySource;

    @Before
    public void setup() {
        repositorySource = new JackrabbitRepositorySource();
    }

    @Test( expected = RuntimeException.class )
    public void shoulThrowAnExceptionWhenGetReferenceIsCalled() throws Exception {
        repositorySource.getReference();
    }

    @Test
    public void shouldSetFactoriesOnInitialize() throws Exception {
        repositorySource.initialize(null);

        assertNotNull(getFieldValue(repositorySource, "credentialsFactory"));
        assertNotNull(getFieldValue(repositorySource, "repositoryFactory"));
        assertNotNull(getFieldValue(repositorySource, "repositoryConnectionFactory"));
    }

    @Test
    public void shouldBeAnnotatedAsThreadSafe() throws Exception {
        ThreadSafe threadSafe = repositorySource.getClass().getAnnotation(ThreadSafe.class);
        assertNotNull(threadSafe);
    }

    @Test
    public void shouldHaveField_url_WithAnnotation_Description() throws Exception {
        checkDescriptionAnnotation(repositorySource, "url");
    }

    @Test
    public void shouldHaveField_url_WithAnnotation_Label() throws Exception {
        checkLabelAnnotation(repositorySource, "url");
    }

    @Test
    public void shouldHaveField_url_WithAnnotation_Category() throws Exception {
        checkCategoryAnnotation(repositorySource, "url");
    }

    @Test( expected = RepositorySourceException.class )
    public void shouldThrowExceptionWhenTryingToGetAConnectionWithParameterUrlNull() throws Exception {
        repositorySource.getConnection();
    }

    @Test( expected = RepositorySourceException.class )
    public void shouldThrowExceptionWhenTryingToGetAConnectionWithParameterUrlBlank() throws Exception {
        repositorySource.setUrl("     ");
        repositorySource.getConnection();
    }

    @Test
    public void shouldThrowALocalizedExceptionWhenUrlPropertyIsNotDefined() throws Exception {
        try {
            repositorySource.getConnection();
            fail("Should throw an exception");
        } catch (RepositorySourceException rse) {
            String message = rse.getMessage();
            assertEquals("The url property is required but has no value", message);
        }
    }

    @Test
    @Ignore("Thinking about how to unit test it")
    public void shouldReturnAJackrabbitRepositoryConnection() throws Exception {
        repositorySource.setUrl("http://some.valid.url");
        RepositoryConnection repositoryConnection = repositorySource.getConnection();
        assertTrue(repositoryConnection instanceof JcrRepositoryConnection);
    }

    @Test
    public void shouldAllowAnyNotNegativeIntegerForRetryLimit() throws Exception {
        repositorySource.setRetryLimit(10);

        assertEquals(10, repositorySource.getRetryLimit());
    }

    @Test
    public void shouldAvoidNegativeValuesForRetryLimit() throws Exception {
        repositorySource.setRetryLimit(-4);

        assertEquals(0, repositorySource.getRetryLimit());
    }

    @Test
    public void shouldSupportSameNameSiblingsCapabilities() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertTrue(sourceCapabilities.supportsSameNameSiblings());
    }

    @Test
    public void shoudlSupportUpdates() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertTrue(sourceCapabilities.supportsUpdates());
    }

    @Test
    public void shouldNotSupportEvents() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertFalse(sourceCapabilities.supportsEvents());
    }

    @Test
    public void shouldSupportWorkspaceCreation() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertTrue(sourceCapabilities.supportsCreatingWorkspaces());
    }

    @Test
    public void shouldSupportReferenceCreation() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertTrue(sourceCapabilities.supportsReferences());
    }

    private void checkDescriptionAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                             String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);

        Description descriptionAnnotation = field.getAnnotation(Description.class);
        String annotationName = "Description";

        assertNotNull(descriptionAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, descriptionAnnotation.i18n());
        assertEquals(fieldName + "Property" + annotationName, descriptionAnnotation.value());
    }

    private void checkLabelAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                       String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);

        Label labelAnnotation = field.getAnnotation(Label.class);

        assertNotNull(labelAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, labelAnnotation.i18n());
        assertEquals(fieldName + "PropertyLabel", labelAnnotation.value());
    }

    private void checkCategoryAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                          String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);

        Category categoryAnnotation = field.getAnnotation(Category.class);

        assertNotNull(categoryAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, categoryAnnotation.i18n());
        assertEquals(fieldName + "PropertyCategory", categoryAnnotation.value());
    }

    private Object getFieldValue( JackrabbitRepositorySource jackrabbitRepositorySource,
                                  String fieldName ) throws NoSuchFieldException, IllegalAccessException {
        Field repositoryContext = getField(jackrabbitRepositorySource, fieldName);
        repositoryContext.setAccessible(true);
        Object repositoryContextValue = repositoryContext.get(jackrabbitRepositorySource);
        return repositoryContextValue;
    }

    private Field getField( JackrabbitRepositorySource jackrabbitRepositorySource,
                            String fieldName ) throws NoSuchFieldException {
        Field repositoryContext = jackrabbitRepositorySource.getClass().getDeclaredField(fieldName);
        return repositoryContext;
    }

}
