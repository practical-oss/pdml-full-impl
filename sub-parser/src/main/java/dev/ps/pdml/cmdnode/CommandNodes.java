package dev.ps.pdml.cmdnode;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.namedobject.MultiNamedObjects;

import java.util.List;

public class CommandNodes extends MultiNamedObjects<CommandNodeBase> {

    public CommandNodes ( @NotNull List<CommandNodeBase> list ) {
        super ( list );
    }
}
