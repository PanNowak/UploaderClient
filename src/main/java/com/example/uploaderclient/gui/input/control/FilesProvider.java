package com.example.uploaderclient.gui.input.control;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.uploaderclient.api.boundary.DataSource;
import com.example.uploaderclient.api.control.FileBasedSource;
import com.example.uploaderclient.api.control.ProcessingService;
import com.example.uploaderclient.gui.input.boundary.DataProvider;
import com.example.uploaderclient.parser.core.entity.FileType;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

@Component
public class FilesProvider implements DataProvider {

    private final FileChooser fileChooser;

    @Autowired
    FilesProvider(@Value("${initial.directory}") File initialDirectory,
                  ProcessingService processingService) {
        ExtensionFilter extensionFilter = getExtensionFilter(processingService.getAllSupportedFileTypes());
        this.fileChooser = createFileChooser(initialDirectory, extensionFilter);
    }

    @Override
    public List<DataSource> getDataSources(Window container) {
        return CollectionUtils.emptyIfNull(fileChooser.showOpenMultipleDialog(container)).stream()
                .map(File::toPath)
                .map(FileBasedSource::new)
                .collect(Collectors.toList());
    }

    private ExtensionFilter getExtensionFilter(Collection<FileType> fileTypes) {
        return fileTypes.stream()
                .map(FileType::getFileExtension)
                .map(extension -> "*." + extension)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        extensions -> new ExtensionFilter("Supported extensions", extensions)));
    }

    private FileChooser createFileChooser(File initialDirectory, ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose files to upload");
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser;
    }
}