package dev.ps.pdml.html.deprecated;

/*
import dev.pp.exception.data.pdml.PdmlException;
import dev.pp.node.data.pdml.Node;
import dev.pp.branch.node.data.pdml.BranchNode;
import dev.pp.leaf.node.data.pdml.LeafNode;
import dev.pp.parser.pdml.PdmlParserConfig;
import dev.pp.parser.pdml.PdmlParserUtil;
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.annotations.basics.Nullable;
import dev.pp.core.reader.resource.text.TextResourceReader;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

 */

@Deprecated
public class PdmlTreeBrowserUtil {}
/*
public class PdmlTreeBrowserUtil {


    private static final @NotNull String WINDOW_TITLE = "PDML Tree";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = (int) (WINDOW_WIDTH / 1.618);
    private static final boolean INCLUDE_WHITESPACE_NODES = true;


    // File

    public static void browseFile (
        @NotNull Path filePath ) throws IOException, PdmlException {

        browseFile ( filePath, PdmlParserConfig.defaultConfig() );
    }

    public static void browseFile (
        @NotNull Path filePath,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        try ( TextResourceReader textResourceReader = new TextResourceReader ( filePath ) ) {
            browseReader ( textResourceReader, config );
        }
    }


    // Code

    public static void browseCode (
        @NotNull String code ) throws IOException, PdmlException {

        browseCode ( code, PdmlParserConfig.defaultConfig() );
    }

    public static void browseCode (
        @NotNull String code,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        try ( TextResourceReader textResourceReader = new TextResourceReader ( code ) ) {
            browseReader ( textResourceReader, config );
        }
    }


    // Reader

    public static void browseReader (
        @NotNull TextResourceReader textResourceReader,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        BranchNode rootNode = PdmlParserUtil.parseReader ( textResourceReader, config );
        browseTree ( rootNode );
    }


    // Tree

    public static void browseTree (
        @NotNull BranchNode pdmlRootNode ) {

        browseTree ( pdmlRootNode, null );
    }

    static void browseTree (
        @NotNull BranchNode pdmlRootNode,
        // used only for tests
        @Nullable CountDownLatch countDownLatch ) {

        try {
            UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName() );
            // default:
            // UIManager.setLookAndFeel ( "javax.swing.plaf.metal.MetalLookAndFeel" );
            // very ugly:
            // UIManager.setLookAndFeel ( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater ( () -> {
            showJFrame ( pdmlRootNode, countDownLatch );
        } );
    }


    private static void showJFrame (
        @NotNull BranchNode pdmlRootNode,
        @Nullable CountDownLatch countDownLatch ) {

        JFrame jFrame = new JFrame();
        JSplitPane splitPane = createJSplitPane  ( pdmlRootNode );
        jFrame.add ( splitPane );

        jFrame.setDefaultCloseOperation ( JFrame.DISPOSE_ON_CLOSE );
        // TODO?
        // jFrame.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
        jFrame.setTitle ( WINDOW_TITLE );
        jFrame.setSize ( WINDOW_WIDTH, WINDOW_HEIGHT );
        jFrame.setLocationRelativeTo ( null ); // center window on screen
        // jFrame.pack();

        if ( countDownLatch != null ) {
            jFrame.addWindowListener ( new WindowAdapter() {
                @Override
                public void windowClosed ( WindowEvent e ) {
                    countDownLatch.countDown(); // Release the latch
                }
            } );
        }

        jFrame.setVisible ( true );
    }

    private static JSplitPane createJSplitPane ( @NotNull BranchNode pdmlRootNode ) {

        DetailPanel detailPanel = new DetailPanel();
        JScrollPane tree = new JScrollPane ( createJTree ( pdmlRootNode, detailPanel ) );
        return new JSplitPane (
            JSplitPane.HORIZONTAL_SPLIT, tree, detailPanel );
    }


    private static JTree createJTree (
        @NotNull BranchNode pdmlRootNode,
        @NotNull DetailPanel detailPanel ) {

        DefaultMutableTreeNode swingRootNode = new DefaultMutableTreeNode ( pdmlRootNode, true );
        appendChildNodes ( swingRootNode, pdmlRootNode );
        JTree jTree = new JTree ( swingRootNode, true );
        jTree.setShowsRootHandles ( true );
        // jTree.convertValueToText (  )
        jTree.setCellRenderer ( new CustomTreeCellRenderer() );

        jTree.getSelectionModel().setSelectionMode ( TreeSelectionModel.SINGLE_TREE_SELECTION );
        jTree.addTreeSelectionListener ( ( TreeSelectionEvent e ) -> {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                jTree.getLastSelectedPathComponent();

            if ( node == null ) return; // no node selected

            Node pdmlNode = (Node) node.getUserObject();
            detailPanel.setTitle ( pdmlNode.path().toString() );
            if ( pdmlNode instanceof BranchNode branchNode ) {
                // detailPanel.setText ( null );
                detailPanel.setText ( branchNode.concatenateTreeTexts() );

            } else if ( pdmlNode instanceof LeafNode leafNode ) {
                String text = leafNode.getText();
                if ( text.isBlank() ) {
                    text = makeWhitespaceVisible (
                        text, true, true, true, true, true );
                }

                detailPanel.setText ( text );
            }
        } );

        return jTree;
    }

    private static void appendChildNodes (
        @NotNull DefaultMutableTreeNode swingParentNode,
        @NotNull BranchNode pdmlParentNode ) {

        pdmlParentNode.getChildNodes().stream().forEach ( pdmlChildNode -> {

            if ( pdmlChildNode instanceof BranchNode branchChild ) {
                DefaultMutableTreeNode swingChildNode = new DefaultMutableTreeNode (
                    pdmlChildNode, true );
                swingParentNode.add ( swingChildNode );
                appendChildNodes ( swingChildNode, branchChild );

            } else if ( pdmlChildNode instanceof LeafNode leafChild ) {
                boolean isWhitespaceNode = leafChild.getText().isBlank();
                if ( INCLUDE_WHITESPACE_NODES || ! isWhitespaceNode ) {
                    DefaultMutableTreeNode swingChildNode = new DefaultMutableTreeNode (
                        pdmlChildNode, false );
                    swingParentNode.add ( swingChildNode );
                }

            } else {
                throw new RuntimeException ( "Unexpected child type " + pdmlChildNode.getClass() );
            }
        } );
    }

    static @Nullable String makeWhitespaceVisible (
        @Nullable String string,
        boolean showSpaces,
        boolean showTabs,
        boolean showCR,
        boolean showLF,
        boolean keepLineBreaks ) {

        if ( string == null ) return null;

        String result = string;

        if ( showSpaces ) {
            result = result.replace ( ' ', '␣' );
        }

        if ( showTabs ) {
            result = result.replace ( '\t', '⇥' );
        }

        if ( showCR ) {
            result = result.replace ( '\r', '␍' );
        } else {
            result = result.replace ( "\r", "" );
        }

        if ( showLF ) {
            if ( keepLineBreaks ) {
                result = result.replace ( "\n", "↵\n" );
            } else {
                result = result.replace ( '\n', '↵' );
            }
        }

        return result;
    }
}

 */
