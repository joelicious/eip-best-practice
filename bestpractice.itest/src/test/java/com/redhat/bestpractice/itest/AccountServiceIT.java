package com.redhat.bestpractice.itest;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.configureConsole;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.editConfigurationFilePut;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.keepRuntimeFolder;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.replaceConfigurationFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.scanFeatures;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;

import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.tooling.exam.options.LogLevelOption.LogLevel;
import org.jboss.fusebyexample.testing.account.Account;
import org.jboss.fusebyexample.testing.account.types.AccountRequest;
import org.jboss.fusebyexample.testing.account.types.AccountResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.options.UrlReference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4TestRunner.class)
public class AccountServiceIT {

	private static final Logger LOG = LoggerFactory
			.getLogger(AccountServiceIT.class);

	private static final String URL_TO_TEST = "http://localhost:9090/bestpractice/account";

	@Inject
	protected BundleContext bundleContext;

	@Inject
	protected FeaturesService featureService;

	private Account createBasicWebClient() {

		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
		clientBean.setServiceClass(Account.class);
		clientBean.setAddress(URL_TO_TEST);

		return (Account) proxyFactory.create();

	}
	
	public static MavenArtifactProvisionOption getFeatureUrl(String groupId,
			String camelId) {
		return mavenBundle().groupId(groupId).artifactId(camelId);
	}

	public static UrlReference getApplicationFeatureUrl() {
		MavenArtifactProvisionOption mavenOption = getFeatureUrl("com.joe",
				"testing.feature");
		return mavenOption.versionAsInProject().type("type").classifier("xml");
	}

	protected void installAndAssertFeature(String feature) throws Exception {
		featureService
				.addRepository(URI
						.create("mvn:com.redhat/bestpractice.feature/1.0-SNAPSHOT/xml/features"));
		featureService.installFeature(feature);
		assertFeatureInstalled(feature);
	}

	public void assertFeatureInstalled(String featureName) {
		Feature[] features = featureService.listInstalledFeatures();
		for (Feature feature : features) {
			if (featureName.equals(feature.getName())) {
				return;
			}
		}
		Assert.fail("Feature " + featureName
				+ " should be installed but is not");
	}

	@Test
	public void testDeploymentSuccess() {

		LOG.info("===  Executing Test Case: testDeploymentSuccess() === ");

		Bundle[] bundleArray = this.bundleContext.getBundles();

		if (bundleArray != null) {
			LOG.info("The number of bundles are: " + bundleArray.length);
		} else {
			LOG.info("The Bundle Array is Zero");
			// assertFalse(true);
		}
		
		try {
			installAndAssertFeature("testing");
		} catch (Exception e) {
			assertFalse("Unable to install feature testing", true);
		}

		Account account = createBasicWebClient();

		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountId("123ClientChecking456");
		accountRequest.setAccountType("ClientChecking");
		accountRequest.setApplicationId("ClientTester");
		accountRequest.setBankName("ClientBanking");
		accountRequest.setBankRoutingNumber("123ClientRouting456");

		LOG.info("Making Request");

		AccountResponse accountResponse = account.createAccount(accountRequest);

		LOG.info("The reply: " + accountResponse.getReply());

		assertEquals("The response was not Account: OK",
				accountResponse.getReply(), "Account: OK");

		LOG.info("=== Exiting Test Case: testDeploymentSuccess() === ");

	}

	@Configuration
	public Option[] config() {
		return new Option[] {

		new DefaultCompositeOption(
				karafDistributionConfiguration()
						.frameworkUrl(
								maven().groupId("org.jboss.fuse")
										.artifactId("jboss-fuse-minimal")
										.versionAsInProject().type("zip"))
						.karafVersion(
								MavenUtils.getArtifactVersion("org.jboss.fuse",
										"jboss-fuse-minimal"))
						.name("JBoss Fuse")
						.unpackDirectory(new File("target/exam"))
						.useDeployFolder(false),
				// It is really nice if the container sticks around
				// after the
				// test so you can check the contents
				// of the data directory when things go wrong.
				keepRuntimeFolder(),
				// Ignore Local Console output to minimize output
				// in the logs.
				configureConsole().ignoreLocalConsole(),
				// Force the log level to INFO so we have more details
				// during
				// the test. It defaults to WARN.
				logLevel(LogLevel.INFO),

				editConfigurationFilePut("etc/config.properties",
						"karaf.startup.message",
						"Loading Fuse from: ${karaf.home}"),
				editConfigurationFilePut("etc/users.properties", "admin",
						"admin,admin"),
				editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg",
						"org.ops4j.pax.url.mvn.localRepository",
						System.getProperty("repositoryLocation")),

				systemProperty("jetty.port").value(
						System.getProperty("serviceHTTPPort")),
				// Provision custom Jetty Connector
				replaceConfigurationFile("etc/jetty.xml",
						new File(System.getProperty("basedir")
								+ "/src/test/karaf/etc/jetty.xml")),

				// Modify Default Ports so they do not conflict
				// with other JBoss Fuse Containers.
				editConfigurationFilePut("etc/org.ops4j.pax.web.cfg",
						"org.osgi.service.http.port",
						System.getProperty("serviceHTTPPort")),
				editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
						"rmiServerPort", "44450"),
				editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
						"rmiRegistryPort", "44440"),
				editConfigurationFilePut("etc/org.apache.karaf.shell.cfg",
						"sshPort", "8102"),
				//
				// // Update with Nexus Repositories
				// editConfigurationFilePut(
				// "etc/org.ops4j.pax.url.mvn.cfg",
				// "org.ops4j.pax.url.mvn.repositories",
				// "http://repo1.maven.org/maven2@id=maven.central.repo, "
				// +
				// "https://repo.fusesource.com/nexus/content/repositories/releases@id=fusesource.release.repo, "
				// +
				// "https://repo.fusesource.com/nexus/content/groups/ea@id=fusesource.ea.repo, "
				// +
				// "http://svn.apache.org/repos/asf/servicemix/m2-repo@id=servicemix.repo, "
				// +
				// "http://repository.springsource.com/maven/bundles/release@id=springsource.release.repo, "
				// +
				// "http://repository.springsource.com/maven/bundles/external@id=springsource.external.repo, "
				// +
				// "https://oss.sonatype.org/content/groups/scala-tools@id=scala.repo"),
				//
				// // Basic container configuration
				// editConfigurationFilePut(
				// "etc/org.apache.karaf.features.cfg",
				// "featuresBoot",
				// "jasypt-encryption,config,management,fabric,fabric-bundle,fabric-maven-proxy,patch,transaction,mq-fabric,war,fabric-redirect,hawtio"))

				// Provision out application configuration
				replaceConfigurationFile(
						"etc/com.joe.testing.core.cfg",
						// Make this portable and flexible.
						new File(
								System.getProperty("basedir")
										+ "/src/test/karaf/etc/com.joe.testing.core.cfg")),
				replaceConfigurationFile(
						"etc/com.joe.testing.jms.cfg",
						// Make this portable and flexible.
						new File(System.getProperty("basedir")
								+ "/src/test/karaf/etc/com.joe.testing.jms.cfg")),
				replaceConfigurationFile("etc/com.joe.testing.ws.cfg",
				// Make this portable and flexible.
						new File(System.getProperty("basedir")
								+ "/src/test/karaf/etc/com.joe.testing.ws.cfg")),
				replaceConfigurationFile(
						"etc/com.joe.testing.client.cfg",
						// Make this portable and flexible.
						new File(
								System.getProperty("basedir")
										+ "/src/test/karaf/etc/com.joe.testing.client.cfg")),

				// Provision application
				scanFeatures(
						"mvn:com.joe/testing.feature/1.0-SNAPSHOT/xml/features",
						"testing"))

		};

	}
}
