/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.service.calltransformer.ejb.local;

/**
 * Local home interface for CallTransformer.
 * @xdoclet-generated at 9-03-10
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface CallTransformerLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/CallTransformerLocal";
   public static final String JNDI_NAME="amalto/local/service/callprocess";

   public com.amalto.service.calltransformer.ejb.local.CallTransformerLocal create()
      throws javax.ejb.CreateException;

}