package dev.ps.pdml.decoder;

import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.type.collection.list.ListInstance;
import dev.ps.prt.type.collection.list.impl.NativeListType;
import dev.ps.prt.type.collection.map.MapInstance;
import dev.ps.prt.type.collection.map.impl.NativeMapType;
import dev.ps.prt.type.record.RecordInstance;
import dev.ps.prt.parameter.Parameters;
import dev.ps.prt.parameter.ParametersBuilder;
import dev.ps.prt.type.CommonTypes;
import dev.ps.prt.type.record.impl.GenericRecordInstance;
import dev.ps.prt.type.record.impl.GenericRecordType;
import dev.ps.prt.type.record.impl.NativeRecordType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class PdmlDecoderTest {

    @Test
    void decodeNativeStringList() throws IOException, InvalidDataException {

        NativeListType<String> type =
            new NativeListType<> ( "string_list", CommonTypes.STRING, null );

        // Standard syntax
        String pdmlCode = """
            [el item 1]
            [el item 2]
            [el item 3]
            """;

        PdmlDecoder decoder = new PdmlDecoder ( new StringReaderResource ( pdmlCode ) );
        ListInstance<List<String>,String> result = type.decodeToInstance ( decoder );
        List<String> list = result.nativeObject ();

        assertEquals ( 3, list.size () );
        assertEquals ( "item 1", list.get ( 0 ) );
        assertEquals ( "item 2", list.get ( 1 ) );
        assertEquals ( "item 3", list.get ( 2 ) );

        // Short syntax for scalars
        pdmlCode = """
            item_1 "item 2"
            \"\"\"
            item
            3
            \"\"\"
            """;

        decoder = new PdmlDecoder ( new StringReaderResource ( pdmlCode ) );
        result = type.decodeToInstance ( decoder );
        list = result.nativeObject ();

        assertEquals ( 3, list.size () );
        assertEquals ( "item_1", list.get ( 0 ) );
        assertEquals ( "item 2", list.get ( 1 ) );
        assertEquals ( "item\n3", list.get ( 2 ) );
    }

    @Test
    void decodeNativeStringMap() throws IOException, InvalidDataException {

        NativeMapType<String,String> type = new NativeMapType<> (
            "string_string_map", CommonTypes.STRING, CommonTypes.STRING, null );

        // Standard syntax
        String pdmlCode = """
            [k1 value 1]
            [k\\s2  value 2 ]
            ["k 3" value 3]
            """;

        PdmlDecoder decoder = new PdmlDecoder ( new StringReaderResource ( pdmlCode ) );
        MapInstance<Map<String,String>,String, String> result = type.decodeToInstance ( decoder );
        Map<String,String> map = result.nativeObject ();

        assertEquals ( 3, map.size() );
        assertEquals ( "value 1", map.get ( "k1" ) );
        assertEquals ( " value 2 ", map.get ( "k 2" ) );
        assertEquals ( "value 3", map.get ( "k 3" ) );
    }

    @Test
    void decodeGenericRecord() throws IOException, InvalidDataException {

        Parameters typeFields = new ParametersBuilder()
            .string ( "name", null, null )
            // .appendInteger ( "age" )
            .append ( "age", CommonTypes.INT32 )
            .string ( "occupation", "unknown", null )
            .stringOrNull ( "remark", null, null )
            .build();
        GenericRecordType type = new GenericRecordType ("person", typeFields, null );

/*
        String pdmlCode = """
            [person
                [name Christian]
                [age 63]
                [remark]
            ]
            """;
 */

        String pdmlCode = """
            [name Christian]
            [age 63]
            [remark]
            """;

        PdmlDecoder decoder = new PdmlDecoder ( new StringReaderResource ( pdmlCode ) );
        GenericRecordInstance result = type.decodeToInstance ( decoder );

        Arguments valueFields = result.genericObject().object();
        assertEquals ( 4, valueFields.count() );
        assertEquals ( "Christian", valueFields.nonNullStringValue ( "name" ) );
        assertEquals ( 63, valueFields.nonNullIntegerValue ( "age" ) );
        assertEquals ( "unknown", valueFields.nonNullStringValue ( "occupation" ) );
        assertNull ( valueFields.nullableStringValue ( "remark" ) );


        // test with positional field values (experimental)

        pdmlCode = "Christian  63 - [remark remark_text]";
        decoder = new PdmlDecoder ( new StringReaderResource ( pdmlCode ) );
        result = type.decodeToInstance ( decoder );

        valueFields = result.genericObject().object();
        assertEquals ( 4, valueFields.count() );
        assertEquals ( "Christian", valueFields.nonNullStringValue ( "name" ) );
        assertEquals ( 63, valueFields.nonNullIntegerValue ( "age" ) );
        assertEquals ( "unknown", valueFields.nonNullStringValue ( "occupation" ) );
        // assertNull ( valueFields.stringOrNullValue ( "remark" ) );
        assertEquals ( "remark_text", valueFields.nonNullStringValue ( "remark" ) );
    }

    public record PersonRecord (
        String name,
        Integer age,
        String occupation,
        String remark ) {}

    @Test
    void decodeNativeRecord() throws IOException, InvalidDataException {

        NativeRecordType<PersonRecord> type =
            new NativeRecordType<> ( PersonRecord.class );

        String pdmlCode = """
            [name Christian]
            [age 63]
            [occupation programmer]
            [remark]
            """;

        PdmlDecoder decoder = new PdmlDecoder ( new StringReaderResource ( pdmlCode ) );
        RecordInstance<PersonRecord> result = type.decodeToInstance ( decoder );

        Arguments valueFields = result.genericObject().object();
        assertEquals ( 4, valueFields.count() );
        assertEquals ( "Christian", valueFields.nonNullStringValue ( "name" ) );
        assertEquals ( 63, valueFields.nonNullIntegerValue ( "age" ) );
        assertEquals ( "programmer", valueFields.nonNullStringValue ( "occupation" ) );
        assertNull ( valueFields.nullableStringValue ( "remark" ) );

        PersonRecord person = result.nativeObject ();
        assertEquals ( "Christian", person.name() );
        assertEquals ( 63, person.age() );
        assertEquals ( "programmer", person.occupation() );
        assertNull ( person.remark() );
    }
}
