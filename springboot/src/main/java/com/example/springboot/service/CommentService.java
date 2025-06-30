package com.example.springboot.service;

import com.example.springboot.entity.Comment;
import java.util.List;
import java.util.Map;

public interface CommentService {
  int addComment(Comment comment);

  List<Comment> getCommentsByNoteId(Integer noteId);

  int deleteComment(Integer id, Integer userId, boolean isAdmin);

  Map<String, Object> getCommentsByNoteIdPaged(Integer noteId, int page, int pageSize);
}