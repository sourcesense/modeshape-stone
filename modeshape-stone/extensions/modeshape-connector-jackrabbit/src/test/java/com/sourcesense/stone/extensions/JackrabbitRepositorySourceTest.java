package com.sourcesense.stone.extensions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import org.junit.Test;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;

public class JackrabbitRepositorySourceTest {

    @Test( expected = RuntimeException.class )
    public void shoulThrowAnExceptionWhenGetReferenceIsCalled() throws Exception {
        new JackrabbitRepositorySource().getReference();
    }

    @Test
    public void shouldSetRepositoryContextOnInitialize() throws Exception {
        RepositoryContext aContext = mock(RepositoryContext.class);
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();

        jackrabbitRepositorySource.initialize(aContext);

        Object repositoryContextValue = getFieldValue(jackrabbitRepositorySource, "repositoryContext");
        assertNotNull(repositoryContextValue);
    }

    @Test
    public void shouldHaveField_name_WithAnnotation_Description() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();

        checkDescriptionAnnotation(jackrabbitRepositorySource, "name");
    }

    @Test
    public void shouldHaveField_retryLimit_WithAnnotation_Description() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();
        
        checkDescriptionAnnotation(jackrabbitRepositorySource, "retryLimit");
    }
    
    @Test
    public void shouldHaveField_name_WithAnnotation_Label() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();

        checkLabelAnnotation(jackrabbitRepositorySource, "name");
    }

    @Test
    public void shouldHaveField_retryLimit_WithAnnotation_Label() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();
        
        checkLabelAnnotation(jackrabbitRepositorySource, "retryLimit");
    }
    
    @Test
    public void shouldHaveField_name_WithAnnotation_Category() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();
        
        Field field = getField(jackrabbitRepositorySource, "name");
        
        Category categoryAnnotation = field.getAnnotation(Category.class);
        
        assertNotNull(categoryAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, categoryAnnotation.i18n());
        assertEquals("namePropertyCategory", categoryAnnotation.value());
    }

    @Test
    public void shouldHaveField_retryLimit_WithAnnotation_Category() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();
        
        Field field = getField(jackrabbitRepositorySource, "retryLimit");
        
        Category categoryAnnotation = field.getAnnotation(Category.class);
        
        assertNotNull(categoryAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, categoryAnnotation.i18n());
        assertEquals("retryLimitPropertyCategory", categoryAnnotation.value());
    }
    
    @Test
    public void shouldReturnAJackrabbitRepositoryConnection() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();
        
        RepositoryConnection repositoryConnection = jackrabbitRepositorySource.getConnection();
        assertTrue(repositoryConnection instanceof JackrabbitRepositoryConnection);
    }

    private void checkDescriptionAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                             String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);
    
        Description descriptionAnnotation = field.getAnnotation(Description.class);
        String annotationName = "Description";
        
        assertNotNull(descriptionAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, descriptionAnnotation.i18n());
        assertEquals(fieldName+"Property"+annotationName, descriptionAnnotation.value());
    }

    private void checkLabelAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                       String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);
    
        Label labelAnnotation = field.getAnnotation(Label.class);
        
        assertNotNull(labelAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, labelAnnotation.i18n());
        assertEquals(fieldName+"PropertyLabel", labelAnnotation.value());
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
