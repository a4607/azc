package com.example.springboot.mapper;

import com.example.springboot.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NoteMapper {
        List<Note> findByUsername(
                        @Param("username") String username,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit,
                        @Param("keyword") String keyword);

        int countByUsername(
                        @Param("username") String username,
                        @Param("keyword") String keyword);

        Note findById(@Param("id") Integer id, @Param("username") String username);

        Note findByShareCode(@Param("shareCode") String shareCode);

        List<Note> findPublicNotes(
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit,
                        @Param("keyword") String keyword);

        int countPublicNotes(@Param("keyword") String keyword);

        int insert(Note note);

        int update(Note note);

        int delete(@Param("id") Integer id, @Param("username") String username);

        int updatePublicStatus(
                        @Param("id") Integer id,
                        @Param("username") String username,
                        @Param("isPublic") Integer isPublic,
                        @Param("shareCode") String shareCode);

        int incrementViewCount(@Param("id") Integer id);

        void insertNoteTag(@Param("noteId") Integer noteId, @Param("tagId") Integer tagId);

        void deleteNoteTagsByNoteId(@Param("noteId") Integer noteId);

        List<Integer> findTagIdsByNoteId(@Param("noteId") Integer noteId);

        List<Note> findByTagId(@Param("tagId") Integer tagId);

        List<Note> findNotesByTagId(@Param("tagId") Integer tagId, @Param("username") String username);
}