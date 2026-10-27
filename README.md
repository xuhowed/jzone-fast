# JZone-Fast

<p align="center">
  <strong>轻量级 Java 快速开发脚手架 / Minimalist Rapid Development Scaffold</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/JDK-17+-orange.svg" alt="JDK 17+" />
  <img src="https://img.shields.io/badge/SpringBoot-3.2.5-brightgreen.svg" alt="SpringBoot 3.2.5" />
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg" alt="MyBatis-Plus 3.5.7" />
  <img src="https://img.shields.io/badge/Sa--Token-1.38.0-red.svg" alt="Sa-Token 1.38.0" />
  <img src="https://img.shields.io/badge/Vue-3.4+-green.svg" alt="Vue 3" />
  <img src="https://img.shields.io/badge/Element--Plus-2.7+-blue.svg" alt="Element Plus" />
  <img src="https://img.shields.io/badge/Vite-5.2+-purple.svg" alt="Vite 5" />
</p>

---

## 1. 项目简介

**JZone-Fast** 是一套面向中小团队与 MVP（最小可行产品）的轻量级快速开发脚手架。  
项目追求**极低的运维部署成本**与**极致的业务交付速度**，采用单体架构与前后端分离模式，去除了不必要的复杂中间件依赖，开箱即用。

- [English Documentation (README.en.md)](./README.en.md)

### 核心特性

- **极简高效架构**：基于 Spring Boot 3 + Vue 3 的轻量级单体架构，零冗余抽象，开箱即用。
- **开箱即用权限体系**：集成轻量安全框架 **Sa-Token**，支持用户、角色、动态菜单树与按钮级细粒度权限控制（`v-auth`）。
- **全方位安全加固**：内置滑块拼图验证码、密码防暴力破解锁定机制与分页动态排序防 SQL 注入过滤。
- **统一响应与异常规范**：封装标准化响应对象 `ResultBean<T>`，全局统一捕获业务校验、权限认证及系统异常。
- **现代化交互体验**：提供动态多级面包屑、多标签页（TagsView）联动导航与友好的 403 / 404 / 500 缺省页。
- **自动审计与高性能缓存**：公共实体自动填充创建/修改审计信息，内置 Caffeine 本地缓存加速热点数据读取。
- **规范的工程化配置**：接入 ESLint + Prettier 自动化代码规范检测工具链，统一团队代码风格。
- **完整业务开发示例**：提供商品管理业务样例，包含完整的分页查询、状态变更、基于 Apache POI 的 Excel 导入与导出。

---

## 2. 界面预览

| 账号登录 | 滑块安全验证 |
| :---: | :---: |
| ![登录界面](docs/screenshots/00-login.png) | ![滑块验证码](docs/screenshots/01-login-captcha.png) |

| 控制台首页 | 用户管理 |
| :---: | :---: |
| ![控制台首页](docs/screenshots/02-dashboard.png) | ![用户管理](docs/screenshots/03-system-user.png) |

| 角色管理 | 菜单管理 |
| :---: | :---: |
| ![角色管理](docs/screenshots/04-system-role.png) | ![菜单管理](docs/screenshots/05-system-menu.png) |

| 数据字典管理 | 业务示例（商品管理） |
| :---: | :---: |
| ![字典管理](docs/screenshots/06-system-dict.png) | ![商品管理](docs/screenshots/07-demo-product.png) |

| 403 权限不足 | 500 系统异常 |
| :---: | :---: |
| ![403页面](docs/screenshots/08-403.png) | ![500页面](docs/screenshots/09-500.png) |

---

## 3. 技术栈

### 后端技术栈

| 组件 | 版本 | 说明 |
| :--- | :--- | :--- |
| **Java** | 17+ | 长期支持版，全面支持新语法特性 |
| **Spring Boot** | 3.2.5 | 现代化应用核心框架 |
| **MyBatis-Plus** | 3.5.7 | ORM 与持久层增强工具 |
| **MySQL** | 8.x | 关系型数据库 |
| **Sa-Token** | 1.38.0 | 轻量级 Java 权限认证框架 |
| **Caffeine** | 3.x | 高性能本地内存缓存 |
| **Hutool** | 5.8.26 | 常用工具类库（字符串、集合、转换等） |
| **Apache POI** | 5.2.5 | Excel 报表导入与导出 |
| **Lombok** | 1.18.x | 简化 Java Bean 样板代码 |
| **BCrypt** | - | 强散列密码加密（基于 Spring Security Crypto） |

### 前端技术栈

| 组件 | 版本 | 说明 |
| :--- | :--- | :--- |
| **Vue** | 3.4+ | 渐进式前端框架（Composition API & `<script setup>`） |
| **Vite** | 5.2+ | 下一代现代化前端构建工具 |
| **Element Plus** | 2.7+ | 现代化企业级 UI 组件库 |
| **Pinia** | 2.1+ | 现代化轻量状态管理 |
| **Vue Router** | 4.3+ | 官方路由管理器（支持动态路由加载） |
| **Axios** | 1.6+ | HTTP 请求库（已统一封装拦截器） |
| **ECharts** | 5.5+ | 数据图表可视化库 |
| **Sass** | 1.75+ | CSS 预处理器 |

---

## 4. 功能模块

```
JZone-Fast
├── 系统管理 (System)
│   ├── 用户管理：用户增删改查、分配角色、重置密码、启禁用状态控制
│   ├── 角色管理：角色增删改查、按角色分配菜单与按钮权限
│   ├── 菜单管理：目录、菜单、按钮权限维护，动态生成前端路由树
│   └── 字典管理：字典类型与字典数据项维护，结合 Caffeine 本地缓存
├── 业务示例 (Demo)
│   └── 商品管理：商品信息 CRUD、价格/库存管理、分类筛选、Excel 数据导入与导出
└── 基础支撑
    ├── 统一登录鉴权与安全防护
    ├── 统一异常捕获与标准化响应
    ├── 面包屑与多标签页（TagsView）联动
    └── 按钮权限指令 `v-auth`
```

---

## 5. 快速开始

### 5.1 环境要求

- **JDK**: 17 或以上
- **Maven**: 3.6 或以上（推荐 3.8+）
- **Node.js**: 18 或以上
- **MySQL**: 8.0 或以上

### 5.2 数据库初始化

1. 创建 MySQL 数据库（字符集推荐 `utf8mb4`，排序规则 `utf8mb4_general_ci`）：
   ```sql
   CREATE DATABASE `jzone-fast` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   ```
2. 执行初始化 SQL 脚本：  
   脚本位置：`src/main/resources/db/init.sql` 与 `src/main/resources/db/demo.sql`

### 5.3 后端服务启动

1. 修改数据库连接配置：  
   打开 `src/main/resources/application-dev.yml`，配置数据库账号密码（或配置环境变量 `DB_USERNAME` 与 `DB_PASSWORD`）：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/jzone-fast?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
       username: root
       password: your_password
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```
2. 编译并启动后端：
   ```bash
   # 根目录下执行
   mvn clean compile
   # 运行启动类 com.jzone.JZoneFastApplication
   ```
   后端服务默认监听端口：`8086`

### 5.4 前端服务启动

1. 进入前端目录并安装依赖：
   ```bash
   cd web
   npm install
   ```
2. 启动前端开发服务器：
   ```bash
   npm run dev
   ```
3. 访问系统：  
   打开浏览器访问：`http://localhost:5173`

### 5.5 默认账号

| 角色 | 用户名 | 密码 | 说明 |
| :--- | :--- | :--- | :--- |
| **超级管理员** | `admin` | `admin` | 拥有全部菜单及按钮操作权限 |
| **测试账号** | `test` | `123456` | 普通权限角色，用于测试菜单展示与按钮级权限（`v-auth`）隔离 |

---

## 6. 项目结构说明

### 后端目录结构

```text
src/main/java/com/jzone/
├── JZoneFastApplication.java            # Spring Boot 启动类
├── common/                              # 公共通用层（实体、常量、工具）
│   ├── constant/                        # 全局常量
│   ├── pojo/                            # 基础 PO/DTO
│   └── util/                            # 通用工具类
├── framework/                           # 核心框架层
│   ├── cache/                           # 本地缓存（Caffeine）
│   ├── exception/                       # 全局异常处理
│   ├── mybatis/                         # MyBatis-Plus 配置与审计填充
│   ├── result/                          # 统一返回结果
│   ├── satoken/                         # 权限与登录配置
│   └── web/                             # Web 基础配置
└── module/                              # 业务模块层
    ├── demo/                            # 业务示例模块（商品管理）
    └── system/                          # 系统管理模块
        ├── dict/                        # 字典管理
        ├── login/                       # 登录认证与安全校验
        ├── menu/                        # 菜单与权限管理
        ├── role/                        # 角色管理
        └── user/                        # 用户管理
```

### 前端目录结构

```text
web/
├── index.html
├── package.json
├── .eslintrc.cjs                        # ESLint 配置
├── .prettierrc                          # Prettier 配置
├── vite.config.js                       # Vite 配置
└── src/
    ├── api/                             # API 接口统一管理
    ├── assets/                          # 静态资源
    ├── components/                      # 公共组件
    ├── composables/                     # 通用组合式函数
    ├── directives/                      # 自定义指令（v-auth 等）
    ├── layout/                          # 系统主框架布局
    ├── router/                          # 路由配置
    ├── store/                           # 状态管理
    ├── utils/                           # 工具函数
    └── views/                           # 页面视图组件
        ├── dashboard/                   # 首页控制台
        ├── demo/product/                # 商品管理页面
        ├── error/                       # 异常缺省页
        ├── login/                       # 登录页面
        └── system/                      # 系统管理页面
```

---

## 7. 开发与规范约定

1. **统一返回体**：所有后端接口均返回 `ResultBean<T>`，包含 `code`, `msg`, `data`。
2. **鉴权与权限**：接口默认均需登录认证，请求头携带 `Authorization: <token>`；方法级权限控制使用 `@SaCheckPermission("模块:资源:动作")`，前端对应 `v-auth="['模块:资源:动作']"`。
3. **软硬删除约定**：默认采用物理硬删除；删除主数据前必须对关联数据做完整性校验。
4. **分页封装**：查询入参统一继承 `PageDTO`，分页结果统一返回 `PageResult<T>`。
5. **依赖注入**：推荐使用 Lombok `@RequiredArgsConstructor` 与 `private final` 字段进行构造器注入，不使用 `@Autowired` 字段注入。
6. **前端代码规范**：统一执行 `npm run lint` 与 `npm run format` 进行静态语法检查与代码格式化。

---

## 8. 打包与部署

### 后端构建
```bash
mvn clean package -DskipTests
# 生成 jar 位于 target/jzone-fast-1.0.0.jar
java -jar target/jzone-fast-1.0.0.jar --spring.profiles.active=prod
```

### 前端构建
```bash
cd web
npm run build
# 生成静态文件位于 web/dist
```
生产环境可直接通过 Nginx 托管前端构建产物并反向代理后端 API。

---

## 9. 开源协议

本项目遵循 [MIT License](./LICENSE) 开源协议。
