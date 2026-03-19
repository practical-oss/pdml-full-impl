package dev.ps.pdml.parser;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.range.*;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.shared.text.unicode.reader.CodePointReader;
import dev.ps.shared.text.unicode.reader.InMemoryCodePointReader;
import dev.ps.shared.text.unicode.reader.StreamingCodePointReader;
import dev.ps.pdml.core.parser.CorePdmlParser;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.namespace.NodeNamespace;
import dev.ps.pdml.data.namespace.NodeNamespaces;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.nodespec.PdmlNodeSpec;
import dev.ps.pdml.data.nodespec.PdmlNodeSpecs;
import dev.ps.pdml.data.util.WhitespaceUtil;
import dev.ps.pdml.ext.DelegatingExtensionNodesHandler;
import dev.ps.pdml.ext.ExtensionNodeHandlers;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.pdml.ext.scripting.context.DocScriptingContext;
import dev.ps.pdml.ext.scripting.context.PdmlScriptingContext;
import dev.ps.pdml.ext.types.PdmlType;
import dev.ps.pdml.ext.types.PdmlTypes;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.pjse.PjseConfig;
import dev.ps.pjse.util.scriptingenv.JavaScriptingEnvironmentWithFixedContext;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.argument.StringArgumentsBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;

public class PdmlParser extends CorePdmlParser {


    // Private Static

    private static final @NotNull Set<Character> SHARED_BARE_STRING_END_CHARS =
        Set.of ( ' ', '\t', '\n', '\r', EXTENSION_START_CHAR );

    private static final @NotNull Set<Character> BARE_STRING_END_CHARS_IN_TAG =
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            CorePdmlConstants.NODE_END_CHAR,
            NAMESPACE_SEPARATOR_CHAR );

    private static final @NotNull Set<Character> BARE_STRING_END_CHARS_IN_TEXT_LEAF =
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            CorePdmlConstants.NODE_END_CHAR,
            CorePdmlConstants.NODE_START_CHAR );

    private static final @NotNull Set<Character> BARE_STRING_END_CHARS_IN_ATTRIBUTE_NAME =
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            ATTRIBUTE_ASSIGN_CHAR );

    private static final @NotNull Set<Character> BARE_STRING_END_CHARS_IN_ATTRIBUTE_VALUE =
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            ATTRIBUTES_END_CHAR,
            CorePdmlConstants.NODE_END_CHAR );

    private static final @NotNull Set<Character> TEXT_SNIPPET_END_CHARS =
        addToCharSet ( CorePdmlConstants.TEXT_LEAF_END_CHARS,
            EXTENSION_START_CHAR );

    private static final @NotNull Set<Character> QUOTED_STRING_LITERAL_SNIPPET_END_CHARS =
        addToCharSet ( QUOTED_STRING_LITERAL_END_CHARS,
            EXTENSION_START_CHAR );

    private static @NotNull Set<Character> addToCharSet (
        @NotNull Set<Character> charSet,
        char... chars ) {

        Set<Character> result = new HashSet<> ( charSet );
        for ( char c : chars ) {
            result.add ( c );
        }
        return Collections.unmodifiableSet ( result );
    }

    private static enum CurrentScope {
        TEXT_LEAF, NODE_TAG, ATTRIBUTE_NAME, ATTRIBUTE_VALUE, STRING_LITERAL
    }

    private static class PendingsChars {

        private final @NotNull StringBuilder chars;
        private @NotNull TextPosition startPosition;


        PendingsChars ( @NotNull TextPosition startPosition ) {
            this.chars = new StringBuilder();
            this.startPosition = startPosition;
        }


        boolean isEmpty() {
            return chars.isEmpty();
        }

        @Nullable String getString() {
            return chars.isEmpty() ? null : chars.toString();
        }

        void append ( @NotNull String string ) {
            chars.append ( string );
        }

        void reset ( @NotNull TextPosition startPosition ) {
            chars.delete ( 0, chars.length() );
            this.startPosition = startPosition;
        }
    }

    private static final int DEFAULT_LOOKAHEAD = 500;


    // Instance Properties

    private final @NotNull PdmlTokenReader pdmlReader;
    @Override
    public @NotNull PdmlTokenReader pdmlReader() { return pdmlReader; }

    private final @NotNull PdmlParserConfig config;
    public @NotNull PdmlParserConfig config() { return config; }

    private final @NotNull DelegatingExtensionNodesHandler extensionNodesHandler;
    public @NotNull DelegatingExtensionNodesHandler extensionNodesHandler () {
        return extensionNodesHandler;
    }

    private final @Nullable PdmlNodeSpecs nodeSpecs;

    private final @Nullable PdmlTypes types;

    private final boolean allowAttributesWithoutCaret;

    private final @NotNull NodeNamespaces currentNamespacesInScope;

    private boolean optimizeTypedNodes = true;
    public void setOptimizeTypedNodes ( boolean optimizeTypedNodes ) {
        this.optimizeTypedNodes = optimizeTypedNodes;
    }


    // Creators

    private PdmlParser (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParserConfig config,
        @NotNull DelegatingExtensionNodesHandler extensionNodesHandler ) {

        super ( pdmlReader, config );

        this.pdmlReader = pdmlReader;
        this.config = config;
        this.extensionNodesHandler = extensionNodesHandler;
        this.nodeSpecs = config.getNodeSpecs();
        this.types = config.getTypes();
        this.allowAttributesWithoutCaret = config.getAllowAttributesWithoutCaret();
        this.currentNamespacesInScope = new NodeNamespaces ( null );
    }

    private static @NotNull PdmlParser create (
        @NotNull CodePointReader cpReader,
        @NotNull PdmlParserConfig config ) throws IOException {

        DocScriptingContext docScriptingContext = new DocScriptingContext();
        PdmlScriptingContext scriptingContext = new PdmlScriptingContext ( docScriptingContext );
        JavaScriptingEnvironmentWithFixedContext<PdmlScriptingContext> scriptingEnvironment =
            new JavaScriptingEnvironmentWithFixedContext<> (
                scriptingContext, "ctx", PjseConfig.DEFAULT_CONFIG );
        DelegatingExtensionNodesHandler delegatingExtensionNodeHandler =
            new DelegatingExtensionNodesHandler (
                ExtensionNodeHandlers.STANDARD_HANDLERS, scriptingEnvironment );

        PdmlTokenReader pdmlReader = new PdmlTokenReader ( cpReader );
        docScriptingContext.setPdmlReader ( pdmlReader );

        return new PdmlParser ( pdmlReader, config, delegatingExtensionNodeHandler );
    }

    public static @NotNull PdmlParser create (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig config ) throws IOException {

        return create ( new InMemoryCodePointReader ( readerResource, null ), config );
    }

    public static @NotNull PdmlParser create (
        @NotNull Reader reader,
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig config ) throws IOException {

        return create ( new StreamingCodePointReader ( reader, readerResource, null ), config );
    }

    /*
    public static @NotNull PdmlParser create (
        @NotNull Reader reader,
        @NotNull PdmlParserConfig config ) throws IOException {

        return create ( reader, null, config );
    }
     */


    // Overridden Methods

    @Override
    public @Nullable TaggedNode parseTaggedNode() throws IOException, PdmlException {

        long startOffset = pdmlReader.currentCodePointOffset();

        TaggedNode taggedNode = parseFromNodeStartToAttributes();
        if ( taggedNode == null ) {
            return null;
        }

        if ( ! pdmlReader.skipNodeEnd() ) {
            parseChildNodes ( taggedNode );
            requireTaggedNodeEnd ( taggedNode );
        }

        // TODO? check if the ReaderResource is the same as for the node start (see parseWithTextRange)
        taggedNode.setTextLocation ( createTextRange ( startOffset ) );

        return taggedNode;
    }

    @Override
    public @Nullable NodeTag parseTag() throws IOException, PdmlException {

        @Nullable ParsedString<?> parsedTagOrNamespaceString = parseWithTextRange (
        parser -> parser.parseStringLiteralOrNull (
            BARE_STRING_END_CHARS_IN_TAG, CurrentScope.NODE_TAG ) );
        if ( parsedTagOrNamespaceString == null ) {
            return null;
        }

        if ( ! pdmlReader.skipNamespaceSeparator() ) {
            return new NodeTag ( parsedTagOrNamespaceString.string(), parsedTagOrNamespaceString.source(), null, null );
        }

        @Nullable ParsedString<?> parsedTagString = parseWithTextRange (
            parser -> parser.parseStringLiteralOrNull (
                BARE_STRING_END_CHARS_IN_TAG, CurrentScope.NODE_TAG ) );
        if ( parsedTagString != null ) {
            return new NodeTag ( parsedTagString.string(), parsedTagString.source(),
                parsedTagOrNamespaceString.string(), parsedTagOrNamespaceString.source() );
        } else {
            throw malformedErrorAtCurrentPosition (
                "Tag required after namespace prefix '" + parsedTagOrNamespaceString.string() + "'.",
                "TAG_REQUIRED" );
        }
    }

    @Override
    public @Nullable TextLeaf parseTextLeaf() throws IOException, PdmlException {

        @Nullable ParsedString<?> parsedString = parseWithTextRange (
            PdmlParser::parseTextLeafAsStringAndIgnoreComments );
        return parsedString != null
            ? new TextLeaf ( parsedString.string(), parsedString.source() )
            : null;

    }

    @Override
    public void parseChildNodes ( @NotNull TaggedNode parentNode ) throws IOException, PdmlException {

        while ( pdmlReader.isNotAtEnd() ) {

            if ( pdmlReader.isAtNodeEnd() ) {
                return;

            } else if ( pdmlReader.isAtNodeStart() ) {
                TaggedNode childNode = requireTaggedNode ();
                parentNode.appendChild ( childNode );

            } else {
                consumeTextLeafFragmentsAndComments (
                    ( text, location ) ->
                        parentNode.appendChild ( new TextLeaf ( text, location ) ),
                    ( comment, location ) ->
                        parentNode.appendChild ( new CommentLeaf ( comment, location ) ) );
            }
        }
    }

    @Override
    public void requireTaggedNodeEnd ( @NotNull TaggedNode taggedNode ) throws IOException, MalformedPdmlException {

        super.requireTaggedNodeEnd ( taggedNode );
        removeNamespacesInScope ( taggedNode.getNamespaceDefinitions() );
    }


    // Text Leaf

    public @Nullable String parseTextLeafAsStringAndIgnoreComments() throws IOException, PdmlException {

        return parseTextFragmentsAndIgnoreComments (
            CurrentScope.TEXT_LEAF,
            TEXT_SNIPPET_END_CHARS,
            CorePdmlConstants.INVALID_TEXT_LEAF_CHARS,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CODE_POINTS );
    }

    public @Nullable String parseTrimmedTextLeafAsStringAndIgnoreComments() throws IOException, PdmlException {

        @Nullable String result = parseTextLeafAsStringAndIgnoreComments ();
        if ( result == null ) {
            return null;
        } else {
            return WhitespaceUtil.trim ( result );
        }
    }

    public void consumeTextLeafFragmentsAndComments (
        @NotNull BiConsumer<String, TextPosition> textFragmentConsumer,
        @NotNull BiConsumer<String, TextPosition> commentConsumer ) throws IOException, PdmlException {

        consumeTextFragmentsAndComments (
            textFragmentConsumer,
            commentConsumer,
            CurrentScope.TEXT_LEAF,
            TEXT_SNIPPET_END_CHARS,
            CorePdmlConstants.INVALID_TEXT_LEAF_CHARS,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CODE_POINTS );
    }

    private @Nullable String parseTextFragmentsAndIgnoreComments (
        @NotNull CurrentScope currentScope,
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars,
        @NotNull Map<@NotNull Integer, @NotNull Integer> charEscapeMap ) throws IOException, PdmlException {

        StringBuilder result = new StringBuilder();
        consumeTextFragmentsAndComments (
            ( textFragment, location ) -> result.append ( textFragment ),
            ( comment, location ) -> {},
            currentScope, endChars, invalidChars, charEscapeMap );
        return result.isEmpty() ? null : result.toString();
    }

    private void consumeTextFragmentsAndComments (
        @NotNull BiConsumer<String, TextPosition> textFragmentConsumer,
        @NotNull BiConsumer<String, TextPosition> commentConsumer,
        @NotNull CurrentScope currentScope,
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars,
        @NotNull Map<@NotNull Integer, @NotNull Integer> charEscapeMap ) throws IOException, PdmlException {

        PendingsChars pendingChars = new PendingsChars ( pdmlReader.currentTextPosition() );

        while ( true ) {
            if ( pdmlReader.isAtExtensionStartChar() ) {
                handleExtension (
                    textFragmentConsumer, commentConsumer, pendingChars, currentScope );
            } else {
                // TODO don't use readTextFragment; use readText or readTag, depending on currentScope
                String textFragment = pdmlReader.readTextFragment ( endChars, invalidChars, charEscapeMap, true );
                if ( textFragment != null) {
                    pendingChars.chars.append ( textFragment );
                } else {
                    break;
                }
            }
        }

        if ( ! pendingChars.isEmpty() ) {
            textFragmentConsumer.accept ( pendingChars.getString(), pendingChars.startPosition );
            // charsSb.delete ( 0, charsSb.length() );
        }
    }


    // Parse From To

    private @Nullable TaggedNode parseFromNodeStartToAttributes() throws IOException, PdmlException {

        NodeTag tag = parseFromNodeStartToTag();
        if ( tag == null ) {
            return null;
        }

        TaggedNode taggedNode = new TaggedNode ( tag );

        boolean isEmptyNode = pdmlReader.isAtNodeEnd();
        if ( ! isEmptyNode ) {
            taggedNode.setSeparator ( requireSeparator() );
        }

        if ( ! isEmptyNode && pdmlReader.isAtNodeEnd() ) {
            // return taggedNode ;
            throw dataErrorAtCurrentPosition (
                "A node cannot be closed after a separator. A separator must be followed by node content (e.g. text or child nodes). Note: an empty node cannot have a separator.",
                "NODE_CONTENT_REQUIRED" );
        }

        PdmlNodeSpec nodeSpec = nodeSpecs == null ? null : nodeSpecs.getOrNull ( taggedNode.getTag () );
        taggedNode.setSpec ( nodeSpec );
        String typeName = nodeSpec == null ? null : nodeSpec.getTypeName();

        if ( typeName == null && isEmptyNode ) {
            return taggedNode;
        }

        if ( ! isEmptyNode ) {

            NodeNamespaces namespaces = parseNamespaces();
            if ( namespaces != null ) {
                taggedNode.setNamespaceDefinitions ( namespaces );
            }

            StringArguments attributes = parseAttributesForNodeSpec ( nodeSpec );
            if ( attributes != null ) {
                taggedNode.setStringAttributes ( attributes );
            }
        }

        if ( typeName != null ) {
            handleType ( typeName, taggedNode );
        }

        return taggedNode;
    }

    public @NotNull TaggedNode requireFromNodeStartToAttributes() throws IOException, PdmlException {

        // TODO? use general purpose require method
        @Nullable TaggedNode taggedNode = parseFromNodeStartToAttributes();
        if ( taggedNode != null ) {
            return taggedNode;
        } else {
            throw malformedErrorAtCurrentPosition (
                "Node required.", "NODE_REQUIRED" );
        }
    }



    // Extensions

    private boolean handleExtension (
        @NotNull BiConsumer<String, TextPosition> charsConsumer,
        @NotNull BiConsumer<String, TextPosition> commentConsumer,
        @NotNull PendingsChars pendingsChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        // Loop because there might be several extension nodes concatenated, e.g.:
        // ^u{set c=v}^{get c}
        while ( pdmlReader.currentCodePoint() == PdmlExtensionsConstants.EXTENSION_START_CHAR ) {

            int nextCodePoint = pdmlReader.peekNextCodePoint();
            if ( nextCodePoint == -1 ) {
                throw malformedErrorAtCurrentPosition (
                    "Expecting more characters to specify the extension.",
                    "INCOMPLETE_EXTENSION_SYNTAX" );
            }

            switch ( nextCodePoint ) {

                case SINGLE_OR_MULTI_LINE_COMMENT_START_CHAR -> {
                    if ( config.isIgnoreComments() ) {
                        parseComment ( commentConsumer, currentScope );
                    } else {
                        if ( ! pendingsChars.isEmpty() ) {
                            charsConsumer.accept ( pendingsChars.getString(), pendingsChars.startPosition );
                        }
                        parseComment ( commentConsumer, currentScope );
                        pendingsChars.reset ( pdmlReader.currentTextPosition() );
                    }
                }

                case QUOTED_STRING_LITERAL_DELIMITER_CHAR, RAW_STRING_LITERAL_START_CHAR -> {
                    String string = parseStringLiteralExtension ( currentScope );
                    if ( string != null ) {
                        pendingsChars.append ( string );
                    }
                }

                default -> {
                    @Nullable InsertReaderResourceExtensionResult result = extensionNodesHandler.handleExtensionNode (
                        pdmlReader, this );
                    if ( result != null ) {
                        // @Nullable String string = result.string();
                        // if ( string != null ) {
                            // if ( result.format() == InsertStringFormat.AS_IS ) {
                            if ( ! result.escapeText() ) {
                                // TODO reader.insertStringToRead ( string, result.readerResource() );
                                // reader.insertStringToRead ( string, (ReaderResource) result.readerResource () );
                                pdmlReader.insertReaderResource ( result.readerResource() );
                            } else {
                                // charsSb.append ( PdmlEscapeUtil.escapeNodeText ( string ) );
                                // pendingsChars.append ( string );
                                String text = result.readerResource().readAll();
                                if ( text != null ) {
                                    pendingsChars.append ( text );
                                }
                            }
                        // }
                    }
                }
            }
        }
        return false;
    }


    // Comments

    private void parseComment (
        @NotNull BiConsumer<String, TextPosition> commentConsumer,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        if ( currentScope != CurrentScope.TEXT_LEAF ) {
            throw malformedErrorAtCurrentPosition (
                "Comments are not allowed in this context.",
                "INVALID_COMMENT" );
        }

        if ( config.isIgnoreComments() ) {
            boolean skipped = pdmlReader.skipSingleOrMultilineComment();
            assert skipped;
        } else {
            TextPosition position = pdmlReader.currentTextPosition();
            String comment = pdmlReader.readSingleOrMultilineComment();
            assert comment != null;
            commentConsumer.accept ( comment, position );
        }
    }


    // Attributes

    public @Nullable StringArguments parseAttributes() throws IOException, PdmlException {

        if ( ! pdmlReader.skipAttributesExtensionStart() ) {
            return null;
        }

        StringArguments attributes = parseAttributesUntilEndChar (
            PdmlExtensionsConstants.ATTRIBUTES_END_CHAR );
        requireAttributesEnd();
        return attributes;
    }

    private @Nullable StringArguments parseAttributesForNodeSpec ( @Nullable PdmlNodeSpec nodeSpec )
        throws IOException, PdmlException {

        if ( nodeSpec != null && nodeSpec.hasOnlyAttributes() ) {
            return parseAttributesWithOptionalParenthesis();

        } else {

            pdmlReader.setMark ( DEFAULT_LOOKAHEAD );
            pdmlReader.skipWhitespace ();

            // Note: don't use reader.isAtString because setMark can't ba called twice in a row
            boolean hasAttributes = pdmlReader.skipAllWhileMatchesString ( ATTRIBUTES_EXTENSION_START );
            // boolean hasAttributes = reader.skipAllWhileMatchesString ( ATTRIBUTES_EXTENSION_START );
            if ( ! hasAttributes && allowAttributesWithoutCaret ) {
                hasAttributes = pdmlReader.skipChar ( ATTRIBUTES_START_CHAR );
            }

            if ( hasAttributes ) {
                // TODO Don't use removeMark(). Use goBackToMark()/reset() and skip whitespace again
                // reader.removeMark();
                pdmlReader.goBackToMark();
                pdmlReader.skipWhitespace();
                pdmlReader.skipChar ( EXTENSION_START_CHAR );
                pdmlReader.skipChar ( ATTRIBUTES_START_CHAR );

                StringArguments attributes = parseAttributesUntilEndChar ( ATTRIBUTES_END_CHAR );
                requireAttributesEnd();
                return attributes;
            } else {
                pdmlReader.goBackToMark();
                return null;
            }
        }
    }

    private @Nullable StringArguments parseAttributesWithOptionalParenthesis()
        throws IOException, PdmlException {

        boolean hasParenthesis = pdmlReader.skipAttributesExtensionStart(); // ^(
        if ( ! hasParenthesis && allowAttributesWithoutCaret ) {
            hasParenthesis = pdmlReader.skipAttributesStart(); // (
        }

        StringArguments attributes = parseAttributesUntilEndChar (
            hasParenthesis ? PdmlExtensionsConstants.ATTRIBUTES_END_CHAR : CorePdmlConstants.NODE_END_CHAR );

        if ( hasParenthesis ) {
            requireAttributesEnd();
        }

        return attributes;
    }

    private @Nullable StringArguments parseAttributesUntilEndChar ( char endChar )
        throws IOException, PdmlException {

        StringArgumentsBuilder builder = new StringArgumentsBuilder();
        boolean isFirstAttribute = true;

        while ( true ) {

            boolean whitespaceSkipped = pdmlReader.skipWhitespaceAndComments();

            if ( pdmlReader.isAtChar ( endChar ) || pdmlReader.isAtEnd() ) {
                break;
            }

            if ( ! isFirstAttribute && ! whitespaceSkipped ) {
                throw malformedErrorAtCurrentPosition (
                    "Whitespace (e.g. a single space) is required to separate name-value assignments.",
                    "ASSIGNMENT_SEPARATOR_REQUIRED" );
            }

            builder.append ( requireAttribute() );
            isFirstAttribute = false;
        }

        return builder.buildOrNull();
    }

    private void requireAttributesEnd() throws IOException {

        boolean hasEnd = pdmlReader.skipAttributesEnd();
        assert hasEnd;
        pdmlReader.skipChar ( ' ' );
    }

    public @Nullable StringArgument parseAttribute() throws IOException, PdmlException {

        ParsedString<?> parsedName = parseWithTextRange ( p -> p.parseEmptyableStringLiteral (
            BARE_STRING_END_CHARS_IN_ATTRIBUTE_NAME, CurrentScope.ATTRIBUTE_NAME ) );
        if ( parsedName == null ) {
            return null;
        }
        String name = parsedName.string();
        if ( name.isEmpty() ) {
            throw dataError (
                "Missing name. Null (empty) names are not allowed.",
                "INVALID_NULL_NAME",
                parsedName.source() );
        }

        // =
        pdmlReader.skipWhitespace();
        if ( ! pdmlReader.skipAttributeAssignChar() ) {
            throw malformedErrorAtCurrentPosition (
                "Expecting '" + ATTRIBUTE_ASSIGN_CHAR + "' to assign a value to '" + name + "'.",
                "MISSING_ASSIGN_CHAR" );
        }
        pdmlReader.skipWhitespace();

        // Value
        ParsedString<?> parsedValue = parseWithTextRange ( p -> p.parseEmptyableStringLiteral (
            BARE_STRING_END_CHARS_IN_ATTRIBUTE_VALUE, CurrentScope.ATTRIBUTE_VALUE ) );
        if ( parsedValue == null ) {
            throw malformedErrorAtCurrentPosition (
                "Expecting a value. A value cannot start with '" + pdmlReader.currentCodePointAsString() + "'.",
                "EXPECTING_STRING_VALUE" );
        }
        String value = parsedValue.string();
        if ( value.isEmpty() ) {
            value = null;
        }

        return new StringArgument ( name, value, parsedName.source(), parsedValue.source() );
    }

    public @NotNull StringArgument requireAttribute() throws IOException, PdmlException {

        return require (
            this::parseAttribute,
            "Name-value assignment (name = value) required.",
            "NAME_VALUE_ASSIGNMENT_REQUIRED" );
    }


    // Namespaces

    public @Nullable NodeNamespaces parseNamespaces()
        throws IOException, PdmlException {

        pdmlReader.setMark ( DEFAULT_LOOKAHEAD );
        pdmlReader.skipWhitespace();

        // TextPositionImpl startPosition = reader.currentTextPosition();
        // Note: don't use reader.isAtString because setMark can't ba called twice in a row
        boolean hasNamespaces = pdmlReader.skipAllWhileMatchesString ( NAMESPACE_DECLARATIONS_EXTENSION_START );

        if ( hasNamespaces ) {
            // reader.removeMark();
            pdmlReader.goBackToMark();
            pdmlReader.skipWhitespace();
            pdmlReader.skipString ( NAMESPACE_DECLARATIONS_EXTENSION_START );

            char endChar = PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_END;
            // @NotNull StringArguments attributes = parseAttributesBetweenDelimiters ( startPosition, endChar );
            @Nullable StringArguments attributes = parseAttributesUntilEndChar ( endChar );
            pdmlReader.skipChar ( endChar );
            pdmlReader.skipChar ( ' ' );
            if ( attributes == null ) {
                return null;
            }

            NodeNamespaces namespaces = attributesToNamespaces ( attributes );
            addNamespacesInScope ( namespaces );
            return namespaces;

        } else {
            pdmlReader.goBackToMark();
            return null;
        }
    }

    private void addNamespacesInScope ( @NotNull NodeNamespaces newNamespaces )
        throws InvalidPdmlDataException {

        Collection<NodeNamespace> list = newNamespaces.list();
        if ( list == null ) return;

        for ( NodeNamespace newNamespace : list ) {
            String prefix = newNamespace.namePrefix();

            if ( ! currentNamespacesInScope.containsNamespace ( newNamespace ) ) {
                currentNamespacesInScope.add ( newNamespace );

            } else {
                NodeNamespace existingNamespace = currentNamespacesInScope.getByPrefix ( prefix );

                String message = "Namespace '" +  prefix + "' has already been declared";
                TextRange por = existingNamespace.namePrefixPositionOrRange();
                // TextPosition position = por != null ? por.startLineColumn_OLD () : null;
                // if ( position != null ) message = message + " at" +
                //    StringConstants.OS_LINE_BREAK + position;
                if ( por != null ) {
                    message = message + " at " + por.startLineColumn ();
                }
                message = message + ".";

                throw dataError (
                    message,
                    "NAMESPACE_NOT_UNIQUE",
                    newNamespace.URIPositionOrRange() );
            }
        }
    }

    private void removeNamespacesInScope ( @Nullable NodeNamespaces namespaces ) {

        Collection<NodeNamespace> list = namespaces == null ? null : namespaces.list();
        if ( list == null ) return;

        for ( NodeNamespace namespace : list ) {
            if ( currentNamespacesInScope.containsNamespace ( namespace ) ) {
                currentNamespacesInScope.remove ( namespace );
            }
        }
    }

    private @NotNull NodeNamespaces attributesToNamespaces (
        @NotNull StringArguments attributes ) throws InvalidPdmlDataException {

        NodeNamespaces namespaces = new NodeNamespaces ( attributes.location() );

        // List<Parameter<String>> list = attributes.list();
        List<StringArgument> list = attributes.list();
        // if ( list == null ) return namespaces;

        // for ( Parameter<String> attribute : list ) {
        for ( StringArgument argument : list ) {
            // TODO check argument.value() != null
            String value = argument.value();
            if ( value == null ) {
                throw dataError (
                    "Invalid empty or null namespace.",
                    "INVALID_EMPTY_NAMESPACE",
                    argument.valueOrNameLocation() );
            }
            namespaces.add ( new NodeNamespace (
                argument.name(), argument.nameLocation(),
                value, argument.valueLocation() ) );
        }

        return namespaces;
    }


    // String Literals

    public @Nullable String parseStringLiteralOrNullInTextLeaf() throws IOException, PdmlException {
        return parseStringLiteralOrNull ( BARE_STRING_END_CHARS_IN_TEXT_LEAF, CurrentScope.TEXT_LEAF );
    }

    private @Nullable String parseStringLiteralOrNull (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        String result = parseEmptyableStringLiteral ( bareStringEndChars, currentScope );
        if ( result != null && result.isEmpty() ) {
            return null;
        } else {
            return result;
        }
    }

    /*
    private @Nullable ParsedString<FromToTextRange> parseStringLiteralOrNullWithRange (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        String result = parseEmptyableStringLiteral ( bareStringEndChars, currentScope );
        if ( result != null && result.isEmpty() ) {
            return null;
        } else {
            return result;
        }
    }
     */

    public @Nullable String parseEmptyableStringLiteralInTextLeaf()
        throws IOException, PdmlException {

        return parseEmptyableStringLiteral (
            BARE_STRING_END_CHARS_IN_TEXT_LEAF, CurrentScope.TEXT_LEAF );
    }

    private @Nullable String parseEmptyableStringLiteral (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        if ( pdmlReader.isAtChar ( QUOTED_STRING_LITERAL_DELIMITER_CHAR ) ) {
            if ( pdmlReader.isAtString ( MULTILINE_STRING_LITERAL_DELIMITER ) ) {
                return pdmlReader.requireMultilineStringLiteral ();
            } else {
                return parseQuotedStringLiteral ( currentScope );
            }
        } else if ( pdmlReader.isAtChar ( RAW_STRING_LITERAL_START_CHAR ) ) {
            return pdmlReader.requireRawStringLiteral ();
        } else {
            // bare string literal
            return parseTextFragmentsAndIgnoreComments (
                currentScope,
                // CorePdmlConstants.INVALID_TAG_CHARS,
                bareStringEndChars,
                CorePdmlConstants.INVALID_TAG_CHARS,
                CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CODE_POINTS );
        }
    }

    /*
    private @Nullable String parseEmptyableStringLiteralWithRange (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        TextPosition startPosition = reader.currentTextPosition();
    }
     */

    private @NotNull String parseQuotedStringLiteral (
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        final char delimiter = QUOTED_STRING_LITERAL_DELIMITER_CHAR;

        TextPosition startPosition = pdmlReader.currentTextPosition();
        pdmlReader.advanceChar(); // skip "

        // "" is used to denote a 'null' value
        if ( pdmlReader.skipChar ( delimiter ) ) {
            return "";
        }

        @Nullable String result = parseTextFragmentsAndIgnoreComments (
            currentScope,
            QUOTED_STRING_LITERAL_SNIPPET_END_CHARS,
            QUOTED_STRING_LITERAL_INVALID_CHARS,
            QUOTED_STRING_LITERAL_ESCAPE_CODE_POINTS );

        if ( ! pdmlReader.skipChar ( delimiter ) ) {
            throw dataError (
                "Missing closing " + delimiter + " to end the text. Text quoted with " + delimiter + " must be closed with a subsequent " + delimiter + ".",
                "CLOSING_QUOTE_REQUIRED",
                startPosition );
        }

        return result == null ? "" : result;
    }

    private @Nullable String parseStringLiteralExtension (
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        if ( currentScope != CurrentScope.TEXT_LEAF ) {
            throw malformedErrorAtCurrentPosition (
                "String literal extensions are not allowed in this context.",
                "INVALID_COMMENT" );
        }

        boolean ok = pdmlReader.skipExtensionStartChar ();
        assert ok;

        String string;
        if ( pdmlReader.isAtChar ( QUOTED_STRING_LITERAL_DELIMITER_CHAR ) ) {
            if ( pdmlReader.isAtString ( PdmlExtensionsConstants.MULTILINE_STRING_LITERAL_DELIMITER ) ) {
                string = pdmlReader.requireMultilineStringLiteral ();
            } else {
                string = parseQuotedStringLiteral ( CurrentScope.STRING_LITERAL );
            }
        } else if ( pdmlReader.isAtChar ( RAW_STRING_LITERAL_START_CHAR ) ) {
            string = pdmlReader.requireRawStringLiteral ();
        } else {
            throw new IllegalStateException ( "Unexpected string literal extension" );
        }

        return string.isEmpty() ? null : string;
    }


    // Convenience Methods

    public interface ParseStringMethodInvoker {
        @Nullable String parseString ( @NotNull PdmlParser parser ) throws IOException, PdmlException;
    }

    public @Nullable ParsedString<?> parseWithTextRange (
        @NotNull ParseStringMethodInvoker methodInvoker ) throws IOException, PdmlException {

        long startOffset = pdmlReader.currentCodePointOffset();
        ReaderResource readerResource = pdmlReader.currentResource();

        String string = methodInvoker.parseString ( this );
        if ( string == null ) {
            return null;
        }

        TextRange parentRange = pdmlReader.codePointReader().parentReaderPosition();

        // If the readerResource has changed then use only the start position,
        // otherwise use the range (start and end)
        TextRange textRange = readerResource == pdmlReader.currentResource()
            ? new FromToTextRangeWithCodePointOffsets (
                readerResource, startOffset, pdmlReader.currentCodePointOffset(), parentRange )
            : new TextPositionWithCodePointOffset ( readerResource, startOffset, parentRange );

        return new ParsedString<> ( string, textRange );
    }

    public @Nullable String parseTextLeafAsStringLiteral()
        throws IOException, PdmlException {

        return parseStringLiteralOrNull (
            BARE_STRING_END_CHARS_IN_TEXT_LEAF, CurrentScope.STRING_LITERAL );
    }

    public @Nullable String parseTrimmedTextLeafAsStringLiteral()
        throws IOException, PdmlException {

        pdmlReader.skipWhitespaceAndComments();
        String result = parseTextLeafAsStringLiteral();
        pdmlReader.skipWhitespaceAndComments();
        return result;
    }

    public @Nullable NullableParsedString<TextPosition> parseTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        pdmlReader.skipWhitespaceAndComments();
        TextPosition position = pdmlReader.currentTextPosition();
        @Nullable String string;
        if ( pdmlReader.isAtChar ( '"' ) ||
            pdmlReader.isAtChar ( '~' ) ) {
            string = parseStringLiteralOrNull (
                TEXT_SNIPPET_END_CHARS, CurrentScope.TEXT_LEAF );
            pdmlReader.skipWhitespaceAndComments();
        } else {
            string = parseTrimmedTextLeafAsStringAndIgnoreComments ();
        }
        return string != null ? new NullableParsedString<> ( string, position ) : null;
    }


    public @Nullable String parseTextLeafAsTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        pdmlReader.skipWhitespaceAndComments();
        @Nullable String string;
        // TextToken textToken = reader.currentCharToken();
        if ( pdmlReader.isAtChar ( '"' ) ||
            pdmlReader.isAtChar ( '~' ) ) {
            string = parseStringLiteralOrNull (
                TEXT_SNIPPET_END_CHARS, CurrentScope.TEXT_LEAF );
            pdmlReader.skipWhitespaceAndComments();
        } else {
            string = parseTrimmedTextLeafAsStringAndIgnoreComments ();
        }
        return string;
    }



    // Types

    private void handleType (
        @NotNull String typeName,
        @NotNull TaggedNode taggedNode ) throws IOException, PdmlException {

        PdmlType<?> type = types == null ? null : types.getOrNull ( typeName );
        if ( type == null ) {
            throw dataError (
                "Type '" + typeName + "' doesn't exist, but is assigned to node '" + taggedNode.getTag() + "'.",
                "INVALID_TYPE",
                taggedNode.getTag().startLocation() );
        }

        // type.parseValidateAndHandleObject ( this, taggedNode,false );
        type.parseValidateAndHandleObject (
            this,
            optimizeTypedNodes ? taggedNode : null,
            false );
    }


    // Error handling

    private MalformedPdmlException malformedErrorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return new MalformedPdmlException ( message, id, pdmlReader.currentTextPosition() );
    }

    private InvalidPdmlDataException dataError (
        @NotNull String message, @NotNull String id, @Nullable TextRange positionOrRange ) {

        return new InvalidPdmlDataException ( message, id, positionOrRange );
    }

    private InvalidPdmlDataException dataErrorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return dataError ( message, id, pdmlReader.currentTextPosition() );
    }
}
