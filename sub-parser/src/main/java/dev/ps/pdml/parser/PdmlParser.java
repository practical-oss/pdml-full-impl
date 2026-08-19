package dev.ps.pdml.parser;

import dev.ps.pdml.cmdnode.*;
import dev.ps.pdml.core.util.EscapeUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.location.*;
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
import dev.ps.pdml.cmdnode.scripting.context.DocScriptingContext;
import dev.ps.pdml.cmdnode.scripting.context.PdmlScriptingContext;
import dev.ps.pdml.cmdnode.types.PdmlType;
import dev.ps.pdml.cmdnode.types.PdmlTypes;
import dev.ps.pdml.parser.PdmlParserConstants.ScopeConfig;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.pjse.PjseConfig;
import dev.ps.pjse.util.scriptingenv.JavaScriptingEnvironmentWithFixedContext;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.argument.MutableStringArguments;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;
import static dev.ps.pdml.parser.PdmlParserConstants.*;

public class PdmlParser extends CorePdmlParser {


    private static final int DEFAULT_LOOKAHEAD = 500;


    // Instance Properties

    private final @NotNull PdmlTokenReader pdmlReader;
    @Override
    public @NotNull PdmlTokenReader pdmlReader() { return pdmlReader; }

    private final @NotNull PdmlParserConfig config;
    public @NotNull PdmlParserConfig config() { return config; }

    private final @NotNull GlobalCommandNodeExecutor commandNodeExecutor;
    /* TODO?
    public @NotNull CommandNodeExecutor commandNodeExecutor() {
        return commandNodeExecutor;
    }
     */

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
        @NotNull GlobalCommandNodeExecutor commandNodeExecutor ) {

        super ( pdmlReader, config );

        this.pdmlReader = pdmlReader;
        this.config = config;
        this.commandNodeExecutor = commandNodeExecutor;
        this.nodeSpecs = config.getNodeSpecs();
        this.types = config.getTypes();
        this.allowAttributesWithoutCaret = config.getAllowAttributesWithoutCaret();
        this.currentNamespacesInScope = new NodeNamespaces ( null );
    }

    private static @NotNull PdmlParser create (
        @NotNull CodePointReader cpReader,
        @NotNull PdmlParserConfig config ) throws IOException {

        DocScriptingContext docScriptingContext = new DocScriptingContext();
        PdmlScriptingContext scriptingContext = new PdmlScriptingContext (
            config.commandNodes(), docScriptingContext );
        JavaScriptingEnvironmentWithFixedContext<PdmlScriptingContext> scriptingEnvironment =
            new JavaScriptingEnvironmentWithFixedContext<> (
                scriptingContext, "ctx", PjseConfig.DEFAULT_CONFIG );

        GlobalCommandNodeExecutor commandNodeExecutor =   new DelegatingCommandNodeExecutor (
            config.commandNodes(), scriptingEnvironment );

        PdmlTokenReader pdmlReader = new PdmlTokenReader ( cpReader );
        docScriptingContext.setPdmlReader ( pdmlReader );

        return new PdmlParser ( pdmlReader, config, commandNodeExecutor );
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
    public void skipWhitespaceBeforeRootNode() throws IOException, PdmlException {

        while ( true ) {
            if ( pdmlReader.skipWhitespace() ) continue;
            if ( pdmlReader.isAtExtensionStartChar() ) {
                handleExtension ( null, null, DOCUMENT_START_CONFIG );
            } else {
                break;
            }
        }
    }

    @Override
    public void requireDocumentEnd() throws IOException, PdmlException {

        // Skip whitespace and comments
        while ( true ) {
            if ( pdmlReader.isAtEnd() ) break;
            if ( pdmlReader.skipWhitespace() ) continue;
            if ( pdmlReader.isAtString ( PdmlTokenReader.LINE_OR_BLOCK_COMMENT_EXTENSION_START ) ) {
                pdmlReader.skipExtensionStartChar();
                pdmlReader.skipLineOrBlockComment();
            } else {
                break;
            }
        }

        if ( pdmlReader.isNotAtEnd() ) {
            throw malformedErrorAtCurrentPosition ( "Text after the end of a PDML document is not allowed (except whitespace).", "END_OF_PDML_DOCUMENT_EXPECTED" );
        }
    }

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
        parser -> parser.parseStringLiteralOrNull ( TAG_CONFIG ) );
        if ( parsedTagOrNamespaceString == null ) {
            return null;
        }

        if ( ! pdmlReader.skipNamespaceSeparator() ) {
            return new NodeTag ( parsedTagOrNamespaceString.string(), parsedTagOrNamespaceString.location (), null, null );
        }

        @Nullable ParsedString<?> parsedTagString = parseWithTextRange (
            parser -> parser.parseStringLiteralOrNull ( TAG_CONFIG ) );
        if ( parsedTagString != null ) {
            return new NodeTag ( parsedTagString.string(), parsedTagString.location (),
                parsedTagOrNamespaceString.string(), parsedTagOrNamespaceString.location () );
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
            ? new TextLeaf ( parsedString.string(), parsedString.location () )
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
        return parseTextFragmentsAndIgnoreComments ( TEXT_LEAF_CONFIG );
    }

    public @Nullable String parseTrimmedTextLeafAsStringAndIgnoreComments() throws IOException, PdmlException {

        @Nullable String result = parseTextLeafAsStringAndIgnoreComments();
        return result != null ? WhitespaceUtil.trim ( result ) : null;
    }

    private @Nullable String parseTextFragmentsAndIgnoreComments (
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        StringBuilder result = new StringBuilder();
        consumeTextFragmentsAndComments (
            ( textFragment, location ) -> result.append ( textFragment ),
            null, scopeConfig );
        return result.isEmpty() ? null : result.toString();
    }

    public void consumeTextLeafFragmentsAndComments (
        @NotNull BiConsumer<String, TextPosition> textFragmentConsumer,
        @Nullable BiConsumer<String, TextPosition> commentConsumer ) throws IOException, PdmlException {

        consumeTextFragmentsAndComments ( textFragmentConsumer, commentConsumer, TEXT_LEAF_CONFIG );
    }

    private void consumeTextFragmentsAndComments (
        @NotNull BiConsumer<String, TextPosition> textFragmentConsumer,
        @Nullable BiConsumer<String, TextPosition> commentConsumer,
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        while ( true ) {
            if ( pdmlReader.isAtExtensionStartChar() ) {
                handleExtension ( textFragmentConsumer, commentConsumer, scopeConfig );
            } else {
                // TODO don't use readTextFragment; use readText or readTag, depending on currentScope
                // String textFragment = pdmlReader.readTextFragment ( endChars, invalidChars, charEscapeMap, true );
                TextPosition startPosition = pdmlReader.currentTextPosition();
                String textFragment = pdmlReader.readTextFragment (
                    scopeConfig.bareStringEndChars(), scopeConfig.bareStringInvalidChars(), scopeConfig.codePointEscapes(), true );
                if ( textFragment != null) {
                    textFragmentConsumer.accept ( textFragment, startPosition );
                } else {
                    break;
                }
            }
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

    private void handleExtension (
        @Nullable BiConsumer<String, TextPosition> textSegmentConsumer,
        @Nullable BiConsumer<String, TextPosition> commentConsumer,
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        assert pdmlReader.isAtExtensionStartChar();

        boolean hasCodeSegments = false;
        List<@NotNull ReaderResource> readerResources = new ArrayList<>();

        // Loop because there might be several extension nodes concatenated, e.g.:
        // ^[ins-file ...]^[ins-file ...]
        while ( pdmlReader.skipExtensionStartChar() ) {

            if ( pdmlReader.isAtEnd() ) {
                throw malformedErrorAtCurrentPosition (
                    "Expecting more characters to specify the extension.",
                    "INCOMPLETE_EXTENSION_SYNTAX" );
            }

            String textFragment = null;
            TextPosition textFragmentStartPosition = null;

            int currentCodePoint = pdmlReader.currentCodePoint();
            switch ( currentCodePoint ) {

                case LINE_OR_BLOCK_COMMENT_START_CHAR -> {
                    parseComment ( commentConsumer, scopeConfig );
                }

                case QUOTED_STRING_LITERAL_DELIMITER_CHAR, RAW_STRING_LITERAL_DELIMITER_CHAR -> {
                    textFragmentStartPosition = pdmlReader().currentTextPosition();
                    textFragment = parseStringLiteralExtension ( scopeConfig );
                }

                case CorePdmlConstants.NODE_START_CHAR -> {
                    textFragmentStartPosition = pdmlReader().currentTextPosition();
                    @Nullable CommandNodeResult result = commandNodeExecutor.executeCommand (
                        pdmlReader, this );
                    if ( result != null ) {
                        if ( ! result.escapeText() ) {
                            readerResources.add ( result.readerResource() );
                            hasCodeSegments = true;
                        } else {
                            textFragment = result.readerResource().readAll();
                        }
                    }
                }

                default -> {
                    throw malformedErrorAtCurrentPosition (
                        "Invalid character '" + Character.toString ( currentCodePoint ) + "'.",
                        "INVALID_EXTENSION_SYNTAX" );
                }
            }

            if ( textFragment != null && ! textFragment.isEmpty() ) {
                if ( ! hasCodeSegments && textSegmentConsumer != null ) {
                    textSegmentConsumer.accept ( textFragment, textFragmentStartPosition );
                } else {
                    readerResources.add ( new StringReaderResource ( EscapeUtil.toText ( textFragment ) ) );
                }
            }
        }

        // Insert in reverse order because 'insertReaderResource' INSERTS at the current position
        for ( ReaderResource readerResource : readerResources.reversed() ) {
            pdmlReader.insertReaderResource ( readerResource );
        }
    }


    // Comments

    private void parseComment (
        @Nullable BiConsumer<String, TextPosition> commentConsumer,
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        if ( ! scopeConfig.commentsAllowed() ) {
            throw malformedErrorAtCurrentPosition (
                "Comments are not allowed in this context.",
                "INVALID_COMMENT" );
        }

        // reader is at /
        if ( commentConsumer != null && ! config.isIgnoreComments() ) {
            TextPosition position = pdmlReader.currentTextPosition();
            String comment = pdmlReader.readLineOrBlockComment();
            assert comment != null;
            commentConsumer.accept ( comment, position );
        } else {
            boolean skipped = pdmlReader.skipLineOrBlockComment();
            assert skipped;
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

        MutableStringArguments builder = new MutableStringArguments ();
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

        return builder.toImmutableOrNull ();
    }

    private void requireAttributesEnd() throws IOException, InvalidPdmlDataException {

        if ( ! pdmlReader.skipAttributesEnd() ) {
            throw dataErrorAtCurrentPosition (
                "'" + ATTRIBUTES_END_CHAR + "' is required to end the attributes.",
                "ATTRIBUTES_END_REQUIRED" );
        }

        // skip optional space after attributes end
        pdmlReader.skipChar ( ' ' );
    }

    public @Nullable StringArgument parseAttribute() throws IOException, PdmlException {

        ParsedString<?> parsedName = parseWithTextRange (
            p -> p.parseEmptyableStringLiteral ( ATTRIBUTE_NAME_CONFIG ) );
        if ( parsedName == null ) {
            return null;
        }
        String name = parsedName.string();
        if ( name.isEmpty() ) {
            throw dataError (
                "Missing name. Null (empty) names are not allowed.",
                "INVALID_NULL_NAME",
                parsedName.location () );
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
        ParsedString<?> parsedValue = parseWithTextRange (
            p -> p.parseEmptyableStringLiteral ( ATTRIBUTE_VALUE_CONFIG ) );
        if ( parsedValue == null ) {
            throw malformedErrorAtCurrentPosition (
                "Expecting a value. A value cannot start with '" + pdmlReader.currentCodePointAsString() + "'.",
                "EXPECTING_STRING_VALUE" );
        }
        String value = parsedValue.string();
        if ( value.isEmpty() ) {
            value = null;
        }

        return new StringArgument ( name, value, parsedName.location (), parsedValue.location () );
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

            char endChar = PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_END_CHAR;
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
                TextLocation por = existingNamespace.namePrefixPositionOrRange();
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
        return parseStringLiteralOrNull ( BARE_STRING_LITERAL_CONFIG );
    }

    public @Nullable String parseEmptyableStringLiteralInTextLeaf()
        throws IOException, PdmlException {

        return parseEmptyableStringLiteral ( BARE_STRING_LITERAL_CONFIG );
    }

    private @Nullable String parseStringLiteralOrNull (
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        String result = parseEmptyableStringLiteral ( scopeConfig );
        if ( result != null && result.isEmpty() ) {
            return null;
        } else {
            return result;
        }
    }

    private @Nullable String parseEmptyableStringLiteral (
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        if ( pdmlReader.isAtChar ( QUOTED_STRING_LITERAL_DELIMITER_CHAR ) ) {
            if ( pdmlReader.isAtString ( MULTILINE_STRING_LITERAL_DELIMITER ) ) {
                return pdmlReader.requireMultilineStringLiteral ();
            } else {
                return parseQuotedStringLiteral();
            }
        } else if ( pdmlReader.isAtChar ( RAW_STRING_LITERAL_DELIMITER_CHAR ) ) {
            return pdmlReader.requireRawStringLiteral ();
        } else {
            // bare string literal
            return parseTextFragmentsAndIgnoreComments ( scopeConfig );
        }
    }

    /*
    private @Nullable String parseEmptyableStringLiteralWithRange (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull CurrentScope currentScope ) throws IOException, PdmlException {

        TextPosition startPosition = reader.currentTextPosition();
    }
     */

    private @NotNull String parseQuotedStringLiteral() throws IOException, PdmlException {

        final char delimiter = QUOTED_STRING_LITERAL_DELIMITER_CHAR;

        TextPosition startPosition = pdmlReader.currentTextPosition();
        pdmlReader.advanceChar(); // skip "

        // "" is used to denote a 'null' value
        if ( pdmlReader.skipChar ( delimiter ) ) {
            return "";
        }

        @Nullable String result = parseTextFragmentsAndIgnoreComments ( QUOTED_STRING_LITERAL_CONFIG );

        if ( ! pdmlReader.skipChar ( delimiter ) ) {
            throw dataError (
                "Expecting a subsequent " + delimiter + " to end the quoted string literal.",
                "CLOSING_QUOTE_REQUIRED",
                startPosition );
        }

        return result == null ? "" : result;
    }

    private @Nullable String parseStringLiteralExtension (
        @NotNull ScopeConfig scopeConfig ) throws IOException, PdmlException {

        if ( ! scopeConfig.stringLiteralsAllowed() ) {
            throw malformedErrorAtCurrentPosition (
                "String literal extensions are not allowed in this context.",
                "INVALID_COMMENT" );
        }

        String string;
        if ( pdmlReader.isAtChar ( QUOTED_STRING_LITERAL_DELIMITER_CHAR ) ) {
            if ( pdmlReader.isAtString ( PdmlExtensionsConstants.MULTILINE_STRING_LITERAL_DELIMITER ) ) {
                string = pdmlReader.requireMultilineStringLiteral ();
            } else {
                // string = parseQuotedStringLiteral ( CurrentScope.STRING_LITERAL );
                string = parseQuotedStringLiteral();
            }
        } else if ( pdmlReader.isAtChar ( RAW_STRING_LITERAL_DELIMITER_CHAR ) ) {
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

        TextLocation parentRange = pdmlReader.codePointReader().parentReaderPosition();

        // If the readerResource has changed then use only the start position,
        // otherwise use the range (start and end)
        TextLocation textLocation = readerResource == pdmlReader.currentResource()
            ? new FromToTextRangeWithCodePointOffsets (
                readerResource, startOffset, pdmlReader.currentCodePointOffset(), parentRange )
            : new TextPositionWithCodePointOffset ( readerResource, startOffset, parentRange );

        return new ParsedString<> ( string, textLocation );
    }

    public @Nullable String parseTextLeafAsTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        pdmlReader.skipWhitespaceAndComments();
        @Nullable String string;
        // TextToken textToken = reader.currentCharToken();
        if ( pdmlReader.isAtChar ( '"' ) ||
            pdmlReader.isAtChar ( '~' ) ) {
            string = parseStringLiteralOrNull ( BARE_STRING_LITERAL_CONFIG );
            pdmlReader.skipWhitespaceAndComments();
        } else {
            string = parseTrimmedTextLeafAsStringAndIgnoreComments();
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
                taggedNode.getTag().location () );
        }

        // type.parseValidateAndHandleObject ( this, taggedNode,false );
        type.parseValidateAndHandleObject (
            this,
            optimizeTypedNodes ? taggedNode : null,
            false );
    }


    // Error handling

    private InvalidPdmlDataException dataError (
        @NotNull String message, @NotNull String id, @Nullable TextLocation location ) {

        return new InvalidPdmlDataException ( message, id, location );
    }

    private InvalidPdmlDataException dataErrorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return dataError ( message, id, pdmlReader.currentTextPosition() );
    }
}
