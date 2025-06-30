package com.example.springboot.service.impl;

import com.example.springboot.entity.Tag;
import com.example.springboot.mapper.TagMapper;
import com.example.springboot.service.TagService;
import com.example.springboot.common.BusinessException;
import com.example.springboot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
  @Autowired
  private TagMapper tagMapper;

  @Override
  public List<Tag> getTags(String username) {
    if (username == null || username.isEmpty()) {
      throw new BusinessException("用户名不能为空");
    }
    List<Tag> tags = tagMapper.findByUsername(username);
    for (Tag tag : tags) {
      try {
        Integer noteCount = tagMapper.countNotesByTagIdAndUsername(tag.getId(), username);
        tag.setNoteCount(noteCount != null ? noteCount : 0);
      } catch (Exception e) {
        System.err.println("获取标签 " + tag.getName() + " 的笔记数量失败: " + e.getMessage());
        tag.setNoteCount(0); // 出现错误时，将笔记数量设置为0
      }
    }
    return tags;
  }

  @Override
  public void createTag(Tag tag) {
    tag.setUsername(SecurityUtil.getCurrentUsername());
    tagMapper.insert(tag);
  }

  @Override
  public void updateTag(Tag tag) {
    tagMapper.update(tag);
  }

  @Override
  public void deleteTag(Integer id) {
    tagMapper.delete(id);
  }
}