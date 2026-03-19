package dev.ps.pdml.parser;

/*
import dev.pp.core.text.inspection.InvalidDataException;
import dev.pp.core.text.span.TextSpan;
import dev.pp.core.text.span.TextPosition;
import dev.pp.core.text.ioresource.reader.ReaderResource;
import dev.pp.core.text.reader.util.NonEmptyParsedString;
import dev.pp.pdml.core.parser.CorePdmlParser_OLD;
import dev.pp.pdml.data.CorePdmlConstants;
import dev.pp.pdml.data.attribute.NodeAttributes;
import dev.pp.pdml.data.exception.InvalidPdmlDataException;
import dev.pp.pdml.data.exception.PdmlException;
import dev.pp.pdml.data.namespace.NodeNamespaces;
import dev.pp.pdml.data.namespace.NodeNamespace;
import dev.pp.pdml.data.node.NodeTag;
import dev.pp.pdml.data.node.tagged.TaggedNode;
import dev.pp.pdml.data.node.leaf.CommentLeaf;
import dev.pp.pdml.data.node.leaf.TextLeaf;
import dev.pp.pdml.data.util.WhitespaceUtil;
import dev.pp.pdml.ext.DelegatingExtensionNodesHandler;
import dev.pp.pdml.ext.ExtensionNodeHandlers;
import dev.pp.pdml.ext.InsertReaderResourceExtensionResult;
import dev.pp.pdml.ext.scripting.context.DocScriptingContext;
import dev.pp.pdml.ext.scripting.context.PdmlScriptingContext;
import dev.pp.pdml.ext.types.PdmlType;
import dev.pp.pdml.data.nodespec.PdmlNodeSpec;
import dev.pp.pdml.data.nodespec.PdmlNodeSpecs;
import dev.pp.pdml.ext.types.PdmlTypes;
import dev.pp.pdml.data.PdmlExtensionsConstants;
import dev.pp.pdml.data.exception.MalformedPdmlException;
import dev.pp.pdml.parser.util.StringAssignmentsUtil;
import dev.pp.pdml.reader.PdmlReader_OLD;
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.pjse.PjseConfig;
import dev.pp.pjse.util.scriptingenv.JavaScriptingEnvironmentWithFixedContext;
import dev.pp.core.text.reader.CharReader;
import dev.pp.core.text.reader.CharReaderImpl;
import dev.pp.prt.argument.StringArgument;
import dev.pp.prt.argument.StringArguments;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;

import static dev.pp.pdml.data.PdmlExtensionsConstants.*;
 */

@Deprecated
public class PdmlParser_OLD {}

/*
public class PdmlParser_OLD extends CorePdmlParser_OLD {


    // Private Static

    private static final @NotNull Set<Character> SHARED_BARE_STRING_END_CHARS =
        Set.of ( ' ', '\t', '\n', '\r', EXTENSION_START_CHAR );

    private static final @NotNull Set<Character> BARE_STRING_END_CHARS_IN_TAG =
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            CorePdmlConstants.NODE_END_CHAR,
            NAMESPACE_SEPARATOR_CHAR );

    protected static final @NotNull Set<Character> BARE_STRING_END_CHARS_IN_TEXT_LEAF =
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
        addToCharSet ( CorePdmlConstants.TEXT_END_CHARS,
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

    protected enum CurrentScope {
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

    // TODO remove
    @Deprecated
    private record KeyValueStringPair(
        @NotNull String key,
        // @NotNull TextPosition keyStartPosition,
        @Nullable TextSpan keyPositionOrRange,
        @Nullable String value,
        // @NotNull TextPosition valueStartPosition,
        @Nullable TextSpan valuePositionOrRange ) {}

    private static final int DEFAULT_LOOKAHEAD = 500;


    // Instance Properties

    protected final @NotNull PdmlReader_OLD reader;
    public @NotNull PdmlReader_OLD getPdmlReader() { return reader; }

    private final @NotNull PdmlParserConfig config;
    public @NotNull PdmlParserConfig getConfig() { return config; }

    private final @NotNull DelegatingExtensionNodesHandler extensionNodesHandler;
    public @NotNull DelegatingExtensionNodesHandler getExtensionNodesHandler () {
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

    protected PdmlParser_OLD (
        @NotNull PdmlReader_OLD reader,
        @NotNull PdmlParserConfig config,
        @NotNull DelegatingExtensionNodesHandler extensionNodesHandler ) {

        super ( reader, config );

        this.reader = reader;
        this.config = config;
        this.extensionNodesHandler = extensionNodesHandler;
        this.nodeSpecs = config.getNodeSpecs();
        this.types = config.getTypes();
        this.allowAttributesWithoutCaret = config.getAllowAttributesWithoutCaret();
        this.currentNamespacesInScope = new NodeNamespaces ( null );
    }

    public static @NotNull PdmlParser_OLD create (
        @NotNull CharReader charReader,
        @NotNull PdmlParserConfig config ) throws IOException {

        DocScriptingContext docScriptingContext = new DocScriptingContext();
        PdmlScriptingContext scriptingContext = new PdmlScriptingContext ( docScriptingContext );
        JavaScriptingEnvironmentWithFixedContext<PdmlScriptingContext> scriptingEnvironment =
            new JavaScriptingEnvironmentWithFixedContext<> (
                scriptingContext, "ctx", PjseConfig.DEFAULT_CONFIG );
        DelegatingExtensionNodesHandler delegatingExtensionNodeHandler =
            new DelegatingExtensionNodesHandler (
                ExtensionNodeHandlers.STANDARD_HANDLERS, scriptingEnvironment );

        PdmlReader_OLD pdmlReader = new PdmlReader_OLD ( charReader );
        docScriptingContext.setPdmlReader ( pdmlReader );

        return new PdmlParser_OLD ( pdmlReader, config, delegatingExtensionNodeHandler );
    }

    public static @NotNull PdmlParser_OLD create (
        @NotNull Reader reader,
        @Nullable ReaderResource readerResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber,
        @NotNull PdmlParserConfig config ) throws IOException {

        return create (
            CharReaderImpl.createAndAdvance ( reader, readerResource, currentLineNumber, currentColumnLineNumber ),
            config );
    }

    public static @NotNull PdmlParser_OLD create (
        @NotNull Reader reader,
        // TODO? @NotNull
        @Nullable ReaderResource readerResource,
        @NotNull PdmlParserConfig config ) throws IOException {

        return create ( reader, readerResource, null, null, config );
    }


    // Node

    @Override
    public @Nullable TaggedNode parseTaggedNode() throws IOException, PdmlException {

        TaggedNode taggedNode = parseTaggedNodeStartUntilAttributes ();
        if ( taggedNode == null ) {
            return null;
        }

        if ( parseTaggedNodeEnd ( taggedNode ) ) {
            // empty node
            return taggedNode;
        }

        parseChildNodes ( taggedNode );
        requireTaggedNodeEnd ( taggedNode );

        return taggedNode;
    }

    private @Nullable TaggedNode parseTaggedNodeStartUntilAttributes () throws IOException, PdmlException {

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag ();
        if ( taggedNode == null ) {
            return null;
        }

        boolean isEmptyNode = reader.isAtNodeEnd();
        if ( ! isEmptyNode ) {
            taggedNode.setSeparator ( requireSeparator() );
        }

        if ( ! isEmptyNode && reader.isAtNodeEnd() ) {
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

            NodeAttributes attributes = parseAttributesForNodeSpec ( nodeSpec );
            if ( attributes != null ) {
                taggedNode.setStringAttributes ( attributes );
            }
        }

        if ( typeName != null ) {
            handleType ( typeName, taggedNode );
        }

        return taggedNode;
    }

    public @NotNull TaggedNode requireTaggedNodeStartUntilAttributes () throws IOException, PdmlException {

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartUntilAttributes ();
        if ( taggedNode != null ) {
            return taggedNode;
        } else {
            throw malformedErrorAtCurrentPosition (
                "Node required.", "NODE_REQUIRED" );
        }
    }

    @Override
    public void requireTaggedNodeEnd ( @NotNull TaggedNode taggedNode ) throws IOException, MalformedPdmlException {

        super.requireTaggedNodeEnd ( taggedNode );
        removeNamespacesInScope ( taggedNode.getNamespaceDefinitions() );
    }


    // Tag

    @Override
    public @Nullable NodeTag parseTag() throws IOException, PdmlException {

        TextPosition tagOrNamespacePosition = reader.currentPosition();
        String tagOrNamespacePrefix = parseStringLiteralOrNull (
            BARE_STRING_END_CHARS_IN_TAG, CurrentScope.NODE_TAG );
        if ( tagOrNamespacePrefix == null ) {
            return null;
        }

        if ( ! reader.skipNamespaceSeparator() ) {
            return new NodeTag ( tagOrNamespacePrefix, tagOrNamespacePosition, null, null );
        }

        TextPosition tagPosition = reader.currentPosition();
        String tag = parseStringLiteralOrNull (
            BARE_STRING_END_CHARS_IN_TAG, CurrentScope.NODE_TAG );
        if ( tag != null ) {
            return new NodeTag ( tag, tagPosition, tagOrNamespacePrefix, tagOrNamespacePosition );
        } else {
            throw malformedErrorAtCurrentPosition (
                "Tag required after namespace prefix '" + tagOrNamespacePrefix + "'.",
                "TAG_REQUIRED" );
        }
    }

    public @Nullable NodeTag parseNodeStartAndTag() throws IOException, PdmlException {

        if ( ! reader.readNodeStart() ) return null;
        return requireTag();
    }

    public @NotNull NodeTag requireNodeStartAndTag() throws IOException, PdmlException {

        @Nullable NodeTag tag = parseNodeStartAndTag();
        if ( tag == null ) {
            throw malformedErrorAtCurrentPosition ( "Node start required.", "NODE_START_REQUIRED" );
        }
        return tag;
    }


    // Text Leaf

    public @Nullable String parseTrimmedTextAndIgnoreComments() throws IOException, PdmlException {

        @Nullable String result = parseTextAndIgnoreComments();
        if ( result == null ) {
            return null;
        } else {
            return WhitespaceUtil.trim ( result );
        }
    }

    public @Nullable String parseTextAndIgnoreComments() throws IOException, PdmlException {

        return parseCharsAndExtensionsAndIgnoreComments (
            CurrentScope.TEXT_LEAF,
            TEXT_SNIPPET_END_CHARS,
            CorePdmlConstants.INVALID_TEXT_CHARS,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS );
    }

    private @Nullable String parseCharsAndExtensionsAndIgnoreComments (
        @NotNull CurrentScope currentScope,
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars,
        @NotNull Map<Character,Character> charEscapeMap ) throws IOException, PdmlException {

        StringBuilder result = new StringBuilder();
        parseCharsCommentsAndExtensions (
            ( text, location ) -> result.append ( text ),
            ( comment, location ) -> {},
            currentScope, endChars, invalidChars, charEscapeMap );
        return result.isEmpty() ? null : result.toString();
    }

    public void parseTextsCommentsAndExtensions (
        @NotNull BiConsumer<String, TextPosition> textConsumer,
        @NotNull BiConsumer<String, TextPosition> commentConsumer ) throws IOException, PdmlException {

        parseCharsCommentsAndExtensions (
            textConsumer,
            commentConsumer,
            CurrentScope.TEXT_LEAF,
            TEXT_SNIPPET_END_CHARS,
            CorePdmlConstants.INVALID_TEXT_CHARS,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS );
    }

    private void parseCharsCommentsAndExtensions (
        @NotNull BiConsumer<String, TextPosition> charsConsumer,
        @NotNull BiConsumer<String, TextPosition> commentConsumer,
        @NotNull CurrentScope currentScope,
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars,
        @NotNull Map<Character,Character> charEscapeMap ) throws IOException, PdmlException {

        PendingsChars pendingChars = new PendingsChars ( reader.currentPosition() );

        while ( true ) {
            if ( reader.isAtExtensionStart() ) {
                handleExtension (
                    charsConsumer, commentConsumer, pendingChars, currentScope );
            } else {
                // TODO don't use readTagOrText; use readText or readTag, depending on currentScope
                String chars = reader.readTagOrText ( endChars, invalidChars, charEscapeMap, true );
                if ( chars != null) {
                    pendingChars.chars.append ( chars );
                } else {
                    break;
                }
            }
        }

        if ( ! pendingChars.isEmpty() ) {
            charsConsumer.accept ( pendingChars.getString(), pendingChars.startPosition );
            // charsSb.delete ( 0, charsSb.length() );
        }
    }


    // Child Nodes

    @Override
    public void parseChildNodes ( @NotNull TaggedNode parentNode ) throws IOException, PdmlException {

        while ( reader.isNotAtEnd() ) {

            if ( reader.isAtNodeEnd() ) {
                return;

            } else if ( reader.isAtNodeStart() ) {
                TaggedNode childNode = requireTaggedNode ();
                parentNode.appendChild ( childNode );

            } else {
                parseTextsCommentsAndExtensions (
                    ( text, location ) ->
                        parentNode.appendChild ( new TextLeaf ( text, location ) ),
                    ( comment, location ) ->
                        parentNode.appendChild ( new CommentLeaf ( comment, location ) ) );
            }
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
        while ( reader.currentChar() == PdmlExtensionsConstants.EXTENSION_START_CHAR ) {

            @Nullable Character nextChar = reader.peekNextChar();
            if ( nextChar == null ) {
                throw malformedErrorAtCurrentPosition (
                    "Expecting more characters to specify the extension.",
                    "INCOMPLETE_EXTENSION_SYNTAX" );
            }

            switch ( nextChar ) {

                case SINGLE_OR_MULTI_LINE_COMMENT_START_CHAR -> {
                    if ( config.isIgnoreComments() ) {
                        parseComment ( commentConsumer, currentScope );
                    } else {
                        if ( ! pendingsChars.isEmpty() ) {
                            charsConsumer.accept ( pendingsChars.getString(), pendingsChars.startPosition );
                        }
                        parseComment ( commentConsumer, currentScope );
                        pendingsChars.reset ( reader.currentPosition() );
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
                        reader, this );
                    if ( result != null ) {
                        @Nullable String string = result.string();
                        if ( string != null ) {
                            // if ( result.format() == InsertStringFormat.AS_IS ) {
                            if ( ! result.escapeText() ) {
                                // TODO reader.insertStringToRead ( string, result.readerResource() );
                                reader.insertStringToRead ( string, (ReaderResource) result.readerResource () );
                            } else {
                                // charsSb.append ( PdmlEscapeUtil.escapeNodeText ( string ) );
                                pendingsChars.append ( string );
                            }
                        }
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
            boolean skipped = reader.skipSingleOrMultilineComment();
            assert skipped;
        } else {
            TextPosition position = reader.currentPosition();
            String comment = reader.readSingleOrMultilineComment();
            assert comment != null;
            commentConsumer.accept ( comment, position );
        }
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

    public @Nullable String parseEmptyableStringLiteralInTextLeaf()
        throws IOException, PdmlException {

        return parseEmptyableStringLiteral (
            BARE_STRING_END_CHARS_IN_TEXT_LEAF, CurrentScope.TEXT_LEAF );
    }

    protected @Nullable String parseEmptyableStringLiteral (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        if ( reader.isAtChar ( QUOTED_STRING_LITERAL_DELIMITER_CHAR ) ) {
            if ( reader.isAtString ( MULTILINE_STRING_LITERAL_DELIMITER ) ) {
                return reader.readMultilineStringLiteral();
            } else {
                return parseQuotedStringLiteral ( currentScope );
            }
        } else if ( reader.isAtChar ( RAW_STRING_LITERAL_START_CHAR ) ) {
            return reader.readRawStringLiteral();
        } else {
            // bare string literal
            return parseCharsAndExtensionsAndIgnoreComments (
                currentScope,
                // CorePdmlConstants.INVALID_TAG_CHARS,
                bareStringEndChars,
                CorePdmlConstants.INVALID_TAG_CHARS,
                CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS );
        }
    }

    private @NotNull String parseQuotedStringLiteral (
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        final char delimiter = QUOTED_STRING_LITERAL_DELIMITER_CHAR;

        TextPosition startPosition = reader.currentPosition();
        reader.advanceChar(); // skip "

        // "" is used to denote a 'null' value
        if ( reader.skipChar ( delimiter ) ) {
            return "";
        }

        @Nullable String result = parseCharsAndExtensionsAndIgnoreComments (
            currentScope,
            QUOTED_STRING_LITERAL_SNIPPET_END_CHARS,
            QUOTED_STRING_LITERAL_INVALID_CHARS,
            QUOTED_STRING_LITERAL_ESCAPE_MAP );

        if ( ! reader.skipChar ( delimiter ) ) {
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

        boolean ok = reader.readExtensionStartChar();
        assert ok;

        String string;
        if ( reader.isAtChar ( QUOTED_STRING_LITERAL_DELIMITER_CHAR ) ) {
            if ( reader.isAtString ( PdmlExtensionsConstants.MULTILINE_STRING_LITERAL_DELIMITER ) ) {
                string = reader.readMultilineStringLiteral();
            } else {
                string = parseQuotedStringLiteral ( CurrentScope.STRING_LITERAL );
            }
        } else if ( reader.isAtChar ( RAW_STRING_LITERAL_START_CHAR ) ) {
            string = reader.readRawStringLiteral();
        } else {
            throw new IllegalStateException ( "Unexpected string literal extension" );
        }

        return string.isEmpty() ? null : string;
    }


    // parseTextLeafAsXXX Convenience Methods

    // String Literal Convenience Methods

    public @Nullable String parseTextLeafAsStringLiteral()
        throws IOException, PdmlException {

        return parseStringLiteralOrNull (
            BARE_STRING_END_CHARS_IN_TEXT_LEAF, CurrentScope.STRING_LITERAL );
    }

    public @Nullable String parseTrimmedTextLeafAsStringLiteral()
        throws IOException, PdmlException {

        reader.skipWhitespaceAndComments();
        String result = parseTextLeafAsStringLiteral();
        reader.skipWhitespaceAndComments();
        return result;
    }

    public @Nullable NonEmptyParsedString parseTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        reader.skipWhitespaceAndComments();
        TextPosition position = reader.currentPosition();
        @Nullable String string;
        if ( reader.isAtChar ( '"' ) ||
            reader.isAtChar ( '~' ) ) {
            string = parseStringLiteralOrNull (
                TEXT_SNIPPET_END_CHARS, CurrentScope.TEXT_LEAF );
            reader.skipWhitespaceAndComments();
        } else {
            string = parseTrimmedTextAndIgnoreComments();
        }
        return string != null ? new NonEmptyParsedString ( string, position ) : null;
    }


    public @Nullable String parseTextLeafAsTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        reader.skipWhitespaceAndComments();
        @Nullable String string;
        // TextToken textToken = reader.currentCharToken();
        if ( reader.isAtChar ( '"' ) ||
            reader.isAtChar ( '~' ) ) {
            string = parseStringLiteralOrNull (
                TEXT_SNIPPET_END_CHARS, CurrentScope.TEXT_LEAF );
            reader.skipWhitespaceAndComments();
        } else {
            string = parseTrimmedTextAndIgnoreComments();
        }
        return string;
    }


    // StringArguments


    // name = value
    public @Nullable StringArgument parseStringArgumentAssignmentForAttributes (
        boolean allowNullValues ) throws IOException, PdmlException {

        // Name
        TextPosition namePosition = reader.currentPosition();
        @Nullable String name = parseEmptyableStringLiteral (
            BARE_STRING_END_CHARS_IN_ATTRIBUTE_NAME, CurrentScope.ATTRIBUTE_NAME );
        if ( name == null ) {
            return null;
        }
        if ( name.isEmpty() ) {
            throw dataError (
                "Missing name. Null names are not allowed.",
                "INVALID_NULL_NAME",
                namePosition );
        }

        // =
        reader.skipWhitespace();
        if ( ! reader.skipChar ( ATTRIBUTE_ASSIGN_CHAR ) ) {
            throw malformedErrorAtCurrentPosition (
                "Expecting '" + ATTRIBUTE_ASSIGN_CHAR + "' to assign a value to '" + name + "'.",
                "MISSING_ASSIGN_CHAR" );
        }
        reader.skipWhitespace();

        // Value
        TextPosition valuePosition = reader.currentPosition();
        @Nullable String value = parseEmptyableStringLiteral (
            BARE_STRING_END_CHARS_IN_ATTRIBUTE_VALUE, CurrentScope.ATTRIBUTE_VALUE );
        if ( value == null ) {
            throw malformedErrorAtCurrentPosition (
                "Expecting a value. A value cannot start with '" + reader.currentChar() + "'.",
                "EXPECTING_STRING_VALUE" );
        }
        if ( value.isEmpty() ) {
            value = null;
        }
        if ( value == null && ! allowNullValues ) {
            throw dataError (
                "Null cannot be assigned to '" + name + "'. Null values are not allowed.",
                "INVALID_NULL_VALUE",
                valuePosition );
        }

        return new StringArgument ( name, value, namePosition, valuePosition );
    }


    // Attributes

    public @Nullable NodeAttributes parseAttributes() throws IOException, PdmlException {

        if ( ! reader.readAttributesExtensionStart() ) {
            return null;
        }

        NodeAttributes attributes = parseAttributesUntilEndChar (
            PdmlExtensionsConstants.ATTRIBUTES_END_CHAR );
        requireAttributesEnd();
        return attributes;
    }

    private @Nullable NodeAttributes parseAttributesForNodeSpec ( @Nullable PdmlNodeSpec nodeSpec )
        throws IOException, PdmlException {

        if ( nodeSpec != null && nodeSpec.hasOnlyAttributes() ) {
            return parseAttributesWithOptionalParenthesis();

        } else {

            reader.setMark ( DEFAULT_LOOKAHEAD );
            reader.skipWhitespace ();

            // Note: don't use reader.isAtString because setMark can't ba called twice in a row
            boolean hasAttributes = reader.skipAllWhileCharsMatch ( ATTRIBUTES_EXTENSION_START );
            // boolean hasAttributes = reader.skipAllWhileMatchesString ( ATTRIBUTES_EXTENSION_START );
            if ( ! hasAttributes && allowAttributesWithoutCaret ) {
                hasAttributes = reader.skipChar ( ATTRIBUTES_START_CHAR );
            }

            if ( hasAttributes ) {
                // TODO Don't use removeMark(). Use goBackToMark()/reset() and skip whitespace again
                reader.removeMark();
                NodeAttributes attributes = parseAttributesUntilEndChar ( ATTRIBUTES_END_CHAR );
                requireAttributesEnd();
                return attributes;
            } else {
                reader.goBackToMark();
                return null;
            }
        }
    }

    private @Nullable NodeAttributes parseAttributesWithOptionalParenthesis()
        throws IOException, PdmlException {

        boolean hasParenthesis = reader.readAttributesExtensionStart(); // ^(
        if ( ! hasParenthesis && allowAttributesWithoutCaret ) {
            hasParenthesis = reader.readAttributesStart(); // (
        }

        NodeAttributes attributes = parseAttributesUntilEndChar (
            hasParenthesis ? PdmlExtensionsConstants.ATTRIBUTES_END_CHAR : CorePdmlConstants.NODE_END_CHAR );

        if ( hasParenthesis ) {
            requireAttributesEnd();
        }

        return attributes;
    }

    private @Nullable NodeAttributes parseAttributesUntilEndChar ( char endChar )
        throws IOException, PdmlException {

        // reader.skipWhitespaceAndComments();
        // @Nullable StringArguments stringArguments =
        //    parseStringArgumentAssignments ( true, endChar );
        // return stringArguments != null ? new NodeAttributes ( stringArguments ) : null;
        try {
            @Nullable StringArguments stringArguments =
                StringAssignmentsUtil.parseAsStringArguments ( this, true );
            return stringArguments != null ? new NodeAttributes ( stringArguments ) : null;
        } catch ( InvalidDataException e ) {
            throw new InvalidPdmlDataException (
                e.getMessage(), e.id (), e.location (), e );
        }
    }

    private void requireAttributesEnd() throws IOException {

        boolean hasEnd = reader.readAttributesEnd();
        assert hasEnd;
        reader.skipChar ( ' ' );
    }


    // Namespaces

    public @Nullable NodeNamespaces parseNamespaces()
        throws IOException, PdmlException {

        reader.setMark ( DEFAULT_LOOKAHEAD );
        reader.skipWhitespace ();

        // TextPositionImpl startPosition = reader.currentPosition();
        // Note: don't use reader.isAtString because setMark can't ba called twice in a row
        boolean hasNamespaces = reader.skipAllWhileCharsMatch ( NAMESPACE_DECLARATIONS_EXTENSION_START );

        if ( hasNamespaces ) {
            reader.removeMark();
            char endChar = PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_END;
            // @NotNull NodeAttributes attributes = parseAttributesBetweenDelimiters ( startPosition, endChar );
            @Nullable NodeAttributes attributes = parseAttributesUntilEndChar ( endChar );
            reader.skipChar ( endChar );
            reader.skipChar ( ' ' );
            if ( attributes == null ) {
                return null;
            }

            NodeNamespaces namespaces = attributesToNamespaces ( attributes );
            addNamespacesInScope ( namespaces );
            return namespaces;

        } else {
            reader.goBackToMark();
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
                TextSpan por = existingNamespace.namePrefixPositionOrRange();
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

    private static @NotNull NodeNamespaces attributesToNamespaces (
        @NotNull NodeAttributes attributes ) {

        NodeNamespaces namespaces = new NodeNamespaces ( attributes.location() );

        // List<Parameter<String>> list = attributes.list();
        List<StringArgument> list = attributes.list();
        // if ( list == null ) return namespaces;

        // for ( Parameter<String> attribute : list ) {
        for ( StringArgument argument : list ) {
            // TODO check argument.value() != null
            namespaces.add ( new NodeNamespace (
                argument.name(), argument.nameLocation(),
                argument.value(), argument.valueLocation() ) );
        }

        return namespaces;
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

    protected MalformedPdmlException malformedErrorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return new MalformedPdmlException ( message, id, reader.currentPosition() );
    }

    protected InvalidPdmlDataException dataError (
        @NotNull String message, @NotNull String id, @Nullable TextSpan positionOrRange ) {

        return new InvalidPdmlDataException ( message, id, positionOrRange );
    }

    protected InvalidPdmlDataException dataErrorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return dataError ( message, id, reader.currentPosition() );
    }
}

 */
