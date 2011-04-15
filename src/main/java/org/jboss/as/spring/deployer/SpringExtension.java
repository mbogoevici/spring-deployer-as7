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

import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.*;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.common.CommonDescriptions;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.as.controller.registry.ModelNodeRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.*;

/**
 * @author Marius Bogoevici
 */
public class SpringExtension implements Extension {

    private static final Logger log = Logger.getLogger("org.jboss.spring");

    public static final String SUBSYSTEM_NAME = "spring";

    public static final String NAMESPACE = "urn:jboss:domain:spring:1.0";

    private static SpringSubsystemElementParser parser = new SpringSubsystemElementParser();

    private static final DescriptionProvider SUBSYSTEM_ADD_DESCRIPTION = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            return SpringDescriptionProviders.getSubsystemAddDescription(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_DESCRIPTION = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            return SpringDescriptionProviders.getSubsystemDescription(locale);
        }
    };

    private static ModelNode createAddSubSystemOperation() {
        final ModelNode subsystem = new ModelNode();
        subsystem.get(OP).set(ADD);
        subsystem.get(OP_ADDR).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
        return subsystem;
    }

    public void initialize(ExtensionContext context) {
        log.debug("Activating Spring Extension");
        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME);
        final ModelNodeRegistration registration = subsystem.registerSubsystemModel(SUBSYSTEM_DESCRIPTION);
        registration.registerOperationHandler(ADD, SpringSubsystemAdd.INSTANCE, SUBSYSTEM_ADD_DESCRIPTION, false);
        registration.registerOperationHandler(DESCRIBE, SpringSubsystemDescribeHandler.INSTANCE, SpringSubsystemDescribeHandler.INSTANCE, false, OperationEntry.EntryType.PRIVATE);
        subsystem.registerXMLElementWriter(parser);
    }

    public void initializeParsers(ExtensionParsingContext context) {
        log.debug("Setting up parsers");
        context.setSubsystemXmlMapping(NAMESPACE, parser);
    }

    static class SpringSubsystemElementParser implements XMLElementReader<List<ModelNode>>,
            XMLElementWriter<SubsystemMarshallingContext> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
            ParseUtils.requireNoAttributes(reader);
            ParseUtils.requireNoContent(reader);
            final ModelNode update = new ModelNode();
            update.get(OP).set(ADD);
            update.get(OP_ADDR).add(SUBSYSTEM, SUBSYSTEM_NAME);
            list.add(createAddSubSystemOperation());
        }

        private static ModelNode createAddSubSystemOperation() {
            final ModelNode subsystem = new ModelNode();
            subsystem.get(OP).set(ADD);
            subsystem.get(OP_ADDR).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
            return subsystem;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeContent(final XMLExtendedStreamWriter writer, final SubsystemMarshallingContext context) throws
                XMLStreamException {
            //TODO seems to be a problem with empty elements cleaning up the queue in FormattingXMLStreamWriter.runAttrQueue
            context.startSubsystemElement(NAMESPACE, false);
            writer.writeEndElement();

        }
    }

    private static class SpringSubsystemDescribeHandler implements ModelQueryOperationHandler, DescriptionProvider {
        static final SpringSubsystemDescribeHandler INSTANCE = new SpringSubsystemDescribeHandler();
        @Override
        public OperationResult execute(OperationContext context, ModelNode operation, ResultHandler resultHandler) {
            ModelNode node = new ModelNode();
            node.add(createAddSubSystemOperation());

            resultHandler.handleResultFragment(Util.NO_LOCATION, node);
            resultHandler.handleResultComplete();
            return new BasicOperationResult();
        }

        @Override
        public ModelNode getModelDescription(Locale locale) {
            return CommonDescriptions.getSubsystemDescribeOperation(locale);
        }
    }

}
