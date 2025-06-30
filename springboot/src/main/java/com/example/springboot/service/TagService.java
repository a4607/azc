package com.example.springboot.service;

import com.example.springboot.entity.Tag;
import java.util.List;

public interface TagService {
  List<Tag> getTags(String username);

  void createTag(Tag tag);

  void updateTag(Tag tag);

  void deleteTag(Integer id);
}