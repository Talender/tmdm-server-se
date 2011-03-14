package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;


public class PagingToolBarEx extends PagingToolBar {

    
    public PagingToolBarEx(int pageSize) {
        super(pageSize);
        LabelToolItem sizeLabel = new LabelToolItem(MessagesFactory.getMessages().page_size_label());
        
        final NumberField sizeField = new NumberField();
        sizeField.setWidth(30);
        sizeField.setValue(pageSize);
        sizeField.setValidator(validator);
        sizeField.addListener(Events.Change, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                if (sizeField.isValid() && sizeField.getValue() != null){
                    setPageSize((int)Double.parseDouble(sizeField.getValue()+""));
                    first();
                }
            }
        });
        sizeField.setValue(pageSize);
        this.insert(new SeparatorToolItem(), this.getItemCount() - 2);
        this.insert(sizeLabel, this.getItemCount() - 2);
        this.insert(sizeField, this.getItemCount() - 2);
    }
    
    Validator validator = new Validator() {
        
        public String validate(Field<?> field, String value) {
            String valueStr = value == null? "": value.toString();
            boolean success = true;
            try{
                int num = Integer.parseInt(valueStr);
                if (num <= 0) {
                    success = false;
                }
            } catch (NumberFormatException e){
                success = false;
            }
            if (!success){
                return MessagesFactory.getMessages().page_size_notice();
            }
            return null;
        }
    };
}
