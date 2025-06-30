package com.example.springboot.mapper;

import com.example.springboot.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
  int insert(Comment comment);

  List<Comment> selectByNoteId(Integer noteId);

  List<Comment> selectByNoteIdPaged(@Param("noteId") Integer noteId, @Param("offset") int offset,
      @Param("pageSize") int pageSize);

  int deleteById(Integer id);

  int countByNoteId(Integer noteId);

  Comment selectById(Integer id);
}