package dev.ps.pdml.utils.treewalker.handler.impl;

import dev.ps.pdml.utils.treewalker.handler.TaggedNodeStartEvent;
import dev.ps.pdml.utils.treewalker.handler.PdmlTreeWalkerEventHandler;
import dev.ps.shared.basics.annotations.NotNull;

public class DoNothing_ParserEventHandler implements PdmlTreeWalkerEventHandler<TaggedNodeStartEvent, String> {


    public DoNothing_ParserEventHandler() {}


    @Override
    public @NotNull TaggedNodeStartEvent onRootNodeStart ( @NotNull TaggedNodeStartEvent event ) { return event; }

    @Override
    public @NotNull TaggedNodeStartEvent onTaggedNodeStart ( @NotNull TaggedNodeStartEvent event, @NotNull TaggedNodeStartEvent parent ) { return event; }

    @Override
    public @NotNull String getResult() { return "nothing"; }
}
