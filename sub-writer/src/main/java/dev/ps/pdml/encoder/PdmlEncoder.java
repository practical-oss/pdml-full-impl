package dev.ps.pdml.encoder;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.encoder.Encoder;
import dev.ps.prt.type.collection.map.MapInstance;
import dev.ps.pdml.writer.PdmlWriter;

import java.io.IOException;
import java.util.List;

public class PdmlEncoder implements Encoder {


    private final @NotNull PdmlWriter pdmlWriter;


    public PdmlEncoder ( @NotNull PdmlWriter pdmlWriter ) {
        this.pdmlWriter = pdmlWriter;
    }


    public void encodeString ( @NotNull String string )
        throws IOException {

        pdmlWriter.writeText ( string, true );
    }

    public void encodeNull() throws IOException {
        // do nothing
    }

    public <E> void encodeList ( @NotNull List<AnyInstance<E>> elements )
        throws IOException {

        throw new UnsupportedOperationException ( "Not yet implemented" );
    }

    // public void encodeMap ( @NotNull MapInstance<?,?,?> mapInstance )
    public <K, V> void encodeMap ( @NotNull List<MapInstance.Entry<K, V>> entries )
        throws IOException {

        throw new UnsupportedOperationException ( "Not yet implemented" );
    }

    public void encodeRecord ( @NotNull Arguments arguments )
        throws IOException {

        throw new UnsupportedOperationException ( "Not yet implemented" );
    }
}
