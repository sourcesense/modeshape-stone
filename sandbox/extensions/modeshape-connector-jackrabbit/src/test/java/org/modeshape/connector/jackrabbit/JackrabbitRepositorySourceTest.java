package org.modeshape.connector.jackrabbit;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.jcip.annotations.ThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.connector.jackrabbit.CredentialsFactory;
import org.modeshape.connector.jackrabbit.JackrabbitRepositorySource;
import org.modeshape.connector.jackrabbit.RepositoryFactory;
import org.modeshape.connector.jcr.JcrConnectorI18n;
import org.modeshape.connector.jcr.JcrRepositorySource;
import org.modeshape.graph.ExecutionContext;
import org.modeshape.graph.Subgraph;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;
import org.modeshape.graph.connector.RepositorySourceCapabilities;
import org.modeshape.graph.connector.RepositorySourceException;
import org.modeshape.graph.observe.Observer;

public class JackrabbitRepositorySourceTest {

    private JackrabbitRepositorySource repositorySource;
    
    @Mock
    private CredentialsFactory mockedCredentialsFactory;
    
    @Mock
    private RepositoryFactory mockedRepositoryFactory;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
    public void shouldReturnAJackrabbitRepositoryConnectionUsingAllInjectedFactories() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = setupJackrabbitRepositorySource();
        
        jackrabbitRepositorySource.getConnection();
        
        verify(mockedRepositoryFactory).createRepository("http://some.valid.url");
        verify(mockedCredentialsFactory).createCredentials("scott", "tiger");
    }

    @Test
    public void shouldAllowMultipleConnectionsToBeOpenAtTheSameTime() throws Exception {
        JackrabbitRepositorySource jackrabbitRepositorySource = setupJackrabbitRepositorySource();
        
        List<RepositoryConnection> connections = new ArrayList<RepositoryConnection>();
        try {
            for (int i = 0; i != 10; ++i) {
                RepositoryConnection conn = jackrabbitRepositorySource.getConnection();
                assertThat(conn, is(notNullValue()));
                connections.add(conn);
            }
        } finally {
            // Close all open connections ...
            for (RepositoryConnection conn : connections) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    @Test
    public void shouldHaveDefaultRetryLimit() {
        assertThat(repositorySource.getRetryLimit(), is(JcrRepositorySource.DEFAULT_RETRY_LIMIT));
    }
    
    @Test
    public void shouldAllowAnyNotNegativeIntegerForRetryLimit() throws Exception {
        repositorySource.setRetryLimit(10);

        assertEquals(10, repositorySource.getRetryLimit());
    }

    @Test
    public void shouldAvoidNegativeValuesForRetryLimit() throws Exception {
        repositorySource.setRetryLimit(-4);

        assertThat(repositorySource.getRetryLimit(), equalTo(0));
    }

    @Test
    public void shouldAllowSettingURL() {
        repositorySource.setUrl("Something");
        assertThat(repositorySource.getUrl(), is("Something"));
        
        repositorySource.setUrl("another url");
        assertThat(repositorySource.getUrl(), is("another url"));
    }
    
    @Test
    public void shouldAllowSettingURLToNull() {
        repositorySource.setUrl("some url");
        repositorySource.setUrl(null);
        assertThat(repositorySource.getUrl(), is(nullValue()));
    }
    
    @Test
    public void shouldReturnNonNullCapabilities() {
        assertThat(repositorySource.getCapabilities(), is(notNullValue()));
    }

    @Test
    public void shouldSupportSameNameSiblings() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertThat(sourceCapabilities.supportsSameNameSiblings(), is(true));
    }
    
    @Test
    public void shouldSupportUpdates() throws Exception {
        RepositorySourceCapabilities sourceCapabilities = repositorySource.getCapabilities();

        assertThat(sourceCapabilities.supportsUpdates(), is(true));
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

    private JackrabbitRepositorySource setupJackrabbitRepositorySource() {
        final JackrabbitRepositorySource jackrabbitRepositorySource = new JackrabbitRepositorySource() {
    
            @Override
            protected RepositoryFactory getRepositoryFactory() {
                return mockedRepositoryFactory;
            }
    
            @Override
            protected CredentialsFactory getCredentialsFactory() {
                return mockedCredentialsFactory;
            }
            
        };
    
        jackrabbitRepositorySource.setUrl("http://some.valid.url");
        jackrabbitRepositorySource.setUsername("scott");
        jackrabbitRepositorySource.setPassword("tiger");
        
        final ExecutionContext context = new ExecutionContext();
        jackrabbitRepositorySource.initialize(new RepositoryContext(){

            public ExecutionContext getExecutionContext() {
                return context;
            }

            public org.modeshape.graph.connector.RepositoryConnectionFactory getRepositoryConnectionFactory() {
                return new org.modeshape.graph.connector.RepositoryConnectionFactory(){

                    public RepositoryConnection createConnection( String sourceName ) throws RepositorySourceException {
                        return jackrabbitRepositorySource.getConnection();
                    }};
            }

            public Observer getObserver() {
                return null;
            }

            public Subgraph getConfiguration( int depth ) {
                return null;
            }});
        
        return jackrabbitRepositorySource;
    }

    private void checkDescriptionAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                             String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);

        Description descriptionAnnotation = field.getAnnotation(Description.class);
        String annotationName = "Description";

        assertNotNull(descriptionAnnotation);
        assertEquals(JcrConnectorI18n.class, descriptionAnnotation.i18n());
        assertEquals(fieldName + "Property" + annotationName, descriptionAnnotation.value());
    }

    private void checkLabelAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                       String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);

        Label labelAnnotation = field.getAnnotation(Label.class);

        assertNotNull(labelAnnotation);
        assertEquals(JcrConnectorI18n.class, labelAnnotation.i18n());
        assertEquals(fieldName + "PropertyLabel", labelAnnotation.value());
    }

    private void checkCategoryAnnotation( JackrabbitRepositorySource jackrabbitRepositorySource,
                                          String fieldName ) throws NoSuchFieldException {
        Field field = getField(jackrabbitRepositorySource, fieldName);

        Category categoryAnnotation = field.getAnnotation(Category.class);

        assertNotNull(categoryAnnotation);
        assertEquals(JcrConnectorI18n.class, categoryAnnotation.i18n());
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
