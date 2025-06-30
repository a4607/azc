package com.example.springboot.entity;

import java.util.Date;
import java.util.List;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

@Data
public class Note {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer userId;
  private String username;
  private String title;
  private String content;
  private Integer isPublic;
  private String shareCode;
  private Integer viewCount;
  private Integer likeCount;
  private Integer commentCount;
  private Integer favoriteCount;
  private String coverImage;
  private Date createTime;
  private Date updateTime;
  private List<Integer> tagIds;
  @TableField(exist = false)
  private List<Tag> tags;
  private User author;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Integer isPublic) {
    this.isPublic = isPublic;
  }

  public String getShareCode() {
    return shareCode;
  }

  public void setShareCode(String shareCode) {
    this.shareCode = shareCode;
  }

  public Integer getViewCount() {
    return viewCount;
  }

  public void setViewCount(Integer viewCount) {
    this.viewCount = viewCount;
  }

  public Integer getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount;
  }

  public Integer getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Integer commentCount) {
    this.commentCount = commentCount;
  }

  public Integer getFavoriteCount() {
    return favoriteCount;
  }

  public void setFavoriteCount(Integer favoriteCount) {
    this.favoriteCount = favoriteCount;
  }

  public String getCoverImage() {
    return coverImage;
  }

  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public List<Integer> getTagIds() {
    return tagIds;
  }

  public void setTagIds(List<Integer> tagIds) {
    this.tagIds = tagIds;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }
}