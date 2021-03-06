/*
 * Copyright WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.cloudconnector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.config.xml.TemplateMediatorFactory;
import org.apache.synapse.mediators.template.TemplateMediator;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.xml.type.internal.QName;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.deserializer.MediatorFactoryUtils;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;
import org.wso2.developerstudio.eclipse.utils.file.FileUtils;

public class CloudConnectorDirectoryTraverser {

	private static final String synapseNS = "http://ws.apache.org/ns/synapse";
	private static final String connectorFileName = "connector.xml";
	private static final String componentFileName = "component.xml";
	private static final String initFileName = "init.xml";
	private static final String DIR_DOT_METADATA = ".metadata";
	public static final String connectorPathFromWorkspace = DIR_DOT_METADATA + File.separator + ".Connectors";
	private Properties properties = new Properties();
	private static String rootDirectory=null;
	private static CloudConnectorDirectoryTraverser instance=null;
	private Connector connector=null;
	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);
	
	/*
	 * Private Constructor
	 */
	private CloudConnectorDirectoryTraverser(){	
	}
	/*
	 * static method for creating an instance of this class 
	 */
	public static CloudConnectorDirectoryTraverser getInstance(String file){
		rootDirectory=file;
		if(instance == null){
			instance=new CloudConnectorDirectoryTraverser();
		}
		return instance;
	}
	
	public static CloudConnectorDirectoryTraverser getInstance(){
		if(instance == null){
			instance=new CloudConnectorDirectoryTraverser();
		}
		return instance;
	}
	
	private void deserializeConnectorXML(){
		try{
			File artifactsFile = new File(rootDirectory+File.separator+connectorFileName);
			String artifactsContent = FileUtils.getContentAsString(artifactsFile);
			connector = new Connector();
			connector.deserialize(artifactsContent);
		}catch(Exception e){
			log.error("Error while deserializing connector xml", e);
		}
	}
		
	private TemplateMediator readTemplateConfiguration(String fileLocation) throws IOException, XMLStreamException{
		String path = fileLocation;	
				String source = FileUtils.getContentAsString(new File(path));
				OMElement element = AXIOMUtil.stringToOM(source);
				TemplateMediator templateMediator=null;
				
				if (element.getFirstChildWithName(new QName(synapseNS, "sequence", null)) != null) {
					MediatorFactoryUtils.registerFactories();
					TemplateMediatorFactory templateMediatorFactory = new TemplateMediatorFactory();
					templateMediator = (TemplateMediator) templateMediatorFactory.createMediator(element, properties);						
				}
		return templateMediator;
	}
	
	public Collection<String> getCloudConnectorConfigurationParameters() throws Exception{
		return readTemplateConfiguration(getConfigurationFileLocation(getOperationFileNamesMap())).getParameters();
	}
	
	/**
	 * Returning Operations map in the Cloud Connector zip. This map contains
	 * the name of the component name and the file name of the operation.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public Map<String, String> getOperationFileNamesMap() throws Exception{		
		Map<String, String> operationFileNamesMap=new HashMap<String,String>();
		deserializeConnectorXML();
		for (Dependency dependency : connector.getComponentDependencies()) {
			String pathname = rootDirectory +File.separator+ dependency.getComponent();
			File artifactFile = new File(pathname + File.separator+componentFileName);
			String artifactContent = FileUtils.getContentAsString(artifactFile);
			Component subComponent = new Component();
			subComponent.deserialize(artifactContent);
			for (SubComponents subComponents : subComponent.getSubComponents()) {
				operationFileNamesMap.put(subComponents.getFileName(),dependency.getComponent());
			}

		}
		return operationFileNamesMap;
	}
	
	/**
	 * Returning Operations map in the Cloud Connector zip. This map contains
	 * the name of the operation and the file name of the operation.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public Map<String, String> getOperationsMap() throws Exception{
		Map<String, String> operationNamesAndFileNamesMap=new HashMap<String,String>();
		deserializeConnectorXML();
		for (Dependency dependency : connector.getComponentDependencies()) {
			String pathname = rootDirectory +File.separator+ dependency.getComponent();
			File artifactFile = new File(pathname + File.separator+componentFileName);
			String artifactContent = FileUtils.getContentAsString(artifactFile);
			Component subComponent = new Component();
			subComponent.deserialize(artifactContent);
			for (SubComponents subComponents : subComponent.getSubComponents()) {
				operationNamesAndFileNamesMap.put(subComponents.getName(),subComponents.getFileName());
			}

		}
		return operationNamesAndFileNamesMap;
	}
	
	
	/**
	 * Returning Operations map in the Cloud Connector zip. This map contains
	 * the name of the operation and the name of the cloud connector component.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public Map<String, String> getOperationsConnectorComponentNameMap() throws Exception{
		Map<String, String> operationNamesAndConnectorComponentNameMap=new HashMap<String,String>();
		deserializeConnectorXML();
		for (Dependency dependency : connector.getComponentDependencies()) {
			String pathname = rootDirectory +File.separator+ dependency.getComponent();
			File artifactFile = new File(pathname + File.separator+componentFileName);
			String artifactContent = FileUtils.getContentAsString(artifactFile);
			Component subComponent = new Component();
			subComponent.deserialize(artifactContent);
			for (SubComponents subComponents : subComponent.getSubComponents()) {
				operationNamesAndConnectorComponentNameMap.put(subComponents.getName(),connector.getConnectorName());
			}

		}
		return operationNamesAndConnectorComponentNameMap;
	}
	
	
	public String getCloudConnectorName(){
		deserializeConnectorXML();
		return connector.getConnectorName();
	}
	
	public String getConfigurationFileLocation(Map<String, String> artifactsMap) throws Exception{
		return rootDirectory+File.separator+artifactsMap.get("init")+File.separator+initFileName;
	}
	
	/**
	 * Get connector directory path from connector name.
	 * @param workspacePath
	 * @param connectorName
	 * @return
	 */
	public String getConnectorDirectoryPathFromConnectorName(String workspacePath,
			String connectorName) {
		String connectorDirectoryPath = null;
		workspacePath += File.separator + connectorPathFromWorkspace;

		File directory = new File(workspacePath);
		if (directory.isDirectory()) {
			String[] children = directory.list();
			for (int i = 0; i < children.length; ++i) {
				if (connectorName.equals(children[i].split("-")[0])) {
					connectorDirectoryPath = workspacePath + File.separator + children[i];
					return connectorDirectoryPath;
				}
			}
		}
		return connectorDirectoryPath;
	}
	
	/**
	 * Get all possible parameters of the given connector operation. 
	 * @param addedConnector
	 * @param addedOperation
	 * @return
	 */
	public Collection<String> getAllParametersOfConnectorOperation(String addedConnector,
			String addedOperation) {
		String directory = null;
		String operationFileName = null;
		List<String> parameters = new ArrayList<String>();
		
		IProject activeProject = EditorUtils.getActiveProject();
		String connectorPath = getConnectorDirectoryPathFromConnectorName(activeProject
				.getWorkspace().getRoot().getLocation().toOSString(), addedConnector);
		CloudConnectorDirectoryTraverser cloudConnectorDirectoryTraverser = CloudConnectorDirectoryTraverser
				.getInstance(connectorPath);

		try {
			operationFileName = cloudConnectorDirectoryTraverser.getOperationsMap().get(
					addedOperation);
			directory = cloudConnectorDirectoryTraverser.getOperationFileNamesMap().get(
					operationFileName);
		} catch (Exception e1) {
			log.error("Error while retrieving data for connector", e1);
		}
		String path = connectorPath + File.separator + directory + File.separator
				+ operationFileName + ".xml";

		try {
			String source = FileUtils.getContentAsString(new File(path));
			OMElement element = AXIOMUtil.stringToOM(source);

			if (element.getFirstChildWithName(new QName(synapseNS, "sequence", null)) != null) {
				TemplateMediatorFactory templateMediatorFactory = new TemplateMediatorFactory();
				TemplateMediator templateMediator = (TemplateMediator) templateMediatorFactory
						.createMediator(element, properties);
				return templateMediator.getParameters();
			}
		} catch (XMLStreamException e) {
			log.error("Error occured while parsing selected template file", e);
		} catch (IOException e) {
			log.error("Error occured while reading selected template file", e);
		}

		return parameters;
	}
}
