package dev.ps.pdml.data.node.leaf;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.pdml.data.util.WhitespaceUtil;
import dev.ps.prt.type.CommonTypes;
import dev.ps.prt.type.scalar.enumtype.EnumTypeUtil;

public class TextLeaf extends UntaggedLeafNode {


    private interface FailableSupplier <T> {
        T get() throws InvalidDataException;
    }


    public TextLeaf (
        @NotNull String text,
        @Nullable TextRange textLocation ) {

        super ( text, textLocation );
        assert ! text.isEmpty();
    }


    public boolean isTextLeaf() { return true; }

    public boolean isCommentLeaf() { return false; }


    // Whitespace Helpers

    public boolean isWhitespace() {
        return WhitespaceUtil.isWhitespaceString ( text );
    }

    public @Nullable String trimmedText() {
        return WhitespaceUtil.trim ( text );
    }

    public @Nullable String leftTrimmedText() {
        return WhitespaceUtil.trimLeft ( text );
    }

    public @Nullable String rightTrimmedText() {
        return WhitespaceUtil.trimRight ( text );
    }

    /*
    // TODO write test
    public boolean isWhitespaceBetweenBranchNodes() {

        if ( ! isWhitespace () ) {
            return false;
        }
        return previousSibling() instanceof BranchNode ||
            nextSibling() instanceof BranchNode;
    }
     */


    // Parse text to Java objects

    public int toInt() throws InvalidPdmlDataException {
        return parse ( () -> CommonTypes.INT32.genericToNativeObject (
            text, textLocation ) );
    }

    public boolean toBoolean() throws InvalidPdmlDataException {
        return parse ( () -> CommonTypes.BOOLEAN.genericToNativeObject (
            text, textLocation ) );
    }

    public <E extends Enum<E>> @NotNull E toEnum (
        @NotNull Class<E> clazz,
        boolean convertTextToUppercase ) throws InvalidPdmlDataException {

        return parse ( () -> EnumTypeUtil.parseEnum (
            text, clazz, convertTextToUppercase, textLocation ) );
    }

    /*
    public @Nullable List<String> toStringListOrNull() throws InvalidPdmlDataException {
        return null;
    }
     */

    private <T> @NotNull T parse ( FailableSupplier<T> supplier ) throws InvalidPdmlDataException {

        try {
            return supplier.get();
        } catch ( InvalidDataException e ) {
            throw new InvalidPdmlDataException ( e );
        }
    }

    /* TODO

    byte short, long, float, double, BigInteger, BigDecimal
    List<String>, Set<String>, Map<String,String>: use methods existing already in parser
    bytes


    ? public <T> @NotNull T toBean ( Class<T> beanClass ) { // throws PdmlDataException
        // TODO
        return null;
    }

    ? public <T> @NotNull T toObject ( Function<String,T> parser ) { // throws PdmlDataException
        // TODO
        return null;
    }

    ? public <E> @NotNull List<E> toList ( Function<String,E> elementParser ) { // throws PdmlDataException
        // TODO
        return null;
    }

    ? public <E> @NotNull Set<E> toSet ( Function<String,E> elementParser ) { // throws PdmlDataException
        // TODO
        return null;
    }

    ? public <V> @NotNull Map<String,V> toMap ( Function<String,V> valueParser ) { // throws PdmlDataException
        // TODO
        return null;
    }
     */
}
