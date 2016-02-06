// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.creator;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.MultiLanguageModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class CellRendererCreator {

    public static GridCellRenderer<ModelData> createRenderer(TypeModel dataType, final String xpath) {
        if (dataType.getType().equals(DataTypeConstants.URL)) {
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    String value = model.get(property);
                    if (value != null) {
                        String[] url = value.split("@@");//$NON-NLS-1$
                        return "<a href='" + url[1] + "'>" + url[0] + "</a>";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                    return "null";//$NON-NLS-1$
                }
            };
            return renderer;
        }
        if (dataType.isSimpleType() && dataType.isMultiOccurrence()) {
            final boolean isMultiLanguageType = dataType.getType().equals(DataTypeConstants.MLS);
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {
                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    String rootNode = property.substring(0,property.indexOf('/'));
                    String targetNode = property.substring(property.lastIndexOf('/')+1);
                    StringBuffer result = new StringBuffer();
                    ItemBean itemBean = (ItemBean) model;
                    Document doc = XMLParser.parse(itemBean.getItemXml());
                    NodeList nodeList = doc.getElementsByTagName(targetNode) ;
                    for (int i=0;i<nodeList.getLength();i++){
                        Node node = nodeList.item(i);
                        String displayValue = node.toString();
                        if (isMultiLanguageType) {
                            MultiLanguageModel multiLanguageModel = new MultiLanguageModel(displayValue);
                            displayValue = Format.htmlEncode(multiLanguageModel.getValueByLanguage(Locale.getLanguage()
                                    .toUpperCase()));
                        }

                        if (node instanceof Element) {
                            if (rootNode.equals(doc.getFirstChild().getNodeName())){
                                appendContent(result, displayValue, ","); //$NON-NLS-1$                            
                            }else{
                                if("result".equals(node.getParentNode().getNodeName())){ //$NON-NLS-1$
                                    appendContent(result, displayValue, ","); //$NON-NLS-1$         
                                }   
                            } 
                        }
                    }
                    if (isMultiLanguageType)
                        model.set(property, result.toString());
                    return result;
                }
            };
            return renderer;
        }   
        if (dataType.getForeignkey() != null) {
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    ItemBean itemBean = (ItemBean) model;
                    ForeignKeyBean fkBean = itemBean.getForeignkeyDesc((String) model.get(property));
                    if (fkBean == null) {
                        return ""; //$NON-NLS-1$
                    }
                    fkBean.setShowInfo(true);
                    return fkBean.toString();
                }
            };
            return renderer;
        }
        if ("string".equals(dataType.getType().getTypeName())){ //$NON-NLS-1$
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    return Format.htmlEncode((String)model.get(property));
                }
            };
            return renderer;
        }
        if (dataType.getType().equals(DataTypeConstants.MLS)) {
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    String multiLanguageString = (String) model.get(property);
                    MultiLanguageModel multiLanguageModel = new MultiLanguageModel(multiLanguageString);
                    return Format.htmlEncode(multiLanguageModel.getValueByLanguage(Locale.getLanguage().toUpperCase()));
                }
            };
            return renderer;
        }
        return null;
    }
    
    private static StringBuffer appendContent(StringBuffer resource,String content,String separater){
        if (!"".equals(resource.toString())){ //$NON-NLS-1$
            resource.append(separater);
        }
        resource.append(content);
        return resource;         
    }
}
