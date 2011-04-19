/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.spring.deployer;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.logging.Logger;
import org.jboss.vfs.VirtualFile;

/**
 * @author Marius Bogoevici
 */
public class SpringStructureProcessor implements DeploymentUnitProcessor {

    private static final Logger log = Logger.getLogger("org.jboss.as.spring");

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        ResourceRoot deploymentRoot = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);

        if (deploymentRoot == null) {
            return;
        }

        SpringContextDefinitionLocations springContextDefinitionLocations = new SpringContextDefinitionLocations();


        VirtualFile metaInf = deploymentRoot.getRoot().getChild("META-INF");
        for (VirtualFile child : metaInf.getChildren()) {
            if (child.getName().endsWith("-spring.xml")) {
                springContextDefinitionLocations.add(child);
                log.debug("Found:" + child.getPathName());
            }
        }

        springContextDefinitionLocations.attachTo(deploymentUnit);

    }

    @Override
    public void undeploy(DeploymentUnit context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
