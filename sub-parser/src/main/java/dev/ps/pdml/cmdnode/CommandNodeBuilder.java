package dev.ps.pdml.cmdnode;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.MutableParameters;
import dev.ps.prt.type.util.DocSupplierUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;

import java.io.IOException;
import java.util.function.Supplier;

public class CommandNodeBuilder {

    public interface CommandNodeExecutor {

        @Nullable CommandNodeResult execute (
            @Nullable Arguments arguments,
            @NotNull CommandNodeExecutorContext context,
            @NotNull NodeTag nodeTag ) throws IOException, PdmlException;
    }

    private final @NotNull String name;
    // private @Nullable List<String> alternativeNames = null;

    private final @NotNull MutableParameters parametersBuilder = new MutableParameters ();

    private @Nullable CommandNodeExecutor executor = null;

    private @Nullable String docTitle = null;
    private @Nullable String docDescription = null;
    private @Nullable String docExamples = null;


    public CommandNodeBuilder ( @NotNull String name ) {
        this.name = name;
    }


    public @NotNull CommandNodeBuilder parameter ( @NotNull Parameter<?> parameter ) {

        parametersBuilder.append ( parameter );
        return this;
    }

    /* TODO?
    public @NotNull CommandNodeBuilder parameters (
        @NotNull Consumer<@NotNull ParametersBuilder> consumer ) {

        consumer.accept ( parametersBuilder );
        return this;
    }
     */

    /* TODO? add methods like the following one for common types
    public @NotNull CommandNodeBuilder stringParam (
        @NotNull String name,
        @Nullable String defaultValue,
        @Nullable String description ) {

        parametersBuilder.append ( ParameterBuilder.ofString ( name, defaultValue, description );
        return this;
    }
     */


    public @NotNull CommandNodeBuilder executor ( @NotNull CommandNodeExecutor executor ) {
        this.executor = executor;
        return this;
    }

    public @NotNull CommandNodeBuilder title ( @NotNull String docTitle ) {
        this.docTitle = docTitle;
        return this;
    }

    public @NotNull CommandNodeBuilder description ( @NotNull String docDescription ) {
        this.docDescription = docDescription;
        return this;
    }

    public @NotNull CommandNodeBuilder examples ( @NotNull String docExamples ) {
        this.docExamples = docExamples;
        return this;
    }

    public @NotNull CommandNode build() {

        if ( executor == null ) {
            throw new IllegalStateException ( "Command 'executor' must be defined." );
        }

        @Nullable Supplier<SimpleDocumentation> documentationSupplier = DocSupplierUtil.of (
            name, docTitle, docDescription, docExamples );

        return new CommandNode (
            name, parametersBuilder.toImmutableOrNull (), documentationSupplier ) {

            @Override
            public @Nullable CommandNodeResult execute (
                @Nullable Arguments arguments,
                @NotNull CommandNodeExecutorContext context,
                @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

                return executor.execute ( arguments, context, nodeTag );
            }
        };
    }
}
