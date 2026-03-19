package dev.ps.pdml.html;

@Deprecated
class PdmlTreeBrowserUtilTest {

/*
    private static void browseCode (
        @NotNull String code,
        @Nullable CountDownLatch countDownLatch ) throws IOException, PdmlException {

        try ( TextResourceReader textResourceReader = new TextResourceReader ( code ) ) {
            BranchNode rootNode = PdmlParserUtil.parseReader (
                textResourceReader, PdmlParserConfig.defaultConfig() );
            PdmlTreeBrowserUtil.browseTree ( rootNode, countDownLatch );
        }
    }

    @Test
    void browseTreeTest() throws IOException, PdmlException, InterruptedException {

        if ( true ) return;

        String code = """
            [test
                [data [color green][dimensions [width 200][height 100]]]
                [data
                    [color green]
                    [dimensions
                        [* pixels *]
                        [width 200]
                        [height 100]
                    ]
                ]
                [markup
                    [p We can use [b bold], [i italic], or [b [i bold and italic]]]
                ]
                [2_lines Line 1
            Line 2]
                [empty_node]
                [whitespace_node

                ]
                [expression [s:exp 1+1]]
                [text_with_embedded_comment text before comment [* comment line 1
                comment line 2
                comment line 3
                comment end line *] text after comment]
            ]
            """;

        // code = "[root]";

        CountDownLatch countDownLatch = new CountDownLatch ( 1 );
        browseCode ( code, countDownLatch );
        countDownLatch.await();
    }

 */
}
