package dev.ps.pdml.html.deprecated;

/*
import dev.pp.branch.node.data.pdml.BranchNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

 */

@Deprecated
public class SimplePdmlTreeBrowserUtil {}
/*
public class SimplePdmlTreeBrowserUtil {

    public static void showTree ( BranchNode pdmlRootNode ) {

        SwingUtilities.invokeLater ( () -> {
            JTree jTree = createJTree ( pdmlRootNode );
            createJFrame ( jTree );
        } );
    }

    public static void createJFrame ( JTree jTree ) {

        JFrame jFrame = new JFrame();
        jFrame.add ( new JScrollPane ( jTree ) );
        jFrame.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
        jFrame.setTitle ( "PDML Tree");
        jFrame.setSize ( 1000, 600 );
        jFrame.setLocationRelativeTo ( null ); // center window on screen
        jFrame.setVisible ( true );
    }

    public static JTree createJTree ( BranchNode pdmlRootNode ) {

        DefaultMutableTreeNode swingRootNode = new DefaultMutableTreeNode ( pdmlRootNode );
        appendChildNodes ( swingRootNode, pdmlRootNode );
        return new JTree ( swingRootNode );
    }


    public static void appendChildNodes (
        DefaultMutableTreeNode swingParentNode,
        BranchNode pdmlParentNode ) {

        pdmlParentNode.getChildNodes().stream().forEach ( pdmlChildNode -> {

            DefaultMutableTreeNode swingChildNode = new DefaultMutableTreeNode ( pdmlChildNode );
            swingParentNode.add ( swingChildNode );
            if ( pdmlChildNode instanceof BranchNode pdmlBranchChild ) {
                appendChildNodes ( swingChildNode, pdmlBranchChild );
            }
        } );
    }
}

 */
