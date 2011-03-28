﻿amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.ItemsBrowser = function () {

	loadResource("/core/secure/yui-2.4.0/build/treeview/treeview.js", "YAHOO.widget.TreeView" );
		
	loadResource("/core/secure/yui-2.4.0/build/treeview/assets/treeview.css", "" );
	
	loadResource("/itemsbrowser/secure/css/ItemsBrowser.css", "" );
	
	loadResource("/itemsbrowser/secure/js/ImprovedDWRProxy.js", "");
	
	loadResource("/talendmdm/secure/js/widget/ForeignKeyField.js", "" );
	
	loadResource("/itemsbrowser/secure/js/SearchEntityPanel.js", "" );
	
	/********************************************************************
	 * Localization
	 ********************************************************************/
	 
	var MSG_LOADING={
		'fr':'Chargement...',
		'en':'Loading...'
	};
	var MSG_READY={
		'fr':'Terminé.',
		'en':'Ready.'
	};
	var BROWSE_ITEMS = {
		'en':'Browse Records',
		'fr':'Accès aux données'
	};

	
	var LABEL_DISPLAYING = {
		'fr':'Enregistrements affichés : ',
		'en':'Displaying records: '
	};
	var LABEL_OF = {
		'fr':'sur',
		'en':'of'
	};
	 
	var ITEM_DETAILS = {
		'fr':'Détail de l\'enregistrement',
		'en':'Record details'
	};
	
	var  TITLE_SEARCH_RESULT =    {
	    'fr' : 'Résultats',
	    'en' : 'Search results'
	};
	var  TITLE_SEARCH_PANEL =    {
	    'fr' : 'Recherche',
	    'en' : 'Search panel'
	};
	var  LABEL_CRITERIA =    {
	    'fr' : 'Critères de recherche',
	    'en' : 'Search criteria'
	};
	var  LABEL_VIEW =    {
	    'fr' : 'Vues',
	    'en' : 'Views'
	};
	
	var LABEL_FILTER = {
		'fr' : 'Filtres',
		'en' : 'Filters'
	};
	
	var  BUTTON_SEARCH =    {
	    'fr' : 'Rechercher',
	    'en' : 'Search'
	};
	
	var  LABEL_LINES_PER_PAGE =    {
	    'fr' : 'Nombre de lignes par page',
	    'en' : 'Number of lines per page'
	};
	
	var  LABEL_NO_RESULT =    {
	    'fr' : 'Pas de résultat',
	    'en' : 'No result'
	};
	var LABEL_LINE={
		'fr':'ligne',
		'en':'line'
	};
	
	var LABEL_SELECT_VIEW = {
		'fr':'Sélectionnez une vue',
		'en':'Select a view'
	};
	
	var LABEL_SELECT_DATAOBJECT={
		'fr':'Sélectionnez une entité',
		'en':'Select an Entity'
	};
	var LABEL_SELECT_TEMPLATE={
        'fr':'Sélectionnez une recherche',
        'en':'Select a Bookmark'
    };
	
	var LABEL_SELECT = {
		'fr':'Sélectionnez',
		'en':'Select'
	};
	
	var MESSAGE_MULTI_SHOW = {
		'fr':'clés possibles trouvées',
		'en':'possible keys are found'
	};
	var MESSAGE_SINGLE_SHOW = {
		'fr':'clé possible trouvée',
		'en':'possible key is found'
	};
	var DISPLAYXSD_TT = {
			'fr':'Afficher les détails XSD',
			'en':'Display XSD details'
	};
	
	
	var OPERATORS = {
		'fr':{
	   			CONTAINS:"contient le(s) mot(s)",
	   			EQUALS:"est égal à",
	   			NOT_EQUALS:"n'est pas égal à",
	   			GREATER_THAN:"est supérieur à",
	   			GREATER_THAN_OR_EQUAL:"est supérieur ou égal à",
	   			LOWER_THAN:"est inférieur à",
	   			LOWER_THAN_OR_EQUAL:"est inférieur ou égal à",
	   			STARTSWITH:"contient un mot commençant par",
	   			STRICTCONTAINS:"contient la phrase"
	   		},
		'en':{
	   			CONTAINS:"contains the word(s)",
	   			EQUALS:"is equal to",
	   			NOT_EQUALS:"is not equal to",
	   			GREATER_THAN:"is greater than",
	   			GREATER_THAN_OR_EQUAL:"is greater or equals",
	   			LOWER_THAN:"is lower than",
	   			LOWER_THAN_OR_EQUAL:"is lower or equals",
	   			STARTSWITH:"contains a word starting with",
	   			STRICTCONTAINS:"contains the sentence"
	   		}	
	};
	
	var FULL_TEXT_OPERS = {
			'fr':{
		           FULLTEXTSEARCH:"Recherche plein-texte"
	             },
 			'en':{
		           FULLTEXTSEARCH:"Full text search"
	             }
	};
	
	var DATE_OPERS = {
			'fr':{
		           EQUALS:"est égal à",
		           LOWER_THAN:"est antérieure à",
		           GREATER_THAN:"est postérieure à"
	             },
 			'en':{
		           EQUALS:"equals",
		           LOWER_THAN:"is before",
		           GREATER_THAN:"is after"
	             }
	};
	
	var NUMBER_OPERS = {
			'fr':{
					EQUALS:"est égal à",
					NOT_EQUALS:"n'est pas égal à",
					GREATER_THAN:"est supérieur à",
					GREATER_THAN_OR_EQUAL:"est supérieur ou égal à",
					LOWER_THAN:"est inférieur à",
					LOWER_THAN_OR_EQUAL:"est inférieur ou égal à"
		         },
			'en':{
					EQUALS:"is equal to",
					NOT_EQUALS:"is not equal to",
					GREATER_THAN:"is greater than",
					GREATER_THAN_OR_EQUAL:"is greater or equals",
					LOWER_THAN:"is lower than",
					LOWER_THAN_OR_EQUAL:"is lower or equals"
				  }	
	};
	
	var BOOLEAN_OPERS = {
			'fr':{
		           EQUALSTRUE:"est égal à vrai",
		           EQUALSFALSE:"est égal à faux"
	             },
	        'en' :{
			         EQUALSTRUE:"is equal to true",
			         EQUALSFALSE:"is equal to false"
	              }
	};
	
	var ENUM_OPERS = {
           'fr' :{
		            EQUALS:"est égal à",
		            LOWER_THAN:"est inférieur à",
		            GREATER_THAN:"est supérieur à",
		            LOWER_THAN_OR_EQUAL:"est inférieur ou égal à",
		            GREATER_THAN_OR_EQUAL:"est supérieur ou égal à"
	             },
	       'en' :{
	            	 EQUALS:"is equal to",
	            	 LOWER_THAN:"is lower than",
	            	 GREATER_THAN:"is greater than",
	            	 LOWER_THAN_OR_EQUAL:"is lower or equals",
	            	 GREATER_THAN_OR_EQUAL:"is greater or equals"
	             }
	};
	
	var BOOLEAN_MAP =
	{
	   'EQUALSTRUE'	 : 'equals true',
	   'EQUALSFALSE' : 'equals false'
	};
	
	var EQUAL_OPERS = 
	{
		'fr' : 'est égal à'	,
		'en' : 'equals'
	};
	
	var OPERATOR_UNDEFINED = {
	    'fr' : 'cliquer pour choisir',
	    'en' : 'click to select'	
	};
	
	
	var  MSG_CONFIRM_DELETE_ITEM =    {
	    'fr' : 'Voulez vous réellement effacer cet enregistrement ',
	    'en' : 'Do you really want to delete this record '
	};
	var  MSG_CONFIRM_DELETE_ITEMS =    {
	    'fr' : 'Voulez vous réellement effacer les enregistrements sélectionnés ',
	    'en' : 'Do you really want to delete the selected records '
	};	
	var  MSG_CONFIRM_LOGICAL_DELETE_ITEM =    {
	    'fr' : 'Veuillez renseigner le path pour supprimer le(s) enregistrement(s) : ',
	    'en' : 'Please input the path to delete the record(s): '
	};
	
	var  MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM =    {
	    'fr' : 'chemin : ',
	    'en' : 'path: '
	};
	
	var MSG_CONFIRM_SAVE_ITEM = {
		'fr' : 'Cet enregistrement existe déjà. Souhaitez-vous l\'écraser ?',
		'en' : 'This record exists. Do you want to overwrite it?'
	};
	var MSG_CONFIRM_OVERRIDE_ITEM = {
			'fr' : 'Cet enregistrement a été modifié par une autre personne. Si vous le sauvegardez maintenant, vous écraserez ses changements. Voulez-vous continuer ?',
			'en' : 'This object was also modified by somebody else. If you save now, you will overwrite his or her changes. Are you sure you want to do that?'
	};	
	
	var BUTTON_DISPLAY = {
		'fr':'Visualiser',
		'en':'Display'
	};
	
	var BUTTON_NEW_ITEM = {
		'fr':'Créer un enregistrement',
		'en':'New Record'
	};
	
	
	
	var BUTTON_BOOKMARK_SEARCH = {
        'fr':'Marquer cette recherche',
        'en':'Bookmark this Search'
    };
    
    var BUTTON_MANAGE_BOOKMARKS = {
        'fr':'Gérer les recherches',
        'en':'Manage Bookmarks' 
    };
	
	var SAVE = {
			'fr':'Sauvegarder',
			'en':'Save'
	};
	var BUTTON_SAVE = {
		'fr':'Sauvegarder',
		'en':'Save'
	};
	
	var BUTTON_REFRESH = {
		'fr':'Sauvegarder',
		'en':'Refresh'
	};
	
	var BUTTON_SAVE_AND_QUIT = {
		'fr':'Sauvegarder et fermer',
		'en':'Save and close'
	};
	
	var BUTTON_PERSONALIZED_VIEW = {
		'fr':'Vue personalisée',
		'en':'Personalized view'	
	};
	
	var BUTTON_TREE_VIEW = {
		'fr':'Vue en arbre',
		'en':'Tree View'
	};
	
	var BUTTON_EXPORT = {
		'fr':'Exporter vers Excel',
		'en':'Export to Excel'
	};
	
	var BUTTON_DELETE = {
		'fr':'Supprimer',
		'en':'Delete'
	};
	
	var BUTTON_DUPLICATE = {
		'fr':'Dupliquer',
		'en':'Duplicate'
	};
	
	var BUTTON_JOURNAL = {
		'fr':'Journal',
		'en':'Journal'
	};
	
	var BUTTON_ACTION = {
        'fr':'Lancement du processus',
        'en':'Launch Process'
    };
	
	var BUTTON_LOGICAL_DEL = {
		'fr':'Envoyer à la corbeille',
		'en':'Send to Trash'
	};
	
	var BUTTON_CREATE_USER = {
		'fr':'Créer un utilisateur',
		'en':'Add a new user'
	};
	
	var LINEAGE_BUTTON = {
		'fr':'Relations',
		'en':'Relations'
	};
	
	var BUTTON_TASK = {
		'fr':'Ouvrir la tâche',
		'en':'Open Task'
	};
	
	var LABEL_DATAOBJECT = {
		'fr':'Entité',
		'en':'Entity'
	};
	
	var LABEL_CONDITION = {
        'fr':'search criteria',
        'en':'search criteria'
    };
	
	var config_cal = {
		'fr':{
	   			MONTHS_LONG:["Janvier","F&eacute;vrier","Mars","Avril","Mai","Juin","Juillet","Aout","Septembre","Octobre","Novembre","D&eacute;cembre"],
	   			WEEKDAYS_SHORT:[]
	   		},
		'en':null		
	};
	
	var ALERT_NO_CHANGE={
		'fr':'Pas de changement depuis la dernière sauvegarde',
		'en':'No change since last save'
	};	
	
	var SELECTED_FIELD = {
		'fr':'champ sélectionné',
		'en':'selected field'
	};
	
	var SELECTED_FIELDS = {
		'fr':'champs sélectionnés',
		'en':'selected fields'
	};
	
	var BUTTON_PRINT = {
		'fr':'Imprimer',
		'en':'Print'
	};
	
	var TOO_MANY_RESULTS = {
		'fr' : 'enregistrements ont été trouvés.<br><br>Lancer tout de même la recherche? <br>Cette opération peut être très longue.',
		'en' : 'records found. <br><br>Do you still want to launch the search? <br>This could take a long time.'
	};
	
	var SEARCH_RESULT = {
		'fr':'Résultat de la recherche',
		'en':'Search result'
	};
	
	var TITLE_WINDOW_FK ={
		'fr':'Choix de la clé étrangère',
		'en':'Choose a foreign key'
	};
	
	var FILTER = {
		'fr':'Saisissez un critère de recherche puis sélectionnez une valeur dans la liste déroulante',
		'en':'Fill the box with a key-word then select a value'
	};
	var SELECT = {
		'fr':'Valider',
		'en':'Select'
	};
	var CLOSE = {
		'fr':'Fermer',
		'en':'Close'
	};
	var BUTTON_SEARCH ={
		'fr':'Rechercher',
		'en':'Search'
	};
	var EDIT_ITEM_TOOLTIP={
		'fr':'Tips: Les champs indiqués par * sont obligatoires.',
		'en':'Tips: Fields with * are required.'
	};
	var PHYSICALLY_DELETE_TOOLTIP={
		'fr':'Suppression physique de l\'enregistrement',
		'en':'Delete the record physically'
	};
	var LOGICALLY_DELETE_TOOLTIP={
		'fr':'Suppression logique de l\'enregistrement',
		'en':'Delete the record logically'
	};
	var SAVE_TOOLTIP={
		'fr':'Sauve cet objet',
		'en':'Save this object'
	}; 		
	var SAVEANDCLOSE_TOOLTIP={
		'fr':'Sauve cet objet et ferme l\'onglet',
		'en':'Save this object and close the tab'
	};
	var DUPLICATE_TOOLTIP={
		'fr':'Duplique l\'enregistrement sélectionné',
		'en':'Clone the selected record'
	};
	var REFRESH_TOOLTIP={
		'fr':'Rafraîchir',
		'en':'Refresh'
	};
	var JOURNAL_TOOLTIP={
		'fr':'Ouvre le journal d\'audit de cet enregistrement',
		'en':'Browse the audit trail for this record'
	};
	var LINEAGE_TOOLTIP={
		'fr':'Liste les enregistrements pointant sur cet enregistrement',
		'en':'List records pointing to this record'
	};
	var TASK_TOOLTIP={
		'fr':'Ouvre la tâche de cet enregistrement', 
		'en':'Browse the task for this record'
	};
	var ACTION_TOOLTIP={
        'fr':'Lancer le process',
        'en':'Launch Process'
    };
	
	var UPLOAD_FILE={
		'fr':'Transférer une image',
		'en':'Upload Picture'
	};
	var UPLOAD={
		'fr':'Transfer',
		'en':'Upload'
	};
	var RESET={
		'fr':'Réinitialiser',
		'en':'Reset'
	};
	var STATUS= {
		'fr' : 'Statut',
		'en' : 'Status'
	}; 
	var FILE_NAME={
		'fr':'Nom de fichier',
		'en':'File Name'
	};
	var errorDesc={
		'fr':'L\'item ne peut pas être sauvegardé, il y a une (des) erreur(s).',
		'en':'The item can not be saved, it contains error(s).'
	};
	var DELETE_ALERT={
		'fr':'Le dernier élément ne peut pas être supprimé',
		'en':'The last element can not be removed'
	};
	
	var CHOICE_ALLTOGETHER_ALERT={
			'fr': 'Vous ne pouvez choisir plus d\'une valeur ',
			'en': 'You may only choose one value '
	};

	var CHOICE_NONE_ALERT={
			'fr': 'Vous devez choisir au moins une valeur',
			'en': 'You must choose at least one value'
	};
	var CONFIRM_DATACLUSTER_CHANGE={
			'fr': 'Le data-container a été changé, veuillez le sélectionner à nouveau',
			'en': 'Data-container has been changed,please select it again'
	};
	
	var MONTH_NAME={
	   'fr':["Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Aout","Septembre","Octobre","Novembre","Decembre"],
	   'en':["January","February","March","April","May","June","July","August","September","October","November","December"]
	};
	var SEARCH_RESULT={
		'fr':'Définissez des critères de recherche et cliquez \'Rechercher\'.',
		'en':'Enter search criteria and hit the \'Search\' button.'
	};
	
	var SELECT_TYPE_MESSAGE={
        'fr': 'Sélectionnez le type',
        'en': 'Select a type'
    };
	
	var NO_TASK_MSG = {
		'fr':'Aucune tâche n\'est reliée à cet enregistrement!',
		'en':'No task related to this record!'
	};
	
	/*****************
	 * EXT 2.0
	 *****************/
	var itemsBrowserPanel;
	var gridContainerPanel;
	/********************************************************************
	 * Action show items
	 ********************************************************************/
	var _viewItems = [];
	var _viewItems2 = [];
	var _gridItems ;
	var _itemDetailsPanel = null;
	var _dataObject;
	var _dataObject2;
	var dataObjectLocalized ="";
	var _tbItems; 
	var itemsElements;
	var itemsPredicates = [];
	var currentPredicate = [];
	var newItem = [];
	var itemTreeList = [];

	var keys = [];
	var updateFlag = [];
	var lastUpdatedInputFlag = [];
	
	var treeCount=1; 
	
	var criteriaCount = 1;
	
	/** The foreign key search window */
	var foreignKeyWindow;
	/** The node date pickerwindow */
	var nodeDatePickerWindow;
	
	var manageSearchTemplateWindow;
	var saveReportWindow;
	/** The node upload file window */
	var uploadFileWindow;
	//var errorDesc = "The item can not be saved, it contains error(s). See details below:";
	var itemNodes = [];
	var map = [];
	var sortIndex=0;
	var sortUporDown="ASC";
	var isUp = true;
	var _criterias = new Array();
	var _searchCriteriaResult = "";
	var _exception = true;
	var lineMax = 20;
	
	var realValues = [];
    var lineMaxPrefix = "lineMax";
    
	//var conceptNameSelect = "";
	var isReadOnlyinItem = false;
	
	var foreignKeyFields = [];
	
	
	var conditions = [];
	
	var itemsCriteriaParentId = "1";
	
	var breadCrumbNavigator;
	
	var breadCrumbPL;
	
	function browseItems(){
		showItemsPanel();
		//populate list
        
		amalto.core.working('');
		ItemsBrowserInterface.getViewsList(getViewsItemsListCB,language);
		foreignKeyFields.length = 0;
		//displayItems();
	}
	
	var _rootNode;
            
           
	function showItemsPanel() {	
		var tabPanel = amalto.core.getTabPanel();
		itemsBrowserPanel = tabPanel.getItem('itemsBrowser');
		
		if(itemsBrowserPanel == undefined){
				
			gridContainerPanel = new Ext.Panel({
				id: 'items-list',
	    		region: 'center',
	    		layout:'fit',
				border: false,
				header:true,
				split:true,
				closable:true,
				collapsible: false
			});
				
           
			itemsBrowserPanel = new Ext.Panel({
				id: 'itemsBrowser',
				title: BROWSE_ITEMS[language],
				layout:'border',
				autoScroll: false,
				border: false,
				bodyBorder:false,
				closable: true,
				items: 
				[	
					new Ext.Panel({
						id: 'items-search',
			    		title: TITLE_SEARCH_PANEL[language],
			    		region: 'north',
						layout:'fit',
						border:false,
						bodyBorder:false,
						autoScroll: true,	
						collapsible: true,
						header:true,
						closable:true,
						bodyStyle:'padding:5px',
						height:200,		
						split:true,
						html: '' +
						'<div>'+LABEL_DATAOBJECT[language]+' : <select id="viewItemsSelect" onChange="amalto.itemsbrowser.ItemsBrowser.getViewItems();"><option value="">'+MSG_LOADING[language]+'</option></select>' +
						'<span id="viewItemsInfos"></span></div>' +

						'<span id="labelItemsCriteria" style="display:none">'+LABEL_CRITERIA[language] +' : </span>'+
						'<div id="itemsCriterias">' +
						'</div>' +
						'<br/>' +
						'<input id="item-search-btn" type="button" value="'+BUTTON_SEARCH[language]+'" disabled="true" onClick="amalto.itemsbrowser.ItemsBrowser.displayItems();"/>' +
						'<input id="item-new-btn" type="button" value="'+BUTTON_NEW_ITEM[language]+'" disabled="true"  onClick="amalto.itemsbrowser.ItemsBrowser.displayItemDetails();"/>'+
						'&nbsp;&nbsp;&nbsp;&nbsp;'+
						'<select id="viewItemsCriteriaListSelect" onChange="amalto.itemsbrowser.ItemsBrowser.getViewItems1();"><option value="">'+LABEL_SELECT_TEMPLATE[language]+'</option></select>' +
                        '<span id="viewItemsCriterias"></span>'+
						'<input id="item-save-btn" type="button" value="'+BUTTON_BOOKMARK_SEARCH[language]+'" disabled="true"  onClick="amalto.itemsbrowser.ItemsBrowser.saveCriteriasClick();"/>'+
						'<input id="item-manage-btn" type="button" value="'+BUTTON_MANAGE_BOOKMARKS[language]+'" disabled="true"  onClick="amalto.itemsbrowser.ItemsBrowser.manageSearchTemplates();"/>',
						border: false,
						bodyborder: false
					}),
				    gridContainerPanel
				]
			});			
			tabPanel.add(itemsBrowserPanel); 
		}//if
		
		itemsBrowserPanel.show();
		itemsBrowserPanel.doLayout();
		amalto.core.doLayout();

				
		$('item-search-btn').disabled = true;
		$('item-new-btn').disabled = true;
		$('item-save-btn').disabled = true;
		$('item-manage-btn').disabled = true;
		DWRUtil.setValue('itemsCriterias',"");
		$('labelItemsCriteria').style.display = "none";
		
		DWRUtil.removeAllOptions('viewItemsCriteriaListSelect');
	}
	
	function getViewsItemsListCB(result){
		var tmp = [LABEL_SELECT_DATAOBJECT[language]];
		DWRUtil.removeAllOptions('viewItemsSelect');
		DWRUtil.addOptions('viewItemsSelect',tmp);
		DWRUtil.addOptions('viewItemsSelect',result);
		amalto.core.ready('');
	}
	
	function getViewItems(){
		var viewName = DWRUtil.getValue('viewItemsSelect');
		//conceptNameSelect =  viewName.replace("Browse_items_","");
		amalto.core.working();
		if(viewName!=LABEL_SELECT_DATAOBJECT[language]){
			if(Ext.get('items-grid')!=undefined) {
				gridContainerPanel.remove('items-grid');
			}
			if(Ext.get('searchResult')==undefined){
				gridContainerPanel.add( {
					xtype : "panel",
					bodyStyle : 'margin:20px',
					style:'color: #8F8FBD;',
					border : false,
					bodyborder : false,
					items : [{
						xtype : 'label',
						id:'searchResult',
						text : SEARCH_RESULT[language]
					}]
				});

			}
			gridContainerPanel.doLayout();
			ItemsBrowserInterface.getviewItemsCriterias(getConditionsCB,viewName,true);
			ItemsBrowserInterface.getView(getViewItemsCB,viewName, language);
		}
		else{
			$('item-search-btn').disabled = true;
			$('item-new-btn').disabled = true;
			$('item-save-btn').disabled = true;
			$('item-manage-btn').disabled = true;
			$('labelItemsCriteria').style.display = "none";
			DWRUtil.setValue('itemsCriterias',"");
			amalto.core.ready();
		}
	}
	
	function getViewItems1(){
	   var viewName = DWRUtil.getValue('viewItemsCriteriaListSelect');
        amalto.core.working();
        if(viewName!=LABEL_SELECT_TEMPLATE[language]){
            ItemsBrowserInterface.getWhereItemsByCriteria(getWhereItemsCB,viewName);
        }
    }
	/**
     * @author ymli
     */
	function getWhereItemsCB(result){
	   var whereCriteria = result.split("###");
	   itemsCriteriaParentId = "1";
	   var criteria = null;
	 
       for(var j=_criterias.length;j>0;j--)
            if(_criterias[j] != undefined)
                removeItemsCriteria(j+1);
       for(var i = 0;i<whereCriteria.length;i++){
            criteria =  whereCriteria[i];
            var items = criteria.split("#");
            if(i==0){
                setValuesForCriteria(items,1);
                outPutCriteriaResult();
                }
             else{
                  addAdnSetItemsCriteria("itemsCriteria"+itemsCriteriaParentId, itemsCriteriaParentId, items[3]);
                  setValuesForCriteria(items,itemsCriteriaParentId);
                  outPutCriteriaResult(); 
                  
             }   
            
       }
	}
	/**
     * @author ymli
     */
	function setValuesForCriteria(items,idtt){
	   	
	   DWRUtil.setValue("itemsSearchField"+idtt,items[0]);
	   updateOperatorList(idtt);
	   
	   DWRUtil.setValue("itemsSearchOperator"+idtt,items[1]); 
	   DWRUtil.setValue("itemsSearchValue"+idtt,items[2]);
	   
	   if(idtt>1){
	       var AndIndex = idtt-1;
	       if(items[3]=="AND")
                $('itemSearchCriteriaForAnd' + AndIndex).checked = true;
            else if(items[3]=="OR")
                $('itemSearchCriteriaForOR' + AndIndex).checked = true;
	   }
	        
	}
	
	/**
     * @author ymli
     * */
	 function toDelete(viewName){
        Ext.MessageBox.confirm("confirm","Do you really want to remove this Bookmark?",function de(e){
            if(e.toLocaleString()=="yes"){
                ItemsBrowserInterface.deleteTemplate(viewName,function(){
                    ManageSearchTemplateStore.reload();
                    var dataObjectLabel=DWRUtil.getValue('viewItemsSelect');
                    ItemsBrowserInterface.getviewItemsCriterias(getConditionsCB,dataObjectLabel,true);
                });
                
                }
            }) ;
    }
	/**
	 * @author ymli
	 */
	function getConditionsCB(result){
	   conditions = result.split("##");
	   //DWRUtil.setValue("viewItemsCriterias","");
	  DWRUtil.removeAllOptions('viewItemsCriteriaListSelect');
	  DWRUtil.addOptions('viewItemsCriteriaListSelect',[LABEL_SELECT_TEMPLATE[language]]);
	  DWRUtil.addOptions('viewItemsCriteriaListSelect',conditions); 
	  amalto.core.ready();
    }
	
	 /**
     * @author ymli
     * */  
    function manageSearchTemplates(){
    	var dataObjectLabel=DWRUtil.getValue('viewItemsSelect');
    	ItemsBrowserInterface.getSearchTemplateNames(getTemplateBC,0,0,dataObjectLabel,false);
    }
    /**
     * @author ymli
     * */
    function deleteItemImg(){
            return "<img src='img/genericUI/delete.gif' style=\"cursor:pointer;\" border=\"0\" />";
        }
      
   /**
     * @author ymli
     * */     
   var ManageSearchTemplateStore = new Ext.data.Store({
                proxy: new Ext.data.DWRProxy(ItemsBrowserInterface.getSearchTemplates,true),
                reader:new Ext.data.ListRangeReader({
                    totalProperty: 'totalSize',
                    root: 'data',
                    id: 'name'
                },Ext.data.Record.create([
                      {name: 'name',type: "string"},
                      {name:null,type: "string"}
                      
                ])
                )
       }); 
       
       /**
     * @author ymli
     * */
    function getTemplateBC(result){
    	
    	if(manageSearchTemplateWindow){
                 manageSearchTemplateWindow.hide();
                 manageSearchTemplateWindow.destroy();
            }
    	
    	var myColumns = [
        {header: "Bookmarks",  sortable: true,dataIndex: 'name'}, 
        {header: "Delete",   sortable: true,renderer: deleteItemImg}];
        var cm = new Ext.grid.ColumnModel(myColumns);       
        cm.defaultSortable = true;
    
       var pageSize = 20;
       ManageSearchTemplateStore.on('beforeload', 
                    function(button, event) {
                    var viewName = DWRUtil.getValue('viewItemsSelect');	
                    
                      Ext.apply(this.baseParams,{
                                  regex: viewName
                                });
                    }
             );
        var manageSearchTemplateGridPanel = new Ext.grid.GridPanel({
            id:'manageSearchTemplate-grid',
            store: ManageSearchTemplateStore,
            autoScroll:true,
            cm:cm,
            enableColumnMove:true,
            closable:true,
            border:false,
            loadMask:true, 
            viewConfig: {
                autoFill:true,
                forceFit: false
            },
            bbar: new Ext.PagingToolbar({
                pageSize: parseInt(pageSize),
                store: ManageSearchTemplateStore,
                displayInfo: true,
                displayMsg: LABEL_DISPLAYING[language]+' {0} - {1} '+LABEL_OF[language]+' {2}',
                emptyMsg: LABEL_NO_RESULT[language]
            }),
             listeners:
                {
                                cellclick: function(g, rowIndex,  columnIndex, e){
                                var record = g.getStore().getAt(rowIndex);
                                if(columnIndex==1){
                                    
                                    toDelete(record.data.name);
                                }
                            }   
           
                }
        });
        
        manageSearchTemplateWindow = new Ext.Window({
                title: 'Manage Search Bookmarks',
                width: 300,
                height:500,
                minWidth: 400,
                minHeight: 600,
                layout: 'fit',
                plain:true,
                bodyStyle:'padding:5px;',
                buttonAlign:'center',
                items: manageSearchTemplateGridPanel
            });
            manageSearchTemplateWindow.show();
            ManageSearchTemplateStore.load({params:{start:0, limit:pageSize}});
    }
	/**
	 * @author ymli
	 * */
	function saveCriteriasClick(){
            var dataObjectLabel=DWRUtil.getValue('viewItemsSelect');
            if(saveReportWindow){
                 saveReportWindow.hide();
                 saveReportWindow.destroy();
            }
            

            var saveReportPanel = new Ext.form.FormPanel({
                 baseCls: 'x-plain',
                 labelAlign: 'left',     
                 //labelWidth: 60,           
                 //layout:'fit', 
                 xtype : "form",
                 items : [{
                    name:"SearchTemplateShared",
                    fieldLabel : "Shared",
                    xtype : "checkbox",
                    checked : false
                 },{
                    name:"SearchTemplateName",
                    fieldLabel : "Bookmark Name",
                    xtype : "textfield",
                    allowBlank : false,
                    value : ""
                 }]
            });
            
            saveReportWindow = new Ext.Window({
                title: "Save Report",
                width: 320,
                height:130,
                layout: 'fit',
                plain:true,
                bodyStyle:'padding:5px;',
                buttonAlign:'center',
                items: saveReportPanel,
                modal:true,
                buttons: [{
                    text: "Save",
                    handler: function(){
                                   saveReportWindowExecuteClick();
                                }.createDelegate(this)
                }]
            });
        
            saveReportWindow.show();

    }
	
    /**
     * @author ymli
     * */
     function saveReportWindowExecuteClick(){
     	 var dataObjectLabel=DWRUtil.getValue('viewItemsSelect');
        
        var reportName = DWRUtil.getValue('SearchTemplateName');
        if(reportName==''){
                Ext.MessageBox.alert("","Enter Bookmark Name, please!");
                return;
        }
        var isSharedReport = DWRUtil.getValue('SearchTemplateShared');
        
        ItemsBrowserInterface.isExistCriteria(dataObjectLabel,reportName,function(isExist){
            if(!isExist)
                ItemsBrowserInterface.saveCriteria(dataObjectLabel,reportName,isSharedReport,_criterias,function(result){
                    if(result=="OK"){
           	            saveReportWindow.destroy();
                        Ext.MessageBox.alert("Save","Save Bookmark Successfully!");
                        ItemsBrowserInterface.getviewItemsCriterias(getConditionsCB,dataObjectLabel,true);
                    }
                    else{
                        Ext.MessageBox.alert("Save",result);
                    }
                });
            else{
                    Ext.MessageBox.alert('Status', "This Bookmark already exist,please enter other name! ",function(){
                    saveCriteriasClick();
                    });
                }
        });
        
        
       
    }
	
	function getViewItemsCB(result){
		_viewItems = []; 
		_criterias = [];
		foreignKeyFields = [];
		_viewItems = result;
		$('labelItemsCriteria').style.display = "block";
		DWRUtil.setValue('itemsCriterias','<span id="itemsCriteria1"><select id="itemsSearchField1" onChange="amalto.itemsbrowser.ItemsBrowser.updateOperatorList(\'1\');amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<select id="itemsSearchOperator1" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<select id="enumSearchValue1" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<input id="itemsSearchValue1" type="text" value="*" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();" onkeyup="amalto.itemsbrowser.ItemsBrowser.checkInputSearchValue(this.id,this.value)" style="display:none;" onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/>' +
						'<span id="itemsForeignKeyValues1" style="display:none" onChange=""></span>' +
						'<span id="itemSearchCalendar1" style="display:none;cursor:pointer;padding-left:4px;padding-right:4px" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\'itemsSearchValue1\' , \'-1\', \'date\',\'null\')"><img src="img/genericUI/date-picker.gif"/></span>' +
						' <input id="itemSearchCriteriaForAnd1" type="radio" name="itemSearchCriteria1" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria1\', \'1\', \'AND\');"> AND '+
						'<input id="itemSearchCriteriaForOR1" type="radio" name="itemSearchCriteria1" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria1\', \'1\', \'OR\');"> OR '+
						'<br/></span>'
						);	
		DWRUtil.addOptions('itemsSearchOperator1',OPERATORS[language]);
		DWRUtil.removeAllOptions('itemsSearchField1');

		$('item-search-btn').disabled = false;

		var viewName = DWRUtil.getValue('viewItemsSelect');
		var tmp = viewName.replace("Browse_items_","");
		_dataObject = tmp.replace(/#.*/,"");
	
		// get root node to know if user can create item
		ItemsBrowserInterface.getRootNode(_dataObject,language, function(rootNode){
			_rootNode=rootNode;
//			if(!rootNode.readOnly) $('item-new-btn').disabled = false;
//			else $('item-new-btn').disabled = true;
			//$('item-new-btn').disabled=rootNode.readOnly;
			$('item-new-btn').disabled=rootNode.denyCreatable;
			$('item-save-btn').disabled=rootNode.readOnly;
			$('item-manage-btn').disabled=rootNode.readOnly;
			//if($('btn-logicaldelete'))$('btn-logicaldelete').disabled=$('item-new-btn').disabled;
			//if($('btn-delete'))$('btn-delete').disabled=$('item-new-btn').disabled;			
			if($('btn-logicaldelete'))$('btn-logicaldelete').disabled=rootNode.denyLogicalDeletable;
			if($('btn-delete'))$('btn-delete').disabled=rootNode.denyPhysicalDeletable;
		});
		//empty grid when another view is selected
		if(_gridItems){    
		    //_gridItems.destroy(false);
		    //DWRUtil.setValue('itemsResultCount','');
	    }
	    DWRUtil.addOptions('itemsSearchField1',_viewItems.searchables);
	    itemsElements = _viewItems.searchables;
	    dataObjectLocalized = _viewItems.descriptionLocalized;
		//getElements();
	    newCriteriaItemSet(0, 'AND');
	    outPutCriteriaResult();
//		updateOperatorList(1)
		
	    itemsPredicates = result.metaDataTypes;
		updateOperatorList(1);
		currentPredicate = [];
		//displayItems();
		amalto.core.ready();
	}
	
	function checkInputSearchValue(id,value){
	   var actualId=id.substring(id.length-1);
	   var operatorName=$('itemsSearchOperator' + actualId).value;
	   if(operatorName=='FULLTEXTSEARCH'){
    	   	var result=value.match('^(\\*|\\?).+');
            if(result!=null){
              Ext.Msg.alert("Warning","'*' or '?' not allowed as first character in WildcardQuery");
              return;
            }
	   }
    }
    
	function getElements(){
		var viewName = DWRUtil.getValue('viewItemsSelect');
		ItemsBrowserInterface.getSearchables(viewName,language,function(result) {
			//alert(DWRUtil.toDescriptiveString(result,3));	
			itemsElements = result;
			DWRUtil.addOptions('itemsSearchField1',result);
		});
	}
	
	function addItemsCriteria(criteriaParent, idx, and){	
		if (isNewCriteria(idx, and) == false) return;
		criteriaCount ++;
		var tpl = new Ext.DomHelper.Template(
						'<span id="itemsCriteria{id}">' +
						'<select id="itemsSearchField{id}" onChange="amalto.itemsbrowser.ItemsBrowser.updateOperatorList(\'{id}\');amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<select id="itemsSearchOperator{id}" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<select id="enumSearchValue{id}" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<input id="itemsSearchValue{id}" type="text" value="*" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();" onkeyup="amalto.itemsbrowser.ItemsBrowser.checkInputSearchValue(this.id,this.value)"                       onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/>' +
						'<span id="itemsForeignKeyValues{id}" style="display:none" onChange=""></span>' +
						'<span id="itemSearchCalendar{id}" style="display:none;cursor:pointer;padding-left:4px;padding-right:4px" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\'itemsSearchValue{id}\' , \'-1\', \'date\')"><img src="img/genericUI/date-picker.gif"/></span>' +
						' <input id="itemSearchCriteriaForAnd{id}" type="radio" name="itemSearchCriteria{id}" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria{id}\', \'{id}\', \'AND\');"> AND '+
						'<input id="itemSearchCriteriaForOR{id}" type="radio" name="itemSearchCriteria{id}" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria{id}\', \'{id}\', \'OR\');"> OR '+
						'<span onClick="amalto.itemsbrowser.ItemsBrowser.removeItemsCriteria(\'{id}\');"><img src="img/genericUI/delete.png"/></span> '  +
						'<br/></span>'
						);
		var actulId = parseInt(idx) + 1;
		tpl.insertAfter(criteriaParent,{id:actulId});
		DWRUtil.addOptions('itemsSearchOperator'+actulId,OPERATORS[language]);
		DWRUtil.addOptions('itemsSearchField'+actulId,itemsElements);
		
		newCriteriaItemSet(idx, and);
		updateOperatorList(actulId);
	}
	
	
	function addAdnSetItemsCriteria(criteriaParent, idx, and){
		
		if (isNewCriteria(idx, and) == false) return;
        criteriaCount ++;
        var tpl = new Ext.DomHelper.Template(
                        '<span id="itemsCriteria{id}">' +
                        '<select id="itemsSearchField{id}" onChange="amalto.itemsbrowser.ItemsBrowser.updateOperatorList(\'{id}\');amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
                        '<select id="itemsSearchOperator{id}" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
                        '<select id="enumSearchValue{id}" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
                        '<input id="itemsSearchValue{id}" type="text" value="*" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();" onkeyup="amalto.itemsbrowser.ItemsBrowser.checkInputSearchValue(this.id,this.value)" onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/>' +
                        '<span id="itemsForeignKeyValues{id}" style="display:none" onChange=""></span>' +
                        '<span id="itemSearchCalendar{id}" style="display:none;cursor:pointer;padding-left:4px;padding-right:4px" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\'itemsSearchValue{id}\' , \'-1\', \'date\')"><img src="img/genericUI/date-picker.gif"/></span>' +
                        ' <input id="itemSearchCriteriaForAnd{id}" type="radio" name="itemSearchCriteria{id}" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria{id}\', \'{id}\', \'AND\');"> AND '+
                        '<input id="itemSearchCriteriaForOR{id}" type="radio" name="itemSearchCriteria{id}" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria{id}\', \'{id}\', \'OR\');"> OR '+
                        '<span onClick="amalto.itemsbrowser.ItemsBrowser.removeItemsCriteria(\'{id}\');"><img src="img/genericUI/delete.png"/></span> '  +
                        '<br/></span>'
                        );
        var actulId = parseInt(idx) + 1;
        tpl.insertAfter(criteriaParent,{id:actulId});
        DWRUtil.addOptions('itemsSearchOperator'+actulId,OPERATORS[language]);
        DWRUtil.addOptions('itemsSearchField'+actulId,itemsElements);
        
        newCriteriaItemSet(idx, and);
        //updateOperatorList(actulId);
		itemsCriteriaParentId =  actulId;
	   
	}
	
	
	function isNewCriteria(idx, and)
	{	
		var parentIdx = parseInt(idx) -1;
		for (var id = parentIdx; id >= 0; id--)
		{
			if (_criterias[id] != null)
			{
				_criterias[id][1] = ' ';
				_criterias[id][2] = and;
				_criterias[id][3] = ' ';
				break;
			}
		}
		
		for (var ic = parseInt(idx); ic < _criterias.length; ic++)
		{
			if (_criterias[ic] != undefined)
				return false;
		}

		return true;
	}
	
	function newCriteriaItemSet(idx, and)
	{	
		var actulId = parseInt(idx) + 1;
		
		var criteria = DWRUtil.getValue('itemsSearchField' + actulId) + ' '
				+ DWRUtil.getValue('itemsSearchOperator' + actulId) + ' '
				+ DWRUtil.getValue('itemsSearchValue' + actulId);
		
		if (_criterias[idx] == undefined)
		{
			_criterias[idx] = [];
			_criterias[idx][0] = criteria;	
		}
	}
	
	function setCriteriaItemSet(idx, and){
	  // var actulId = parseInt(idx) + 1;
        
        var criteria = DWRUtil.getValue('itemsSearchField' + idx) + ' '
                + DWRUtil.getValue('itemsSearchOperator' + idx) + ' '
                + DWRUtil.getValue('itemsSearchValue' + idx)+ ' '+and;
        
        if (_criterias[idx-1] != undefined)
        {
            _criterias[idx-1] = [];
            _criterias[idx-1][0] = criteria;  
        }
	}
	
	
	function removeItemsCriteria(id){
		//criteria.splice(parseInt(id),1);
		var criteriaId = "itemsCriteria"+id;
		$('itemsCriterias').removeChild($(criteriaId));
		foreignKeyFields[id] = null;
		
		id = parseInt(id);
		_criterias[id-1] = null;
//		_criterias.length=_criterias.length-1;
		for (var subid = id-2; subid >= 0 ; subid--)
		{
			if (_criterias[subid] != undefined)
			{
				var last = true;
				for (var l = subid+1; l < _criterias.length; l++)
				{
					if (_criterias[l] != undefined)
					{
						last = false;
						break;
					}
				}
				if (last == true)
				{
					_criterias[subid][2] = null;
					_criterias[subid][3] = null;
					subid = subid +1;
					$('itemSearchCriteriaForAnd' + subid).checked = false;
					$('itemSearchCriteriaForOR' + subid).checked = false;
				}

				criteriaCount--;
				break;
			}
		}
		
		 outPutCriteriaResult();
	}
	
	function itemsCriteriaWithConstraints(criteriaParent, id, and){
		addItemsCriteria(criteriaParent, id, and);
		outPutCriteriaResult();
	}
	
	
	function updateCurrentPredicate(id)
	{
		currentPredicate[id] = "";
		if(!$('itemsSearchField' + id))return;
		var search = $('itemsSearchField' + id).value;
//		var delimeter = search.indexOf("/");
//		if(delimeter != -1)
//		{
//			search = search.substring(delimeter + 1);
//		}
		if(itemsPredicates[search] == null)return;
		var predicateValues =  itemsPredicates[search][0];
		if(predicateValues == 'xsd:boolean')
		{
			currentPredicate[id] = 'boolean';
		}
		else if(predicateValues == 'foreign key')
		{
			currentPredicate[id] = 'foreign key';
		}
		else if(predicateValues == 'enumeration')
		{
			currentPredicate[id] = 'enumeration';
		}
	}
	
	function updateOperatorList(id){
        if($('itemsSearchField' + id) == null)return;
		var search = $('itemsSearchField' + id).value;
		var delimeter = search.indexOf("/");
		if(delimeter==-1){
			 var viewName = DWRUtil.getValue('viewItemsSelect');
			 //see 0011618: The web app does not recognize a FT search when the browse view has a suffix (Browse_item_<entity>#suffix) 
			 var conceptName = viewName.replace("Browse_items_","").replace(/#.*/,"");
			 if(search==conceptName){
			 	DWRUtil.removeAllOptions('itemsSearchOperator' + id);
			 	DWRUtil.addOptions('itemsSearchOperator' + id ,FULL_TEXT_OPERS[language]);
			 	var itemsSearchValuex = "itemsSearchValue" + id;
			 	$(itemsSearchValuex).value = "*";
			    $(itemsSearchValuex).style.display = 'inline';
			    $('enumSearchValue' + id).style.display = 'none';
			 	return;
			 }
		}else{
//			search = search.substring(delimeter + 1);
		}
		
        if(itemsPredicates[search] == null) return;
		var predicateValues =  itemsPredicates[search][0];
		var itemsSearchValuex = "itemsSearchValue" + id;
		var itemsForeignKeyValues = "itemsForeignKeyValues" +id;
		DWRUtil.removeAllOptions('itemsSearchOperator' + id);
		DWRUtil.removeAllOptions('enumSearchValue' + id);
		$('itemSearchCalendar' + id).style.display = 'none';
		$('itemsForeignKeyValues' + id).style.display = 'none';
		$('enumSearchValue' + id).style.display = 'none';
		currentPredicate[id] = "";
		
		if(predicateValues == 'xsd:string' || predicateValues == 'xsd:normalizedString' || predicateValues == 'xsd:token')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,OPERATORS[language]);
			$(itemsSearchValuex).value = "*";
			$(itemsSearchValuex).style.display = 'inline';
		}
		else if(predicateValues == 'xsd:date' || predicateValues == 'xsd:time' || predicateValues == 'xsd:dateTime')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,DATE_OPERS[language]);
			$('itemSearchCalendar' + id).style.display = 'inline';
			$(itemsSearchValuex).style.display = 'inline';
		}
		else if (predicateValues == 'xsd:double'
			    || predicateValues == 'xsd:float'
				|| predicateValues == 'xsd:integer'
				|| predicateValues == 'xsd:decimal'
				|| predicateValues == 'xsd:byte'
				|| predicateValues == 'xsd:int'
				|| predicateValues == 'xsd:long'
				|| predicateValues == 'xsd:negativeInteger'
				|| predicateValues == 'xsd:nonNegativeInteger'
				|| predicateValues == 'xsd:nonPositiveInteger'
				|| predicateValues == 'xsd:positiveInteger'
				|| predicateValues == 'xsd:short'
				|| predicateValues == 'xsd:unsignedLong'
				|| predicateValues == 'xsd:unsignedInt'
				|| predicateValues == 'xsd:unsignedShort'
				|| predicateValues == 'xsd:unsignedByte')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,NUMBER_OPERS[language]);
			$(itemsSearchValuex).style.display = 'inline';
		}
		else if(predicateValues == 'xsd:boolean')
		{
			var booleanPredicates = ['true' , 'false']; 
			var prefix = EQUAL_OPERS[language];
		    for(var i = 0; i < booleanPredicates.length; i++)
		    {
		    	booleanPredicates[i] = prefix + " " +  booleanPredicates[i];
		    }
			DWRUtil.addOptions('itemsSearchOperator' + id ,BOOLEAN_OPERS[language]);
			$(itemsSearchValuex).style.display = 'none';
			currentPredicate[id] = 'boolean';
		}
		else if(predicateValues == 'foreign key')
		{
			var foreignValues = itemsPredicates[search];
			var foreignPredicates = []; 
			var prefix = EQUAL_OPERS[language];
			for(var i = 1; i < foreignValues.length; i++)
			{
				foreignPredicates[i -1] = foreignValues[i]; //prefix + " " + foreignValues[i];
			}
			DWRUtil.addOptions('itemsSearchOperator' + id ,OPERATORS[language]);
			$(itemsSearchValuex).style.display = "none";
			$(itemsForeignKeyValues).style.display='inline';
//			$(itemsForeignKeyValues).readOnly = false;
//			DWRUtil.removeAllOptions('itemsForeignKeyValues' + id);
//			DWRUtil.addOptions(itemsForeignKeyValues, foreignPredicates);

			var fkField = foreignKeyFields[id];
			var fks = [];
			var path = $('itemsSearchField' + id).value;
			ItemsBrowserInterface.getFKvalueInfoFromXSDElem(_dataObject,path, function(fkInfos){
				fks = fkInfos;
				
				if(fkField == null) {
					fkField = new amalto.widget.ForeignKeyField({
				    	id:itemsForeignKeyValues,
				    	name:itemsForeignKeyValues,
				    	autoHeight : true,
			            width: 160,
			            defaultAutoCreate : {tag: "input", type: "text", style :"height:21px;" ,autocomplete: "off"},
						xpathForeignKey : fks["foreignKey"] + "",
						xpathForeignKeyInfo: fks["foreignKeyInfo"] + "",
						//fkFilter : fks["foreignKeyFilter"] + "",
						fkFilter : "",
						retrieveFKinfos : fks["foreignKeyRetrieve"] + "",
						showDeleteButton : false,
						renderTo : itemsForeignKeyValues
					 });
					foreignKeyFields[id] = fkField;
				}
				else
				{
					fkField.setForeignKey(fks["foreignKey"] + "", fks["foreignKeyInfo"] + "", fks["foreignKeyFilter"] + "");
				}
			});
				

			
			
			if(fkField != null)
			   fkField.show(this);
			
			currentPredicate[id] = 'foreign key';
		}
		else if(predicateValues == 'enumeration')
		{
			var enumValues = itemsPredicates[search];
			var enumPredicates = [];
			var prefix = EQUAL_OPERS[language];
			for(var i = 1; i < enumValues.length; i++)
			{
				enumPredicates[i -1] = enumValues[i]; //prefix + " " + enumValues[i];
			}
			DWRUtil.addOptions('itemsSearchOperator' + id ,ENUM_OPERS[language]);
			DWRUtil.addOptions('enumSearchValue' + id, enumPredicates);
			$('enumSearchValue' + id).style.display = "inline";
			$(itemsSearchValuex).style.display = "none";
			currentPredicate[id] = 'enumeration';
		}
		else if(predicateValues == 'complex type')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,OPERATORS[language]);
			$(itemsSearchValuex).value = "*";
			$(itemsSearchValuex).style.display = 'inline';
		}
		else
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,OPERATORS[language]);
			$(itemsSearchValuex).value = "*";
			$(itemsSearchValuex).style.display = 'inline';
		}
	}
	

	
	function outPutCriteriaResult(){
		var cpy = new Array();
		for (var idx = 0; idx < _criterias.length; idx++)
		{
			if (_criterias[idx] == undefined)
				continue;
			var actulID = idx+1;
			
			if(!$('itemsSearchField' + actulID))break;
			
			var criteria = DWRUtil.getValue('itemsSearchField' + actulID) + ' '
					+ convertSearchValueInEnglish(actulID) + ' ';
			var searchValue = "";
			if($('itemsSearchValue' + actulID).style.display == "inline")
			{
				searchValue = DWRUtil.getValue('itemsSearchValue' + actulID);
				searchValue = searchValue.replace(/\[/g, "\\[");
				searchValue = searchValue.replace(/\]/g, "\\]");
			}
			else if($('itemsForeignKeyValues' + actulID).style.display == "inline")
			{
				var fkField = foreignKeyFields[actulID];
//				searchValue = DWRUtil.getValue('itemsForeignKeyValues' + actulID);
				if(fkField != undefined || fkField != null)
				{
				   searchValue = fkField.el.dom.value;
				   if(fkField.getValue().length > 0)
				   {
					   searchValue = _criterias.length == 1 ? fkField.getValue().replace(/\[|\]/g, "") : fkField.getValue();
				   }
				   else if(searchValue == fkField.getTextOrg())
				   {
					   searchValue = fkField.getValueOrg();
				   }
				}
					

			}
			_criterias[idx][0] = criteria + searchValue;
		}
		
		for (var findex = 0; findex < _criterias.length; findex++)
		{
			if (_criterias[findex] != undefined)
			{
				for (var sindex = 0; sindex <_criterias[findex].length; sindex++)
				{
					var indx = cpy.length;
					if (_criterias[findex][sindex] != null) {
						cpy[indx] = _criterias[findex][sindex];
					}
				}
			}
		}
		
		var cruise = true;
		var sign = 0;
		
		while (cruise) {
			for ( var i = sign; i < cpy.length; i++) {
				if (cpy[i] == 'AND') {
					if (cpy[i - 2] == null) {
						cpy.unshift('(');
						break;
					} else if (cpy[i - 4] != '(' && cpy[i - 4] != 'AND') {
						if (cpy[i - 3] != '(') {
							cpy.splice(i - 2, 0, '(');
							break;
						}
					}

					if (cpy[i + 4] == 'AND') {
						sign = i + 2;
						continue;
					} else {
						if (cpy[i + 4] == 'OR') {
							cpy.splice(i + 3, 0, ')');
							break;
						} else if (cpy[i + 4] == null) {
							cpy.push(')');
							cruise = false;
							break;
						}
					}
				}

				if (i == cpy.length - 1) {
					cruise = false;
					break;
				}
			}
		}
		
		_searchCriteriaResult = cpy.join('');
   }
	

	function convertSearchValueInEnglish(id)
	{
		var searchValue = $('itemsSearchValue' + id).value;
		var operValue = $('itemsSearchOperator' + id).value;

		if(currentPredicate[id] == 'boolean')
		{
			searchValue = BOOLEAN_MAP[operValue];
		}
		else if(currentPredicate[id] == 'foreign key')
		{
			var cpyOper = operValue;
			var equalInFr = 'est égal à';
			var v = cpyOper.indexOf(equalInFr);
			
            if(v == 0)
			{
				var res = "equals " + cpyOper.substring(v + 11);
				searchValue = res;
			}
            else
            {
            	searchValue = cpyOper;
            }

			
		}
		else if(currentPredicate[id] == 'enumeration')
		{
			searchValue =  operValue + " " + $('enumSearchValue' + id).value;;
		}
		else
		{
			searchValue = operValue;
		}
		
		
		return searchValue;
	}
	
	function getLineMaxFromCookie(){
		if(document.cookie == undefined || document.cookie == null){
			return 20;
		}
		
        var strCookie=document.cookie;
        var arrCookie=strCookie.split("; ");
        for(var i=0;i<arrCookie.length;i++){
              var arr=arrCookie[i].split("=");
              if(arr[0]=='lineMax')
            	  return arr[1];
        }


		return 20;
	}
	
	function setLineMaxToCookie(lineNum){
		if(document.cookie == undefined || document.cookie == null){
			return;
		}
		
		var posValue = document.cookie.indexOf(lineMaxPrefix + '=');
		var middleValue = lineMaxPrefix + "=" + lineNum;
	    var futdate = new Date()		
	    var expdate = futdate.getTime()  
	    expdate += 3600*10000000  
	    futdate.setTime(expdate)
	    var newCookie= lineMaxPrefix + "=" + lineNum + "; path=/;"	//Set the new cookie values up
	    newCookie += " expires=" + futdate.toGMTString()

		window.document.cookie = newCookie;
	}
	
	function displayItems(){
		updateCurrentPredicate(1);
		outPutCriteriaResult();
		if(!$('viewItemsSelect'))return;
		var viewName = DWRUtil.getValue('viewItemsSelect');
		if(viewName!=LABEL_SELECT_DATAOBJECT[language] && viewName!=""){	
			amalto.core.working();
			var columnsHeader = [];
			ItemsBrowserInterface.getViewables(viewName, language, function(result){		
				columnsHeader = result;
				var lineNum = getLineMaxFromCookie();
				displayItems2(columnsHeader,lineNum);
				
				//delete/logicaldelete should be the same as new buttton
				//$('btn-logicaldelete').disabled=$('item-new-btn').disabled;
				//$('btn-delete').disabled=$('item-new-btn').disabled;
			});	
		}
	
	}
	

	
	
	function displayItems2(columnsHeader, pageSize) {
		
		
		_dataObject2 = _dataObject;
		_viewItems2 = _viewItems;
		var itemPK = [];
		amalto.core.working();
		var viewName = DWRUtil.getValue('viewItemsSelect');	
		if(_gridItems){    
		    _gridItems.destroy(false);
	    }
	
		var displayDoc = function(grid, rowIndex, columnIndex, e){
		    try{
		    	for(var i=0; i<_viewItems2.keys.length; i++) {
		    		itemPK[i] = grid.dataModel.getRow(rowIndex)[i];
		    	}
		    	displayItemDetails(itemPK,_dataObject2);
	        }
	        catch(error){
	        	Ext.Msg.alert("error",error);
	        }
	    };

	   
	    var tmpFields = new Array();
	    for(var i=0; i<_viewItems2.viewables.length; i++) {
	    	var tmp = "/"+_viewItems2.viewables[i];
	    	tmpFields.push(tmp);
	    }
	    
	    var myColumns = [];	    
	    var sm2 = new Ext.grid.CheckboxSelectionModel();
	    //myColumns.push(new Ext.grid.RowNumberer());
	    myColumns.push(sm2);
		for(var k=0;k<_viewItems2.viewables.length;k++){
			myColumns.push({
				header: columnsHeader[k], 
				sortable: true,
				width:100,
				dataIndex: tmpFields[k]
				
			});
		}
		
		var cm = new Ext.grid.ColumnModel(myColumns);		
		//cm.defaultWidth = 200;
	   	cm.defaultSortable = true;
	   	
		var schema = {
		    root: 'items',
	    	totalProperty: 'TotalCount',
	    	id: 'nothing',
			fields: tmpFields
		};
	
	    
		//Build a comma-dash separated list of search criteria  searchCriteriaResult
	    var criteria = _searchCriteriaResult;	

		var store = new Ext.data.Store({
			    proxy: new Ext.data.HttpProxy({
		        	url: '/itemsbrowser/secure/ItemsRemotePaging',
		        	timeout : 360000
		        }),
		        //sortInfo:{field: _viewItems2.keys[sortIndex], direction: sortUporDown},
		        baseParams:{viewName: viewName,criteria:criteria},
		        reader: new Ext.data.JsonReader(schema),
		        remoteSort: true
		    });
		
		store.on("loadexception",function(obj, options, response, e) {
	        //alert('Exception occurred while loading item list! ');
			if(response != undefined && response.responseText != undefined) {
				if(response.responseText.indexOf("Data Container can't be empty!") >-1)
					Ext.MessageBox.alert("Warning", response.responseText);
				else if(response.responseText != null)
					Ext.MessageBox.alert("Error", response.responseText);	
			}
			else {
				Ext.MessageBox.alert("Error",'Exception occurred while loading item list! ');
			}
	    });
		    
		var grid = new Ext.grid.GridPanel({
			id:'items-grid',
		    store: store,
		    autoScroll:true,
		    //columns: myColumns,
		    cm:cm,
			enableColumnMove:true,
			closable:true,
			border:false,
			loadMask:true, 
		    viewConfig: {
		    	autoFill:true,
		        forceFit: false
		    },
		    sm: sm2,
	        // inline buttons
	        bbar: [
	        	{text:BUTTON_DELETE[language],
	        		id:'btn-delete',
	        		//hidden :true,
	        		xtype:'button',
	        		iconCls : 'item_bt_delete',
	        		disabled: _rootNode.denyPhysicalDeletable, //$('item-new-btn').disabled,
	        		tooltip:PHYSICALLY_DELETE_TOOLTIP[language],
		        	listeners:{
		        		'click':function(){
			        		var sel=sm2.getSelections();
			        		var dcount = 0;//used to record the successful callback count
			        		if(sel.length==0) return;
				    		Ext.MessageBox.confirm("confirm",MSG_CONFIRM_DELETE_ITEMS[language]+ " ?",function re(en){
					    		if(en=="yes"){
					        		for(var j=0; j<sel.length; j++){
					        			//get ItemPK
					        			var itemPK=[];
								    	for(var i=0; i<_viewItems2.keys.length; i++) {
								    		itemPK[i] = sel[j].get(_viewItems2.keys[i]);
								    	}
								    	var ids="";
//										for(var i=0; i<itemPK.length; i++) {
//											ids += (ids==""?"":"@"); 
//											ids += itemPK[i];			
//										}							    	
										var treeIndex=1;
										if(_dataObject==null) _dataObject=_dataObject2;
										ItemsBrowserInterface.getUriArray(_dataObject, itemPK, function(picUriArray){
											var uriArray = [];
											uriArray=picUriArray;
											for (var index = 0; index < uriArray.length; index++) {
												var picUri=uriArray[index];
											     if(picUri!=""){
													 var pos=picUri.indexOf('?');
									 				 var uri=picUri.substring("/imageserver/".length,pos); 
													 Ext.Ajax.request({  
													       	 url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
													         method: 'post',  
													         callback: function(options, success, response) {  
													         }  
													 });
											     }//end if
			                               }//end for
			                            });//end callback									
										ItemsBrowserInterface.deleteItem(_dataObject, itemPK,0, function(result){
											if(result==null)
												return;
											else if(result.lastIndexOf("ERROR")>-1){
												var err1=result.substring(7);
												if(err1==null || err1==""){
													return;
												}else{
                                                    var errOne = dropOnErrorMsg(err1);
													Ext.MessageBox.show({msg:errOne, buttons:{"ok":"CANCEL"}, icon:Ext.MessageBox.ERROR});
													return;
												}
											}
											amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);										
											
											dcount++;
											//added by lzhang
											//fix bug 0017503
											if ( dcount == sel.length)
												displayItems();
											
											if(result)Ext.MessageBox.show({msg:result, buttons:{"ok":"OK"}, icon:Ext.MessageBox.INFO});			
										});
					        		}				        		
					    		}				    			
				    		});	//comfirm		        			        		
		        		}
		        	}
	        	},
	        	new Ext.Toolbar.Separator(),
	        	{text:BUTTON_LOGICAL_DEL[language],
	        		id:'btn-logicaldelete',
	        		xtype:'button',
	        		iconCls : 'item_bt_sendTrash',
	        		disabled:_rootNode.denyLogicalDeletable,
	        		tooltip:LOGICALLY_DELETE_TOOLTIP[language],
	        		listeners:{
		        		'click':function(){
			        		var sel=sm2.getSelections();
			        		if(sel.length==0) return;
							Ext.Msg.show({
							   title: MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM[language],
							   msg:  MSG_CONFIRM_LOGICAL_DELETE_ITEM[language],
							   buttons: Ext.Msg.OKCANCEL,
							   fn: doLogicalDelete,
							   prompt:true,
							   value: '/',
							   width:300
							});
							
							function doLogicalDelete(btn, path){
						       if (btn == 'cancel') {
									return;
								}
						       	
								var dcount = 0; //used to record the successful callback count 		        		
				        		for(var j=0; j<sel.length; j++){
				        			//get ItemPK
				        			var itemPK=[];
							    	for(var i=0; i<_viewItems2.keys.length; i++) {
							    		itemPK[i] = sel[j].get(_viewItems2.keys[i]);
							    	}
//							    	var ids="";
//									for(var i=0; i<itemPK.length; i++) {
//										ids += (ids==""?"":"@"); 
//										ids += itemPK[i];			
//									}							    	
									var treeIndex=1;
									if(_dataObject==null) _dataObject=_dataObject2;
									ItemsBrowserInterface.logicalDeleteItem(_dataObject, itemPK, path,treeIndex, function(result){
											if (result == null) return;
											else if(result.lastIndexOf("ERROR")>-1){
												var err1=result.substring(7);
												Ext.MessageBox.alert("ERROR", err1);
												return;
											}
											dcount++;
											//added by lzhang
											//fix bug 0017503
											if (dcount == sel.length)
												displayItems();
									});									
				        		}		
				        			
							};			        				        		
		        		}
		        	}
	        	}
	        ],
	        buttonAlign:'left',		    
		    listeners: {
		    	'rowdblclick': function(g, rowIndex, e){
								//alert("keys "+DWRUtil.toDescriptiveString(_viewItems2.keys,3));		    					
						    	for(var i=0; i<_viewItems2.keys.length; i++) {
						    		itemPK[i] = g.getStore().getAt(rowIndex).get(_viewItems2.keys[i]);
						    	}
						    	if(itemPK && itemPK.length>0){
						    		displayItemDetails(itemPK,_dataObject2);
						    	}
		                	}
	    	},
			tbar: new Ext.PagingToolbar({
		        pageSize: parseInt(pageSize),
		        store: store,
		        displayInfo: true,
		        displayMsg: LABEL_DISPLAYING[language]+' {0} - {1} '+LABEL_OF[language]+' {2}',
		        emptyMsg: LABEL_NO_RESULT[language],
		        items:[ 
		        	new Ext.Toolbar.Separator(),
		        	new Ext.Toolbar.TextItem(LABEL_LINES_PER_PAGE[language]+" : "),
		        	new Ext.form.TextField({
    					id:'itemslineMaxItems',
    					value:pageSize,
    					width:30,
    					listeners: {
		                	'specialkey': function(a, e) {
					            if(e.getKey() == e.ENTER) {
			                		lineMax = DWRUtil.getValue('itemslineMaxItems');
									if(lineMax==null || lineMax=="") 
										lineMax=20;
									
									if (isNaN(lineMax))
									{
										Ext.MessageBox.alert("Error","Please Input a valid number");
										return;
									}
									setLineMaxToCookie(lineMax);
									displayItems2(columnsHeader,lineMax);
					            } 
							},
							'change':function(field,newValue,oldValue){
								
								if(newValue != oldValue){
								    lineMax = newValue;
								    if(lineMax==null || lineMax=="") 
                                        lineMax=20;
								    setLineMaxToCookie(lineMax);
								    displayItems2(columnsHeader,lineMax);
								}
							
							}
		                }
		            })
		        ]
		    })
		});
	
		
		if(Ext.get('items-grid')!=undefined) {
			gridContainerPanel.remove('items-grid');
		}		
		gridContainerPanel.insert(0,grid);
		//grid.render();
		itemsBrowserPanel.doLayout();  
		amalto.core.doLayout();
		grid.setHeight(gridContainerPanel.getInnerHeight());	

		store.load({params:{start:0,limit:pageSize}});
		amalto.core.ready();

		store.on('load',function(){
			grid.render();
		});	

	}   
	
	
	/********
	  Toolbars management
	*********/
	
	
	// options
	var O_TREE_VIEW 	= 1;
	var O_PERSO_VIEW 	= 2;
	var O_PRINT 		= 4;
	var O_SAVE 			= 8;
	var O_SAVE_QUIT 	= 16;
	var O_DELETE 		= 32;
	var O_LOGICAL_DEL   = 64;
	var O_DUPLICATE     = 128;
	var O_JOURNAL       = 256;
	var O_ACTION        = 512;
	var O_REFRESH       = 1024;
	var O_TASK          = 2048;
	var O_LINEAGE       = 4096;
	var O_SMARTVIEW_SWITCH = 8192;
	
	// modes
	var M_TREE_VIEW		= 1;
	var M_PERSO_VIEW	= 2;
	
	function clearToolBar(toolbar)
	{
		if (toolbar.tr!=undefined && toolbar.tr.childNodes)
		{
			while(toolbar.tr.childNodes.length > 0)
			{
				toolbar.tr.removeChild(toolbar.tr.childNodes[0]);
			}
		}
	}
	
	function initToolBar2(toolbar, mode){
		
	}
	
	function reFreshToolBar(toolbar) {
		toolbar.baseOptions |= O_DELETE|O_LOGICAL_DEL|O_DUPLICATE|O_JOURNAL|O_REFRESH; 
		initToolBar(toolbar, M_TREE_VIEW);
	}
	
	function initToolBar(/*Ext.Toolbar*/toolbar, mode)
	{
		
		clearToolBar(toolbar);
		toolbar.currentMode = mode;
		var nbButtons = 0;
	
		var options = 0;
		switch(mode)
		{
			case M_TREE_VIEW:
				options |= (toolbar.baseOptions & O_PERSO_VIEW);
				options |= (toolbar.baseOptions & O_SAVE);
				options |= (toolbar.baseOptions & O_SAVE_QUIT);
				options |= (toolbar.baseOptions & O_DELETE);
				options |= (toolbar.baseOptions & O_LOGICAL_DEL);
				options |= (toolbar.baseOptions & O_DUPLICATE);
				options |= (toolbar.baseOptions & O_JOURNAL);
				options |= (toolbar.baseOptions & O_ACTION);
				options |= (toolbar.baseOptions & O_REFRESH);
				options |= (toolbar.baseOptions & O_TASK);
				options |= (toolbar.baseOptions & O_LINEAGE);
				
			break;
			case M_PERSO_VIEW:
				options |= O_TREE_VIEW;
				options |= (toolbar.baseOptions & O_PRINT);
				options |= (toolbar.baseOptions & O_SMARTVIEW_SWITCH);
				options |= (toolbar.baseOptions & O_DUPLICATE);
				options |= (toolbar.baseOptions & O_JOURNAL);
				options |= (toolbar.baseOptions & O_ACTION);
				options |= (toolbar.baseOptions & O_REFRESH);
				//options |= (toolbar.baseOptions & O_DELETE);
			break;
		}
		
	
		// tree view
		if ( (options&O_TREE_VIEW)==O_TREE_VIEW )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton(new Ext.Toolbar.Button({text: BUTTON_TREE_VIEW[language], handler:toolbar.displayTreeHandler }));
			nbButtons++;
		}
		
		// perso view
		if ( (options&O_PERSO_VIEW)==O_PERSO_VIEW )
		{
		
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton({text: BUTTON_PERSONALIZED_VIEW[language],  handler:toolbar.displaySmartViewHandler });
			nbButtons++;
		}
		// smart view
        if ( (options&O_SMARTVIEW_SWITCH)==O_SMARTVIEW_SWITCH )
        {
        
            if (nbButtons>0)
            {
                toolbar.addSeparator();
                nbButtons++;
            }
            
            // add a combobox to the toolbar           
            var smartViewStore = new Ext.data.Store({
                 proxy: new Ext.data.DWRProxy(ItemsBrowserInterface.getSmartViewList , true),
                 reader:new Ext.data.ListRangeReader({
                      totalProperty: 'totalSize',
                      id: 'value',
                      root: 'data'
                  }, Ext.data.Record.create([
                            {name: 'value',mapping:'value',type:'string'},
                            {name: 'text',mapping:'text',type:'string'}
                           ])
                   )
                });
                
            smartViewStore.on('beforeload', 
                    function(button, event) {
                      var dataObjectValue=toolbar.dataObject;
                      var input=dataObjectValue+"&"+language;
                      Ext.apply(smartViewStore.baseParams,{
                                  start: 0, 
                                  limit: 0,
                                  regex: input
                                });
                                
                    }
             );
               
            var combo = new Ext.form.ComboBox({
                id :  "smartViewCombo"+toolbar.treeIndex,
                name : "smartViewCombo"+toolbar.treeIndex,
                editable : false,
                store: smartViewStore,
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                triggerAction: 'all',
                forceSelection:true,
                resizable:true,
                listeners : {                     
                               'select' : function(combo,record,index) {
                                
                                                      var optName=null;
                                                      if(record.data.value.indexOf('#')!=-1)optName=record.data.text;
                                                      var frameUrl='/itemsbrowser/secure/SmartViewServlet?ids='+toolbar.ids+'&concept='+toolbar.dataObject+'&language='+language;
                                                      if(optName!=null)frameUrl+=('&optname='+optName);
                                                      document.getElementById("smartViewFrame"+toolbar.treeIndex).src=frameUrl;
                                                      
                                          }
                             }

            });
            
            toolbar.addField(combo);
            nbButtons++;
        }
	    // print
		if ( (options&O_PRINT)==O_PRINT )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton({text: BUTTON_PRINT[language], className: 'tb-button tb-button-nude', handler: toolbar.printHandler});
			nbButtons++;
		}

		
		// save
		if ( (options&O_SAVE)==O_SAVE )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {id:'saveBTN', disabled : isReadOnlyinItem, iconCls:'item_bt_save',tooltip:SAVE_TOOLTIP[language],text: BUTTON_SAVE[language], className: 'tb-button tb-button-nude', handler: toolbar.saveItemHandler});
			nbButtons++;
		}
	
		// save and quit
		if ( (options&O_SAVE_QUIT)==O_SAVE_QUIT )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {id:'saveAndQBTN', disabled : isReadOnlyinItem, iconCls:'item_bt_saveAndQ',tooltip:SAVEANDCLOSE_TOOLTIP[language],text: BUTTON_SAVE_AND_QUIT[language], className: 'tb-button tb-button-nude', handler: toolbar.saveItemAndQuitHandler});
			nbButtons++;
		}
		
		var deleteBTN;
		var logicalDelBTN;
		// delete
		if ( (options&O_DELETE)==O_DELETE )
		{
			if (nbButtons>0)
			{
				//toolbar.addSeparator();
				nbButtons++;
			}
	
			deleteBTN = {text: BUTTON_DELETE[language], disabled:_rootNode.denyPhysicalDeletable, iconCls : 'item_bt_delete', handler: toolbar.deleteItemHandler};
			nbButtons++;
		}
		
		// logical delete
		if ( (options&O_LOGICAL_DEL)==O_LOGICAL_DEL )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			logicalDelBTN = {text: BUTTON_LOGICAL_DEL[language], disabled:_rootNode.denyLogicalDeletable, iconCls : 'item_bt_sendTrash', handler: toolbar.logicalDelItemHandler};
			nbButtons++;
		}
		
		if(( (options&O_DELETE)==O_DELETE ) || ( (options&O_LOGICAL_DEL)==O_LOGICAL_DEL )) {
			toolbar.add({
				xtype : 'tbsplit',
				text: BUTTON_DELETE[language],
				iconCls : 'item_bt_delete',
				//disabled : !_rootNode.logicalDeletable,
				menu : {
					items : [logicalDelBTN, deleteBTN]
				}
				//handler : toolbar.logicalDelItemHandler
			});
		}
		
		// duplicate
		if ( (options&O_DUPLICATE)==O_DUPLICATE )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:DUPLICATE_TOOLTIP[language],disabled:_rootNode.denyCreatable, iconCls : 'item_bt_duplicate', text: BUTTON_DUPLICATE[language], className: 'tb-button tb-button-nude', handler: toolbar.duplicateItemHandler});
			nbButtons++;
		}
		
		// journal
		if ( (options&O_JOURNAL)==O_JOURNAL && amalto.updatereport)
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:JOURNAL_TOOLTIP[language], iconCls : 'item_bt_journal', text: BUTTON_JOURNAL[language], className: 'tb-button tb-button-nude', handler: toolbar.journalItemHandler});
			nbButtons++;
		}
		
		// refresh
		if((options&O_REFRESH)==O_REFRESH)
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:REFRESH_TOOLTIP[language],iconCls: 'item_bt_refresh', className: 'tb-button tb-button-nude', handler: toolbar.refreshItemHandler});
			nbButtons++;
		}
		
		// Lineage button
		if ((options&O_LINEAGE) == O_LINEAGE) {
			if (nbButtons > 0){
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:LINEAGE_TOOLTIP[language], iconCls : 'item_bt_task', text: LINEAGE_BUTTON[language], className: 'tb-button tb-button-nude', handler: toolbar.lineageItemHandler});
			nbButtons++;
		}
		
		// task
		if ( (options&O_TASK)==O_TASK) {
			if (nbButtons>0){
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:TASK_TOOLTIP[language], iconCls : 'item_bt_task', text: BUTTON_TASK[language], className: 'tb-button tb-button-nude', handler: toolbar.taskItemHandler});
			nbButtons++;
		}
		
		// action
        if ( (options&O_ACTION)==O_ACTION )
        {
            if (nbButtons>0)
            {
                toolbar.addSeparator();
                nbButtons++;
            }
            
            // add a combobox to the toolbar           
            var processStore = new Ext.data.Store({
                 proxy: new Ext.data.DWRProxy(ItemsBrowserInterface.getRunnableProcessList , true),
                 reader:new Ext.data.ListRangeReader({
                      totalProperty: 'totalSize',
                      id: 'value',
                      root: 'data'
                  }, Ext.data.Record.create([
                            {name: 'value',mapping:'value',type:'string'},
                            {name: 'text',mapping:'text',type:'string'}
                           ])
                   )
                });
                
            processStore.on('beforeload', 
                    function(button, event) {
                      var dataObjectValue=toolbar.dataObject;
                      var input=dataObjectValue+"&"+language;
                      Ext.apply(processStore.baseParams,{
                                  start: 0, 
                                  limit: 0,
                                  regex: input
                                });
                                
                    }
             );
               
            var combo = new Ext.form.ComboBox({
            	id :  "processCombo"+toolbar.treeIndex,
            	name : "processCombo"+toolbar.treeIndex,
            	editable : false,
                store: processStore,
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                triggerAction: 'all',
                forceSelection:true,
                resizable:true 
            });
            
            toolbar.addField(combo);
            toolbar.addButton( {tooltip:ACTION_TOOLTIP[language], iconCls : 'item_bt_process', className: 'tb-button tb-button-nude', handler: toolbar.processItemHandler});
            nbButtons++;
        }
	}
	
	
	/*********************************************************
	 *            ITEM DETAIL
	 *********************************************************/
	
	//var itemTree;
	var itemTreeFK;
	
	function displayItemDetails(itemPK2, dataObject, isBreadCrumb,parentLink){
	    displayItemDetails2(itemPK2, dataObject, false ,displayItems,isBreadCrumb,parentLink);
	}
	
	function displayItemDetails4Duplicate(itemPK2, dataObject){
		
	    displayItemDetails2(itemPK2, dataObject, true, displayItems);
	    
	}
	
	function renderFormWindow(itemPK2, dataObject, isDuplicate, refreshCB, formWindow, isDetail) {
		
		DWREngine.setAsync(false); 
		ItemsBrowserInterface.getRootNode(dataObject,language, function(rootNode){
			_rootNode=rootNode;
		});
        ItemsBrowserInterface.prepareSessionForItemDetails(dataObject,language,function(status){});
		DWREngine.setAsync(true);
		loadResource("/itemsbrowser/secure/js/ItemNode.js", "amalto.itemsbrowser.ItemNode" );
		loadResource("/itemsbrowser/secure/js/BreadCrumb.js", "amalto.itemsbrowser.BreadCrumb" );
		//alert("display items "+DWRUtil.toDescriptiveString(itemPK2,2)+" "+ dataObject);
		amalto.core.working();
		itemNodes = [];
		treeCount++;	
		var treeIndex = treeCount;
		var tabPanel = amalto.core.getTabPanel();
		var contentPanel=tabPanel.getItem('itemDetailsdiv'+treeIndex);
		var ids = "";
		
		
		if(itemPK2==null) {
			newItem[treeIndex] = true;
		}
		else {
			newItem[treeIndex] = false;
			for(var i=0; i<itemPK2.length; i++) {
				ids += (ids==""?"":"@"); 
				ids += itemPK2[i];			
			}
		}
		
		//add for duplicate case
		if(isDuplicate){
		   newItem[treeIndex] = true;
		}
	
	
		lastUpdatedInputFlag[treeIndex] = null;
		updateFlag[treeIndex] = 0;
	
		keys[treeIndex] = [];
		map[treeIndex] = [];
		if(dataObject==null) dataObject=_dataObject;
		ItemsBrowserInterface.getRootNode2(dataObject,itemPK2,treeIndex,language, function(rootNode){
			
			if(contentPanel == undefined){
					
				var smartView = '';
				if(newItem[treeIndex]==false ) {
					smartView = '<iframe width="100%" height="100%" frameborder=0 scrolling=auto src="/itemsbrowser/secure/SmartViewServlet?ids='+ids+'&concept='+dataObject+'&language='+language+'">';
				}	
	
				var breadCrumbHtml = '<div id="breadCrumbHtml"></div>';
				
				//update the div structure
				var errorHtml = '<div id="errorDesc'  + treeIndex + '" style="display:none;color:red;font-weight:bold;font-size:11px;padding-left:25px;padding-top:5px"><img src="img/genericUI/errorstate.gif" style="vertical-align:middle"/><span style="padding-left:10px;text-align:center;vertical-align:middle;">'
                         + errorDesc[language] + '</span></div>' +
                    '<div id="errorDetail' + treeIndex + '" style="display:none;color:red;font-weight:bold;font-size:11px;padding-left:65px"></div></br>';
				
				var html =
					'<div>' +
					'		<span id="itemDetails'+treeIndex+'" class="itemTree"></span>' +
					'		<span id="smartView'+treeIndex+'" style="display=none;">'+smartView+'</span>' +
					'</div>' ;
										

				var tbDetail = new Ext.Toolbar({id:treeIndex+'_item-tb'});
				
				tbDetail.baseOptions = 0;
				tbDetail.ids = ids;
				tbDetail.dataObject = dataObject;
				tbDetail.treeIndex = treeIndex;
					
				var myTitle = "";
				if(_dataObject!=null) myTitle=_dataObject;
		
				if(dataObject!=null) myTitle = dataObject;	

		        if(rootNode.primaryKeyInfo!=null&&rootNode.primaryKeyInfo.length>0){
		        	if(rootNode.name!=null)	myTitle += " "+rootNode.name;
		        }else{
		        	if(rootNode.name!=null)	myTitle = rootNode.name;
		        	if(itemPK2!=null) {
						for(var i=0; i<itemPK2.length; i++) {
							myTitle +=" "+itemPK2[i];
						}	
					}
		        }	
				
				//get item readonly
				var itempk=itemPK2;
				if(isDuplicate){
					itempk=null;
				}
				
				ItemsBrowserInterface.isReadOnlyinItem(dataObject,itempk, function(result){
					isReadOnlyinItem=result;
				});
    			if(dataObject==null) dataObject=_dataObject;	
    			
    			var addOptions;
    			
    			var itemTree= new YAHOO.widget.TreeView("itemDetails"+treeIndex);
    			itemTreeList[treeIndex] = itemTree;    			
    			//add for duplicate case
				if(isDuplicate){
					
				   var fnLoadData = function(oNode, fnCallback) {
    				ItemsBrowserInterface.getChildrenWithKeyMask(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,true,Ext.get(oNode.index+"TypeSelector")==null?null:DWRUtil.getValue(oNode.index+"TypeSelector"),function(result){
    					if(result==null) {
    						fnCallback();
    						return;
    					}
    	
    					for(var i=0; i<result.length; i++) {
    						var readOnly = (result[i].readOnly==true || (result[i].key==true && newItem[treeIndex]==false));
    						if (!readOnly)
    						{
    							if (!(tbDetail.baseOptions&O_SAVE))
    							{
									//case new
									tbDetail.baseOptions |= O_SAVE|O_SAVE_QUIT;
									initToolBar(tbDetail, tbDetail.currentMode);
    							}
    						}    						
    						var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,
    									itemTree.getNodeByIndex(oNode.index),false,true,isReadOnlyinItem);
    						//new Ext.form.TextField({applyTo:result[i].nodeId+'Value'});
    						if(result[i].type=="simple") tmp.setDynamicLoad();
    						else tmp.setDynamicLoad(fnLoadData, 1);
    						itemNodes[i] = tmp;
    						var length = map[treeIndex].length;
    						map[treeIndex][length + i] = tmp;
    					}
    					fnCallback();
        			});
        		  };
        		  
				}else{
					
					var fnLoadData = function(oNode, fnCallback) {
    				ItemsBrowserInterface.getChildren(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,Ext.get(oNode.index+"TypeSelector")==null?null:DWRUtil.getValue(oNode.index+"TypeSelector"),function(result){
    					if(result==null) {
    						fnCallback();
    						return;
    					}
    	
    					for(var i=0; i<result.length; i++) {
    						var readOnly = (result[i].readOnly==true || (result[i].key==true && newItem[treeIndex]==false));
    						if (!readOnly)
    						{
    							//var tbDetail = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
    							if (!(tbDetail.baseOptions&O_SAVE))
    							{
									//case new
									tbDetail.baseOptions |= O_SAVE|O_SAVE_QUIT; 
									initToolBar(tbDetail, tbDetail.currentMode);
    							}
    						}
    	
    						var parentLink = [];
    						parentLink["title"] = myTitle;
    						parentLink["ids"] = ids;
    						parentLink["conceptName"] = dataObject;
    						
    						var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,
    									itemTree.getNodeByIndex(oNode.index),false,true,isReadOnlyinItem, parentLink);
    						//new Ext.form.TextField({applyTo:result[i].nodeId+'Value'});
    						if(result[i].type=="simple") tmp.setDynamicLoad();
    						else tmp.setDynamicLoad(fnLoadData, 1);
    						itemNodes[i] = tmp;
    						var length = map[treeIndex].length;
    						map[treeIndex][length + i] = tmp;
    					}
    					fnCallback();
        			});
        		  };
				
				}
    				
    			
    			
        		var root = itemTree.getRoot();	
        		var nameTmp = dataObject;
        		var descInfo = "";
        		var selectedProcess=null;
        		if(rootNode.name!=null) nameTmp = '<div style="width:180;float:left;font-size:22px;font-weight:bold">'+rootNode.name+'</div>';
        		if(rootNode.description!=null&&rootNode.description!="")descInfo=' <img src="img/genericUI/information_icon.gif" ext:qtitle="Description" ext:qtip="'+rootNode.description+'"/>';
        		nameTmp=nameTmp+descInfo;
        		var node1 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
        		
        	
        		tbDetail.deleteItemHandler = function() {
        			//@temp yguo, deleteItem and close 
        			var tmp = "";
        			var itemPK = ids.split('@');
        			for(var i=0; i<itemPK.length; i++) {
        				tmp += " "+itemPK[i];
        			}
        			Ext.MessageBox.confirm("confirm",MSG_CONFIRM_DELETE_ITEM[language]+ " ?",function re(en){
        			if(en=="yes"){
        				ItemsBrowserInterface.getUriArray(dataObject, itemPK, function(picUriArray){
        					var uriArray = [];
        					uriArray=picUriArray;
        					for (var index = 0; index < uriArray.length; index++) {
        						var picUri=uriArray[index];
        					if(picUri!=""){
        						 var pos=picUri.indexOf('?');
        		 				 var uri=picUri.substring("/imageserver/".length,pos); 
        					     Ext.Ajax.request({  
        					       	 url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
        					         method: 'post',  
        					         callback: function(options, success, response) {  
        					         }  
        					     });    
        					  }
        				   }
        				});
        				ItemsBrowserInterface.deleteItem(dataObject, itemPK,treeIndex, function(result){
        					if(result.lastIndexOf("ERROR")>-1){
        						var err1=result.substring(7);
        						Ext.MessageBox.show({msg:err1, buttons:{"ok":"CANCEL"}, icon:Ext.MessageBox.ERROR});
        						return;
        					}
        					else if(result.lastIndexOf)
        					
        					if(result)Ext.MessageBox.show({msg:result, buttons:{"ok":"OK"}, icon:Ext.MessageBox.INFO});		
        					window.callGxt();
        				});		
        			}});
        			
        			//@temp yguo, close the window
        		};
        		
        		tbDetail.logicalDelItemHandler = function() {
        			var tmp = "";
        			var itemPK = ids.split('@');
        			
        			Ext.Msg.show({
        			   title: MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM[language],
        			   msg:  MSG_CONFIRM_LOGICAL_DELETE_ITEM[language],
        			   buttons: Ext.Msg.OKCANCEL,
        			   fn: doLogicalDel,
        			   prompt:true,
        			   value: '/',
        			   width:300
        			});
        			
        			function doLogicalDel(btn, path){
        		       if (btn == 'cancel') {
        					return;
        				}
        		       
        		       	var tmp = "";
        				var itemPK = ids.split('@');
        				for(var i=0; i<itemPK.length; i++) {
        					tmp += " "+itemPK[i];
        				}
        				ItemsBrowserInterface.logicalDeleteItem(dataObject, itemPK, path, treeIndex, function(result){
        					if(result.lastIndexOf("ERROR")>-1){
        						var err1=result.substring(7);
        						//Ext.MessageBox.alert("ERROR", err1);
        						$('errorDetail' + treeIndex).style.display = "block";
        						$('errorDetail' + treeIndex).innerHTML ="<br/>"+err1+"<br/>";					
        						return;
        					}			

        					amalto.core.ready(result);
        					if(result){
        						Ext.MessageBox.alert('Status', result, function() {
        							window.callGxt();
        						});				
        					}
        				});
        			};
        			
        			//@temp yguo, deleteItem and close 
        		};
        		
        		tbDetail.duplicateItemHandler = function() {
        			duplicateItem(ids, dataObject);
        		};
        		
        		tbDetail.journalItemHandler = function() {
        			journalItem(ids, dataObject);
        		};
        		
        		if(rootNode.taskId != "null" && "" != rootNode.taskId && rootNode.taskId != null) {
    				tbDetail.baseOptions |= O_TASK;
    			};
        		
    			DWREngine.setAsync(false);
    			var lineagEntities; 
    			ItemsBrowserInterface.getLineageEntity(dataObject, function(results) {
    				lineagEntities = results;
    			});
    			
    			if(lineagEntities != null && lineagEntities.length != 0 && lineagEntities != "") {
    				tbDetail.baseOptions |= O_LINEAGE;
    			}
    			DWREngine.setAsync(true);
    			
    			tbDetail.lineageItemHandler = function() {
        			lineageItem(lineagEntities);
        		};
        		
        		tbDetail.taskItemHandler = function() {
        			taskItem(rootNode.taskId);
        		};
        		
        		tbDetail.processItemHandler = function() {
                    
                    selectedProcess = Ext.getCmp('processCombo'+tbDetail.treeIndex).value;
                    if(selectedProcess==null||selectedProcess==''){
                       Ext.MessageBox.alert("Warnning", "Please select a process first! ");
                       return;
                    }
                    
                    DWREngine.setAsync(false);
                    var timeoutConfig;
                    ItemsBrowserInterface.getProperty('runnable.process.timeout', function(result) {
                    	timeoutConfig = result;
                    });
                    DWREngine.setAsync(true);
                    //TODO:add a confirm here( do save)
                    
                    Ext.MessageBox.show({
                           msg: 'Processing, please wait...',
                           progressText: 'Processing...',
                           width:300,
                           wait:true,
                           waitConfig: {interval:200}
                        });
                    ItemsBrowserInterface.processItem(tbDetail.dataObject, tbDetail.ids, tbDetail.treeIndex, selectedProcess, {
                    	callback:function(result){
                           Ext.MessageBox.hide();
                           if(result.indexOf("Ok") >= 0){
                               Ext.MessageBox.alert('Status', "Process done! ");
                               //FIXME mock refresh
                               itemTree.removeNode(itemTree.getRoot().children[0]);
                               node1 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
                               var viewName =  getSelectedViewName(dataObject);
                               ItemsBrowserInterface.setTree(dataObject,viewName, itemPK2, node1.index, false, treeIndex, false, function(result){                             
                                    node1.setDynamicLoad(fnLoadData,1);
                                    node1.expand();
                                    itemTree.draw();
                               });
    
                               //amalto.core.getTabPanel().remove('itemDetailsdiv'+ treeIndex);
                               //displayItemDetails(itemPK2,dataObject);
                                                          
                               var itempanel = amalto.core.getTabPanel().activeTab;
                               if (itempanel) {
                                    //It is already up to date
                                    itempanel.isdirty = false;
                               }
                               
                               displayItems.call(); 
                                
                               if(result == "Ok"){
                                   var url = "/itemsbrowser/secure/RefreshProperties?output_report=true&process=" + selectedProcess + "&id=" + tbDetail.treeIndex;    
                                   var panelID = "mimepanel" + tbDetail.treeIndex ;
                                   
                                   var mimepanel = new Ext.Panel( {   
                                   id : panelID,   
                                   title : "output_report of " + selectedProcess + " " + tbDetail.ids,
                                   closable:true,
                                   fitToFrame: true,                   
                                   html: '<iframe id="frame1" src=' + url+ ' frameborder="0" width="100%" height="100%"></iframe>'  
                                   });
                                   
                                   var tabPanel = amalto.core.getTabPanel();
                                   var contentPanel=tabPanel.getItem(panelID);
                                   if (contentPanel == undefined){
                                   	tabPanel.add(mimepanel);
                                   }
                                   tabPanel.show();
                                   tabPanel.doLayout();
                                   amalto.core.doLayout(); 
                               }
                           }else{
                           Ext.MessageBox.alert('Status', "Process failed! ");  
                           }
                        },
                        timeout:timeoutConfig,
                        errorHandler:function(errorString, exception) {  
                              alert('Error:'+ errorString);
                              Ext.MessageBox.hide();
                        }  
                    });
                };

        		ItemsBrowserInterface.checkSmartViewExists(dataObject, language, function(result){
        			
        			var mode = M_TREE_VIEW;
        			//var tb = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
        			var tb = tbDetail;
        			if(result==true && newItem[treeIndex]==false) {
        				
        				mode = M_PERSO_VIEW;
        				
        				tbDetail.displayTreeHandler = function(){	
        					getTree(ids,''+dataObject,treeIndex);
        				};				
        				
        				tbDetail.printHandler = function(){			
        					printSmartView(ids, dataObject, treeIndex);
        				};
        	
        				tb.baseOptions |= O_PRINT | O_PERSO_VIEW| O_SMARTVIEW_SWITCH;
        	
        				$('smartView'+treeIndex).style.display='block';
        				$('itemDetails'+treeIndex).style.display='none';
        			}
        	
        			initToolBar(tb, mode);
        			
        		});
    	       
    			tbDetail.saveItemHandler = function(){			
    				//@temp yguo, 
    				saveForGXT(ids,dataObject,treeIndex,function(){
    					
    				});
    			};			
    			tbDetail.refreshItemHandler = function() {
    				
                    var ids1;
                    if(ids.length==0)
                        ids1 = itemPK2;
                    else
                        ids1 = ids;
                    if(!ids1 || keys[treeIndex].length>0){
                        ids1=keys[treeIndex];
                        //reset ids
                        ids=ids1.join('.');
                    }					
					if(ids1 && ids1.length>0){
    				ItemsBrowserInterface.reloadItem(dataObject, ids1, treeIndex, function(){    					
                            reloadNode(node1.index,treeIndex);
                    });
                    }
    			};
    			
    			tbDetail.saveItemAndQuitHandler = function(){	
    				saveForGXT(ids,dataObject,treeIndex, function() {
    					//@TEMP YGUO, CLOSE THE WINDOW
    					window.callGxt();
    				});
    				
    			};
    		
    			//case edit and no editable
    			if(rootNode.readOnly==false && newItem[treeIndex]==false) {
    				tbDetail.baseOptions |= O_DELETE|O_LOGICAL_DEL|O_DUPLICATE|O_JOURNAL|O_ACTION|O_REFRESH;	
    				
    			}
    			//add by ymli; fix the bug:0012534
    			else
    	           tbDetail.baseOptions |=O_ACTION;
    			
                 var viewName =  getSelectedViewName(dataObject);
    			//add for duplicate case
				if(isDuplicate){
					ItemsBrowserInterface.setTree(dataObject, viewName,itemPK2, node1.index, false, treeIndex, false, function(result){
    				node1.setDynamicLoad(fnLoadData,1);
    				node1.expand();
    				itemTree.draw();
    				ItemsBrowserInterface.updateKeyNodesToEmptyInItemDocument(treeIndex);
    			    });
				}else{
					ItemsBrowserInterface.setTree(dataObject,viewName, itemPK2, node1.index, false, treeIndex, false, function(result){
    				node1.setDynamicLoad(fnLoadData,1);
    				node1.expand();
    				itemTree.draw();
    			    });
				}
    			
				var errorContentPanel = new Ext.Panel({
				    id:'errorDetailsdiv'+treeIndex, 
                    headerAsText:false,
                    autoScroll:false,
                    html:errorHtml,
                    border:false,
                    closable:false
				});
				
				var treeDetailPanel = new Ext.Panel({
                    id:'treeDetailsdiv'+treeIndex, 
                     headerAsText:false,
                    style:"overflow:no;",
                    autoScroll:true,
                    html:html,
                    border:false,
                    closable:false
                });
				
                contentPanel = new Ext.Panel({
                    id:'itemDetailsdiv'+treeIndex, 
                    title: myTitle, 
                    tbar: tbDetail,
                    header:false,
                    style:"height:99%;",
                    closable:true,
                    items:[errorContentPanel,treeDetailPanel],
                    bbar : new Ext.Toolbar([{
                        text : EDIT_ITEM_TOOLTIP[language],
                        xtype : "tbtext"
                    }])                 
                });
			}
			formWindow.innerHTML = "";
			contentPanel.render(formWindow);
			
			//record the item id
			contentPanel.itemid=itemPK2+"."+dataObject;
			
		    amalto.core.doLayout();	    
		});
		
		amalto.core.ready();
	}
	
	function saveForGXT(itemPK,dataObject,treeIndex, callbackOnSuccess) {
		ItemsBrowserInterface.saveItem(itemPK,dataObject, newItem[treeIndex], treeIndex,{
			callback:function(result){ 
				amalto.core.ready(result);
				if(result.status == 2) { //unchanged
					amalto.core.ready(ALERT_NO_CHANGE[language]);
				}
				else if(result.status == 1) { //failure
				    if(result.description!=null){
				    	if(result.description.indexOf('ERROR_3:')==0){
					      // add for before saving transformer check
                          amalto.core.ready(result.substring(8));
                          Ext.MessageBox.alert("Status",result.description.substring(8));
                        }else if(result.description.indexOf('Unable to save item')==0){
                	        amalto.core.ready();
    				        showExceptionMsg(result.description, null, treeIndex);
                        }else if(result.description.indexOf('Save item')==0){
                	        amalto.core.ready();
    				        showExceptionMsg(result.description, null, treeIndex);
                        }
				    }
				}
				else if(result.status == 0) { 
					//@temp yguo check 
					$('errorDesc'+ treeIndex).style.display = "block";
					$('errorDesc'+ treeIndex).style.display = "none";
				}
				
				if(result.description==null || result.description==""){
					return;
				}else{
					if(result.status == 1) {
						var err1=result.description;
					    if(err1.lastIndexOf("ERROR")>-1)
						    var err1=result.description.substring(8);
						if(err1==null || err1==""){
						    return;
						}else{
						    err1 = dropOnErrorMsg(err1);
						    Ext.MessageBox.show({
						        msg:err1,
						        buttons:{"ok":"CANCEL"},
						        icon:Ext.MessageBox.ERROR
						     });
						    return;
						}
					}else{
						var msg2show=result.description;
						Ext.MessageBox.show({
						    msg:msg2show,
						    buttons:{"ok":"OK"},
						    icon:Ext.MessageBox.INFO
						 });
						if(callbackOnSuccess && result.status != 1)callbackOnSuccess();
						return;
					}
				}
				amalto.core.ready();
			}
        });
	}
	
	function displayItemDetails4Reference(itemPK2, dataObject, refreshCB){
		
		DWREngine.setAsync(false); 
		ItemsBrowserInterface.getRootNode(dataObject,language, function(rootNode){
			_rootNode=rootNode;
		});
        ItemsBrowserInterface.prepareSessionForItemDetails(dataObject,language,function(status){});
		DWREngine.setAsync(true);
		
        
	    displayItemDetails2(itemPK2, dataObject, false, refreshCB);
	    
	}
	
	function displayItemDetails2(itemPK2, dataObject, isDuplicate, refreshCB, isBreadCrumb,parentLink){
		loadResource("/itemsbrowser/secure/js/ItemNode.js", "amalto.itemsbrowser.ItemNode" );
		//alert("display items "+DWRUtil.toDescriptiveString(itemPK2,2)+" "+ dataObject);
		amalto.core.working();
		itemNodes = [];
		treeCount++;	
		var treeIndex = treeCount;
		var tabPanel = amalto.core.getTabPanel();
		var contentPanel=tabPanel.getItem('itemDetailsdiv'+treeIndex);
		//see  	 0013478 prevent 2 tabs from being opened on the same record. 
		var itemContentPanel;
		tabPanel.items.each(function(item){
			if(item.itemid==itemPK2+"."+dataObject){
				itemContentPanel=item;
				return;
			}
		});
		if(itemContentPanel && isDuplicate==false) {
			if(itemContentPanel.itemid==itemPK2+"."+dataObject){
				itemContentPanel.show();
				return;
			}
		};
		//end
		
		//see 0019470 check user's denyCreatable
		if(itemPK2==null){
			var isCreatable=true;
			DWREngine.setAsync(false);
			ItemsBrowserInterface.isEntityCreatable(dataObject,function(result){
				isCreatable=result;
			});
			DWREngine.setAsync(true);
			if(!isCreatable) return;
		}
		var ids = "";
		
		
		if(itemPK2==null) {
			newItem[treeIndex] = true;
		}
		else {
			newItem[treeIndex] = false;
			for(var i=0; i<itemPK2.length; i++) {
				ids += (ids==""?"":"@"); 
				ids += itemPK2[i];			
			}
		}
		
		//add for duplicate case
		if(isDuplicate){
		   newItem[treeIndex] = true;
		}
	
	
		lastUpdatedInputFlag[treeIndex] = null;
		updateFlag[treeIndex] = 0;
	
		keys[treeIndex] = [];
		map[treeIndex] = [];
		if(dataObject==null) dataObject=_dataObject;
		ItemsBrowserInterface.getRootNode2(dataObject,itemPK2,treeIndex,language, function(rootNode){
			
			if(contentPanel == undefined){
					
				var smartView = '';
				if(newItem[treeIndex]==false ) {
					smartView = '<iframe id="smartViewFrame'+treeIndex+'" width="100%" height="100%" frameborder=0 scrolling=auto src="/itemsbrowser/secure/SmartViewServlet?ids='+ids+'&concept='+dataObject+'&language='+language+'">';
				}	
	
				var breadCrumbHtml = '<div id="breadCrumbHtml'  + treeIndex + '"></div>';
				
				//update the div structure
				var errorHtml = '<div id="errorDesc'  + treeIndex + '" style="display:none;color:red;font-weight:bold;font-size:11px;padding-left:25px;padding-top:5px"><img src="img/genericUI/errorstate.gif" style="vertical-align:middle"/><span style="padding-left:10px;text-align:center;vertical-align:middle;">'
                         + errorDesc[language] + '</span></div>' +
                    '<div id="errorDetail' + treeIndex + '" style="display:none;color:red;font-weight:bold;font-size:11px;padding-left:65px"></div></br>';
				
				var html =
					'<div>' +
					'		<span id="itemDetails'+treeIndex+'" class="itemTree"></span>' +
					'		<span id="smartView'+treeIndex+'" style="display=none;">'+smartView+'</span>' +
					'</div>' ;
										

				var tbDetail = new Ext.Toolbar({id:treeIndex+'_item-tb'});
				
				tbDetail.baseOptions = 0;
				tbDetail.ids = ids;
				tbDetail.dataObject = dataObject;
				tbDetail.treeIndex = treeIndex;
					
				var myTitle = "";
				if(_dataObject!=null) myTitle=_dataObject;
		
				if(dataObject!=null) myTitle = dataObject;	

		        if(rootNode.primaryKeyInfo!=null&&rootNode.primaryKeyInfo.length>0){
		        	if(rootNode.name!=null)	myTitle += " "+rootNode.name;
		        }else{
		        	if(rootNode.name!=null)	myTitle = rootNode.name;
		        	if(itemPK2!=null) {
						for(var i=0; i<itemPK2.length; i++) {
							myTitle +=" "+itemPK2[i];
						}	
					}
		        }	
				
				//get item readonly
				var itempk=itemPK2;
				if(isDuplicate){
					itempk=null;
				}
				
				/*if(conceptNameSelect =="")
				    conceptNameSelect = dataObject;*/
				ItemsBrowserInterface.isReadOnlyinItem(dataObject,itempk, function(result){
					isReadOnlyinItem=result;
				});
    			if(dataObject==null) dataObject=_dataObject;	
    			
    			var addOptions;
    			
    			var itemTree= new YAHOO.widget.TreeView("itemDetails"+treeIndex);
    			itemTreeList[treeIndex] = itemTree;    			
    			//add for duplicate case
				if(isDuplicate){
					
				   var fnLoadData = function(oNode, fnCallback) {
    				//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
    				ItemsBrowserInterface.getChildrenWithKeyMask(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,true,Ext.get(oNode.index+"TypeSelector")==null?null:DWRUtil.getValue(oNode.index+"TypeSelector"),function(result){
    					if(result==null) {
    						fnCallback();
    						return;
    					}
    	
    					for(var i=0; i<result.length; i++) {
    						var readOnly = (result[i].readOnly==true || (result[i].key==true && newItem[treeIndex]==false));
    						if (!readOnly)
    						{
    							//var tbDetail = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
    							if (!(tbDetail.baseOptions&O_SAVE))
    							{
    								//case new
    								tbDetail.baseOptions |= O_SAVE|O_SAVE_QUIT; 
    								initToolBar(tbDetail, tbDetail.currentMode);
    							}
    						}    						
    						var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,
    									itemTree.getNodeByIndex(oNode.index),false,true,isReadOnlyinItem);
    						//new Ext.form.TextField({applyTo:result[i].nodeId+'Value'});
    						if(result[i].type=="simple") tmp.setDynamicLoad();
    						else tmp.setDynamicLoad(fnLoadData, 1);
    						itemNodes[i] = tmp;
    						var length = map[treeIndex].length;
    						map[treeIndex][length + i] = tmp;
    					}
    					fnCallback();
        			});
        		  };
        		  
				}else{
					
					var fnLoadData = function(oNode, fnCallback) {
    				//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
    				ItemsBrowserInterface.getChildren(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,Ext.get(oNode.index+"TypeSelector")==null?null:DWRUtil.getValue(oNode.index+"TypeSelector"),function(result){
    					if(result==null) {
    						fnCallback();
    						return;
    					}
    	
    					for(var i=0; i<result.length; i++) {
    						var readOnly = (result[i].readOnly==true || (result[i].key==true && newItem[treeIndex]==false));
    						if (!readOnly)
    						{
    							//var tbDetail = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
    							if (!(tbDetail.baseOptions&O_SAVE))
    							{
    								//case new
    								tbDetail.baseOptions |= O_SAVE|O_SAVE_QUIT; 
    								initToolBar(tbDetail, tbDetail.currentMode);
    							}
    						}
    						var parentLink = [];
    						parentLink["title"] = myTitle;
    						parentLink["ids"] = ids;
    						parentLink["conceptName"] = dataObject;
    						
    						var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,
    									itemTree.getNodeByIndex(oNode.index),false,true,isReadOnlyinItem,parentLink);
    						//new Ext.form.TextField({applyTo:result[i].nodeId+'Value'});
    						if(result[i].type=="simple") tmp.setDynamicLoad();
    						else tmp.setDynamicLoad(fnLoadData, 1);
    						itemNodes[i] = tmp;
    						var length = map[treeIndex].length;
    						map[treeIndex][length + i] = tmp;
    					}
    					fnCallback();
        			});
        		  };
				
				}
    				
    			
    			
        		var root = itemTree.getRoot();	
        		var nameTmp = dataObject;
        		var descInfo = "";
        		var selectedProcess=null;
        		if(rootNode.name!=null) nameTmp = '<div style="width:180;float:left;font-size:22px;font-weight:bold">'+rootNode.name+'</div>';
        		if(rootNode.description!=null&&rootNode.description!="")descInfo=' <img src="img/genericUI/information_icon.gif" ext:qtitle="Description" ext:qtip="'+rootNode.description+'"/>';
        		nameTmp=nameTmp+descInfo;
        		var node1 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
        		
        	
        		tbDetail.deleteItemHandler = function() {
        			deleteItem(ids, dataObject, treeIndex,refreshCB);
        		};
        		
        		tbDetail.logicalDelItemHandler = function() {
        			logicalDelItem(ids, dataObject, treeIndex,refreshCB);
        		};
        		
        		tbDetail.duplicateItemHandler = function() {
        			duplicateItem(ids, dataObject);
        		};
        		
        		tbDetail.journalItemHandler = function() {
        			journalItem(ids, dataObject);
        		};
        		
        		if(rootNode.taskId != "null" && "" != rootNode.taskId && rootNode.taskId != null) {
    				tbDetail.baseOptions |= O_TASK;
    			};
        		
    			DWREngine.setAsync(false);
    			var lineagEntities; 
    			ItemsBrowserInterface.getLineageEntity(dataObject, function(results) {
    				lineagEntities = results;
    			});
    			
    			if(lineagEntities != null && lineagEntities.length != 0 && lineagEntities != "") {
    				tbDetail.baseOptions |= O_LINEAGE;
    			}
    			DWREngine.setAsync(true);
    			
    			tbDetail.lineageItemHandler = function() {
        			lineageItem(lineagEntities);
        		};
        		
        		tbDetail.taskItemHandler = function() {
        			taskItem(rootNode.taskId);
        		};
        		
        		tbDetail.processItemHandler = function() {
                    
                    selectedProcess = Ext.getCmp('processCombo'+tbDetail.treeIndex).value;
                    if(selectedProcess==null||selectedProcess==''){
                       Ext.MessageBox.alert("Warnning", "Please select a process first! ");
                       return;
                    }
                    
                    DWREngine.setAsync(false);
                    var timeoutConfig;
                    ItemsBrowserInterface.getProperty('runnable.process.timeout', function(result) {
                    	timeoutConfig = result;
                    });
                    DWREngine.setAsync(true);
                    //TODO:add a confirm here( do save)
                    
                    Ext.MessageBox.show({
                           msg: 'Processing, please wait...',
                           progressText: 'Processing...',
                           width:300,
                           wait:true,
                           waitConfig: {interval:200}
                        });
                    ItemsBrowserInterface.processItem(tbDetail.dataObject, tbDetail.ids, tbDetail.treeIndex, selectedProcess, {
                    	callback:function(result){
                           Ext.MessageBox.hide();
                           if(result.indexOf("Ok") >= 0){
                               Ext.MessageBox.alert('Status', "Process done! ");
                               //FIXME mock refresh
                               itemTree.removeNode(itemTree.getRoot().children[0]);
                               node1 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
                               var viewName =  getSelectedViewName(dataObject);
                               ItemsBrowserInterface.setTree(dataObject,viewName, itemPK2, node1.index, false, treeIndex, false, function(result){                             
                                    node1.setDynamicLoad(fnLoadData,1);
                                    node1.expand();
                                    itemTree.draw();
                               });
    
                               //amalto.core.getTabPanel().remove('itemDetailsdiv'+ treeIndex);
                               //displayItemDetails(itemPK2,dataObject);
                                                          
                               var itempanel = amalto.core.getTabPanel().activeTab;
                               if (itempanel) {
                                    //It is already up to date
                                    itempanel.isdirty = false;
                               }
                               
                               displayItems.call(); 
                                
                               if(result == "Ok"){
                                   var url = "/itemsbrowser/secure/RefreshProperties?output_report=true&process=" + selectedProcess + "&id=" + tbDetail.treeIndex;    
                                   var panelID = "mimepanel" + tbDetail.treeIndex ;
                                   
                                   var mimepanel = new Ext.Panel( {   
                                   id : panelID,   
                                   title : "output_report of " + selectedProcess + " " + tbDetail.ids,
                                   closable:true,
                                   fitToFrame: true,                   
                                   html: '<iframe id="frame1" src=' + url+ ' frameborder="0" width="100%" height="100%"></iframe>'  
                                   });
                                   
                                   var tabPanel = amalto.core.getTabPanel();
                                   var contentPanel=tabPanel.getItem(panelID);
                                   if (contentPanel == undefined){
                                   	tabPanel.add(mimepanel);
                                   }
                                   tabPanel.show();
                                   tabPanel.doLayout();
                                   amalto.core.doLayout(); 
                               }
                           }else{
                           Ext.MessageBox.alert('Status', "Process failed! ");  
                           }
                        },
                        timeout:timeoutConfig,
                        errorHandler:function(errorString, exception) {  
                              alert('Error:'+ errorString);
                              Ext.MessageBox.hide();
                        }  
                    });
                };

        		ItemsBrowserInterface.checkSmartViewExists(dataObject, language, function(result){
        			
        			var mode = M_TREE_VIEW;
        			//var tb = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
        			var tb = tbDetail;
        			if(result==true && newItem[treeIndex]==false) {
        				
        				mode = M_PERSO_VIEW;
        				
        				tbDetail.displayTreeHandler = function(){	
        					getTree(ids,''+dataObject,treeIndex);
        				};				
        				
        				tbDetail.printHandler = function(){			
        					printSmartView(ids, dataObject, treeIndex);
        				};
        	
        				tb.baseOptions |= O_PRINT | O_PERSO_VIEW | O_SMARTVIEW_SWITCH;
        	
        				$('smartView'+treeIndex).style.display='block';
        				$('itemDetails'+treeIndex).style.display='none';
        			}
        	
        			initToolBar(tb, mode);
        			
        		});
    	       
    			tbDetail.saveItemHandler = function(){			
    				saveItemWithoutQuit(ids,dataObject,treeIndex,refreshCB);
    			};			
    			tbDetail.refreshItemHandler = function() {
    				
                    var ids1;
                    if(ids.length==0)
                        ids1 = itemPK2;
                    else
                        ids1 = ids;
                    if(!ids1 || keys[treeIndex].length>0){
                        ids1=keys[treeIndex];
                        //reset ids
                        ids=ids1.join('.');
                    }					
					if(ids1 && ids1.length>0){
    				ItemsBrowserInterface.reloadItem(dataObject, ids1, treeIndex, function(){    					
                            reloadNode(node1.index,treeIndex);
                    });
                    }
    				
    			    
    				/*
    			    var node2 = new YAHOO.widget.HTMLNode(nameTmp, root, false, true);
    				var rootnode = root.children[0];
    				itemTree.removeNode(rootnode);
    				var ids1;
    				if(ids.length==0)
    				    ids1 = itemPK2;
    				else
    				    ids1 = ids;
    				
    				var viewName =  getSelectedViewName(dataObject);
    				ItemsBrowserInterface.setTree(dataObject,viewName, ids1, node2.index, false, treeIndex, true, function(result){
                        node2.setDynamicLoad(fnLoadData, 1);
                        node2.expand();
                        itemTree.draw();
                        //reset the ids
                        if(result){
                        	ids=result;
                        }            		                       
                    });
    		        //displayItems.call(); 
    		         * 
    		         */
    			};
    			
    			tbDetail.saveItemAndQuitHandler = function(){			
    				saveItemAndQuit(ids,dataObject,treeIndex,refreshCB);
    			};
    		
    			//case edit and no editable
    			if(rootNode.readOnly==false && newItem[treeIndex]==false) {
    				tbDetail.baseOptions |= O_DELETE|O_LOGICAL_DEL|O_DUPLICATE|O_JOURNAL|O_ACTION|O_REFRESH;	
    				
    			}
    			//add by ymli; fix the bug:0012534
    			else
    	           tbDetail.baseOptions |=O_ACTION;
    			
                 var viewName =  getSelectedViewName(dataObject);
    			//add for duplicate case
				if(isDuplicate){
					ItemsBrowserInterface.setTree(dataObject, viewName,itemPK2, node1.index, false, treeIndex, false, function(result){
    				node1.setDynamicLoad(fnLoadData,1);
    				node1.expand();
    				itemTree.draw();
    				ItemsBrowserInterface.updateKeyNodesToEmptyInItemDocument(treeIndex);
    			    });
				}else{
					ItemsBrowserInterface.setTree(dataObject,viewName, itemPK2, node1.index, false, treeIndex, false, function(result){
    				node1.setDynamicLoad(fnLoadData,1);
    				node1.expand();
    				itemTree.draw();
    			    });
				}
    			
				var breadCrumbPL = new Ext.Panel({
				    id:'breadCrumbPL' + treeIndex, 
                    headerAsText:false,
                    autoScroll:false,
                    html:breadCrumbHtml,
                    border:false,
                    closable:false
				});
				
				var errorContentPanel = new Ext.Panel({
				    id:'errorDetailsdiv'+treeIndex, 
                    headerAsText:false,
                    //activeTab: 0,
                    //tabPosition:'bottom',
                    //layout:'border',
                    autoScroll:false,
                    html:errorHtml,
                    border:false,
                    closable:false
				});
				
				var treeDetailPanel = new Ext.Panel({
                    id:'treeDetailsdiv'+treeIndex, 
                     headerAsText:false,
                    //activeTab: 0,
                    //tabPosition:'bottom',
                    //layout:'border',
                   // height:501,
                    style:"overflow:auto;height:93%;",
                    autoScroll:true,
                    html:html,
                    border:false,
                    closable:false
                });
    		
    			/*contentPanel = new Ext.Panel({
    				id:'itemDetailsdiv'+treeIndex, 
    				title: myTitle, 
    				//activeTab: 0,
    				//tabPosition:'bottom',
    				//layout:'border',
    				autoScroll:true,
    				html:html,
    				closable:true,
    				bbar : new Ext.Toolbar([{
						text : EDIT_ITEM_TOOLTIP[language],
						xtype : "tbtext"
					}])					
    			});*/
				
				var itemsPL = [];
				
				if(isBreadCrumb) {
					itemsPL = [breadCrumbPL, errorContentPanel,treeDetailPanel];
				}
				else {
					itemsPL = [errorContentPanel,treeDetailPanel];
				}
				
                contentPanel = new Ext.Panel({
                    id:'itemDetailsdiv'+treeIndex, 
                    title: myTitle, 
                    //activeTab: 0,
                    //tabPosition:'bottom',
                    //layout:'border',
                    //height:500,
                    tbar: tbDetail,
                    closable:true,
                    items:itemsPL,
                    bbar : new Ext.Toolbar([{
                        text : EDIT_ITEM_TOOLTIP[language],
                        xtype : "tbtext"
                    }])                 
                });
			}
			tabPanel.add(contentPanel); 

			//record the item id
			contentPanel.itemid=itemPK2+"."+dataObject;
			
			contentPanel.show();
			contentPanel.doLayout();
			
			if(isBreadCrumb) {
				var parentTreeIndex = parentLink["treeIndex"];
				var parentTabPL = amalto.core.getTabPanel().getItem('itemDetailsdiv'+parentTreeIndex);
				var currentTabPL = amalto.core.getTabPanel().getItem('itemDetailsdiv'+treeIndex);
				var currentPL = currentTabPL.getComponent("breadCrumbPL" + treeIndex);

				if(parentTabPL != undefined) {
					var parentPL = parentTabPL.getComponent("breadCrumbPL" + parentTreeIndex);
					
					if(parentPL != undefined && parentPL.items != undefined) {
						var parentNav = parentPL.getComponent('breadCrumbNav' + parentTreeIndex);
						var newNav = new Ext.Toolbar({id:'breadCrumbNav' + treeIndex});
						currentPL.add(newNav);
						currentPL.show();
						currentPL.doLayout();
	
						for(var index = 0; index < parentNav.items.getCount(); index++) {
							var item = parentNav.items.get(index);
							if(item.getXType() == 'tbbutton') {
								var text = item.getText();
								newNav.addButton(new Ext.Toolbar.Button({text: text, handler:item.handler}));
							}
						}
						
						newNav.show();
						
						newNav.addText(">>");
						newNav.addButton(new Ext.Toolbar.Button({text: parentLink["title"], handler:function() {
							displayItemDetails(parentLink["ids"], parentLink["conceptName"]);
						}}));
					}
					else {
						breadCrumbNavigator = new Ext.Toolbar({id:'breadCrumbNav' + treeIndex});
						currentPL.add(breadCrumbNavigator);
						currentPL.show();
						currentPL.doLayout();
						breadCrumbNavigator.addButton(new Ext.Toolbar.Button({text: parentLink["title"], handler:function() {
							displayItemDetails(parentLink["ids"], parentLink["conceptName"]);
						}}));
					}
				}
				else if(currentPL != undefined) {
					breadCrumbNavigator = new Ext.Toolbar({id:'breadCrumbNav' + treeIndex});
					currentPL.add(breadCrumbNavigator);
					currentPL.show();
					currentPL.doLayout();
					breadCrumbNavigator.addButton(new Ext.Toolbar.Button({text: parentLink["title"], handler:function() {
						displayItemDetails(parentLink["ids"], parentLink["conceptName"]);
					}}));
				}
			}
			else if(currentPL != undefined) {
				breadCrumbNavigator = new Ext.Toolbar({id:'breadCrumbNav' + treeIndex});
				currentPL.add(breadCrumbNavigator);
				currentPL.show();
				currentPL.doLayout();
				breadCrumbNavigator.addButton(new Ext.Toolbar.Button({text: parentLink["title"], handler:function() {
					displayItemDetails(parentLink["ids"], parentLink["conceptName"]);
				}}));
			}
			
		    amalto.core.doLayout();	    
		});
		
		amalto.core.ready();
		
	}
	
	function getSelectedViewName(dataObject){
		
		 var viewName = "";
		 if(Ext.get('viewItemsSelect')!=null){
			 viewName = DWRUtil.getValue('viewItemsSelect'); 
		 }else{
			 viewName =  "Browse_items_"+dataObject;
		 }
		 return viewName;
	}
	
	function updateNode2(id,node, value,treeIndex){
		if(node.itemData.valueInfo != null)
        {
            value = node.itemData.value;
        }

        
        if(node.itemData.key==true){
            keys[treeIndex][node.itemData.keyIndex] = value;
        }
        
        $('errorDetail' + treeIndex).style.display = "none";
        
        //node.itemData.nodeId = id;
        //edit by ymli: fix the bug:0013463
        ItemsBrowserInterface.updateNode(id, value, treeIndex,function(result){
        	amalto.core.ready(result);
        	$('errorDesc' + treeIndex).style.display = "block";
        });
        if(updateValue(id,treeIndex)==false){                        			
            allUpdate = true;           
        } 
       
       else
        {   
        	
            allUpdate = updateItemNodesBeforeSaving(treeIndex);
            if($('errorDesc' + treeIndex)){
	            if (allUpdate == true) {
	                $('errorDesc' + treeIndex).style.display = "block";
	            }
	            else
	             $('errorDesc' + treeIndex).style.display = "none";
            }
        }
               
	}
	
	function reloadNode(id, treeIndex){
		var itemTree = itemTreeList[treeIndex];
		var node = itemTree.getNodeByIndex(id);
		itemTree.removeChildren(node);
		node.expand();
	}
	
	function updateNode(id, treeIndex,format,typeName){
		updateFlag[treeIndex] = 1;
		var allUpdate = false;
		_exception = false;
		var itemTree = itemTreeList[treeIndex];
		//var data = itemTree.getNodeByIndex(id).data;	
		var node = itemTree.getNodeByIndex(id);
		var value  = DWRUtil.getValue(id+"Value");
		if(format!="null")
		 ItemsBrowserInterface.printFormat(language,format,value,typeName,function(result){
		   value = result;
		 
		  updateNode2(id,node,value,treeIndex);
		  });//interface.updatevalue( ;
		  else
		       updateNode2(id,node,value,treeIndex);
		
		amalto.core.ready();
		
		//set isdirty=true
		var itempanel = amalto.core.getTabPanel().activeTab;
		if(itempanel){
			itempanel.isdirty=true;
		}
		
	}
	
	function showExceptionMsg(errorString, exception, treeIndex)
	{
		var reg = /\[(.*?):(.*?)\]/gi;
		var errorsArray = errorString.match(reg);
		if(errorsArray!=null){
			for(var i=0;i<errorsArray.length;i++){
				  if(errorsArray[i].indexOf("[")>=0){
					  errorsArray[i] = errorsArray[i].replace("[","").trim();
				  }
				  if(errorsArray[i].indexOf("]")>=0){
				      errorsArray[i] = errorsArray[i].replace("]"," ").trim();
				  }
			   }
			   var flag=false;
			   var defualtErrorMsg=errorString;
			   for(var i=0;i<errorsArray.length;i++){
				   if(language==errorsArray[i].split(":")[0].toLowerCase()&&errorsArray[i].split(":")[1]!=null&&errorsArray[i].split(":")[0].trim()!=""){
					   errorString=errorsArray[i].substr(errorsArray[i].indexOf(":")+1);
					   flag=true;
				   }
				   if("en"==errorsArray[i].split(":")[0].toLowerCase()){
//					   defualtErrorMsg=errorsArray[i].split(":",1)[1];
					   defualtErrorMsg=errorsArray[i].substr(errorsArray[i].indexOf(":")+1);
				   	
				   }
			   }
			   if(!flag){
				   errorString=defualtErrorMsg;
			   }
		   }
		   var error = itemTreeList[treeIndex];
           $('errorDesc'+ treeIndex).style.display = errorString.indexOf("Save item") == 0 ? "none" : "block";
            var reCat = /\[Error\].*\n/gi;
            var innerHml ="";
         	var arrMactches = errorString.match(reCat);
         	if (arrMactches != null)
             	for (var i=0; i < arrMactches.length ; i++)
				{
				  innerHml +=arrMactches[i];
				  if (i < arrMactches.length-1)
				   innerHml += '<br/>';
				}
			else
			 innerHml += errorString +'<br/>';
			$('errorDetail' + treeIndex).style.display = "block";
			$('errorDetail' + treeIndex).innerHTML = innerHml;
			_exception = true;
	}
	
	function setlastUpdatedInputFlag(id, treeIndex){
		//alert("flag");
		lastUpdatedInputFlag[treeIndex] = id;
	}
	//add by lym fix bug 0009620;
	function getSiblingsLength(node){
		var siblingLength = 0;
		if(node.parent != null){
			for(var i = 0;i < node.parent.children.length;i++){
				if(node.parent.children[i].itemData.xmlTag == node.itemData.xmlTag)
					siblingLength++;
			}
		}
		return siblingLength;
	}
	//see 0011614: The Web App resets the form when clicking on '+' to add a complex element. 
	function getChildrenValues(parent){
		var array=parent.children;
		var values = [];		
		for (var i = 0; i < array.length;i++) {
			var nodenew = array[i];
			if(nodenew.children.length==0){
		  		if(!(typeof(nodenew) == "undefined")&& $(nodenew.index+"Value")!=null){// && nodenew.itemData.nodeId==node.itemData.nodeId)
					var value1 = nodenew.index+"--"+$(nodenew.index+"Value").value;//DWRUtil.getValue(nodenew.index+"Value");
					values.push(value1);					 
		  		}				
			}else{
				var var1=getChildrenValues(nodenew);				
				for(var ii=0; ii<var1.length; ii++){
					if(var1[ii])values.push(var1[ii]);
				}
			}
		}
		return values;
	}
	
	function cloneObject(obj){
	    if(obj == null || typeof(obj) != 'object')
	        return obj;

	    var temp = obj.constructor(); // changed

	    for(var key in obj)
	        temp[key] = cloneObject(obj[key]);
	    return temp;
	}
	
	function cloneNode2(siblingId, hasIcon, treeIndex){
		var itemTree = itemTreeList[treeIndex];
		var siblingNode = itemTree.getNodeByIndex(siblingId);
		
		//add by ymli. remember the values of the siblingNodes.fix the bug:0010576
		var values = [];
		values=getChildrenValues(siblingNode.parent);
		//var value = DWRUtil.getValue(siblingId+"Value");
		//modified by ymli. If the Items is more than maxOccurs, alert and return
		if(siblingNode.itemData.maxOccurs>=0&&siblingNode.parent!=null && getSiblingsLength(siblingNode)>=siblingNode.itemData.maxOccurs){
			Ext.MessageBox.alert("Status",siblingNode.itemData.maxOccurs+" "+siblingNode.itemData.name+"(s) at most");
			return;
		}
		var nodeCount = YAHOO.widget.TreeView.nodeCount;	
		
		DWREngine.setAsync(false);
		ItemsBrowserInterface.updateNodeDspValue(treeIndex,siblingNode.index, function(result){
			if (result != null)
				siblingNode.itemData.value = result;
			else
				//add by yguo. fix bug clone node then browseForeignKey. the value of node equals siblingNode's value.
				siblingNode.itemData.value = "";
		});
		DWREngine.setAsync(true);
		var newNode = new amalto.itemsbrowser.ItemNode(cloneObject(siblingNode.itemData),true,treeIndex,siblingNode.parent,true,true,isReadOnlyinItem);		
		newNode.updateNodeId(nodeCount);
		//remove by ymli; fix the bug:0013463
		//newNode.updateValue(" ");
		ItemsBrowserInterface.cloneNode(siblingId,newNode.index, treeIndex,function(result){
			amalto.core.ready(result);
		});
		
		newNode.insertAfter(siblingNode);
		//newNode.appendTo(siblingNode.parent);
		itemTree.getRoot().childrenRendered=false;
		
		var fnLoadData = function(oNode,fnCallback){
			//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
			ItemsBrowserInterface.getChildren(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,null,function(result){
				if(result==null) {
					fnCallback();
					return;
				}
				for(var i=0; i<result.length; i++) {					
					var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,itemTree.getNodeByIndex(oNode.index),false,true,isReadOnlyinItem);
				if(result[i].type=="simple") tmp.setDynamicLoad();
				   else tmp.setDynamicLoad(fnLoadData,1);
			}
			fnCallback();
			});
		};
		var length = map[treeIndex].length;
    	map[treeIndex][length] = newNode;
    	
		newNode.setDynamicLoad(fnLoadData);
		//itemTree.getRoot().refresh();
		siblingNode.parent.refresh();
		//itemTree.getRoot().refresh();
		amalto.core.ready();
		//removed by lzhang, if need, (line 3722 siblingNode.itemData.value = "") has done this
		//if($(nodeCount+"Value"))$(nodeCount+"Value").value = "";
		
		//add by ymli. set the values of the siblingNodes. fix the bug:0010576
		for(var t=0;t<values.length;t++){
			var value = values[t];
			var idValue = value.split("--");
			if(idValue!=null&&$(idValue[0]+"Value")!=null){
				$(idValue[0]+"Value").value = idValue[1];
				//reset URL & PICTURE type fields
				if($(idValue[0]+'showPicture') && idValue[1].length>0)$(idValue[0]+'showPicture').src=idValue[1];
				if($("showUrl" + idValue[0])){
					var urlvalues=idValue[1].split('@@');
					if(urlvalues && urlvalues.length==2)
					DWRUtil.setValue("showUrl" + idValue[0], "<a target='_blank' href='"+ urlvalues[1]+ "'>"+urlvalues[0]+"</a>");
				}				
			}
		}		
	}
	
	function showEditWindow(nodeIndex, treeIndex, nodeType){
		var tree=itemTreeList[treeIndex];
		var node=tree.getNodeByIndex(nodeIndex);
		var values=new Array();
		if(node.itemData.value!=null) values=node.itemData.value.split('@@');
		 var form = new Ext.form.FormPanel({
		        baseCls: 'x-plain',
		        labelWidth: 55,
		        url:'save-form.php',
		        defaultType: 'textfield',

		        items: [{
		            fieldLabel: 'Name',
		            id: 'name',
		            value: values[0]==null?"":values[0],
		            anchor:'100%'  
		        },{
		            fieldLabel: 'Url',
		            id: 'url',
		            value: values[1]==null?"http://":values[1],
		            anchor: '100%'  
		        }]
		    });

		    var window = new Ext.Window({
		        title: 'Edit Url',
		        width: 300,
		        height:200,
		        minWidth: 300,
		        minHeight: 200,
		        layout: 'fit',
		        plain:true,
		        bodyStyle:'padding:5px;',
		        buttonAlign:'center',
		        items: form,

		        buttons: [{
		            text: 'Save',
		            handler: function() {
		        	var showUrlIndex = "showUrl" + nodeIndex;
		        	DWRUtil.setValue(nodeIndex+"Value",Ext.getCmp('name').getValue()+"@@"+Ext.getCmp('url').getValue());
		        	DWRUtil.setValue(showUrlIndex, "<a target='_blank' href='"+ Ext.getCmp('url').getValue()+ "'>"+Ext.getCmp('name').getValue()+"</a>");
		        	updateNode(nodeIndex,treeIndex);
		        	window.destroy();
		        		
		           }
		        },{
		            text: 'Cancel',
		            handler: function() {
		        	window.destroy();
		        }
		        }]
		    });

		    window.show();
	}
	function editNode2(siblingId, hasIcon, treeIndex){
		var itemTree = itemTreeList[treeIndex];
		var siblingNode = itemTree.getNodeByIndex(siblingId);
		//modified by ymli. If the Items is more than maxOccurs, alert and return
		if(siblingNode.itemData.maxOccurs>=0&&siblingNode.parent!=null && getSiblingsLength(siblingNode)>=siblingNode.itemData.maxOccurs){
			Ext.MessageBox.alert("Status",siblingNode.itemData.maxOccurs+" "+siblingNode.itemData.name+"(s) at most");
			return;
		}
		var nodeCount = YAHOO.widget.TreeView.nodeCount;
	
		var newNode = new amalto.itemsbrowser.ItemNode(siblingNode.itemData,true,treeIndex,siblingNode.parent,true,true,isReadOnlyinItem);
		newNode.updateNodeId(nodeCount);
		//remove by ymli; fix the bug:0013463
		//newNode.updateValue(" ");
	
		newNode.insertAfter(siblingNode);
		//newNode.appendTo(siblingNode.parent);
		itemTree.getRoot().childrenRendered=false;
		
		var fnLoadData = function(oNode,fnCallback){
			//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
			ItemsBrowserInterface.getChildren(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,null,function(result){
				if(result==null) {
					fnCallback();
					return;
				}
				for(var i=0; i<result.length; i++) {					
					var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,itemTree.getNodeByIndex(oNode.index),false,true,isReadOnlyinItem);
				if(result[i].type=="simple") tmp.setDynamicLoad();
				else tmp.setDynamicLoad(fnLoadData,1);
			}
			fnCallback();
			});
		};
		
		newNode.setDynamicLoad(fnLoadData);
		siblingNode.parent.refresh();
		amalto.core.ready();
	}

	function removeNode2(id, treeIndex) {
		updateFlag[treeIndex] = true;
		var value = "";
		if (Ext.get(id + "Value"))
			value = DWRUtil.getValue(id + "Value");
		var itemTree = itemTreeList[treeIndex];
		// add by ymli
		// modified by ymli. If the Items is less than minOccurs, alert and
		// return
		var node = itemTree.getNodeByIndex(id);
		var siblingLength = getSiblingsLength(node);
		if (node.parent != null
				&& siblingLength<= node.itemData.minOccurs) {
			Ext.MessageBox.alert("Status",node.itemData.minOccurs + " " + node.itemData.name
					+ "(s) at least");
			return;
		} else if (siblingLength <= 1) {
			Ext.MessageBox.alert("Status",DELETE_ALERT[language]);
			return;
		}
		
		//add by ymli. move the node which is deleted from map[treeIndex]
		var array = map[treeIndex];
		for (var i = 0; i < array.length; i++) {
	  		var nodenew = array[i];
	  		if(nodenew!=null && nodenew.index==node.index)
	  			array.splice(i,1);
		}
		
		//add by ymli. remember the values of Nodes avoid them to be null 
		//map[treeIndex]=array;
		var values = [];
		//var array = map[treeIndex];
		var j = 0;
		
		values = getChildrenValues(itemTree.getRoot());
		itemNodes.remove(itemTree.getNodeByIndex(id));
		itemTree.removeNode(itemTree.getNodeByIndex(id), true);
		ItemsBrowserInterface.removeNode(id, treeIndex, value,
				function(result) {
					amalto.core.ready(result);
				});
		
		itemTree.getRoot().refresh();
		//add by ymli. set the values of nodes
		for(var t=0;t<values.length;t++){
			var value = values[t];
			var idValue = value.split("--");
			if(idValue!=null&&$(idValue[0]+"Value")!=null){
				$(idValue[0]+"Value").value = idValue[1];
				//reset URL & PICTURE type fields
				if($(idValue[0]+'showPicture') && idValue[1].length>0)$(idValue[0]+'showPicture').src=idValue[1];
				if($("showUrl" + idValue[0])){
					var urlvalues=idValue[1].split('@@');
					if(urlvalues && urlvalues.length==2)
					DWRUtil.setValue("showUrl" + idValue[0], "<a target='_blank' href='"+ urlvalues[1]+ "'>"+urlvalues[0]+"</a>");
				}				
			}
		}
		amalto.core.ready();
	}
	
	function removeEleFromArray(array,deleteNode){
		
	  	
	}
	
	function saveItemAndQuit(ids, dataObject, treeIndex, refreshCB) {
		saveItem(ids, dataObject, treeIndex, function() {
					var itempanel = amalto.core.getTabPanel().activeTab;
					if (itempanel) {
						itempanel.isdirty = false;
					}
					amalto.core.getTabPanel().remove('itemDetailsdiv'
							+ treeIndex);
					refreshCB.call();
				});

	}
	
	function saveItemWithoutQuit(ids,dataObject,treeIndex,refreshCB){
		DWREngine.setAsync(false);
		saveItem(ids,dataObject,treeIndex,function(){
		    //amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
	        //refreshCB.call();
			var toolbar = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
			toolbar.refreshItemHandler();
			//set isdirty=true
			var itempanel = amalto.core.getTabPanel().activeTab;
			if(itempanel){
				itempanel.isdirty=false;
			}				
			
			//refresh the search result tabs
            if(Ext.get('items-grid')){
    			var grid=gridContainerPanel.items.first();            			
    			grid.store.reload();
            }
		 });
		
		DWREngine.setAsync(true);
	}

	function updateItemNodesBeforeSaving(treeIndex)
	{
	  var nodes = map[treeIndex];
	  var error = false;
	  _exception = false;
	  for (var i = 0; i < nodes.length; i++) {
	  	var node = nodes[i];
	  	if (node && node instanceof amalto.itemsbrowser.ItemNode)
	  	{   
	  		//edit by ymli; fix the bug:0013463
	  		//if (node.itemData.choice == false &&  node.update() == false)
	  		var isUpdate = updateValue(node.itemData.nodeId,treeIndex);
	  		if (node.itemData.choice == false &&  isUpdate == false)
	  		  error = true;
	  		else if(node.itemData.choice == true)
	  		{
	  			var childNodesWithinChoice = node.parent.children;
	  			var allValid = true;
	  			for (var m = 0; m < childNodesWithinChoice.length; m++){
	  				if (childNodesWithinChoice[m] instanceof amalto.itemsbrowser.ItemNode){
	  					//if (childNodesWithinChoice[m].update() == false){
	  					if (updateValue(childNodesWithinChoice[m].itemData.nodeId,treeIndex )== false){
	  						allValid = false;
	  					}
	  				}
	  			}
	  			if (allValid == true){
	  				var inputState = 0;
	  				for (var m = 0; m < childNodesWithinChoice.length; m++){
		  				if (childNodesWithinChoice[m] instanceof amalto.itemsbrowser.ItemNode){
		  					var nodeValue =  DWRUtil.getValue(childNodesWithinChoice[m].itemData.nodeId + "Value");
		  					if (nodeValue != "")
		  						inputState += 1;
		  				}
	  				}
	  				
	  				for (var n = 0; n < childNodesWithinChoice.length; n++){
		  				if (inputState == 0){
		  					childNodesWithinChoice[n].displayErrorMessage(childNodesWithinChoice[n].itemData.nodeId, CHOICE_NONE_ALERT[language]);
		  					error = true;
		  				}
		  				else if(inputState <= childNodesWithinChoice.length && inputState > 1){
		  					childNodesWithinChoice[n].displayErrorMessage(childNodesWithinChoice[n].itemData.nodeId, CHOICE_ALLTOGETHER_ALERT[language]);
		  					error = true;
		  				}
	  				}
	  			}
	  		}
	  		if(node.itemData.nodeId.type == "simple")
	  		{
		  		var thisValue =  DWRUtil.getValue(node.itemData.nodeId + "Value");
				ItemsBrowserInterface.updateNode(node.itemData.nodeId,thisValue,treeIndex,function(result){
					amalto.core.ready(result);
				});
	  		}
	  	}
	  	   
	  }
	  return error;
	}

	function saveItem(ids,dataObject,treeIndex,callbackOnSuccess){
		if(navigator.appName=="Microsoft Internet Explorer" && lastUpdatedInputFlag[treeIndex]!=null) 

		{
					updateNode(lastUpdatedInputFlag[treeIndex], treeIndex);
				}
				
			    if (updateItemNodesBeforeSaving(treeIndex) == true) {
					$('errorDesc'+ treeIndex).style.display = "block";
					$('errorDetail'+ treeIndex).style.display = "none";
					return;
				}
				
			    if(_exception == true)return;
			    $('errorDesc'+ treeIndex).style.display = "none";
			    $('errorDetail'+ treeIndex).style.display = "none";


			    var cluster = DWRUtil.getValue('datacluster-select');
			    ItemsBrowserInterface.isDataClusterExists(cluster,{callback:function(result){
			    	if(!result){
			    		Ext.Msg.confirm("confirm",CONFIRM_DATACLUSTER_CHANGE[language],function re(en){
			    			var dataClusterCmb=Ext.getCmp("datacluster-select");
				    		dataClusterCmb.store.reload();
			    		});
			    		
			    		
			    	}else{
						ItemsBrowserInterface.checkIfDocumentExists(keys[treeIndex], dataObject, function(result){
							if(result==true) {
		//						if(!Ext.MessageBox.confirm(MSG_CONFIRM_SAVE_ITEM[language])) return;
								Ext.Msg.confirm("confirm",MSG_CONFIRM_SAVE_ITEM[language],function re(en){
									if(en=="no") {
										return;
									}else {
										saveItem0(ids, dataObject, treeIndex, callbackOnSuccess);
									}
								});
							}
							else {
								saveItem0(ids, dataObject, treeIndex, callbackOnSuccess);
							}
				});
		
		}}});
    }
	
	function saveItem0(ids,dataObject,treeIndex,callbackOnSuccess) {
		ItemsBrowserInterface.isItemModifiedByOther(newItem[treeIndex],treeIndex,function(result){
			if(result){
				Ext.Msg.confirm("confirm",MSG_CONFIRM_OVERRIDE_ITEM[language],function(en){
					if(en=="no") {
						return;
					}else {
						saveItem1(ids, dataObject, treeIndex, callbackOnSuccess);
					}
				});
			}else{
				saveItem1(ids, dataObject, treeIndex, callbackOnSuccess);
			}
		});
	}	
	function saveItem1(ids,dataObject,treeIndex,callbackOnSuccess) {
		var itemPK = ids.split('@');
		amalto.core.working("Saving...");
		var tbDetail = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
		tbDetail.items.get('saveBTN').disable();
		tbDetail.items.get('saveAndQBTN').disable();
			ItemsBrowserInterface.saveItem(itemPK,dataObject, newItem[treeIndex],treeIndex,{
				callback:function(result){ 
					amalto.core.ready(result);
					if(result.status == 2) { //unchanged
						amalto.core.ready(ALERT_NO_CHANGE[language]);
					}
					else if(result.status == 1) { //failure
					    if(result.description!=null){
					    	if(result.description.indexOf('ERROR_3:')==0){
						      // add for before saving transformer check
	                          amalto.core.ready(result.substring(8));
	                          Ext.MessageBox.alert("Status",result.description.substring(8));
	                        }else if(result.description.indexOf('Unable to save item')==0){
	                	        amalto.core.ready();
	    				        showExceptionMsg(result.description, null, treeIndex);
	                        }else if(result.description.indexOf('Save item')==0){
	                	        amalto.core.ready();
	    				        showExceptionMsg(result.description, null, treeIndex);
	                        }
					    }
					}
					else if(result.status == 0) {//by yguo, fix 0016461. hard code baseOptions of tool bar to 2016. 
						reFreshToolBar(tbDetail);
					}
					
					tbDetail.items.get('saveBTN').enable();
			        tbDetail.items.get('saveAndQBTN').enable();
			        
					if(callbackOnSuccess && result.status != 1)callbackOnSuccess();
					
					if(result.description==null || result.description==""){
						return;
					}else{
						if(result.status == 1) {
							var err1=result.description;
						    if(err1.lastIndexOf("ERROR")>-1)
							    var err1=result.description.substring(8);
							if(err1==null || err1==""){
							    return;
							}else{
							    err1 = dropOnErrorMsg(err1);
							    Ext.MessageBox.show({
							        msg:err1,
							        buttons:{"ok":"CANCEL"},
							        icon:Ext.MessageBox.ERROR
							     });
							    return;
							}
						}else{
							var msg2show=result.description;
							Ext.MessageBox.show({
							    msg:msg2show,
							    buttons:{"ok":"OK"},
							    icon:Ext.MessageBox.INFO
							 });
							return;
						}
					}
					amalto.core.ready();
					tbDetail.items.get('saveBTN').enable();
					tbDetail.items.get('saveAndQBTN').enable();
				}
	        });
	}

	function deleteItem(ids, dataObject, treeIndex,refreshCB) {
		//var viewName = DWRUtil.getValue('viewItemsSelect');
		//var dataObject = viewName.replace("Browse_items_",""); 
		var tmp = "";
		var itemPK = ids.split('@');
		for(var i=0; i<itemPK.length; i++) {
			tmp += " "+itemPK[i];
		}
		Ext.MessageBox.confirm("confirm",MSG_CONFIRM_DELETE_ITEM[language]+ " ?",function re(en){
		if(en=="yes"){
			ItemsBrowserInterface.getUriArray(dataObject, itemPK, function(picUriArray){
				var uriArray = [];
				uriArray=picUriArray;
				for (var index = 0; index < uriArray.length; index++) {
					var picUri=uriArray[index];
				if(picUri!=""){
					 var pos=picUri.indexOf('?');
	 				 var uri=picUri.substring("/imageserver/".length,pos); 
				     Ext.Ajax.request({  
				       	 url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
				         method: 'post',  
				         callback: function(options, success, response) {  
				         }  
				     });    
				  }
			   }
			});
			ItemsBrowserInterface.deleteItem(dataObject, itemPK,treeIndex, function(result){
				if(result.lastIndexOf("ERROR")>-1){
					var err1=result.substring(7);
					Ext.MessageBox.show({msg:err1, buttons:{"ok":"CANCEL"}, icon:Ext.MessageBox.ERROR});
//					$('errorDetail' + treeIndex).style.display = "block";
//					$('errorDetail' + treeIndex).innerHTML ="<br/>"+err1+"<br/>";					
					return;
				}
				else if(result.lastIndexOf)
				var itempanel = amalto.core.getTabPanel().activeTab;
				if(itempanel){
					itempanel.isdirty=false;
				}				
				amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
				amalto.core.ready(result);
				displayItems();
				refreshCB.call();
				if(result)Ext.MessageBox.show({msg:result, buttons:{"ok":"OK"}, icon:Ext.MessageBox.INFO});		
			});		
		}});		
	}
	
	function logicalDelOneItem(ids, dataObject, treeIndex, path, refreshCB){
		var tmp = "";
		var itemPK = ids.split('@');
		for(var i=0; i<itemPK.length; i++) {
			tmp += " "+itemPK[i];
		}
		ItemsBrowserInterface.logicalDeleteItem(dataObject, itemPK, path, treeIndex, function(result){
			if(result.lastIndexOf("ERROR")>-1){
				var err1=result.substring(7);
				//Ext.MessageBox.alert("ERROR", err1);
				$('errorDetail' + treeIndex).style.display = "block";
				$('errorDetail' + treeIndex).innerHTML ="<br/>"+err1+"<br/>";					
				return;
			}			
			var itempanel = amalto.core.getTabPanel().activeTab;
			if(itempanel){
				itempanel.isdirty=false;
			}			
			amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
			amalto.core.ready(result);
			//displayItems();
			refreshCB.call();
			if(result)Ext.MessageBox.alert('Status', result);				
		});
	}
	
	function logicalDelItem(ids, dataObject, treeIndex, refreshCB){
		var tmp = "";
		var itemPK = ids.split('@');
		
		Ext.Msg.show({
		   title: MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM[language],
		   msg:  MSG_CONFIRM_LOGICAL_DELETE_ITEM[language],
		   buttons: Ext.Msg.OKCANCEL,
		   fn: doLogicalDel,
		   prompt:true,
		   value: '/',
		   width:300
		});
		
		function doLogicalDel(btn, path){
	       if (btn == 'cancel') {
				return;
			}
			logicalDelOneItem(ids,dataObject, treeIndex,path,refreshCB);
		};
	}
	
	function duplicateItem(ids, dataObject){
		if(ids){
		var itemPK = ids.split('@');
		displayItemDetails4Duplicate(itemPK,dataObject,true);
	    }
	}
	
	function lineageItem(lineageEntities) {
		//@temp, open the searchentitypanel on a fresh tab.
//		ItemsBrowserInterface
		var tabPanel = amalto.core.getTabPanel();
		
		var searchEntityPanel = tabPanel.getItem("searchEntityPanel");
		
		if(searchEntityPanel) {
			tabPanel.remove(searchEntityPanel);
			searchEntityPanel.destroy();
		}
		
		searchEntityPanel = new amalto.itemsbrowser.SearchEntityPanel({
			lineageEntities : lineageEntities
		});
		
		tabPanel.add(searchEntityPanel);
		
		searchEntityPanel.show();
		searchEntityPanel.doLayout();
		amalto.core.doLayout();
	}
	
	function taskItem(taskId) {
		if(taskId == undefined) {
			Ext.Msg.alert('Error', NO_TASK_MSG[language]);
			return;
		}
		amalto.datastewardship.Datastewardship.taskItem(taskId);
	}
	
	function journalItem(ids, dataObject){
		if(ids.indexOf("@")>0)ids=ids.replaceAll("@",".");
	    amalto.updatereport.UpdateReport.browseUpdateReportWithSearchCriteria(dataObject, ids, true);	
	}
	
	function showDatePicker(nodeId, treeIndex, nodeType,displayFormats) {
		if (nodeDatePickerWindow) {
			nodeDatePickerWindow.hide();
			nodeDatePickerWindow.destroy();
		}

		var inputText = nodeId + "Value";
		if(treeIndex == -1)
		{
			inputText = nodeId;
		}
		
		var nodeDatePickerPanel = new Ext.form.FormPanel({
					baseCls : 'x-plain',
					labelAlign : 'right',
					// layout:'fit',
					defaultType : 'datefield',
					items : [{
								id : 'date111',
								xtype : 'datepicker',
								fieldLabel : 'nodeDatePickerPanel ',
								name : 'datePicker',
								layout : 'fit',
								inputType : 'textfield',
								monthNames:MONTH_NAME[language],
								listeners : {

									select : function(src, date) {
										var setValue = date.format("Y-m-d");
										if (nodeType == "dateTime") {
											setValue += "T00:00:00";
										};
										if(displayFormats!="null")
										ItemsBrowserInterface.printFormatDate(language,displayFormats,setValue,"date",function(resultValue){
										  //setValue = resultValue;
										  if(resultValue!="null")
                                             
                                             /*if(realValues[treeIndex] == undefined)
                                                realValues[treeIndex] = [];
                                             realValues[treeIndex][nodeId] = setValue;*/
										  
										  DWRUtil.setValue(inputText, setValue);
										 if(treeIndex != -1)
										  {
										   updateNode(nodeId, treeIndex,displayFormats,nodeType);	
										   
										  }
										  DWRUtil.setValue(inputText,resultValue);
										  nodeDatePickerWindow.hide();
										  nodeDatePickerWindow.destroy();										
										});
										else{
										  DWRUtil.setValue(inputText, setValue);
                                          if(treeIndex != -1)
                                          {
                                          	 /*if(realValues[treeIndex] == undefined)
                                                realValues[treeIndex] = [];
                                           realValues[treeIndex][nodeId] = setValue;*/
                                           updateNode(nodeId, treeIndex,displayFormats,nodeType);   
                                          }
                                          nodeDatePickerWindow.hide();
                                          nodeDatePickerWindow.destroy();   
										}
									}
								}
							}]
				});
		nodeDatePickerWindow = new Ext.Window({
					title : 'Date Picker',
					width : 205,
					height : 240,
					layout : 'fit',
					plain : true,
					bodyStyle : 'padding:1px;',
					buttonAlign : 'center',
					items : nodeDatePickerPanel
				});

		// var value1 = nodeDatePickerPanel.getForm().getValues('date111');
		var initValue = $(inputText).value;
		if (initValue != null && initValue != "") {
			if (initValue.indexOf('T') != -1)
				initValue = initValue.substring(0, initValue.indexOf('T'));
			// nodeDatePicker.setValue(Date.parseDate(initValue,"Y-m-d"));
		}

		nodeDatePickerWindow.show();
	}
	
	function removePicture(nodeId, treeIndex){
	var url=$(nodeId+"Value").value;
	var pos=url.indexOf('?');
	var uri=url.substring("/imageserver/".length,pos);
	
    var form = new Ext.form.FormPanel({
        baseCls: 'x-plain',
        layout:'fit',
        method: 'POST',   
        frame:true, 
        url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
        defaultType: 'textfield',

        items: [{
            xtype:'label',
            text: 'Are you sure to delete?'
        }]
    });	
    var window = new Ext.Window({
        title: 'Confirm',
        width: 200,
        height:120,
        layout: 'fit',
        plain:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: form,
        buttons: [{
            text: 'Yes',
	        handler: function() {          	        	
	        form.getForm().submit({    
		        success: function(form, action){		          	           
		            Ext.Msg.alert('Sucess', 'Picture delete sucessfully!');		 
				    var inputText=nodeId+"Value";	
				    DWRUtil.setValue(inputText,'');
				    updateNode(nodeId,treeIndex);
				    if($(nodeId+'showPicture'))
				       $(nodeId+'showPicture').src='img/genericUI/no_image.gif';		            
		           window.hide();
		        },    
		       failure: function(form, action){  
		       	 var text=action.result;
	          	Ext.Msg.alert('Failed', text.message);
	          	window.hide();
		       },
		       waitMsg : 'Deleting...'
		     });    
		    }              
        },{
            text: 'No',
            handler:function(){
            	window.hide();
            }
        }]
    });

    window.show();	       
	}
	
	function showUploadFile(nodeId, treeIndex, nodeType){

	if(uploadFileWindow){
		 uploadFileWindow.hide();
		 uploadFileWindow.destroy();
	}
    var uploadFilePanel = new Ext.form.FormPanel({
          baseCls: 'x-plain',
	     labelAlign: 'right',    	      
	     labelWidth: 60,           
         //layout:'fit',
         defaultType: 'textfield',
	     url: '/imageserver/secure/ImageUploadServlet',//fileUploadServlet    
	     fileUpload: true,   
	     items: [{    
	        xtype: 'textfield',    
	        fieldLabel: FILE_NAME[language],    
	        name: 'imageFile',
	        height:20,
	        inputType: 'file'//file type
	      }]
        
    });

    uploadFileWindow = new Ext.Window({
        title: UPLOAD_FILE[language],
        width: 340,
        height:120,
        layout: 'fit',
        plain:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: uploadFilePanel,

	    buttons: [{    
	        text: UPLOAD[language],    
	        handler: function() {    
	        uploadFilePanel.getForm().submit({    
		        success: function(uploadFilePanel, action){
		           var text=action.response.responseText;
		           var url='/imageserver/'+eval('('+text+')').message+"?width=150&height=90&preserveAspectRatio=true";		
		           var inputText=nodeId+"Value";	
				    DWRUtil.setValue(inputText,url);
				    updateNode(nodeId,treeIndex);		           
		            Ext.Msg.alert('Success', 'File upload sucessfully!');
		            if($(nodeId+'showPicture'))$(nodeId+'showPicture').src=url;
		            uploadFileWindow.hide();
		        },    
		       failure: function(form, action){    
	          	Ext.Msg.alert('Failed', action.result.message);    
		       },
		       waitMsg : 'Uploading...'
		     });    
		    }    
	 	 },{
            text: RESET[language],
            handler: function(){
                uploadFilePanel.getForm().reset();
            }
        }]    
    });
    
    uploadFileWindow.show();
	}
	var panel;
	var originalXpathForeignKey;
	var lastSelectedType;
	var foreignKeyTypeCounter;
	function chooseForeignKey(nodeId, xpathForeignKey, xpathInfoForeignKey, fkFilter, treeIndex , isSwitch, conceptName) {
		amalto.core.working('Running...');
	    amalto.core.ready();
	    if(!isSwitch){
	       originalXpathForeignKey=xpathForeignKey;
	       lastSelectedType="";
	       foreignKeyTypeCounter=0;
	    }
	    var tbDetail = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex);
	    var dataObject=tbDetail && tbDetail.getTopToolbar() ? tbDetail.getTopToolbar().dataObject : conceptName;
		ItemsBrowserInterface.countForeignKey_filter(dataObject,xpathForeignKey,fkFilter,treeIndex,nodeId, function(count){
			//Display a pop-up window to search for foreign keys
			if(foreignKeyWindow){
			    foreignKeyWindow.hide();
			    foreignKeyWindow.destroy();
			}
			
			//type combo
            var xpath4GetForeignKeyPolymTypeList;
            if(isSwitch)xpath4GetForeignKeyPolymTypeList=originalXpathForeignKey;
            else xpath4GetForeignKeyPolymTypeList=xpathForeignKey;
            
            var showTypeCombo=false;
            
            DWREngine.setAsync(false);
            ItemsBrowserInterface.isPolymForeignKey(xpath4GetForeignKeyPolymTypeList,function(result){showTypeCombo=result;});
            DWREngine.setAsync(true);
            
            var typeStore = new Ext.data.Store({
                proxy: new Ext.ux.data.ImprovedDWRProxy({
                        dwrFunction: ItemsBrowserInterface.getForeignKeyPolymTypeList,
                        dwrAdditional: [xpath4GetForeignKeyPolymTypeList,treeIndex,nodeId]
                }),
                reader: new Ext.data.JsonReader({
                    root: 'rows',
                    totalProperty: 'total',
                    id: 'value'
                },[
                    {name: 'value', mapping: 'value'},
                    {name: 'text', mapping: 'text'}
                ])
            });
            
            var typeCombo = new Ext.form.ComboBox({
                
                    id: 'foreign-key-type-filter',
                    name : "foreign-key-type-filter",
                    fieldLabel : SELECT_TYPE_MESSAGE[language],
                    editable : false,
                    xtype : "combo",
                    store: typeStore,
                    displayField:'text',
                    valueField:'value',   
                    typeAhead: true,
                    triggerAction: 'all',
                    forceSelection:true,
                    resizable:true,
                    hidden:!showTypeCombo,
                    hideLabel:!showTypeCombo
            });

            typeCombo.setValue(lastSelectedType);
            typeStore.on('load', function() {  
                 foreignKeyTypeCounter=this.getCount();
                 typeCombo.on('select',function(box, record, index){ 
                    lastSelectedType=record.get("text");
                    ItemsBrowserInterface.switchForeignKeyType(lastSelectedType,xpathForeignKey, xpathInfoForeignKey, fkFilter, function(fkDrawer){
                    	    if(fkDrawer==null)return;
                            amalto.itemsbrowser.ItemsBrowser.chooseForeignKey(nodeId, fkDrawer.xpathForeignKey, fkDrawer.xpathInfoForeignKey, fkFilter, treeIndex, true);
                    });
                 }); 
                 
            });
            
			// FK combo	
			var store = new Ext.data.Store({
				proxy: new Ext.ux.data.ImprovedDWRProxy({
			        dwrFunction: ItemsBrowserInterface.getForeignKeyListWithCount,
			        dwrAdditional: [dataObject,xpathForeignKey, xpathInfoForeignKey,fkFilter,treeIndex,nodeId] //, Ext.getCmp('foreign-key-filter').getValue()]}
				}),
		        reader: new Ext.data.JsonReader({
		            root: 'rows',
		            totalProperty: 'count',
		            id: 'keys'
		        }, [
		            {name: 'keys', mapping: 'keys'},
		            {name: 'infos', mapping: 'infos'}
		        ])
			});
			
		    // Custom rendering Template
		    var resultTpl = new Ext.XTemplate(
			        '<tpl for="."><div class="search-item" style="font-size: 0.8em">',
			            '<h3>{infos}</h3>',
			            '{keys}',
			        '</div></tpl>'
			    );
		    
		    var combo = new Ext.form.ComboBox({
                width: 280,
                resizable:true, 
                fieldLabel: FILTER[language],
                id: 'foreign-key-filter',
		        store: store,
		        displayField:'title',
		        typeAhead: false,
		        triggerAction: 'all',
		        loadingText: 'Searching...',
		        pageSize:20,
		        minChars: 0,
		        hideTrigger: true,
		        tpl: resultTpl,
		        listAlign: 'tl-bl',
		        itemSelector: 'div.search-item',
		        onSelect: function(record){
                    this.collapse();
		        	foreignKeyWindow.hide();
            		var itemTree = itemTreeList[treeIndex];
            		var node = itemTree.getNodeByIndex(nodeId);
            		node.itemData.value = record.get("keys");
            		node.itemData.valueInfo = record.get("infos");
            		
            		if(node.itemData.retrieveFKinfos) {
            			DWRUtil.setValue(nodeId+'Value', record.get("infos"));//+"--"+record.get("infos"));
            		}
            		else {
            			DWRUtil.setValue(nodeId+'Value', record.get("keys"));//+"--"+record.get("fk"));
            		}
            		
            		//fk-ploym //FIXME:do not use a different asynchronous http request
                    if(foreignKeyTypeCounter>1){
                    	var updateXpath=node.itemData.bindingPath;
                    	var typeName=DWRUtil.getValue('foreign-key-type-filter');
                        ItemsBrowserInterface.updateForeignKeyPolymMap(updateXpath,typeName,treeIndex,function(status){});
                    }
            		
                	updateNode(nodeId,treeIndex);
                	
		        }
		    });
		    
			foreignKeyWindow = new Ext.Window({
                layout:'fit',
                width:300,
                height: 150,
                resizable:true, 
                closeAction:'hide',
                plain: true,
                title: TITLE_WINDOW_FK[language]+'<br/>('+count+' '+(count>1?MESSAGE_MULTI_SHOW[language]+')':MESSAGE_SINGLE_SHOW[language]+')'),
                
                items: [new Ext.form.FormPanel({
                	labelAlign: 'top',
                    items: [
                        typeCombo,    
                        new Ext.Panel({
                       	 	html: '', 
               		 		border: false
               		 	}),
	                  	combo
	                ]
                })]

            });
			
			foreignKeyWindow.on('show', function() {
				var combo = Ext.getCmp('foreign-key-filter');
				combo.focus(true, 100);
				//combo.setSize(foreignKeyWindow.getSize());
				combo.reset();
				if(count<500) {
					
					combo.setRawValue("");
					combo.doQuery(".*",true);
					combo.focus();
		            combo.expand();

				}
			});

			/*foreignKeyWindow.on('syncSize', function() {
				var combo = Ext.getCmp('foreign-key-filter');
				combo.focus(true, 100);
				combo.reset();
				if(count<500) {
					combo.setSize(foreignKeyWindow.getSize());
					combo.setRawValue(".*");
					combo.doQuery(".*",true);
					combo.focus();
		            combo.expand();
				}
			});*/
			foreignKeyWindow.show(this);
			
		});
		
		
	}
	
	function removeForeignKey(nodeId,treeIndex) {
		
		var url=$(nodeId+"Value").value;
		DWRUtil.setValue(nodeId+'Value','');
		var itemTree = itemTreeList[treeIndex];
		var node = itemTree.getNodeByIndex(nodeId);
		node.itemData.value = '';
		node.itemData.valueInfo = '';
		updateNode(nodeId,treeIndex);
	}
	
	var fnLoadData2;
	
		
	function browseForeignKey(nodeId, foreignKeyXpath, treeIndex,title,ids,conceptName){
		var itemTree = itemTreeList[treeIndex];
		var node = itemTree.getNodeByIndex(nodeId);
		var keyValue = node.itemData.value;
		
		if(keyValue != null && keyValue.match(/\[(.*?)\]/g)!=null){
		     var result = new Array();
			 var aggregate = 0;
			 var cordon = 0;
		     for(var i = 0; i < keyValue.length; i++)
			 {
		        var ch = keyValue.charAt(i);
				if(ch == '[')
				{
				 aggregate++;
				 if(aggregate == 1)
				 {
				   cordon = i;
				 }
				}
				else if(ch == ']')
				{
		          aggregate--;
				  if(aggregate == 0)
				  {
					 result.push(keyValue.substring(cordon+1, i));
				  }
				}
			 }
			var itemPK =result;
		} else{
			var itemPK = [keyValue];
		}
		
		var dataObject = foreignKeyXpath.split("/")[0];
		if(dataObject.split("[")[0] != null)
		{
			dataObject = dataObject.split("[")[0];
		}
		if(itemPK =="")
			Ext.Msg.alert("Warning","The concept does not exist.");
		else{
			var parentLink =[];
			parentLink["title"] = title;
			parentLink["ids"] = ids;
			parentLink["conceptName"] = conceptName;
			parentLink["treeIndex"] = treeIndex;
			//@temp yguo, build linkpath map
			displayItemDetails(itemPK, dataObject, true, parentLink);
		}
	}
	
	/* seems this function is useless
	function browseForeignKey2(nodeId, foreignKeyXpath){
		amalto.core.working('Running...');
		var itemPK = DWRUtil.getValue(nodeId+'Value');
		if(panel) panel.destroy();
		var theBody = '<div id="itemFKTree"></div>';	
		panel = new YAHOO.widget.Panel(
		         "itemFKTreecontainer", 
				 {   
				 //y:500,		
				  width: "800px",		 
				   visible: true,
				   draggable: true,
				   close: true,
				   zIndex:10000,
				   x:(document.body.offsetWidth/2)-350,
				   y:200
				 } );
	    panel.setBody(theBody);  
	    panel.render(document.body); 
	    amalto.core.ready();
	
		var addOptions;
		var dataObject = foreignKeyXpath.split("/")[0];
		fnLoadData2 = function(oNode,fnCallback){
			getChildren(oNode.index,fnCallback, true, false, null,0);
		
		};
		//if(tree) tree._deleteNode();
		itemTreeFK= new YAHOO.widget.TreeView("itemFKTree");	
		var root = itemTreeFK.getRoot();
		
		ItemsBrowserInterface.getRootNode(dataObject,language, function(rootNode){
			var nameTmp = dataObject;
			var viewName = DWRUtil.getValue('viewItemsSelect');	
			if(rootNode.name!=null) nameTmp = rootNode.name;
			var node2 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
			//itemTreeFK.setDynamicLoad(fnLoadData2);
			ItemsBrowserInterface.setTree(dataObject,viewName,itemPK,node2.index,true,"", false, function(result){
			node2.setDynamicLoad(fnLoadData2);
			node2.expand();
			itemTreeFK.draw();
			});
		}
		);
	}
	*/
	
	function setForeignKey(nodeId,treeIndex){	
		var fk= DWRUtil.getValue('foreignKeyList');
		DWRUtil.setValue(nodeId+'Value',fk);
		panel.destroy();
		updateNode(nodeId,treeIndex);
	}
	
	function displayXsdDetails(id){
		var divId = id+"XsdDetails";
		var openerId = id+"OpenDetails";
		if($(divId).style.display == "none"){
			$(divId).style.display = "block";
			DWRUtil.setValue(openerId," <img src=\"img/genericUI/close-detail2.gif\"/>");
		}
			
		else{
			$(divId).style.display = "none";
			DWRUtil.setValue(openerId," <img src=\"img/genericUI/open-detail2.gif\" title=\""+ DISPLAYXSD_TT[language] + "\"/>");
		}		
	}
	
	function getSmartView(ids, dataObject, treeIndex){
		var tbDetail = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
	
		tbDetail.displayTreeHandler = function(){	
			getTree(ids,''+dataObject,treeIndex);
		};
		
		tbDetail.printHandler = function() {			
			printSmartView(ids,dataObject,treeIndex);
		};
		
		$('smartView'+treeIndex).style.display='block';
		$('itemDetails'+treeIndex).style.display='none';
		
		// updating toolbar
	
		initToolBar(tbDetail, M_PERSO_VIEW);
	}
	
	function getTree(ids,dataObject, treeIndex){
		var tbDetail = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
		tbDetail.displaySmartViewHandler = function(){	
			getSmartView(ids,''+dataObject,treeIndex);
		};
		
		$('itemDetails'+treeIndex).style.display='block';
		$('smartView'+treeIndex).style.display='none';
		
		// updating toolbar
		initToolBar(tbDetail, M_TREE_VIEW);
	
	}
	
	
	function printSmartView(ids,dataObject,treeIndex){
		var optname = null;
		var selectedSmartView = Ext.getCmp('smartViewCombo'+treeIndex).value;
		if(selectedSmartView!=undefined&&selectedSmartView!=null){
			if(selectedSmartView.indexOf('#')!=-1)
			    optname=DWRUtil.getValue('smartViewCombo'+treeIndex);
		}
		
		var url = '/itemsbrowser/secure/SmartViewServlet?ids='+ids+'&concept='+dataObject+'&language='+language;
		if(optname!=null)url+='&optname='+optname;
		window.open(url,'Print','toolbar=no,location=no,directories=no,menubar=yes,scrollbars=yes,resizable=yes');
	}
	
	function saveConfig(ids, dataObject, treeIndex, callbackOnSuccess){
		var cluster = DWRUtil.getValue('datacluster-select2');
		
		ItemsBrowserInterface.setClusterAndModel(cluster,function(result){
		Ext.Msg.alert(STATUS[language],"  "+result);
		if(result=="Done"){
			ItemsBrowserInterface.checkIfDocumentExists(keys[treeIndex], dataObject, function(result){
				if(result==true) {
//					if(!Ext.MessageBox.confirm(MSG_CONFIRM_SAVE_ITEM[language])) return;
					Ext.Msg.confirm("confirm",MSG_CONFIRM_SAVE_ITEM[language],function re(en){
						if(en=="no") {
							return;
						}
						else {
							saveItem0(ids, dataObject, treeIndex, callbackOnSuccess);
						}
					});
				}
				else {
					saveItem0(ids, dataObject, treeIndex, callbackOnSuccess);
				}
			});
			}
		});
	}
/*	*//**
	 * @author ymli fix the bug:0013463
	 * get the realValue of the node
	 *//*
    function getRealValue(id,treeIndex){
    	var itemTree = itemTreeList[treeIndex];
        var node = itemTree.getNodeByIndex(id);
        var value = node.itemData.realValue;
        if(realValues[treeIndex] != undefined && realValues[treeIndex][id]!= undefined)
            value = realValues[treeIndex][id];
        var inputText=id+"Value";  
        DWRUtil.setValue(inputText,value);
       
    }
        *//**
     * @author ymli fix the bug:0013463
     * get the realValue of the node
     *//*
    function setFormatValue(id,treeIndex,displayFormats){
        var itemTree = itemTreeList[treeIndex];
        var node = itemTree.getNodeByIndex(id);
        var inputText=id+"Value";  
        var value =  DWRUtil.getValue(inputText);
        var typeName = node.itemData.typeName;
        if(value!="")
         ItemsBrowserInterface.printFormat(language,displayFormats,value,typeName, function(result){
         	if(result!="null")
                DWRUtil.setValue(inputText,result);
            if(realValues[treeIndex] == undefined)
                realValues[treeIndex] = [];
             realValues[treeIndex][id] = value;
             updateNode(id, treeIndex); 
         });
    }*/
    /**
     * @author ymli; test validation from server
     */
    function updateValue(nodeId,treeIndex){
    	
    	var returnRe = true;
        var itemTree = itemTreeList[treeIndex];
        //var data = itemTree.getNodeByIndex(id).data;  
        var node = itemTree.getNodeByIndex(nodeId);
        if(node == undefined)
            return true;
        if(node.itemData.type == "complex")
            return true;
        if($(nodeId+'Value')){    
            var value = DWRUtil.getValue(nodeId+'Value');
            node.resetErrorMessage(nodeId);
            DWREngine.setAsync(false);
            ItemsBrowserInterface.validateNode(language, nodeId, value,function(result){
                if(result=="null")
                    returnRe = true;
                else{
                    node.displayErrorMessage(nodeId,result);
                    returnRe = false;
                }
            });
            DWREngine.setAsync(true);
        }
        return returnRe;
    }
    
    function dropOnErrorMsg(raw){
        var reg = /\[(.*?):(.*?)\]/gi;
        var errorsArray = raw.match(reg);
         if(errorsArray!=null){
          for(var i=0;i<errorsArray.length;i++){
            var error = errorsArray[i];
            if(error.indexOf("[")>=0){
              error = error.replace("[","").trim();
           }
            if(error.indexOf("]")>=0){
              error = error.replace("]"," ").trim();
            }
            if(language.toLowerCase()==error.split(":")[0].toLowerCase().trim()){
             return error.split(":")[1].trim();
            }
          }
        }
        
        return raw;
    }
    
    
 	return {
		init: function() {browseItems(); },
		getViewItems:function() {getViewItems();},
		
		getViewItems1:function(){getViewItems1();},
		saveCriteriasClick:function(){saveCriteriasClick();},
		manageSearchTemplates:function(){manageSearchTemplates();},
		
		displayItems:function() {displayItems();},
		addItemsCriteria:function(criteriaParent) {addItemsCriteria(criteriaParent);},
		removeItemsCriteria:function(id){removeItemsCriteria(id);},
		itemsCriteriaWithConstraints:function(criteriaParent, id, add){itemsCriteriaWithConstraints(criteriaParent, id, add);},
		outPutCriteriaResult:function(){outPutCriteriaResult();},
		convertSearchValueInEnglish:function(id){convertSearchValueInEnglish(id);},
		updateOperatorList:function(id){updateOperatorList(id);},
		updateNode:function(id,treeIndex,format,typeName){updateNode(id, treeIndex,format,typeName);},
		reloadNode:function(id,treeIndex){reloadNode(id, treeIndex);},
		setlastUpdatedInputFlagPublic:function(id,treeIndex){setlastUpdatedInputFlag(id,treeIndex);},
		browseForeignKey:function(nodeId, foreignKeyXpath, foreignKeyInfo, title, ids, concept){browseForeignKey(nodeId, foreignKeyXpath, foreignKeyInfo,title,ids,concept);},
		showDatePicker:function(nodeId,treeIndex,nodeType,displayFormats){showDatePicker(nodeId,treeIndex,nodeType,displayFormats);},
		showUploadFile: function (nodeId, treeIndex, nodeType){showUploadFile(nodeId, treeIndex, nodeType);},
		removePicture: function (nodeId, treeIndex){removePicture(nodeId, treeIndex);},
		chooseForeignKey:function(nodeId,xpath,xpathInfo,fkFilter,treeIndex,isSwitch, conceptName){chooseForeignKey(nodeId,xpath,xpathInfo,fkFilter,treeIndex,isSwitch,conceptName);},
		cloneNode2:function(siblingId,hasIcon,treeIndex){cloneNode2(siblingId,hasIcon,treeIndex);},
		removeNode2:function(id,treeIndex){removeNode2(id,treeIndex);},
		displayXsdDetails:function(id){displayXsdDetails(id);},
		setForeignKey:function(nodeId,treeIndex){setForeignKey(nodeId,treeIndex);},
		removeForeignKey:function(nodeId, treeIndex){removeForeignKey(nodeId,treeIndex);},
		displayItemDetails:function(itemPK2, dataObject){displayItemDetails(itemPK2, dataObject);},
		editItemDetails:function(itemPK,dataObject,refreshCB){displayItemDetails4Reference(itemPK,dataObject,refreshCB);},
		getSiblingsLength:function(node){getSiblingsLength(node);},
		showEditWindow:function(nodeIndex, treeIndex, nodeType){showEditWindow(nodeIndex, treeIndex, nodeType);},
		checkInputSearchValue:function(id,value){checkInputSearchValue(id,value);},
		renderFormWindow:function(itemPK2, dataObject, isDuplicate, refreshCB, formWindow, isDetail){renderFormWindow(itemPK2, dataObject, isDuplicate, refreshCB, formWindow,isDetail);}
		/*getRealValue:function(id,treeIndex){getRealValue(id,treeIndex);},
		setFormatValue:function(id,treeIndex,displayFormats){setFormatValue(id,treeIndex,displayFormats);}*/
 	};
}();
