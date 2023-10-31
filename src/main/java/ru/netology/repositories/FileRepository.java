package ru.netology.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.entities.File;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    //@Query:
    //Чтобы определить SQL для выполнения метода репозитория данных Spring,
    //можно аннотировать метод аннотацией @ Query - его атрибут value содержит JPQL или SQL для выполнения.

    //@Param:
    //По умолчанию в JPA данных пружины используется привязка параметров на основе положения,
    //как было показано в предыдущих учебных пособиях. Мы также можем использовать именованный параметр с аннотацией @ Param,
    //чтобы дать параметру метода конкретное имя и привязать имя в запросе.
    //Это упрощает рефакторинг кода в случае необходимости добавления/удаления дополнительных параметров.
    //
    //@ Param работает как с @ Query, так и с @ NamedQuery.
    @Query(value = "select f from File f where f.owner = :owner")
    Optional<List<File>> findAllByOwner(@Param("owner") String owner);

    //кастомный запрос
    File findByFilenameAndOwner(String filename, String owner);

    //кастомный запрос
    void removeByFilenameAndOwner(String filename, String owner);

    @Modifying
    //Аннотация @Modifying используется для улучшения аннотации @Query,
    //чтобы мы могли выполнять не только запросы SELECT , но также запросы INSERT , UPDATE , DELETE и даже DDL .
    @Query("update File f set f.filename = :newName where f.filename = :filename and f.owner = :owner")
    void renameFile(@Param("filename") String filename, @Param("newName") String newFilename, @Param("owner") String owner);
}