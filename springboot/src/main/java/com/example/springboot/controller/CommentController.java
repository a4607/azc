package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Comment;
import com.example.springboot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
  @Autowired
  private CommentService commentService;

  @PostMapping("")
  public Result addComment(@RequestBody Comment comment) {
    int res = commentService.addComment(comment);
    if (res > 0) {
      return Result.success(comment);
    } else {
      return Result.error("评论失败");
    }
  }

  @GetMapping("/note/{noteId}")
  public Result getCommentsByNoteId(@PathVariable Integer noteId) {
    List<Comment> comments = commentService.getCommentsByNoteId(noteId);
    return Result.success(comments);
  }

  @GetMapping("/note/{noteId}/paged")
  public Result getCommentsByNoteIdPaged(@PathVariable Integer noteId, @RequestParam int page,
      @RequestParam int pageSize) {
    Map<String, Object> result = commentService.getCommentsByNoteIdPaged(noteId, page, pageSize);
    return Result.success(result);
  }

  @DeleteMapping("/{id}")
  public Result deleteComment(@PathVariable Integer id, @RequestParam Integer userId,
      @RequestParam(defaultValue = "false") boolean isAdmin) {
    int res = commentService.deleteComment(id, userId, isAdmin);
    if (res > 0) {
      return Result.success("删除成功");
    } else {
      return Result.error("无权限或评论不存在");
    }
  }
}