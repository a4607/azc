# AZC 笔记管理系统

一个基于Vue.js前端和Spring Boot后端的全栈笔记管理系统。

## 项目结构

```
azc/
├── frontend/          # Vue.js前端应用
├── springboot/        # Spring Boot后端应用
└── MySQL/            # 数据库脚本
```

## 技术栈

### 前端
- Vue.js 3
- Vue Router
- Vuex
- Vite
- Element Plus (UI组件库)

### 后端
- Spring Boot
- Spring Security
- MyBatis
- MySQL
- JWT认证

## 功能特性

- 用户注册和登录
- 笔记的创建、编辑、删除
- 笔记分类和标签管理
- 笔记分享功能
- 富文本编辑器
- 响应式设计

## 快速开始

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 后端启动
```bash
cd springboot
mvn spring-boot:run
```

### 数据库
1. 创建MySQL数据库
2. 执行 `MySQL/frontend_notes.sql` 脚本

## 开发环境

- Node.js 16+
- Java 8+
- MySQL 5.7+
- Maven 3.6+

## 许可证

MIT License 