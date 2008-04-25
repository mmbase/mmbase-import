package com.finalist.cmsc.openoffice.addon;
//import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.text.XTextDocument;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

public final class CMSContainerAddOn extends WeakBase
        implements com.sun.star.lang.XServiceInfo,
        com.sun.star.frame.XDispatchProvider,
        com.sun.star.lang.XInitialization,
        com.sun.star.frame.XDispatch {
    private final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = CMSContainerAddOn.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler" };
    
    private static final String _buttonName = "UploadButton";
    private static final String _cancelButtonName = "CancelButton";
    private static final String _labelName = "CMSCLabel";
    private static final String _labelPrefix = "URL of the CMS container: ";
    private static final String _editName = "EditText";
    
    
     XMultiComponentFactory xMultiComponentFactory = null;
             // get the service manager from the dialog model
  XMultiServiceFactory xMultiServiceFactory = null;
   XNameContainer xNameCont = null;
   
   XControlContainer xControlCont = null;

    public CMSContainerAddOn( XComponentContext context ) {
        m_xContext = context;
        
    };
    
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;
        
        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(CMSContainerAddOn.class, m_serviceNames);
        return xFactory;
    }
    
    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                m_serviceNames,
                xRegistryKey);
    }
    
    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
        return m_implementationName;
    }
    
    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;
        
        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }
    
    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }
    
    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch( com.sun.star.util.URL aURL,
            String sTargetFrameName,
            int iSearchFlags ) {
        if ( aURL.Protocol.compareTo("com.finalist.cmsc.openoffice.addon.cmscontaineraddon:") == 0 ) {
            if ( aURL.Path.compareTo("Export") == 0 )
                return this;
        }
        return null;
    }
    
    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
            com.sun.star.frame.DispatchDescriptor[] seqDescriptors ) {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
                new com.sun.star.frame.XDispatch[seqDescriptors.length];
        
        for( int i=0; i < nCount; ++i ) {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                    seqDescriptors[i].FrameName,
                    seqDescriptors[i].SearchFlags );
        }
        return seqDispatcher;
    }
    
    // com.sun.star.lang.XInitialization:
    public void initialize( Object[] object )
    throws com.sun.star.uno.Exception {
        if ( object.length > 0 ) {
            m_xFrame = (com.sun.star.frame.XFrame)UnoRuntime.queryInterface(
                    com.sun.star.frame.XFrame.class, object[0]);
        }
    }
    
    // com.sun.star.frame.XDispatch:
    public void dispatch( com.sun.star.util.URL aURL,
            com.sun.star.beans.PropertyValue[] aArguments ) {
        if ( aURL.Protocol.compareTo("com.finalist.cmsc.openoffice.addon.cmscontaineraddon:") == 0 ) {
            if ( aURL.Path.compareTo("Export") == 0 ) {
                try {
                  createDialog();
                  // loginDialog();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }
    
    private void createDialog() throws com.sun.star.uno.Exception {
        
        // get the service manager from the component context

     xMultiComponentFactory = m_xContext.getServiceManager();
      // create the dialog model and set the properties
        Object dialogModel = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialogModel", m_xContext );
        XPropertySet xPSetDialog = ( XPropertySet )UnoRuntime.queryInterface( XPropertySet.class, dialogModel );
        xPSetDialog.setPropertyValue( "PositionX", new Integer( 100 ) );
        xPSetDialog.setPropertyValue( "PositionY", new Integer( 100 ) );
        xPSetDialog.setPropertyValue( "Width", new Integer( 300 ) );
        xPSetDialog.setPropertyValue( "Height", new Integer( 50 ) );
        xPSetDialog.setPropertyValue( "Title", new String( "Export to CMS Container" ) );
        
        // get the service manager from the dialog model
         xMultiServiceFactory = ( XMultiServiceFactory )UnoRuntime.queryInterface(XMultiServiceFactory.class, dialogModel );
        
        // create the upload button model and set the properties
        Object buttonModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetButton = ( XPropertySet )UnoRuntime.queryInterface(XPropertySet.class, buttonModel );
        xPSetButton.setPropertyValue( "PositionX", new Integer( 100 ) );
        xPSetButton.setPropertyValue( "PositionY", new Integer( 25 ) );
        xPSetButton.setPropertyValue( "Width", new Integer( 40 ) );
        xPSetButton.setPropertyValue( "Height", new Integer( 15 ) );
        xPSetButton.setPropertyValue( "Name", _buttonName );
        xPSetButton.setPropertyValue( "TabIndex", new Short( (short)1 ) );
        xPSetButton.setPropertyValue( "Label", new String( "Upload" ) );
        
        // create a Cancel button model and set the properties
        Object cancelButtonModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetCancelButton = ( XPropertySet )UnoRuntime.queryInterface(XPropertySet.class, cancelButtonModel );
        xPSetCancelButton.setPropertyValue( "PositionX", new Integer( 160 ) );
        xPSetCancelButton.setPropertyValue( "PositionY", new Integer( 25 ) );
        xPSetCancelButton.setPropertyValue( "Width", new Integer( 40 ) );
        xPSetCancelButton.setPropertyValue( "Height", new Integer( 15 ) );
        xPSetCancelButton.setPropertyValue( "Name", _cancelButtonName );
        xPSetCancelButton.setPropertyValue( "TabIndex", new Short( (short)2 ) );
        xPSetCancelButton.setPropertyValue( "PushButtonType", new Short( (short)2 ) );
        xPSetCancelButton.setPropertyValue( "Label", new String( "Cancel" ) );
        
        
       
        // create the label model and set the properties
        Object labelModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel" );
        XPropertySet xPSetLabel = ( XPropertySet )UnoRuntime.queryInterface(XPropertySet.class, labelModel );
        xPSetLabel.setPropertyValue( "PositionX", new Integer( 10 ) );
        xPSetLabel.setPropertyValue( "PositionY", new Integer( 10 ) );
        xPSetLabel.setPropertyValue( "Width", new Integer( 80 ) );
        xPSetLabel.setPropertyValue( "Height", new Integer( 10 ) );
        xPSetLabel.setPropertyValue( "Name", _labelName );
        xPSetLabel.setPropertyValue( "Label", _labelPrefix );
        
        // create the text model and set the properties
        Object editModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlEditModel");
        XPropertySet xPSetEdit = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, editModel);
        xPSetEdit.setPropertyValue("PositionX", new Integer(80));
        xPSetEdit.setPropertyValue("PositionY", new Integer(10));
        xPSetEdit.setPropertyValue("Width", new Integer(200));
        xPSetEdit.setPropertyValue("Height", new Integer(10));
        xPSetEdit.setPropertyValue("Text", new String("http://"));
        xPSetEdit.setPropertyValue("TabIndex", new Short((short)0));
        xPSetEdit.setPropertyValue("Name", _editName);
        
        // insert the control models into the dialog model
         xNameCont = ( XNameContainer )UnoRuntime.queryInterface(XNameContainer.class, dialogModel );
        xNameCont.insertByName( _labelName, labelModel );
        xNameCont.insertByName( _editName, editModel );
        xNameCont.insertByName( _buttonName, buttonModel );
        xNameCont.insertByName( _cancelButtonName, cancelButtonModel );
        
        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext( "com.sun.star.awt.UnoControlDialog", m_xContext );
        XControl xControl = ( XControl )UnoRuntime.queryInterface( XControl.class, dialog );
        XControlModel xControlModel = ( XControlModel )UnoRuntime.queryInterface( XControlModel.class, dialogModel );
        xControl.setModel( xControlModel );
        
        // add an action listener to the button control
         xControlCont = ( XControlContainer )UnoRuntime.queryInterface( XControlContainer.class, dialog );
        Object objectButton = xControlCont.getControl( _buttonName );
        XButton xButton = ( XButton )UnoRuntime.queryInterface( XButton.class, objectButton );
        xButton.addActionListener( new ActionListenerImpl( xControlCont ) );
        
        
        // com.sun.star.awt.
        // create a peer
        Object toolkit = xMultiComponentFactory.createInstanceWithContext( "com.sun.star.awt.ExtToolkit", m_xContext );
        XToolkit xToolkit = ( XToolkit )UnoRuntime.queryInterface( XToolkit.class, toolkit );
        XWindow xWindow = ( XWindow )UnoRuntime.queryInterface( XWindow.class, xControl );
        xWindow.setVisible( false );
        xControl.createPeer( xToolkit, null );
        
        // execute the dialog
        XDialog xDialog = ( XDialog )UnoRuntime.queryInterface( XDialog.class, dialog );
        xDialog.execute();
        
        // dispose the dialog
        XComponent xComponent = ( XComponent )UnoRuntime.queryInterface( XComponent.class, dialog );
        xComponent.dispose();
    }
  

    //upload listener
    public class ActionListenerImpl implements com.sun.star.awt.XActionListener  {
        private XControlContainer _xControlCont;
        public ActionListenerImpl( XControlContainer xControlCont ) {
            _xControlCont = xControlCont;
        }
        public void disposing( EventObject eventObject ) {
            _xControlCont = null;
        }
        
        //upload event
        public void actionPerformed( com.sun.star.awt.ActionEvent actionEvent )  {
      
           File targetFile = null;
           PostMethod filePost =null;
          
            Object text = _xControlCont.getControl( _editName);
           XTextComponent xText = ( XTextComponent )UnoRuntime.queryInterface( XTextComponent.class, text );
           XTextDocument xDoc = (XTextDocument) UnoRuntime.queryInterface(XTextDocument.class, m_xFrame.getController().getModel());
       
            String fileUrl = xDoc.getURL();
            String actionPath = "";
          //  actionPath += HTTP_PROTOCOL;
   
            if("".equals(fileUrl)){
                XWindow xWindow = m_xFrame.getContainerWindow();
                XWindowPeer xWindowPeer =  (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, xWindow);
                createMessageBox(xWindowPeer, "Info", "You should save the doc first.");
            } 
            else{
                try {
                    if(fileUrl.startsWith("file:")) {                  
                        fileUrl = fileUrl.substring(5);
                    }
                    while(fileUrl.startsWith(File.separator)) {
                         fileUrl = fileUrl.substring(1);
                     }

                   if(fileUrl.indexOf("%20") >-1)  {                      
                      fileUrl = fileUrl.replaceAll("%20"," ");
                   }
                    String url = xText.getText();
                    if(url.endsWith("/"))
                        url.substring(0, url.length()-1);                  
                  
                     actionPath = chansferUrl(url)+ACTION_PATH;
                     url = chansferUrl(url)+RESOURCE_PATH;                     
                     
                    targetFile = new File(fileUrl);                                    
                     filePost =  new PostMethod(url);                    
                     filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE,
                           false);
                     
                    Part[] parts = {
                            new FilePart(targetFile.getName(), targetFile)
                        };
                        filePost.setRequestEntity(
                                new MultipartRequestEntity(parts, filePost.getParams())
                                );
                        HttpClient client = new HttpClient();
                        client.getHttpConnectionManager().
                                getParams().setConnectionTimeout(5000);
                        int status = client.executeMethod(filePost);

                        if (status == HttpStatus.SC_OK) {
                            
                             XWindow xWindow1 = m_xFrame.getContainerWindow();
                             XWindowPeer xWindowPeer1 =  (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, xWindow1);
                              createMessageBox(xWindowPeer1, "Info", "upload success.");
                              
                              String response = filePost.getResponseBodyAsString().trim();
                              if(response.indexOf("root") > -1)
                                  response = response.substring(response.indexOf("root")+5);
                             actionPath+= "?root="+ response;;
                         }                    
                    String cmd = "rundll32 url.dll,FileProtocolHandler " + actionPath;
                    Runtime.getRuntime().exec(cmd);
                } 
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                //catch(com.sun.star.uno.Exception e){}
            }
        }
    }
    
    private void createMessageBox(XWindowPeer _xParentWindowPeer, String _sTitle, String _sMessage){
        XComponent xComponent = null;
        try {
            XMultiComponentFactory xMultiComponentFactory = m_xContext.getServiceManager();
            Object oToolkit = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
            XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, oToolkit);
            // rectangle may be empty if position is in the center of the parent peer
            Rectangle aRectangle = new Rectangle();
            XMessageBox xMessageBox = xMessageBoxFactory.createMessageBox(_xParentWindowPeer, aRectangle, "infobox", com.sun.star.awt.MessageBoxButtons.BUTTONS_OK, _sTitle, _sMessage);
            xComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, xMessageBox);
            if (xMessageBox != null){
                short nResult = xMessageBox.execute();
            }
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace(System.out);
        } finally{
            //make sure always to dispose the component and free the memory!
            if (xComponent != null){
                xComponent.dispose();
            }
        }
    }
    
    public void addStatusListener( com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL ) {
        // add your own code here
    }
    
    public void removeStatusListener( com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL ) {
        // add your own code here
    }
    

    
    
        /** makes a String unique by appending a numerical suffix
     * @param _xElementContainer the com.sun.star.container.XNameAccess container
     * that the new Element is going to be inserted to
     * @param _sElementName the StemName of the Element
     */
    public static String createUniqueName(XNameAccess _xElementContainer, String _sElementName) {
        boolean bElementexists = true;
        int i = 1;
        String sIncSuffix = "";
        String BaseName = _sElementName;
        while (bElementexists) {
            bElementexists = _xElementContainer.hasByName(_sElementName);
            if (bElementexists) {
                i += 1;
                _sElementName = BaseName + Integer.toString(i);
            }
        }
        return _sElementName;
    }
    
    private static String chansferUrl(String url)
    {
        String tempStr = "";
        if(url.startsWith(HTTP_PROTOCOL))
        {
            url = url.substring(7);
            tempStr = HTTP_PROTOCOL;
        }
        else
            tempStr = HTTP_PROTOCOL;
        String[]  tempArray= url.split("/");
        
        if(tempArray != null )
        {
            if(tempArray.length ==1)
            tempStr += tempArray[0];
            if(tempArray.length > 1)
                tempStr += tempArray[0]+"/"+tempArray[1];
 
        }
        
      
        return tempStr;
    }
     public static void main(String[] args) throws Exception {
         
         String url = "http://localhost:8080/cmsc-demo-staging/editors/repository/attachmentupload.jsp";
         
         System.out.println(chansferUrl(url));
          String url1 = "localhost:8080/cmsc-demo-staging/";
         
         System.out.println(chansferUrl(url1));
         
         
           String url2 = "http://localhost:8080/";
         
         System.out.println(chansferUrl(url2));
       
         XComponentContext xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
        CMSContainerAddOn addOn = new CMSContainerAddOn(xContext);
       addOn.initialize(new String[0]);
       addOn.createDialog();    
        
    }   
     private static final  String  HTTP_PROTOCOL= "http://";
     private static final  String  RESOURCE_PATH= "/editors/repository/attachmentupload.jsp";
          private static final  String  ACTION_PATH= "/editors/upload/OdtStore.do";
          
         
}
