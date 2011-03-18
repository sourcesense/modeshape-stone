package com.sourcesense.stone.extensions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.lang.reflect.Field;
import net.jcip.annotations.ThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;
import org.modeshape.graph.connector.RepositorySourceCapabilities;

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
    public void shouldSetRepositoryContextOnInitialize() throws Exception {
        RepositoryContext aContext = mock(RepositoryContext.class);
        repositorySource.initialize(aContext);

        Object repositoryContextValue = getFieldValue(repositorySource, "repositoryContext");
        assertNotNull(repositoryContextValue);
    }

    @Test
    public void shouldBeAnnotatedAsThreadSafe() throws Exception {
        ThreadSafe threadSafe = repositorySource.getClass().getAnnotation(ThreadSafe.class);
        assertNotNull(threadSafe);
    }
    
    @Test
    public void shouldHaveField_name_WithAnnotation_Description() throws Exception {
        checkDescriptionAnnotation(repositorySource, "name");
    }

    @Test
    public void shouldHaveField_retryLimit_WithAnnotation_Description() throws Exception {
        checkDescriptionAnnotation(repositorySource, "retryLimit");
    }

    @Test
    public void shouldHaveField_name_WithAnnotation_Label() throws Exception {
        checkLabelAnnotation(repositorySource, "name");
    }

    @Test
    public void shouldHaveField_retryLimit_WithAnnotation_Label() throws Exception {
        checkLabelAnnotation(repositorySource, "retryLimit");
    }

    @Test
    public void shouldHaveField_name_WithAnnotation_Category() throws Exception {
        checkCategoryAnnotation(repositorySource, "name");
    }

    @Test
    public void shouldHaveField_retryLimit_WithAnnotation_Category() throws Exception {
        checkCategoryAnnotation(repositorySource, "retryLimit");
    }

    @Test
    public void shouldReturnAJackrabbitRepositoryConnection() throws Exception {
        RepositoryConnection repositoryConnection = repositorySource.getConnection();
        assertTrue(repositoryConnection instanceof JackrabbitRepositoryConnection);
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

    @Test
    public void shouldDeleteReferencesOnClose() throws Exception {
        RepositoryContext aContext = mock(RepositoryContext.class);
        repositorySource.initialize(aContext);
        
        Object repositorySourceValue = getFieldValue(repositorySource, "repositoryContext");
        assertNotNull(repositorySourceValue);
        
        repositorySource.close();
        Object newRepositorySourceValue = getFieldValue(repositorySource, "repositoryContext");
        assertNull(newRepositorySourceValue);
        
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
