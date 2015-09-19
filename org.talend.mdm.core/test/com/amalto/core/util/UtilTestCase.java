// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.save.SaveException;
import com.amalto.core.storage.DispatchWrapper;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

/**
 * DOC achen  class global comment. Detailled comment
 */
@SuppressWarnings("nls")
public class UtilTestCase extends TestCase {

    public void testDefaultValidate() throws IOException, ParserConfigurationException, SAXException {
        // missing mandontory field cvc-complex-type.2.4.b
        InputStream in = UtilTestCase.class.getResourceAsStream("Agency_ME02.xml");
        String xml = getStringFromInputStream(in);
        Element element = Util.parse(xml).getDocumentElement();
        InputStream inxsd = UtilTestCase.class.getResourceAsStream("DStar.xsd");
        String schema = getStringFromInputStream(inxsd);

        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            String str = e.getLocalizedMessage();
            assertTrue(str.contains("cvc-complex-type.2.4.b"));
        }
        // invalid content cvc-complex-type.2.4.

        String invalidXml = "<Agency>aa</Agency>";
        element = Util.parse(invalidXml).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            String str = e.getLocalizedMessage();
            assertTrue(str.contains("cvc-complex-type.2.4"));
        }

        // correct xmlstring
        String xmlString = "<Agency xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "<Name>Portland</Name>"
                + "<City>Portland</City>" + "<State>ME</State>" + "<Zip>04102</Zip>" + "<Region>EAST</Region>" + "<Id>ME03</Id>"
                + "</Agency>";
        element = Util.parse(xmlString).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            throw new SAXException(e);
        }

         // non mandontory field contains empty mandontory child fields is OK
        String xml1 = "<Product><Picture>htt:aa</Picture><Id>id1</Id><Name>name2</Name><Description>des1</Description>"
                + "<Features><Sizes><Size/></Sizes><Colors><Color/></Colors></Features>"
                + "<Availability>false</Availability><Price>0.0</Price><Family></Family><OnlineStore>gg2@d</OnlineStore></Product>";
        element = Util.parse(xml1).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    private static String getStringFromInputStream(InputStream in) throws IOException {
        int total = in.available();
        byte[] buf = new byte[total];
        in.read(buf);
        return new String(buf);
    }
    
    
    
    String[] ids=new String[]{"1"};
    String concept="Product";
    String projection="<Product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Id>1</Id><Name>name1</Name><Description>desc1</Description><Price>1</Price></Product>";
    DataClusterPOJOPK dcpk=new DataClusterPOJOPK("Product");

    /**
     * Simulate test beforeDeleting
     * DOC Administrator Comment method "testBeforeDeleting".
     * @throws Exception
     */
    public void testBeforeDeleting()throws Exception{
    	//delete item in recyclebin
    	ItemPOJO pojo=null;
    	String ret=beforeDeleting(pojo);
    	assertTrue(ret!=null && ret.contains("[EN:ok]"));
    	
    	//delete item in data container
    	pojo=new ItemPOJO();
    	pojo.setDataClusterPK(dcpk);
    	pojo.setItemIds(ids);
    	pojo.setConceptName(concept);
    	pojo.setProjectionAsString(projection);
    	
    	ret=beforeDeleting(pojo);
    	
    	assertTrue(ret!=null && ret.contains("[EN:ok]"));
    }
    
    /**
     * the simulate droppedItem
     * DOC Administrator Comment method "getDroppedItem".
     * @return
     */
    private DroppedItemPOJO getDroppedItem(){
    	DroppedItemPOJO pojo=new DroppedItemPOJO();
    	pojo.setProjection("<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>1</i><t>1330671403828</t><p>"+
    			projection +
    			"</p></ii>");
    	
    	return pojo;
    }
    
    /**
     * the simulate beforeDeleting() according to the Util.beforeDeleting()    
     * DOC Administrator Comment method "beforeDeleting".
     * @param pj
     * @return
     * @throws Exception
     */
    private  String beforeDeleting(ItemPOJO pj) throws Exception {
        // check before deleting transformer
        boolean isBeforeDeletingTransformerExist = true;
        

        if (isBeforeDeletingTransformerExist) {
            try {
                // call before deleting transformer
                // load the item
                ItemPOJO pojo= pj;//ItemPOJO.load(itempk);
                String xml=null;
                if(pojo==null){//load from recyclebin
                	DroppedItemPOJO dpPojo=getDroppedItem(); //Util.getDroppedItemCtrlLocal().loadDroppedItem(dpitempk);
                	if(dpPojo!=null){
                		xml=dpPojo.getProjection();             		
                		Document doc = Util.parse(xml);
    	                Node item = (Node) XPathFactory.newInstance().newXPath().evaluate("//ii/p", doc, XPathConstants.NODE); //$NON-NLS-1$
    	                if (item != null && item instanceof Element) {
    	                    NodeList list = item.getChildNodes();
    	                    Node node = null;
    	                    for (int i = 0; i < list.getLength(); i++) {
    	                        if (list.item(i) instanceof Element) {
    	                            node = list.item(i);
    	                            break;
    	                        }
    	                    }
    	                    if (node != null) {
    	                        xml = Util.nodeToString(node);
    	                    }
    	                }
                	}
                }else{
                	xml=pojo.getProjectionAsString();
                }
                String resultUpdateReport = null;//Util.createUpdateReport(ids, concept, "PHYSICAL_DELETE", null,                         "", dcpk.getUniqueId()); //$NON-NLS-1$
                String exchangeData = Util.mergeExchangeData(xml, resultUpdateReport);
                final String RUNNING = "XtentisWSBean.executeTransformerV2.beforeDeleting.running";
                TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeDeleting_Product"));
                context.put(RUNNING, Boolean.TRUE);
                String outputErrorMessage="<message type=\"info\">[EN:ok]</message>";
                if(xml==null){
                	outputErrorMessage=null;
                }
                // handle error message
                if (outputErrorMessage != null && outputErrorMessage.length() > 0) {
                    return outputErrorMessage;
                } else {
                    return "<report><message type=\"error\"/></report> "; //$NON-NLS-1$
                }
            } catch (Exception e) {
                Logger.getLogger(Util.class).error(e);
                throw e;
            }
        }
        // TODO Scan the entries - in priority, taka the content of the specific
        // entry
        return null;
    }

    public void testGetException() {
        String str = "Failed";
        Exception e = new Exception(new SaveException(new ValidateException((str))));
        ValidateException ve = Util.getException(e, ValidateException.class);
        assertNotNull(ve);
        assertEquals(str, ve.getMessage());
        
        e = new Exception(new SaveException(new Exception(str)));
        ve = Util.getException(e, ValidateException.class);
        assertNull(ve);
    }


    public void __testGetMetaDataTypesgetMetaDataTypes() throws Exception {
        List<IWhereItem> whereItems=new ArrayList<IWhereItem>();
        IWhereItem whereItem=new WhereCondition("BusinessType/ZthesId","=","11111d","NONE");
        IWhereItem whereItem2 = new WhereCondition("BusinessRef/Id", "=", "5", "NONE");
        whereItems.add(whereItem);
        whereItems.add(whereItem2);
        IWhereItem whereAnd=new WhereAnd(whereItems);
        Map<String, ArrayList<String>> metaDataTypes = Util.getMetaDataTypes(whereAnd, new SchemaTestAgent());

        assertNull(metaDataTypes.get("BusinessRef/Id"));
        assertEquals("xsd:string", metaDataTypes.get("BusinessType/ZthesId").get(0));
    }

    public void __testGetUserDataModel() throws Exception {
        String userXml = "<User><username>user</username><password>ee11cbb19052e40b07aac0ca060c23ee</password><givenname>Default</givenname><familyname>User</familyname><company>Company</company><id>null</id><realemail>user@company.com</realemail><viewrealemail>no</viewrealemail><registrationdate>1342699860823</registrationdate><lastvisitdate>0</lastvisitdate><enabled>yes</enabled><homepage>Home</homepage><roles><role>OM5_Admin</role><role>System_Interactive</role></roles><properties><property><name>cluster</name><value>OM5Container</value></property><property><name>model</name><value>OM5</value></property></properties></User>";
        Element userEl = Util.parse(userXml).getDocumentElement();
        String dataModel = Util.getUserDataModel(userEl);

        assertEquals("OM5", dataModel);

        userXml = "<User><username>user</username><password>ee11cbb19052e40b07aac0ca060c23ee</password><givenname>Default</givenname><familyname>User</familyname><company>Company</company><id>null</id><realemail>user@company.com</realemail><viewrealemail>no</viewrealemail><registrationdate>1342699860823</registrationdate><lastvisitdate>0</lastvisitdate><enabled>yes</enabled><homepage>Home</homepage><roles><role>OM5_Admin</role><role>System_Interactive</role></roles></User>";
        userEl = Util.parse(userXml).getDocumentElement();
        dataModel = Util.getUserDataModel(userEl);

        assertNull(dataModel);
    }

    public void testIsSystemDC() throws Exception {
        assertFalse(Util.isSystemDC(null));
        assertTrue(Util.isSystemDC(new DataClusterPOJOPK("PROVISIONING")));
        assertTrue(Util.isSystemDC(new DataClusterPOJOPK("MDMDomainObjects")));
        assertFalse(Util.isSystemDC(new DataClusterPOJOPK("Product")));
    }

    public void testExtractCharset() {
        String messages[] = { "text/xml; charset  =   'utf-8' ;  ", "text/xml; charset  =   utf-8  ",
                "text/xml; charset  =  \"utf-8\" ;  ", "text/xml; charset  =   utf-8  ; abc = charset ",
                "text/xml; charset  =   'utf-8'  ; abc = charset ", "text/xml; charset  =   \"utf-8\"  ; abc = charset " };
        for (String m : messages) {
            assertTrue("UTF-8".equals(Util.extractCharset(m, "iso-8891")));
        }
    }

    class SchemaTestAgent extends SchemaManager {

        public SchemaTestAgent() {

        }

        @Override
        protected boolean existInPool(DataModelID dataModelID) {
            return false;
        }

        @Override
        protected void removeFromPool(DataModelID dataModelID) {
            throw new RuntimeException("Not supported! ");
        }

        @Override
        protected void addToPool(DataModelID dataModelID, DataModelBean dataModelBean) {
            throw new RuntimeException("Not supported! ");
        }

        @Override
        protected DataModelBean getFromPool(DataModelID dataModelID) throws Exception {
            throw new RuntimeException("Not supported! ");
        }

        @Override
        public BusinessConcept getBusinessConceptForCurrentUser(String conceptName) throws Exception {

            BusinessConcept bizConcept = new MyBuissnesConcept();
            bizConcept.setName("BusinessType");
            
            Map<String, String> xpathTypeMap=new HashMap<String,String>();
            xpathTypeMap.put("BusinessType/Id", "xsd:string");
            xpathTypeMap.put("BusinessType/ZthesId", "xsd:string");
            bizConcept.setXpathTypeMap(xpathTypeMap);

            return bizConcept;
        }

    }

    class MyBuissnesConcept extends BusinessConcept {

        @Override
        public void load() {
            // do nothing
        }
    }

}
