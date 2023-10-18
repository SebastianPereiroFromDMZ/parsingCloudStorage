package ru.netology.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;
import ru.netology.security.JwtTokenUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    @InjectMocks
    private StorageService storageService;
    @Mock
    FileRepository fileRepository;
    @Mock
    JwtTokenUtils jwtTokenUtils;
    private final File file = new File();
    private final List<File> fileList = new ArrayList<>();
    private final String OWNER = "owner";
    private final String FILENAME = "filename";

    @Test
    void getFilesTest() {
        String token = UUID.randomUUID().toString();
        int limit = 1;
        file.setFilename(FILENAME);
        fileList.add(file);

        given(jwtTokenUtils.getUsernameFromToken(token.substring(7))).willReturn(OWNER);
        given(fileRepository.findAllByOwner(OWNER)).willReturn(Optional.of(fileList));

        List<FileResponse> responseList = storageService.getFiles(token, limit);

        assertEquals(responseList.get(0).getFilename(), file.getFilename());
    }

    @Test
    void uploadFileTest() throws IOException {
        String token = UUID.randomUUID().toString();
        byte[] content = token.getBytes();
        file.setFilename(FILENAME);
        file.setContent(content);
        file.setSize(36L);
        MultipartFile multipartFile = new MockMultipartFile(FILENAME, content);

        storageService.uploadFile(token, FILENAME, multipartFile);

        verify(fileRepository, times(1)).save(file);
    }

    @Test
    void deleteFileTest() {
        String token = UUID.randomUUID().toString();

        given(jwtTokenUtils.getUsernameFromToken(token.substring(7))).willReturn(OWNER);

        storageService.deleteFile(token, FILENAME);

        verify(fileRepository, times(1)).removeByFilenameAndOwner(FILENAME, OWNER);
    }

    @Test
    void downloadFileTest() {
        String token = UUID.randomUUID().toString();
        file.setFilename(FILENAME);

        given(jwtTokenUtils.getUsernameFromToken(token.substring(7))).willReturn(OWNER);
        given(fileRepository.findByFilenameAndOwner(FILENAME, OWNER)).willReturn(file);

        File newFile = storageService.downloadFile(token, FILENAME);

        assertEquals(file.getFilename(), newFile.getFilename());
    }

    @Test
    void renameFileTest() {
        String token = UUID.randomUUID().toString();

        given(jwtTokenUtils.getUsernameFromToken(token.substring(7))).willReturn(OWNER);

        storageService.renameFile(token, FILENAME, FILENAME);

        verify(fileRepository, times(1)).renameFile(FILENAME, FILENAME, OWNER);
    }
}