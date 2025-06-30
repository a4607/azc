package com.example.springboot.controller;

import com.example.springboot.entity.Note;
import com.example.springboot.service.NoteService;
import com.example.springboot.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {
  private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

  @Autowired
  private NoteService noteService;

  @GetMapping("/list")
  public Result getNotes(
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(required = false) String keyword) {
    try {
      List<Note> notes = noteService.getNotes(page, limit, keyword);
      int total = noteService.getTotal(keyword);
      return Result.success(notes, total);
    } catch (Exception e) {
      logger.error("获取笔记列表失败", e);
      return Result.error("获取笔记列表失败");
    }
  }

  @GetMapping("/{id}")
  public Result getNote(@PathVariable Integer id) {
    Note note = noteService.getNote(id);
    return Result.success(note);
  }

  @PostMapping
  public Result createNote(@RequestBody Note note) {
    noteService.createNote(note);
    return Result.success(note);
  }

  @PutMapping("/{id}")
  public Result updateNote(@PathVariable Integer id, @RequestBody Note note) {
    note.setId(id);
    noteService.updateNote(note);
    return Result.success(note);
  }

  @DeleteMapping("/{id}")
  public Result deleteNote(@PathVariable Integer id) {
    noteService.deleteNote(id);
    return Result.success(null);
  }

  @GetMapping("/public")
  public Result getPublicNotes(
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(required = false) String keyword) {
    try {
      List<Note> notes = noteService.getPublicNotes(page, limit, keyword);
      int total = noteService.getPublicTotal(keyword);
      return Result.success(notes, total);
    } catch (Exception e) {
      logger.error("获取公开笔记列表失败", e);
      return Result.error("获取公开笔记列表失败");
    }
  }

  @GetMapping("/share/{shareCode}")
  public Result getNoteByShareCode(@PathVariable String shareCode) {
    Note note = noteService.getNoteByShareCode(shareCode);
    return Result.success(note);
  }

  @PutMapping("/{id}/public")
  public Result updatePublicStatus(
      @PathVariable Integer id,
      @RequestParam Integer isPublic) {
    noteService.updatePublicStatus(id, isPublic);
    return Result.success(null, 0);
  }

  @GetMapping("/tag")
  public Result getNotesByTag(@RequestParam Integer tagId) {
    List<Note> notes = noteService.getNotesByTagId(tagId);
    java.util.Map<String, Object> map = new java.util.HashMap<>();
    map.put("items", notes);
    map.put("total", notes.size());
    return Result.success(map);
  }
}