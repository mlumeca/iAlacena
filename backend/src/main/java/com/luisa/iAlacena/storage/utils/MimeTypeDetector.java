package com.luisa.iAlacena.storage.utils;

import org.springframework.core.io.Resource;

public interface MimeTypeDetector {
    String getMimeType(Resource resource);
}