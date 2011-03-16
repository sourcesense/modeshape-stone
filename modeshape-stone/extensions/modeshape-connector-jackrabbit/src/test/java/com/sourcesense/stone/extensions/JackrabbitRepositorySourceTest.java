package com.sourcesense.stone.extensions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import org.junit.Test;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
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

        Field field = getField(jackrabbitRepositorySource, "name");

        Description descriptionAnnotation = field.getAnnotation(Description.class);
        
        assertNotNull(descriptionAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, descriptionAnnotation.i18n());
        assertEquals("namePropertyDescription", descriptionAnnotation.value());
    }
    
    @Test
    public void shouldHaveField_name_WithAnnotation_Label() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource();

        Field field = getField(jackrabbitRepositorySource, "name");

        Label labelAnnotation = field.getAnnotation(Label.class);
        
        assertNotNull(labelAnnotation);
        assertEquals(JackrabbitConnectorI18n.class, labelAnnotation.i18n());
        assertEquals("namePropertyLabel", labelAnnotation.value());
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
