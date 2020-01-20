package com.example.uploaderclient.gui.input.boundary;

import java.util.List;

import com.example.uploaderclient.api.boundary.DataSource;

import javafx.stage.Window;

@FunctionalInterface
public interface DataProvider {

    List<DataSource> getDataSources(Window container);
}