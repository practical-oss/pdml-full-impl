package dev.ps.pdml.data.node.leaf;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public class CommentLeaf extends UntaggedLeafNode {


    public static @NotNull String removeDelimiters ( @NotNull String comment ) {

        if ( ! comment.startsWith ( PdmlExtensionsConstants.MULTI_LINE_COMMENT_EXTENSION_START ) ) {
            return comment;
        }

        // Count the number of stars used, e.g. ^/** **/ -> 2
        int starsCount = 1;
        for ( int i = 3; i < comment.length(); i++ ) {
            if ( comment.charAt ( i ) == PdmlExtensionsConstants.MULTI_LINE_COMMENT_STAR_CHAR ) {
                starsCount++;
            } else {
                break;
            }
        }

        int startIndex = 2 + starsCount;
        int endIndex = comment.length() - starsCount - 1;
        return comment.substring ( startIndex, endIndex );
    }


    public CommentLeaf (
        @NotNull String text,
        @Nullable TextRange textLocation ) {

        super ( text, textLocation );
    }


    public boolean isTextLeaf () { return false; }

    public boolean isCommentLeaf () { return true; }

    public @NotNull String textWithoutDelimiters() {
        return removeDelimiters ( text );
    }
}
