package org.unileipzig.jqassistant.plugin.parser.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FilePatternMatcher;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.unileipzig.jqassistant.plugin.parser.api.model.ModuleDescriptor;
import org.unileipzig.jqassistant.plugin.parser.api.scanner.ModuleScannerPlugin;

import java.io.IOException;

@Requires(FileDescriptor.class)
public class JavaModuleScannerPlugin extends ModuleScannerPlugin<ModuleDescriptor> {

    public static final String PROPERTY_INCLUDE = "java.file.include";
    public static final String PROPERTY_EXCLUDE = "java.file.exclude";

    private FilePatternMatcher filePatternMatcher;

    @Override
    protected void configure() {
        filePatternMatcher = FilePatternMatcher.Builder.newInstance().include(getStringProperty(PROPERTY_INCLUDE, ""))
            .exclude(getStringProperty(PROPERTY_EXCLUDE, null)).build();
    }

    @Override
    public boolean accepts(FileResource item, String path, Scope scope) throws IOException {
        return filePatternMatcher.accepts(path);
    }

    @Override
    public ModuleDescriptor scan(String input, ModuleDescriptor descriptor, Scope scope, Scanner scanner) throws IOException {
        return descriptor;
    }
}