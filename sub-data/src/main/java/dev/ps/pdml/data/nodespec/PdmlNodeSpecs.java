package dev.ps.pdml.data.nodespec;

import dev.ps.pdml.data.node.NodeTag;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// TODO? extends NamedObjects<PdmlNodeSpec>
public class PdmlNodeSpecs {

    private final Map<String, PdmlNodeSpec> map;


    public PdmlNodeSpecs () {
        this.map = new HashMap<> ();
    }


    public boolean containsQualifiedName ( @NotNull String qualifiedName ) { return map.containsKey ( qualifiedName ); }


    public PdmlNodeSpecs add ( @NotNull PdmlNodeSpec nodeSpec ) {
        checkNotExists ( nodeSpec );
        map.put ( nodeSpec.qualifiedName(), nodeSpec );

        return this;
    }

    public @Nullable PdmlNodeSpec getOrNull ( NodeTag name ) {

        return map.get ( name.qualifiedTag () );
    }

    private void checkExists ( @NotNull PdmlNodeSpec nodeSpec ) {

        String name = nodeSpec.qualifiedName();
        if ( ! containsQualifiedName ( name ) )
            throw new IllegalArgumentException ( "Node spec. '" + name + "' does not exist." );
    }

    private void checkNotExists ( @NotNull PdmlNodeSpec nodeSpec ) {

        String name = nodeSpec.qualifiedName();
        if ( containsQualifiedName ( name ) )
            throw new IllegalArgumentException ( "Node spec. '" + name + "' exists already." );
    }
}
