package com.luisa.iAlacena.storage.utils;

import com.luisa.iAlacena.storage.exception.StorageException;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TikaMimeTypeDetector implements MimeTypeDetector{

    private Tika tika;

    public TikaMimeTypeDetector() {
        tika = new Tika();
    }

    @Override
    public String getMimeType(Resource resource) {
        try {
            if (resource.isFile())
                return tika.detect(resource.getFile());
            else
                return tika.detect(resource.getInputStream());

        } catch (IOException ex) {
            throw new StorageException("Error trying to get the MIME type", ex);
        }
    }
}