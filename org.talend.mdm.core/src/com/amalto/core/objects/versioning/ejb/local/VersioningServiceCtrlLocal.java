/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.core.objects.versioning.ejb.local;

/**
 * Local interface for VersioningServiceCtrl.
 * @xdoclet-generated at 9-10-09
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface VersioningServiceCtrlLocal
   extends com.amalto.core.ejb.local.ServiceCtrlLocal
{
   /**
    * Returns the History of an item or object The path is constitued of the clustername/instancename
    * @throws EJBException
    */
   public com.amalto.core.objects.versioning.util.HistoryInfos getHistory( java.lang.String path ) throws com.amalto.core.util.XtentisException;

   /**
    * Returns the Versioning History of an item or object The path is constitued of the clustername/instancename
    * @throws EJBException
    */
   public com.amalto.core.objects.versioning.util.HistoryInfos getVersions( java.lang.String path ) throws com.amalto.core.util.XtentisException;

   /**
    * Checkouts The path is constitued of the clustername/instancename If tag is null, the checkout will be made from the head
    * @return the checked out xml
    * @throws EJBException
    */
   public java.lang.String[] checkOut( java.lang.String path,java.lang.String tag,java.lang.String revision ) throws com.amalto.core.util.XtentisException;

   /**
    * Commits to the head of the repository The path is constitued of the clustername/instancename
    * @throws EJBException
    */
   public void commit( java.lang.String path,java.lang.String xml,java.lang.String comment,java.lang.String username ) throws com.amalto.core.util.XtentisException;

   /**
    * Branch the head to a particular tag The path is constitued of the clustername/instancename If the path is a clustername only, all instances will be branched
    * @throws EJBException
    */
   public void branch( java.lang.String path,java.lang.String tag,java.lang.String comment,java.lang.String username ) throws com.amalto.core.util.XtentisException;

   /**
    * Clean the head by keeping on the listed instances of the particular cluster
    * @throws EJBException
    */
   public void clean( java.lang.String clustername,java.lang.String[] instancenames ) throws com.amalto.core.util.XtentisException;

   /**
    * Get instances name for a particular cluster and a particular tag If tag null, return the instance names of the head
    * @throws EJBException
    */
   public java.lang.String[] getInstances( java.lang.String clustername,java.lang.String tag ) throws com.amalto.core.util.XtentisException;

   /**
    * Sets the default/current versioning system configuration
    * @throws EJBException
    */
   public void setCurrentVersioningSystemConfiguration( java.lang.String name,java.lang.String url,java.lang.String username,java.lang.String password ) throws com.amalto.core.util.XtentisException;

   /**
    * To be Implemented. Returns the unique JNDI name of the service. The JNDI name must be of the type amalto/local/service/[NAME] where [NAME] matchs the pattern "[a-zA-Z][a-zA-Z0-9]*" and is unique accross services
    * @throws EJBException
    */
   public java.lang.String getJNDIName(  ) throws com.amalto.core.util.XtentisException;

   /**
    * To be Implemented. Returns the description of the service. Can be null
    * @throws EJBException
    */
   public java.lang.String getDescription( java.lang.String twoLettersLanguageCode ) throws com.amalto.core.util.XtentisException;

   public java.lang.String getDocumentation( java.lang.String twoLettersLanguageCode ) throws com.amalto.core.util.XtentisException;

   /**
    * To be Implemented. Starts if needed the service Can be null
    * @throws EJBException
    */
   public void start(  ) throws com.amalto.core.util.XtentisException;

   /**
    * To be Implemented. Stops if needed the service Can be null
    * @throws EJBException
    */
   public void stop(  ) throws com.amalto.core.util.XtentisException;

   /**
    * To be Implemented. Returns a status of the service Can be null
    * @throws EJBException
    */
   public java.lang.String getStatus(  ) throws com.amalto.core.util.XtentisException;

   /**
    * To be implemented Runs the service. The object received in an HashMap made of -username - String -password - String -contentType - String -charset - String -bytes - bytes[] -paramameters - HashMap
    * @throws EJBException
    * @return Serializable - a serializable Object to be passed backed to the connector
    */
   public java.io.Serializable receiveFromOutbound( java.util.HashMap map ) throws com.amalto.core.util.XtentisException;

   /**
    * To be implemented Runs the service. The item received in an XML String
    * @param itemPK - the item that triggered a Routing Rule <hich created the Active Routing Order
    * @param routingOrderID - the routing Order ID of the routing rule that called - From 2.19.0, the Routing Order is an ActiveRoutingOrderPOJO
    * @param parameters - the routing rules parameters
    * @return this value is appended at the end of the message field of the Routing Order
    * @throws XtentisException
    */
   public java.lang.String receiveFromInbound( com.amalto.core.ejb.ItemPOJOPK itemPK,java.lang.String routingOrderID,java.lang.String parameters ) throws com.amalto.core.util.XtentisException;

   /**
    * To be implemented To request and get the response from other applications
    * @param command - used to call different pull method in service Object
    * @param parameters - incoming parameters, may be in xml format
    * @param schedulePlanID - the ID of schedule plan, if in schedule mode
    * @return Serializable - a serializable Object to be passed backed to the system
    * @throws XtentisException
    */
   public java.io.Serializable fetchFromOutbound( java.lang.String command,java.lang.String parameters,java.lang.String schedulePlanID ) throws com.amalto.core.util.XtentisException;

   /**
    * Configuration received from outbound, typically a portlet The default implementation stores the configuration string "as is"
    * @throws EJBException
    */
   public void putConfiguration( java.lang.String configuration ) throws com.amalto.core.util.XtentisException;

   /**
    * Returns the XML schema for the configuration<br> Can be null
    * @throws XtentisException
    */
   public java.lang.String getConfigurationSchema(  ) throws com.amalto.core.util.XtentisException;

   /**
    * return default the configuration<br> Can be null
    * @throws XtentisException
    */
   public java.lang.String getDefaultConfiguration(  ) throws com.amalto.core.util.XtentisException;

   /**
    * Retrieves the configuration The default implementation renders the configuration string "as stored" and ignore the optional parameter
    * @throws EJBException
    */
   public java.lang.String getConfiguration( java.lang.String optionalParameter ) throws com.amalto.core.util.XtentisException;

   /**
    * Configuration received from outbound, typically a portlet The default implementation stores the configuration string "as is"
    * @throws EJBException
    */
   public void putServiceData( java.lang.String serviceData ) throws com.amalto.core.util.XtentisException;

}
