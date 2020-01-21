package com.example.uploaderclient.uploader.parser.core.entity;

public enum FileType {

    CSV("csv"),
    XML("xml");

    private final String fileExtension;

    FileType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}