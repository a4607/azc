package com.example.springboot.service.impl;

import com.example.springboot.entity.Comment;
import com.example.springboot.mapper.CommentMapper;
import com.example.springboot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired
  private CommentMapper commentMapper;

  @Override
  public int addComment(Comment comment) {
    return commentMapper.insert(comment);
  }

  @Override
  public List<Comment> getCommentsByNoteId(Integer noteId) {
    return commentMapper.selectByNoteId(noteId);
  }

  @Override
  public int deleteComment(Integer id, Integer userId, boolean isAdmin) {
    // 仅本人或管理员可删
    Comment comment = commentMapper.selectById(id);
    if (comment == null)
      return 0;
    if (isAdmin || (comment.getUserId() != null && comment.getUserId().equals(userId))) {
      return commentMapper.deleteById(id);
    }
    return 0;
  }

  @Override
  public Map<String, Object> getCommentsByNoteIdPaged(Integer noteId, int page, int pageSize) {
    // 1. 查出该笔记下所有评论
    List<Comment> allComments = commentMapper.selectByNoteId(noteId);

    // 2. 将所有评论按父子关系构建成树
    Map<Integer, List<Comment>> parentIdToChildren = allComments.stream()
        .filter(c -> c.getParentId() != null)
        .collect(Collectors.groupingBy(Comment::getParentId));

    allComments.forEach(comment -> {
      comment.setChildren(parentIdToChildren.get(comment.getId()));
    });

    // 3. 筛选出所有根评论
    List<Comment> rootComments = allComments.stream()
        .filter(c -> c.getParentId() == null)
        .collect(Collectors.toList());

    // 4. 对根评论进行手动分页
    int total = rootComments.size();
    int offset = (page - 1) * pageSize;
    List<Comment> pagedRootComments = rootComments.stream()
        .skip(offset)
        .limit(pageSize)
        .collect(Collectors.toList());

    Map<String, Object> result = new HashMap<>();
    result.put("list", pagedRootComments);
    result.put("total", total);
    return result;
  }
}