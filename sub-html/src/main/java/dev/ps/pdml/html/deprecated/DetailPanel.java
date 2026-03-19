package dev.ps.pdml.html.deprecated;

/*
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.annotations.basics.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

 */

@Deprecated
class DetailPanel {}
/*
class DetailPanel extends JPanel {

    private final @NotNull JLabel titleField;
    // private final @NotNull JTextField titleField;
    private final @NotNull JTextArea textArea;


    public DetailPanel () {

        super ();

        setLayout ( new BoxLayout ( this, BoxLayout.Y_AXIS ) );
        // setLayout ( new Box ( this, BoxLayout.Y_AXIS ) );

        this.titleField = new JLabel ();
        int padding = 3;
        titleField.setBorder( new EmptyBorder ( padding, padding, padding, padding ) );
        // titleField.setIcon ( new MetalIconFactory.FileIcon16() );
        // titleField.setHorizontalTextPosition ( SwingConstants.LEFT );
        // titleField.setHorizontalAlignment ( SwingConstants.LEFT );
        // titleField.setAlignmentX ( 0 );
        titleField.setFont ( new Font ( null, Font.PLAIN, 14 ) );
        // this.titleField = new JTextField();

        textArea = new JTextArea ();
        padding = 5;
        textArea.setBorder( new EmptyBorder ( padding, padding, padding, padding ) );
        textArea.setFont ( new Font ( null, Font.PLAIN, 12 ) );
        add ( titleField );
        add ( new JScrollPane ( textArea ) );
        // add ( new JLabel ( "T Icon", T_ICON, SwingConstants.LEFT ) );
    }

    public void setTitle ( @Nullable String title ) {
        label.setText ( title );
    }

    public void setTitle ( @Nullable String title ) {
        titleField.setText ( title );
    }

    public void setText ( @Nullable String text ) {
        textArea.setText ( text );
    }
}

 */
