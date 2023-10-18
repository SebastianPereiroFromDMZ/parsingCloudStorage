package ru.netology.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;
import ru.netology.security.JwtTokenUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StorageService {
    private final FileRepository fileRepository;
    private final JwtTokenUtils jwtTokenUtils;

    public StorageService(FileRepository fileRepository, JwtTokenUtils jwtTokenUtils) {
        this.fileRepository = fileRepository;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public List<FileResponse> getFiles(String authToken, int limit) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken.substring(7));
        Optional<List<File>> fileList = fileRepository.findAllByOwner(owner);
        return fileList.get().stream().map(fr -> new FileResponse(fr.getFilename(), fr.getSize()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken.substring(7));
        fileRepository.save(new File(filename, file.getContentType(), file.getSize(), file.getBytes(), owner));
    }

    public void deleteFile(String authToken, String filename) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken.substring(7));
        fileRepository.removeByFilenameAndOwner(filename, owner);
    }

    public File downloadFile(String authToken, String filename) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken.substring(7));
        return fileRepository.findByFilenameAndOwner(filename, owner);
    }

    public void renameFile(String authToken, String filename, String newFilename) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken.substring(7));
        fileRepository.renameFile(filename, newFilename, owner);
    }
}