package dev.ps.pdml.decoder;

import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.type.decoder.DecoderUtil;
import dev.ps.prt.type.record.RecordInstanceUtil;
import dev.ps.prt.parameter.Parameters;
import dev.ps.prt.parameter.ParametersBuilder;
import dev.ps.prt.type.CommonTypes;
import dev.ps.prt.type.record.impl.GenericRecordInstance;
import dev.ps.prt.type.record.impl.GenericRecordType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordInstanceUtilTest {

    @Test
    void stringsToGenericRecord() throws Exception {

        StringArguments strings = StringArguments.builder()
            .append ( "name", "Albert" )
            .append ( "age", "25" )
            .build();

        Parameters fields = new ParametersBuilder()
            .string ( "name", null, null )
            // .appendInteger ( "age", 20 )
            .append ( "age", CommonTypes.INT32, () -> 20 )
            .build();
        GenericRecordType recordType = new GenericRecordType (
            "Person", fields, null );

        DecoderUtil.DecoderSupplier decoderSupplier = PdmlDecoderUtil.DEFAULT_DECODER_SUPPLIER;

        GenericRecordInstance result = RecordInstanceUtil.stringsToGenericRecord (
            strings, recordType, decoderSupplier, false );

        assertEquals ( "Albert", result.genericObject().object().nullableCastedValue ( "name" ) );
        assertEquals ( 25, (Integer) result.genericObject().object().nullableCastedValue ( "age" ) );


        // Default Value

        strings = StringArguments.builder()
            .append ( "name", "Albert" )
            .build();

        result = RecordInstanceUtil.stringsToGenericRecord (
            strings, recordType, decoderSupplier, false );

        assertEquals ( "Albert", result.genericObject().object().nullableCastedValue ( "name" ) );
        assertEquals ( 20, (Integer) result.genericObject().object().nullableCastedValue ( "age" ) );


        // Invalid
        strings = StringArguments.builder()
            .append ( "name", "Albert" )
            .append ( "invalid", "Foo" )
            .build();
        StringArguments finalStrings = strings;
        assertThrows ( InvalidDataException.class, () -> RecordInstanceUtil.stringsToGenericRecord (
            finalStrings, recordType, decoderSupplier, false ) );
    }
}
