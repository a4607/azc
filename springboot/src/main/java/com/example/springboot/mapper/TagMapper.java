package com.example.springboot.mapper;

import com.example.springboot.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TagMapper {
  List<Tag> findByUsername(@Param("username") String username);

  List<Tag> findAll();

  int insert(Tag tag);

  int update(Tag tag);

  int delete(@Param("id") Integer id);

  Integer countNotesByTagIdAndUsername(@Param("tagId") Integer tagId, @Param("username") String username);

  List<Tag> findByIds(@Param("ids") List<Integer> ids);
}