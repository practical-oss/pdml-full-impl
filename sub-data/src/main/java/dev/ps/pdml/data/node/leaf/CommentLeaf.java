package dev.ps.pdml.data.node.leaf;

import dev.ps.shared.text.location.TextLocation;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public class CommentLeaf extends UntaggedLeafNode {


    public static @NotNull String removeDelimiters ( @NotNull String comment ) {

        if ( ! comment.startsWith ( PdmlExtensionsConstants.BLOCK_COMMENT_START ) ) {
            return comment;
        }

        // Count the number of stars used, e.g. ^/** **/ -> 2
        int starsCount = 1;
        for ( int i = 2; i < comment.length(); i++ ) {
            if ( comment.charAt ( i ) == PdmlExtensionsConstants.BLOCK_COMMENT_STAR_CHAR ) {
                starsCount++;
            } else {
                break;
            }
        }

        int startIndex = 1 + starsCount;
        int endIndex = comment.length() - starsCount - 1;
        return comment.substring ( startIndex, endIndex );
    }


    public CommentLeaf (
        @NotNull String text,
        @Nullable TextLocation textLocation ) {

        super ( text, textLocation );
    }


    public boolean isTextLeaf () { return false; }

    public boolean isCommentLeaf () { return true; }

    public @NotNull String textWithoutDelimiters() {
        return removeDelimiters ( text );
    }
}
