package com.example.springboot.service;

import com.example.springboot.entity.Note;
import java.util.List;

public interface NoteService {
  List<Note> getNotes(Integer page, Integer limit, String keyword);

  int getTotal(String keyword);

  Note getNote(Integer id);

  void createNote(Note note);

  void updateNote(Note note);

  void deleteNote(Integer id);

  // 公开访问相关方法
  List<Note> getPublicNotes(Integer page, Integer limit, String keyword);

  int getPublicTotal(String keyword);

  Note getNoteByShareCode(String shareCode);

  void updatePublicStatus(Integer id, Integer isPublic);

  void incrementViewCount(Integer id);

  List<Note> getNotesByTag(Integer tagId);

  List<Note> getNotesByTagId(Integer tagId);
}