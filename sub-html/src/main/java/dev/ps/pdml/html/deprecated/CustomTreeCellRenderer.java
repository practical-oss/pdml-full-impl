package dev.ps.pdml.html.deprecated;

/*
import dev.pp.node.data.pdml.Node;
import dev.pp.branch.node.data.pdml.BranchNode;
import dev.pp.leaf.node.data.pdml.CommentLeaf;
import dev.pp.leaf.node.data.pdml.LeafNode;
import dev.pp.leaf.node.data.pdml.TextLeaf;
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.annotations.basics.Nullable;
import dev.pp.core.string.utilities.basics.StringTruncator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

 */

@Deprecated
class CustomTreeCellRenderer {}
/*
class CustomTreeCellRenderer extends DefaultTreeCellRenderer {


    private static final int MAX_CHARS_IN_NODE_LABEL = 20;

    private static final @NotNull Icon TEXT_ICON =
        // createImageIconForCharacter ( 'T' );
        loadIcon ( "gui/browser/textIcon.jpg" );
    private static final @NotNull Icon WHITESPACE_ICON =
        loadIcon ( "gui/browser/whitespaceIcon.jpg" );
    private static final @NotNull Icon COMMENT_ICON =
        loadIcon ( "gui/browser/commentIcon.jpg" );


    @Override
    public Component getTreeCellRendererComponent (
        JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {

        // Get the default renderer
        JLabel label = (JLabel) super.getTreeCellRendererComponent (
            tree, value, selected, expanded, leaf, row, hasFocus );

        DefaultMutableTreeNode swingNode = (DefaultMutableTreeNode) value;
        Node pdmlNode = (Node) swingNode.getUserObject ();

        label.setText ( defineNodeText ( pdmlNode ) );
        Icon icon = defineNodeIcon ( pdmlNode );
        if ( icon != null ) {
            label.setIcon ( icon );
        }

        return label;
    }

    private @Nullable String defineNodeText ( @NotNull Node pdmlNode ) {

        String nodeText;

        if ( pdmlNode instanceof BranchNode branchNode ) {
            nodeText = branchNode.getName ().toString ();
            // TODO? exclude whitespace nodes if they are not displayed
            int childCount = branchNode.getChildNodes().size ();
            if ( childCount > 0 ) {
                nodeText = nodeText + " (" + childCount + ")";
            }

        } else if ( pdmlNode instanceof LeafNode leafNode ) {
            nodeText = leafNode.getText();
            if ( nodeText.isBlank() ) {
                nodeText = PdmlTreeBrowserUtil.makeWhitespaceVisible (
                    nodeText, true, true, false, true, false );
            } else {
                nodeText = PdmlTreeBrowserUtil.makeWhitespaceVisible (
                    nodeText, false, false, false, true, false );
            }

        } else {
            throw new RuntimeException ( "Unexpected node type " + pdmlNode.getClass () );
        }

        nodeText = StringTruncator.truncateWithEllipses ( nodeText, MAX_CHARS_IN_NODE_LABEL );

        return nodeText;
    }

    private @Nullable Icon defineNodeIcon ( @NotNull Node pdmlNode ) {

        if ( pdmlNode instanceof BranchNode branchNode ) {
            return null;

        } else if ( pdmlNode instanceof TextLeaf textLeaf ) {
            if ( textLeaf.getText().isBlank () ) {
                return WHITESPACE_ICON;
            } else {
                return TEXT_ICON;
            }

        } else if ( pdmlNode instanceof CommentLeaf ) {
            return COMMENT_ICON;

        } else {
            throw new RuntimeException ( "Unexpected node type " + pdmlNode.getClass () );
        }
    }

    private static @NotNull Icon loadIcon ( @NotNull String path ) {

        try ( InputStream is = CustomTreeCellRenderer.class.getClassLoader().getResourceAsStream ( path ) ) {
            assert is != null;
            return new ImageIcon ( is.readAllBytes() );
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

 */

    /*
    private static @NotNull ImageIcon createImageIconForCharacter (
        @NotNull Character character ) {

        return createImageIconForCharacter ( character, 20, 20 );
    }

    private static @NotNull ImageIcon createImageIconForCharacter (
        @NotNull Character character, int width, int height ) {

        BufferedImage image = new BufferedImage (
            width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = image.createGraphics();
        // g2d.setFont ( new Font ("Arial", Font.BOLD, height - 10 ) );
        g2d.setFont ( new Font ("Arial", Font.PLAIN, 12 ) );
        // g2d.drawString ( character.toString(), width / 4, height - 10 );
        // g2d.drawString ( character.toString(), 0, 0 );
        // g2d.setBackground ( Color.YELLOW );
        g2d.setColor ( Color.BLACK );
        // g2d.fillRect (0, 0, width, height );
        g2d.drawRect (1, 1, width - 2, height - 2 );
        // g2d.setColor ( Color.BLUE );
        g2d.drawString ( character.toString(), 3, 13 );
        g2d.dispose();
        // try {
        //     ImageIO.write ( image, "jpeg", new File ( "C:\\temp\\i.jpg" ) );
        // } catch ( IOException e ) {
        //     throw new RuntimeException ( e );
        // }
        return new ImageIcon ( image );
    }
    */
// }
