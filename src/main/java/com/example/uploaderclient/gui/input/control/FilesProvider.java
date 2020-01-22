package com.example.uploaderclient.gui.input.control;

import com.example.uploaderclient.gui.input.boundary.DataProvider;
import com.example.uploaderclient.uploader.api.boundary.DataSource;
import com.example.uploaderclient.uploader.api.control.FileBasedSource;
import com.example.uploaderclient.uploader.api.control.ProcessingService;
import com.example.uploaderclient.uploader.parser.core.entity.FileType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilesProvider implements DataProvider {

    private final FileChooser fileChooser;

    @Autowired
    FilesProvider(@Value("#{systemProperties['user.home']}") File initialDirectory,
                  ProcessingService processingService) {
        ExtensionFilter extensionFilter = getExtensionFilter(processingService.getAllSupportedFileTypes());
        this.fileChooser = createFileChooser(initialDirectory, extensionFilter);
    }

    @Override
    public List<DataSource> getDataSources(Stage primaryStage) {
        return CollectionUtils.emptyIfNull(fileChooser.showOpenMultipleDialog(primaryStage)).stream()
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