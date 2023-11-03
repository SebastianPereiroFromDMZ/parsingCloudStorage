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

    //@InjectMocks что то на подобие того что говорим в этот класс который помечен @InjectMocks будем встраивать заглушки помоченные @Mock
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
        //при вызывании jwtTokenUtils.getUsernameFromToken(token.substring(7))) возвращается OWNER
        given(jwtTokenUtils.getUsernameFromToken(token.substring(7))).willReturn(OWNER);
        //при вызове fileRepository.findAllByOwner(OWNER)) возвращает Optional.of(fileList)
        given(fileRepository.findAllByOwner(OWNER)).willReturn(Optional.of(fileList));

        //у нашего сервиса вызываем getFiles в котором уже зашиты ответы двух верхних методов
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
        //Макетная реализация интерфейса MultipartFile.
        //Полезно в сочетании с MockMultipartStartServletRequest для тестирования контроллеров приложений, которые получают доступ к многопартовым загрузкам.
        MultipartFile multipartFile = new MockMultipartFile(FILENAME, content);

        //как бы загружаем файл
        storageService.uploadFile(token, FILENAME, multipartFile);

        //verify Проверка определенного поведения, произошедшего один раз.

        //verify позволяет проверить точное число вызовов.
        //verify (mock, times (2)) .someMethod («некоторые arg»);
        //Парамы:
        //wantedNumberOfInvocations - требуемое количество просьб
        //Возврат:
        //режим проверки

        //другими словами проверяет сколько вызывался тот или иной метод
        verify(fileRepository, times(1)).save(file);
    }


    @Test
    void deleteFileTest() {
        //создаем токен
        String token = UUID.randomUUID().toString();

        //при вызове jwtTokenUtils.getUsernameFromToken(token.substring(7))) возвращаем .willReturn(OWNER);
        given(jwtTokenUtils.getUsernameFromToken(token.substring(7))).willReturn(OWNER);

        //удаляем файл
        storageService.deleteFile(token, FILENAME);

        //проверяем сколько раз был вызван removeByFilenameAndOwner(FILENAME, OWNER);
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

        //инициируем переименование
        storageService.renameFile(token, FILENAME, FILENAME);

        //и смотрим склько раз был вызван тот или иной метод
        verify(fileRepository, times(1)).renameFile(FILENAME, FILENAME, OWNER);
    }
}