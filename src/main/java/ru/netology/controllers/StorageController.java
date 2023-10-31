package ru.netology.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entities.File;
import ru.netology.services.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/list")
    //@RequestHeader иногда вам требуется получить в качестве параметров вашего метода в контроллере непосредственно заголовки HTTP-запроса.
    public ResponseEntity<List<FileResponse>> getAllFiles(@RequestHeader("auth-token") String authToken,
                                                          @RequestParam("limit") int limit) {
        return ResponseEntity.ok(storageService.getFiles(authToken, limit));
    }

    //ResponseEntity<?>, где под ? понимается любой Java объект.
    //Конструктор ResponseEntity позволяет перегружать этот объект, добавляя в него не только наш возвращаемый тип,
    //но и статус, чтобы фронтенд мог понимать, что именно пошло не так.
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {
        //MultipartFile Представление выгруженного файла, полученного в многопортовом запросе.
        //Содержимое файла хранится в памяти или временно на диске.
        //В любом случае пользователь отвечает за копирование содержимого файла в сеансовый уровень или постоянное хранилище,
        //как и при необходимости. Временное хранилище будет очищено по окончании обработки запроса.
        storageService.uploadFile(authToken, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody Map<String, String> fileNameRequest) {
        storageService.renameFile(authToken, filename, fileNameRequest.get("filename"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename) {
        storageService.deleteFile(authToken, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String filename) {
        File file = storageService.downloadFile(authToken, filename);
        return ResponseEntity.ok()
                //.contentType Задайте тип носителя тела, как указано в заголовке Content-Type.
                //Парамы:
                //contentType - тип содержимого
                //Возврат:
                //этот строитель
                //См. также:
                //StartHeaders.setContentType (MediaType)

                //MediaType Подкласс MiveType, который добавляет поддержку параметров качества, определенных в спецификации HTTP.

                //.parseMediaType Разберите данную строку на один тип MediaType.
                //Парамы:
                //mediaType - строка для синтаксического анализа
                //Возврат:
                //тип носителя
                //Броски:
                //InvalidMediaTypeException - если значение типа носителя не может быть проанализировано
                .contentType(MediaType.parseMediaType(file.getType()))
                //.header Добавьте данное значение одиночного заголовка под данным именем.
                //Парамы:
                //heeyName - имя заголовка heeyValues - значение (значения) заголовка
                //Возврат:
                //это здание

                //HttpHeaders.CONTENT_DISPOSITION Имя поля заголовка HTTP Content-Disposition.
                //См. также:
                //RFC 6266
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                //.body Задайте тело объекта ответа и возвращает его.
                //Парамы:
                //body - тело объекта ответа
                //Возврат:
                //построенный объект ответа
                .body(file.getContent());
    }
}