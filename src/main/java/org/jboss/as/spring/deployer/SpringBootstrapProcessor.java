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
import org.jboss.spring.factory.NamedXmlApplicationContext;
import org.jboss.spring.vfs.VFSResource;
import org.jboss.vfs.VirtualFile;

/**
 * @author Marius Bogoevici
 */
public class SpringBootstrapProcessor implements DeploymentUnitProcessor {

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
       SpringContextDefinitionLocations locations = SpringContextDefinitionLocations.retrieveFrom(phaseContext.getDeploymentUnit());
        for (VirtualFile virtualFile : locations.getContextDefinitionLocations()) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(phaseContext.getDeploymentUnit().getAttachment(Attachments.MODULE).getClassLoader());
                new NamedXmlApplicationContext("default", new VFSResource(virtualFile));
            } finally {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

    @Override
    public void undeploy(DeploymentUnit context) {

    }
}
