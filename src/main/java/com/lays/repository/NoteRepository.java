package com.lays.repository;

import com.lays.model.Note;
import com.lays.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByAuthor(User author);

    List<Note> findByIsPublicTrue();

    @Query("select n from Note n where lower(n.title) like lower(concat('%', :keyword, '%'))")
    List<Note> findByTitleContaining(@Param("keyword") String keyword);
}