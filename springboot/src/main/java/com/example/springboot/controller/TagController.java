package com.example.springboot.controller;

import com.example.springboot.entity.Tag;
import com.example.springboot.service.TagService;
import com.example.springboot.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
  @Autowired
  private TagService tagService;

  @GetMapping
  public Result getTags(@RequestParam(required = false) String username) {
    try {
      List<Tag> tags = tagService.getTags(username);
      return Result.success(tags);
    } catch (Exception e) {
      return Result.error("获取标签列表失败");
    }
  }

  @PostMapping
  public Result createTag(@RequestBody Tag tag) {
    try {
      tagService.createTag(tag);
      return Result.success(tag);
    } catch (Exception e) {
      return Result.error("创建标签失败");
    }
  }

  @PutMapping("/{id}")
  public Result updateTag(@PathVariable Integer id, @RequestBody Tag tag) {
    try {
      tag.setId(id);
      tagService.updateTag(tag);
      return Result.success(tag);
    } catch (Exception e) {
      return Result.error("更新标签失败");
    }
  }

  @DeleteMapping("/{id}")
  public Result deleteTag(@PathVariable Integer id) {
    try {
      tagService.deleteTag(id);
      return Result.success(null);
    } catch (Exception e) {
      return Result.error("删除标签失败");
    }
  }
}