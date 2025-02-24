package com.luisa.iAlacena.storage;

import com.luisa.iAlacena.storage.exception.StorageException;
import com.luisa.iAlacena.storage.model.FileMetadata;
import com.luisa.iAlacena.storage.model.LocalFileMetadataImpl;
import com.luisa.iAlacena.storage.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${storage.location:uploads}")
    private String storageLocation;

    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() {
        rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public FileMetadata store(MultipartFile file) {
        try {
            String filename = store(file.getBytes(), file.getOriginalFilename(), file.getContentType());
            return LocalFileMetadataImpl.of(filename);
        } catch (Exception ex) {
            throw new StorageException("Error storing file: " + file.getOriginalFilename(), ex);
        }
    }

    @Override
    public Resource loadAsResource(String id) {
        try {
            Path file = load(id);
            UrlResource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + id);
            }
        } catch (MalformedURLException ex) {
            throw new StorageException("Could not read file: " + id);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Files.deleteIfExists(load(filename));
        } catch (IOException e) {
            throw new StorageException("Could not delete file: " + filename);
        }
    }

    private String store(byte[] file, String filename, String contentType) throws Exception {
        String newFilename = StringUtils.cleanPath(filename);
        if (file.length == 0) {
            throw new StorageException("The file is empty");
        }
        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            Files.copy(inputStream, rootLocation.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new StorageException("Error storing file: " + newFilename, ex);
        }
        return newFilename;
    }

    private Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public void deleteAll() {
        try {
            FileSystemUtils.deleteRecursively(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not delete all");
        }
    }
}