package com.example.comepaga.model.gridfs;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Data
public abstract class GridFsObject {

    public abstract void setResource(Resource resource);
    public abstract InputStream getInputStream() throws IOException;
    public abstract String getFileName();
    public abstract String getContentType();
    public abstract String getId();
}
