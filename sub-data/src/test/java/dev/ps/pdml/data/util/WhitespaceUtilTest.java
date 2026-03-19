package dev.ps.pdml.data.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhitespaceUtilTest {

    @Test
    void isWhitespaceString() {

        assertFalse ( WhitespaceUtil.isWhitespaceString ( "foo" ) );
        assertFalse ( WhitespaceUtil.isWhitespaceString ( " f  " ) );
        assertTrue ( WhitespaceUtil.isWhitespaceString ( " " ) );
        assertTrue ( WhitespaceUtil.isWhitespaceString ( " \n\r\t\f" ) );
    }

    @Test
    void trim() {

        assertEquals ( "foo", WhitespaceUtil.trim ( "foo" ) );
        assertEquals ( "foo", WhitespaceUtil.trim ( " foo" ) );
        assertEquals ( "foo", WhitespaceUtil.trim ( "foo " ) );
        assertEquals ( "foo", WhitespaceUtil.trim ( " foo " ) );
        assertEquals ( "f", WhitespaceUtil.trim ( "\r\n f \r\n" ) );
        assertNull ( WhitespaceUtil.trim ( " " ) );
        assertNull ( WhitespaceUtil.trim ( "\r\n" ) );
    }

    @Test
    void trimLeft() {

        assertEquals ( "foo", WhitespaceUtil.trimLeft ( "foo" ) );
        assertEquals ( "foo", WhitespaceUtil.trimLeft ( " foo" ) );
        assertEquals ( "f", WhitespaceUtil.trimLeft ( "\r\n f" ) );
        assertNull ( WhitespaceUtil.trimLeft ( " " ) );
        assertNull ( WhitespaceUtil.trimLeft ( "\r\n" ) );
    }

    @Test
    void trimRight() {

        assertEquals ( "foo", WhitespaceUtil.trimRight ( "foo" ) );
        assertEquals ( "foo", WhitespaceUtil.trimRight ( "foo " ) );
        assertEquals ( "f", WhitespaceUtil.trimRight ( "f\r\n" ) );
        assertNull ( WhitespaceUtil.trimRight ( " " ) );
        assertNull ( WhitespaceUtil.trimRight ( "\r\n" ) );
    }
}
