/**
 *  Copyright (C) 2010 Mentor Graphics Corporation
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Based on the libvirt-plugin which is:
 *  Copyright (C) 2010, Byte-Code srl <http://www.byte-code.com>
 *
 * Date: Mar 04, 2010
 * Author: Marco Mornati<mmornati@byte-code.com>
 */
package hudson.plugins.labmanager;

import hudson.util.FormValidation;
import hudson.util.Scrambler;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.Label;
import hudson.Extension;
import hudson.slaves.Cloud;
import hudson.slaves.NodeProvisioner;
import java.util.ArrayList;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.security.Security;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.X509Certificate;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

import com.vmware.labmanager.*;
import com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.*;

/**
 * Represents a virtual Lab Manager Organization/Workspace/Configuration
 * combination.
 *
 * @author Tom Rini <tom_rini@mentor.com>
 */
public class LabManager extends Cloud {
    private final String lmHost;
    private final String lmDescription;
    private final String lmOrganization;
    private final String lmWorkspace;
    private final String lmConfiguration;
    private final String username;
    private final String password;

    /**
     * Information to connect to Lab Manager and send SOAP requests.
     */
    private AuthenticationHeaderE lmAuth = null;

    /**
     * Lazily computed list of virtual machines in this configuration.
     */
    private transient List<LabManagerVirtualMachine> virtualMachineList = null;

    @DataBoundConstructor
    public LabManager(String lmHost, String lmDescription,
                    String lmOrganization, String lmWorkspace,
                    String lmConfiguration, String username,
                    String password) {
        super("LabManager");
        this.lmHost = lmHost;
        this.lmDescription = lmDescription;
        this.lmOrganization = lmOrganization;
        if (lmWorkspace.length() != 0)
            this.lmWorkspace = lmWorkspace;
        else
            this.lmWorkspace = "main";
        this.lmConfiguration = lmConfiguration;
        this.username = username;
        this.password = Scrambler.scramble(Util.fixEmptyAndTrim(password));
        /* Setup our auth token. */
        AuthenticationHeader ah = new AuthenticationHeader();
        ah.setUsername(username);
        ah.setPassword(password);
        this.lmAuth = new AuthenticationHeaderE();
        this.lmAuth.setAuthenticationHeader(ah);
        virtualMachineList = retrieveLabManagerVirtualMachines();
    }

    /* This is something that we need to make sure
     * happens when Hudson is restarted for example. */
    private void fixTrustManager() {
        /* Install the all-trusting trust manager */
        Security.addProvider( new DummyTrustProvider() );
        Security.setProperty("ssl.TrustManagerFactory.algorithm",
            "TrustAllCertificates");
    }

    public String getLmHost() {
        return lmHost;
    }

    public String getLmDescription() {
        return lmDescription;
    }

    public String getLmOrganization() {
        return lmOrganization;
    }

    public String getLmWorkspace() {
        return lmWorkspace;
    }

    public String getLmConfiguration() {
        return lmConfiguration;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Scrambler.descramble(password);
    }

    public LabManager_x0020_SOAP_x0020_interfaceStub getLmStub() {
        /* Make sure the trust manager is right. */
        fixTrustManager();

        LabManager_x0020_SOAP_x0020_interfaceStub lmStub = null;
        try {
            lmStub = new LabManager_x0020_SOAP_x0020_interfaceStub(lmHost + "/LabManager/SOAP/LabManager.asmx");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return lmStub;
    }

    public AuthenticationHeaderE getLmAuth() {
        return lmAuth;
    }

    private List<LabManagerVirtualMachine> retrieveLabManagerVirtualMachines() {
        LabManager_x0020_SOAP_x0020_interfaceStub lmStub = getLmStub();
        List<LabManagerVirtualMachine> vmList = new ArrayList<LabManagerVirtualMachine>();
        /* Get the list of machines.  We do this by asking for our
         * configuration and then passing that ID to a request for
         * listMachines.
         */
        try {
            GetSingleConfigurationByName gscbnReq = new GetSingleConfigurationByName();
            gscbnReq.setName(lmConfiguration);
            GetSingleConfigurationByNameResponse gscbnResp = lmStub.getSingleConfigurationByName(gscbnReq, lmAuth);
            ListMachines lmReq = new ListMachines();
            lmReq.setConfigurationId(gscbnResp.getGetSingleConfigurationByNameResult().getId());
            ListMachinesResponse lmResp = lmStub.listMachines(lmReq, lmAuth);

            ArrayOfMachine aom = lmResp.getListMachinesResult();
            for (Machine mach : aom.getMachine())
                vmList.add(new LabManagerVirtualMachine(this, mach.getName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return vmList;
    }

    public synchronized List<LabManagerVirtualMachine> getLabManagerVirtualMachines() {
        if (virtualMachineList == null) {
            virtualMachineList = retrieveLabManagerVirtualMachines();
        }
        return virtualMachineList;
    }

    public Collection<NodeProvisioner.PlannedNode> provision(Label label, int i) {
        return Collections.emptySet();
    }

    public boolean canProvision(Label label) {
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LabManager");
        sb.append("{Host='").append(lmHost).append('\'');
        sb.append(", Description='").append(lmDescription).append('\'');
        sb.append(", Organization='").append(lmOrganization).append('\'');
        sb.append(", Workspace='").append(lmWorkspace).append('\'');
        sb.append(", Configuration='").append(lmConfiguration).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<Cloud> {
        public final ConcurrentMap<String, LabManager> hypervisors = new ConcurrentHashMap<String, LabManager>();
        private String lmHost;
        private String lmOrganization;
        private String lmWorkspace;
        private String lmConfiguration;
        private String username;
        private String password;

        @Override
        public String getDisplayName() {
            return "Lab Manager";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject o)
                throws FormException {
            lmHost = o.getString("lmHost");
            lmOrganization = o.getString("lmOrganization");
            lmWorkspace = o.getString("lmWorkspace");
            lmConfiguration = o.getString("lmConfiguration");
            username = o.getString("username");
            password = o.getString("password");
            save();
            return super.configure(req, o);
        }

        /**
         * For UI.
         */
        public FormValidation doTestConnection(@QueryParameter String lmHost,
                @QueryParameter String lmOrganization,
                @QueryParameter String lmDescription,
                @QueryParameter String lmWorkspace,
                @QueryParameter String lmConfiguration,
                @QueryParameter String username,
                @QueryParameter String password) {
            try {
                /* We know that these objects are not null */
                if (lmHost.length() == 0)
                    return FormValidation.error("Lab Manager host is not specified");
                else {
                    /* Perform other sanity checks. */
                    if (!lmHost.startsWith("https://"))
                        return FormValidation.error("Lab Manager host must start with https://");
                }

                if (lmOrganization.length() == 0)
                    return FormValidation.error("Lab Manager organization is not specified");

                if (lmConfiguration.length() == 0)
                    return FormValidation.error("Lab Manager configuration is not specified");

                if (username.length() == 0)
                    return FormValidation.error("Username is not specified");

                if (password.length() == 0)
                    return FormValidation.error("Password is not specified");

                /* Install the all-trusting trust manager */
                Security.addProvider( new DummyTrustProvider() );
                Security.setProperty("ssl.TrustManagerFactory.algorithm",
                    "TrustAllCertificates");

                /* Try and connect to it. */
                LabManager_x0020_SOAP_x0020_interfaceStub stub = new LabManager_x0020_SOAP_x0020_interfaceStub(lmHost + "/LabManager/SOAP/LabManager.asmx");
                AuthenticationHeader ah = new AuthenticationHeader();
                ah.setUsername(username);
                ah.setPassword(password);
                AuthenticationHeaderE ahe = new AuthenticationHeaderE();
                ahe.setAuthenticationHeader(ah);

                /* GetCurrentOrganizationName */
                GetSingleConfigurationByName request = new GetSingleConfigurationByName();
                request.setName(lmConfiguration);
                GetSingleConfigurationByNameResponse resp = stub.getSingleConfigurationByName(request, ahe);
                if (lmConfiguration.equals(resp.getGetSingleConfigurationByNameResult().getName()))
                    return FormValidation.ok("Connected successfully");
                else
                    return FormValidation.error("Could not login and retrieve basic information to confirm setup");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This is taken from:
     * http://knowledgehub.zeus.com/articles/2006/01/03/using_the_control_api_with_java
     * The following code disables certificate checking.
     * Use the Security.addProvider and Security.setProperty calls to enable it.
     **/

    private static class DummyTrustProvider extends Provider {
        public DummyTrustProvider() {
            super( "DummyTrustProvider", 1.0, "Trust certificates" );
            put( "TrustManagerFactory.TrustAllCertificates",
                MyTrustManagerFactory.class.getName() );
        }

        protected static class MyTrustManagerFactory extends TrustManagerFactorySpi {
            public MyTrustManagerFactory() {}
            protected void engineInit(KeyStore keystore) {}
            protected void engineInit(ManagerFactoryParameters mgrparams) {}
            protected TrustManager[] engineGetTrustManagers() {
                return new TrustManager[] {new MyX509TrustManager()};
            }
        }

        protected static class MyX509TrustManager implements X509TrustManager {

            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }
    }
}
