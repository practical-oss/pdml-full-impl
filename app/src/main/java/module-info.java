module dev.ps.pdml {

    requires dev.ps.shared;
    requires dev.ps.pjse;
    requires dev.ps.prt;
    requires java.xml;
    requires jdk.javadoc;
    requires com.fasterxml.jackson.databind;

/*
    requires dev.pdml.core;
    requires dev.pdml.data;
    requires dev.pdml.html;
    requires dev.pdml.json;
    requires dev.pdml.parser;
    requires dev.pdml.utils;
    requires dev.pdml.writer;
    requires dev.pdml.xml;
 */
    exports dev.ps.pdml.core.parser;
    exports dev.ps.pdml.core.reader;
    exports dev.ps.pdml.core.util;
    exports dev.ps.pdml.core.writer;

    exports dev.ps.pdml.data;
    exports dev.ps.pdml.data.exception;
    exports dev.ps.pdml.data.namespace;
    exports dev.ps.pdml.data.node;
    exports dev.ps.pdml.data.node.leaf;
    exports dev.ps.pdml.data.node.tagged;
    exports dev.ps.pdml.data.nodespec;
    exports dev.ps.pdml.data.util;
    exports dev.ps.pdml.data.validation;

    exports dev.ps.pdml.html.treeview;

    exports dev.ps.pdml.json;

    exports dev.ps.pdml.ext;
    exports dev.ps.pdml.ext.scripting.context;
    exports dev.ps.pdml.ext.types;
    exports dev.ps.pdml.ext.types.instances;
    exports dev.ps.pdml.parser;
    exports dev.ps.pdml.parser.util;

    exports dev.ps.pdml.reader;

    exports dev.ps.pdml.utils;
    exports dev.ps.pdml.utils.lists;
    exports dev.ps.pdml.utils.networking.socket;
    exports dev.ps.pdml.utils.parser;
    exports dev.ps.pdml.utils.parameterizedtext;
    exports dev.ps.pdml.utils.scripting;
    exports dev.ps.pdml.utils.scriptingapidoc;
    exports dev.ps.pdml.utils.treewalker;
    exports dev.ps.pdml.utils.treewalker.handler;
    exports dev.ps.pdml.utils.treewalker.handler.impl;

    exports dev.ps.pdml.writer;
    exports dev.ps.pdml.writer.node;

    exports dev.ps.pdml.xml;
    exports dev.ps.pdml.xml.eventhandlers;

    exports dev.ps.pdml.companion.commands;
    exports dev.ps.pdml.companion.commands.xml;
    exports dev.ps.pdml.companion.commands.scripting;
    exports dev.ps.pdml.companion.commands.scriptingapidoc;
    exports dev.ps.pdml.companion.cli;
}
