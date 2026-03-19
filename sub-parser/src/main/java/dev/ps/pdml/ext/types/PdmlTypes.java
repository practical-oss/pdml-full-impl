package dev.ps.pdml.ext.types;

import dev.ps.pdml.ext.types.instances.LocalDateType;
import dev.ps.pdml.ext.types.instances.StringLiteralType;
import dev.ps.pdml.ext.types.instances.TextType;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PdmlTypes {


    public static final @NotNull PdmlTypes STANDARD_TYPES =
        new PdmlTypes()
            .add ( TextType.NON_NULL_INSTANCE )
            .add ( TextType.NULLABLE_INSTANCE )
            .add ( StringLiteralType.NON_NULL_INSTANCE )
            .add ( StringLiteralType.NULLABLE_INSTANCE )
            .add ( LocalDateType.NON_NULL_INSTANCE )
            .add ( LocalDateType.NULLABLE_INSTANCE );


    // private final @NotNull Map<NodeName, PdmlType<?>> map;
    private final @NotNull Map<String, PdmlType<?>> map;


    public PdmlTypes() {
        this.map = new HashMap<>();
    }


    public boolean isEmpty() { return map.isEmpty(); }

    public @Nullable PdmlType<?> getOrNull ( @NotNull String typeName ) {
        return map.get ( typeName );
    }

    public @Nullable Collection<PdmlType<?>> getAll() {

        if ( ! map.isEmpty() ) {
            return map.values();
        } else {
            return null;
        }
    }

    public PdmlTypes add ( PdmlType<?> type ) {

        // NodeName tag = type.nodeName();
        String name = type.getName();
        if ( map.containsKey ( name ) ) {
            throw new IllegalStateException ( "Type '" + type.getName() + "' exists already." );
        }

        map.put ( name, type );
        return this;
    }
}
