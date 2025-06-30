package com.example.springboot.service.impl;

import com.example.springboot.entity.Note;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Tag;
import com.example.springboot.mapper.NoteMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.mapper.TagMapper;
import com.example.springboot.service.NoteService;
import com.example.springboot.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {
  private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

  @Autowired
  private NoteMapper noteMapper;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private TagMapper tagMapper;

  // 辅助方法：从Markdown内容中提取第一张图片URL
  private String extractFirstImageUrl(String markdownContent) {
    if (markdownContent == null || markdownContent.isEmpty()) {
      return null;
    }
    // 匹配Markdown图片语法: ![alt text](image url "title")
    Pattern pattern = Pattern.compile("!\\[.*\\]\\((.*?)( \".*\")*\\)");
    Matcher matcher = pattern.matcher(markdownContent);
    if (matcher.find()) {
      return matcher.group(1); // 返回第一个捕获组，即URL
    }
    return null;
  }

  @Override
  public List<Note> getNotes(Integer page, Integer limit, String keyword) {
    String username = SecurityUtil.getCurrentUsername();
    logger.info("当前用户名: {}, 页码: {}, 每页数量: {}, 关键词: {}",
        username, page, limit, keyword);
    int offset = (page - 1) * limit;
    List<Note> notes = noteMapper.findByUsername(username, offset, limit, keyword);
    logger.info("查询到的笔记数量: {}", notes.size());
    return notes;
  }

  @Override
  public int getTotal(String keyword) {
    String username = SecurityUtil.getCurrentUsername();
    return noteMapper.countByUsername(username, keyword);
  }

  @Override
  public Note getNote(Integer id) {
    String username = SecurityUtil.getCurrentUsername();
    logger.info("获取笔记详情 - 笔记ID: {}, 用户名: {}", id, username);
    Note note = noteMapper.findById(id, username);
    if (note == null) {
      throw new RuntimeException("笔记不存在或无权访问");
    }
    // 获取笔记关联的标签ID
    List<Integer> tagIds = noteMapper.findTagIdsByNoteId(note.getId());
    note.setTagIds(tagIds);
    if (tagIds != null && !tagIds.isEmpty()) {
      List<Tag> tags = tagMapper.findByIds(tagIds);
      note.setTags(tags);
    } else {
      note.setTags(new ArrayList<>());
    }
    return note;
  }

  @Override
  public void createNote(Note note) {
    String username = SecurityUtil.getCurrentUsername();
    logger.info("创建笔记 - 用户名: {}", username);

    User user = userMapper.findByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    note.setUsername(username);
    note.setUserId(user.getId());
    note.setCreateTime(new Date());
    note.setUpdateTime(new Date());
    note.setViewCount(0);
    // 提取并设置封面图片
    note.setCoverImage(extractFirstImageUrl(note.getContent()));

    // 如果isPublic为1但没有shareCode，则生成一个
    if (note.getIsPublic() == 1 && note.getShareCode() == null) {
      note.setShareCode(UUID.randomUUID().toString().replace("-", ""));
    }

    logger.info("创建笔记 - 笔记信息: {}", note);
    noteMapper.insert(note);

    // 处理笔记与标签的关联
    List<Integer> tagIds = note.getTagIds();
    if (tagIds != null && !tagIds.isEmpty()) {
      // 过滤出数据库中真实存在的标签ID
      List<Tag> existingTags = tagMapper.findByIds(tagIds);
      List<Integer> existingTagIds = existingTags.stream().map(Tag::getId).collect(Collectors.toList());
      for (Integer tagId : existingTagIds) {
        noteMapper.insertNoteTag(note.getId(), tagId);
      }
    }
  }

  @Override
  public void updateNote(Note note) {
    String username = SecurityUtil.getCurrentUsername();
    logger.info("更新笔记 - 笔记ID: {}, 用户名: {}", note.getId(), username);

    Note existingNote = noteMapper.findById(note.getId(), username);
    if (existingNote == null) {
      throw new RuntimeException("笔记不存在或无权访问");
    }

    // 将传入的 note 对象的属性更新到 existingNote
    existingNote.setTitle(note.getTitle());
    existingNote.setContent(note.getContent());
    existingNote.setIsPublic(note.getIsPublic());
    existingNote.setTagIds(note.getTagIds()); // 更新标签

    // 提取并设置封面图片
    existingNote.setCoverImage(extractFirstImageUrl(note.getContent()));

    // 根据isPublic状态生成或清除shareCode
    if (existingNote.getIsPublic() != null) {
      if (existingNote.getIsPublic() == 1) {
        // 如果设置为公开但没有分享码，则生成一个
        if (existingNote.getShareCode() == null || existingNote.getShareCode().isEmpty()) {
          existingNote.setShareCode(UUID.randomUUID().toString().replace("-", ""));
        }
      } else {
        // 如果设置为私密，清除分享码
        existingNote.setShareCode(null);
      }
    }

    noteMapper.update(existingNote);

    // 处理笔记与标签的关联
    // 1. 删除所有旧的关联
    noteMapper.deleteNoteTagsByNoteId(existingNote.getId());
    // 2. 插入新的关联
    List<Integer> tagIds = existingNote.getTagIds();
    if (tagIds != null && !tagIds.isEmpty()) {
      // 过滤出数据库中真实存在的标签ID
      List<Tag> existingTags = tagMapper.findByIds(tagIds);
      List<Integer> existingTagIds = existingTags.stream().map(Tag::getId).collect(Collectors.toList());
      for (Integer tagId : existingTagIds) {
        noteMapper.insertNoteTag(existingNote.getId(), tagId);
      }
    }
  }

  @Override
  public void deleteNote(Integer id) {
    String username = SecurityUtil.getCurrentUsername();
    logger.info("删除笔记 - 笔记ID: {}, 用户名: {}", id, username);

    Note note = noteMapper.findById(id, username);
    if (note == null) {
      throw new RuntimeException("笔记不存在或无权访问");
    }

    noteMapper.delete(id, username);
  }

  @Override
  public List<Note> getPublicNotes(Integer page, Integer limit, String keyword) {
    logger.info("获取公开笔记 - 页码: {}, 每页数量: {}, 关键词: {}", page, limit, keyword);
    int offset = (page - 1) * limit;
    List<Note> notes = noteMapper.findPublicNotes(offset, limit, keyword);
    logger.info("查询到的公开笔记数量: {}", notes.size());
    return notes;
  }

  @Override
  public int getPublicTotal(String keyword) {
    return noteMapper.countPublicNotes(keyword);
  }

  @Override
  public Note getNoteByShareCode(String shareCode) {
    logger.info("通过分享码获取笔记 - 分享码: {}", shareCode);
    Note note = noteMapper.findByShareCode(shareCode);
    if (note != null) {
      // 查询并设置标签
      List<Integer> tagIds = noteMapper.findTagIdsByNoteId(note.getId());
      if (tagIds != null && !tagIds.isEmpty()) {
        List<Tag> tags = tagMapper.findByIds(tagIds);
        note.setTags(tags);
      }
      noteMapper.incrementViewCount(note.getId());
    }
    return note;
  }

  @Override
  public void updatePublicStatus(Integer id, Integer isPublic) {
    String username = SecurityUtil.getCurrentUsername();
    logger.info("更新笔记公开状态 - 笔记ID: {}, 用户名: {}, 是否公开: {}", id, username, isPublic);

    Note note = noteMapper.findById(id, username);
    if (note == null) {
      throw new RuntimeException("笔记不存在或无权访问");
    }

    // 如果设置为公开，生成分享码；如果设置为私密，清除分享码
    String shareCode = isPublic == 1 ? UUID.randomUUID().toString().replace("-", "") : null;

    // 更新笔记状态（包括 isPublic、status 和 shareCode）
    noteMapper.updatePublicStatus(id, username, isPublic, shareCode);
  }

  @Override
  public void incrementViewCount(Integer id) {
    noteMapper.incrementViewCount(id);
  }

  @Override
  public List<Note> getNotesByTag(Integer tagId) {
    return noteMapper.findByTagId(tagId);
  }

  @Override
  public List<Note> getNotesByTagId(Integer tagId) {
    String username = SecurityUtil.getCurrentUsername();
    return noteMapper.findNotesByTagId(tagId, username);
  }
}