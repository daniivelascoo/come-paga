package com.example.comepaga.model.restaurant;

import com.example.comepaga.model.gridfs.GridFsObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;

import java.io.IOException;
import java.io.InputStream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenRestaurante extends GridFsObject {

    @Id
    @JsonProperty("_id")
    private String id;
    @JsonProperty("imagen")
    private Resource resource;

    @JsonProperty("_contentType")
    private String contentType;

    @Override
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }

    @Override
    public String getFileName() {
        return this.resource.getFilename();
    }
}
