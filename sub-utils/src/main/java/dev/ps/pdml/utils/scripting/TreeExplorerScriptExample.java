package dev.ps.pdml.utils.scripting;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pjse.util.interfaces.FailableConsumer;

public class TreeExplorerScriptExample implements FailableConsumer<TaggedNode> {

    public void accept ( TaggedNode rootNode ) throws Exception {

        System.out.println ( "Hi from explorer" );

        System.out.println ( "rootNode: " + rootNode.toString() );

        System.out.println ( "All tagged nodes" );
        rootNode.treeTaggedNodeStream ( true ).forEach ( node -> {
            System.out.println ( node.toString() );
        });

        System.out.println ( "concatenated text: " + rootNode.concatenateTreeTexts() );
    }
}
