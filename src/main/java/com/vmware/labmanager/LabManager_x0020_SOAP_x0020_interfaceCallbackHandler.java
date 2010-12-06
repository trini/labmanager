
/**
 * LabManager_x0020_SOAP_x0020_interfaceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.3  Built on : Nov 12, 2010 (02:24:07 CET)
 */

    package com.vmware.labmanager;

    /**
     *  LabManager_x0020_SOAP_x0020_interfaceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class LabManager_x0020_SOAP_x0020_interfaceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public LabManager_x0020_SOAP_x0020_interfaceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public LabManager_x0020_SOAP_x0020_interfaceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for configurationPerformAction method
            * override this method for handling normal response from configurationPerformAction operation
            */
           public void receiveResultconfigurationPerformAction(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationPerformActionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationPerformAction operation
           */
            public void receiveErrorconfigurationPerformAction(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getConfigurationByName method
            * override this method for handling normal response from getConfigurationByName operation
            */
           public void receiveResultgetConfigurationByName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetConfigurationByNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getConfigurationByName operation
           */
            public void receiveErrorgetConfigurationByName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for listConfigurations method
            * override this method for handling normal response from listConfigurations operation
            */
           public void receiveResultlistConfigurations(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ListConfigurationsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from listConfigurations operation
           */
            public void receiveErrorlistConfigurations(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setCurrentOrganizationByName method
            * override this method for handling normal response from setCurrentOrganizationByName operation
            */
           public void receiveResultsetCurrentOrganizationByName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.SetCurrentOrganizationByNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setCurrentOrganizationByName operation
           */
            public void receiveErrorsetCurrentOrganizationByName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMachineByName method
            * override this method for handling normal response from getMachineByName operation
            */
           public void receiveResultgetMachineByName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetMachineByNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMachineByName operation
           */
            public void receiveErrorgetMachineByName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCurrentWorkspaceName method
            * override this method for handling normal response from getCurrentWorkspaceName operation
            */
           public void receiveResultgetCurrentWorkspaceName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetCurrentWorkspaceNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCurrentWorkspaceName operation
           */
            public void receiveErrorgetCurrentWorkspaceName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for listMachines method
            * override this method for handling normal response from listMachines operation
            */
           public void receiveResultlistMachines(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ListMachinesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from listMachines operation
           */
            public void receiveErrorlistMachines(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationSetPublicPrivate method
            * override this method for handling normal response from configurationSetPublicPrivate operation
            */
           public void receiveResultconfigurationSetPublicPrivate(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationSetPublicPrivateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationSetPublicPrivate operation
           */
            public void receiveErrorconfigurationSetPublicPrivate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSingleConfigurationByName method
            * override this method for handling normal response from getSingleConfigurationByName operation
            */
           public void receiveResultgetSingleConfigurationByName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetSingleConfigurationByNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSingleConfigurationByName operation
           */
            public void receiveErrorgetSingleConfigurationByName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for liveLink method
            * override this method for handling normal response from liveLink operation
            */
           public void receiveResultliveLink(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.LiveLinkResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from liveLink operation
           */
            public void receiveErrorliveLink(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationClone method
            * override this method for handling normal response from configurationClone operation
            */
           public void receiveResultconfigurationClone(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationCloneResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationClone operation
           */
            public void receiveErrorconfigurationClone(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCurrentOrganizationName method
            * override this method for handling normal response from getCurrentOrganizationName operation
            */
           public void receiveResultgetCurrentOrganizationName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetCurrentOrganizationNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCurrentOrganizationName operation
           */
            public void receiveErrorgetCurrentOrganizationName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for machinePerformAction method
            * override this method for handling normal response from machinePerformAction operation
            */
           public void receiveResultmachinePerformAction(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.MachinePerformActionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from machinePerformAction operation
           */
            public void receiveErrormachinePerformAction(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMachine method
            * override this method for handling normal response from getMachine operation
            */
           public void receiveResultgetMachine(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetMachineResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMachine operation
           */
            public void receiveErrorgetMachine(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationUndeploy method
            * override this method for handling normal response from configurationUndeploy operation
            */
           public void receiveResultconfigurationUndeploy(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationUndeployResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationUndeploy operation
           */
            public void receiveErrorconfigurationUndeploy(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationCapture method
            * override this method for handling normal response from configurationCapture operation
            */
           public void receiveResultconfigurationCapture(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationCaptureResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationCapture operation
           */
            public void receiveErrorconfigurationCapture(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationDeploy method
            * override this method for handling normal response from configurationDeploy operation
            */
           public void receiveResultconfigurationDeploy(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationDeployResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationDeploy operation
           */
            public void receiveErrorconfigurationDeploy(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationCheckout method
            * override this method for handling normal response from configurationCheckout operation
            */
           public void receiveResultconfigurationCheckout(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationCheckoutResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationCheckout operation
           */
            public void receiveErrorconfigurationCheckout(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getConfiguration method
            * override this method for handling normal response from getConfiguration operation
            */
           public void receiveResultgetConfiguration(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.GetConfigurationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getConfiguration operation
           */
            public void receiveErrorgetConfiguration(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setCurrentWorkspaceByName method
            * override this method for handling normal response from setCurrentWorkspaceByName operation
            */
           public void receiveResultsetCurrentWorkspaceByName(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.SetCurrentWorkspaceByNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setCurrentWorkspaceByName operation
           */
            public void receiveErrorsetCurrentWorkspaceByName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for configurationDelete method
            * override this method for handling normal response from configurationDelete operation
            */
           public void receiveResultconfigurationDelete(
                    com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.ConfigurationDeleteResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from configurationDelete operation
           */
            public void receiveErrorconfigurationDelete(java.lang.Exception e) {
            }
                


    }
    