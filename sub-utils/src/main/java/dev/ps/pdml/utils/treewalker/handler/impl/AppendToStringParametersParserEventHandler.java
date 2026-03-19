package dev.ps.pdml.utils.treewalker.handler.impl;

@Deprecated
public class AppendToStringParametersParserEventHandler{}

/*
public class AppendToStringParametersParserEventHandler
    implements PdmlTreeWalkerEventHandler<TaggedNodeStartEvent, MutableParameters<String>> {


    private final @NotNull MutableParameters<String> stringParameters;
    private @Nullable TaggedNodeStartEvent rootNode;
    private @Nullable TextToken currentNameToken;


    public AppendToStringParametersParserEventHandler (
        @NotNull MutableParameters<String> stringParameters ) {

        this.stringParameters = stringParameters;
        this.rootNode = null;
        this.currentNameToken = null;
    }


    @Override
    public @NotNull TaggedNodeStartEvent onRootNodeStart ( @NotNull TaggedNodeStartEvent startEvent ) throws PdmlException {

        rootNode = startEvent;
        addAttributes ( startEvent.attributes(), startEvent );
        return rootNode;
    }

    @Override
    public @NotNull TaggedNodeStartEvent onTaggedNodeStart ( @NotNull TaggedNodeStartEvent startEvent, @NotNull TaggedNodeStartEvent parentStartEvent ) throws PdmlException {

        if ( parentStartEvent != rootNode ) {
            throw new PdmlException (
                "Node '" + parentStartEvent.tag().qualifiedTag() + "' cannot have child nodes.",
                "CHILD_NODE_NOT_ALLOWED",
                startEvent.tag().tagPositionOrRange() );
        }

        @NotNull String name = startEvent.tag ().qualifiedTag ();
        if ( stringParameters.containsName ( name ) ) {
            throw new PdmlException (
                "Node '" + name + "' has already been defined.",
                "KEY_EXISTS_ALREADY",
                startEvent.tag().tagPositionOrRange() );
        }

        currentNameToken = startEvent.tag ().qualifiedTagToken ();

        addAttributes ( startEvent.attributes(), parentStartEvent );

        return startEvent;
    }

    public void addAttributes (
        @Nullable NodeAttributes attributes,
        @NotNull TaggedNodeStartEvent nodeStartEvent ) throws PdmlException {

        if ( attributes == null ) return;

        // Allow attributes only in root node

        if ( nodeStartEvent != rootNode ) {
            throw new PdmlException (
                "Node '" + nodeStartEvent.tag ().qualifiedTagToken ().getText() + "' cannot have attributes.",
                "ATTRIBUTES_NOT_ALLOWED",
                attributes.getTextPositionOrRange() );
        }

        // attributes.forEach ( stringParameters::add );
        attributes.forEach ( ( Parameter<String> attribute ) ->
            stringParameters.add ( StringParameterUtils.emptyValueToNull ( attribute ) ) );
    }

    @Override
    public void onTaggedNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull TaggedNodeStartEvent start ) {

        if ( currentNameToken != null ) {
            // It's an empty node (no text defined)
            stringParameters.add (
                currentNameToken.getText(), null, null, currentNameToken.readerResourcePosition(), null );
        }
    }

    @Override
    public void onText ( @NotNull TextEvent event, @NotNull TaggedNodeStartEvent parent ) throws PdmlException {

        String text = event.text();

        if ( parent == rootNode ) {
            if ( text.trim().isEmpty() ) {
                return;
            } else {
                throw new PdmlException (
                    "Node '" + parent + "' cannot contain text.",
                    "TEXT_NOT_ALLOWED",
                    parent.tag().startPosition() );
            }
        }

        assert currentNameToken != null;
        stringParameters.add (
            currentNameToken.getText(), text,
            null,
            // currentNameToken.getLocation(), event.location ().toTextToken () );
            currentNameToken.readerResourcePosition (), null );

        currentNameToken = null;
    }

    public @Nullable MutableParameters<String> getResult() {
        return stringParameters.isEmpty() ? null : stringParameters;
    }
}
 */
