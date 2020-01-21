package com.example.uploaderclient.gui.input.boundary;

import com.example.uploaderclient.uploader.api.boundary.DataSource;
import javafx.stage.Window;

import java.util.List;

@FunctionalInterface
public interface DataProvider {

    List<DataSource> getDataSources(Window container);
}