# 用户管理系统

基于 **Spring Boot + MyBatis + Vue2 + ElementUI** 实现的用户管理系统。

## 技术栈

### 后端
- **Spring Boot 2.7.14** - 应用框架
- **MyBatis 2.3.1** - 持久层框架
- **MySQL 8.0** - 数据库
- **Druid 1.2.18** - 数据库连接池

### 前端
- **Vue 2.6.14** - 前端框架
- **ElementUI 2.15.14** - UI组件库
- **Vue Router 3.6.5** - 路由管理
- **Vuex 3.6.2** - 状态管理
- **Axios 1.6.2** - HTTP客户端

## 项目结构

```
user-management-system/
├── pom.xml                          # Maven配置文件
├── README.md                        # 项目说明文档
├── src/
│   ├── main/
│   │   ├── java/                    # 后端Java代码
│   │   │   └── com/example/usermanagement/
│   │   │       ├── UserManagementApplication.java
│   │   │       ├── common/          # 通用类
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器层
│   │   │       ├── entity/          # 实体类
│   │   │       ├── exception/       # 异常处理
│   │   │       ├── interceptor/     # 拦截器
│   │   │       ├── mapper/          # 数据访问层
│   │   │       ├── scheduler/       # 定时任务
│   │   │       ├── service/         # 服务层
│   │   │       └── util/            # 工具类
│   │   └── resources/
│   │       ├── application.yml      # 应用配置
│   │       ├── mapper/              # MyBatis XML
│   │       ├── db/schema.sql        # 数据库脚本
│   │       ├── frontend/            # 前端Vue项目
│   │       │   ├── package.json
│   │       │   ├── vue.config.js
│   │       │   ├── babel.config.js
│   │       │   ├── public/
│   │       │   └── src/
│   │       │       ├── main.js
│   │       │       ├── App.vue
│   │       │       ├── router/      # 路由配置
│   │       │       ├── store/       # Vuex状态
│   │       │       ├── views/       # 页面组件
│   │       │       │   ├── Login.vue
│   │       │       │   ├── Register.vue
│   │       │       │   ├── Layout.vue
│   │       │       │   ├── Home.vue
│   │       │       │   └── User.vue
│   │       │       └── components/  # 公共组件
│   │       │           └── UserFormDialog.vue
│   │       ├── static/              # 前端静态资源（打包后）
│   │       └── templates/           # 前端入口页面（打包后）
│   └── test/
```

## 快速开始

### 1. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE user_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行 `src/main/resources/db/schema.sql` 创建表结构和初始数据。

**默认管理员账号：**
- 用户名：admin
- 密码：admin123（明文存储）

### 2. 修改后端配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_management?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的数据库用户名
    password: root        # 修改为你的数据库密码
```

### 3. 运行方式

#### 方式一：一键运行（推荐）

双击运行项目根目录下的批处理脚本：

1. **一键运行脚本.bat** - 自动启动MySQL、后端、前端服务
2. **一键停止脚本.bat** - 停止所有服务
3. **初始化数据库.bat** - 单独初始化数据库（首次使用前）

> ⚠️ **首次使用前**，请先修改脚本中的配置：
> - `MYSQL_DIR` - MySQL安装路径
> - `MYSQL_DATA` - MySQL数据目录
> - `MYSQL_PORT` - MySQL端口（默认3306）
> - `MYSQL_USER` / `MYSQL_PWD` - 数据库用户名密码

#### 方式二：前后端分离开发（推荐开发时使用）

**启动后端：**
```bash
# 在项目根目录执行
mvn spring-boot:run
```
后端服务运行在 http://localhost:8080

**启动前端：**
```bash
# 进入前端目录
cd src/main/resources/frontend

# 安装依赖（首次需要）
npm install

# 启动开发服务器
npm run serve
```
前端服务运行在 http://localhost:8081

**访问系统：**
打开浏览器访问 http://localhost:8081

#### 方式三：打包整合运行（生产环境）

```bash
# 1. 进入前端目录，安装依赖并打包
cd src/main/resources/frontend
npm install
npm run build

# 2. 返回项目根目录，打包整个项目
cd ../../../..
mvn clean package -DskipTests

# 3. 运行打包后的jar
java -jar target/user-management-system-1.0.0.jar
```

访问 http://localhost:8080

## 功能特性

### 用户端功能
1. **用户登录/注册** - 支持用户注册和登录
2. **用户管理** - 查看、新增、编辑、删除用户
3. **多条件筛选** - 支持用户名、昵称、状态、类型、日期等筛选
4. **分页显示** - 支持分页和每页条数切换

### 后端功能
1. **用户管理API** - 完整的CRUD接口
2. **登录认证** - 基于Session的认证机制
3. **权限控制** - 登录拦截、admin用户保护
4. **数据校验** - 密码强度、手机号、邮箱格式校验
5. **用户状态** - 正常/禁用/冻结三种状态
6. **定时检查** - 每5分钟检查用户状态

### 前端特性
1. **ElementUI组件** - 美观的UI界面
2. **表单验证** - 前端数据校验
3. **响应式布局** - 适配不同屏幕
4. **路由管理** - Vue Router实现SPA
5. **状态管理** - Vuex管理登录状态
6. **Axios拦截** - 统一处理请求和响应

## API接口

### 登录相关
- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册
- `POST /api/logout` - 用户登出
- `GET /api/currentUser` - 获取当前用户
- `GET /api/checkUserState` - 检查用户状态

### 用户管理
- `POST /api/user/add` - 新增用户
- `POST /api/user/delete` - 删除用户
- `POST /api/user/edit` - 修改用户
- `GET /api/user/query` - 查询用户（不分页）
- `GET /api/user/query/page` - 查询用户（分页）
- `GET /api/user/get/{id}` - 根据ID查询
- `GET /api/user/creators` - 查询创建人列表

## 注意事项

1. **开发环境**：需要安装 Node.js 16+ 和 MySQL 8.0
2. **数据库**：首次运行前确保MySQL已启动并创建数据库
3. **密码存储**：所有密码明文存储（开发测试用途）
4. **跨域**：开发环境已配置代理，生产环境前后端整合

## 常见问题

### Q: 登录时提示"网络错误"？
A: 请检查：
1. MySQL服务是否已启动
2. 数据库是否已初始化（运行"初始化数据库.bat"）
3. 数据库用户名密码是否正确（默认root/root）

### Q: 一键运行脚本无法启动？
A: 请检查：
1. MySQL安装路径是否正确
2. Java是否已安装（需要JDK 8+）
3. Node.js是否已安装（需要16+）

### Q: 如何修改数据库配置？
A: 编辑以下文件：
- 脚本配置：修改"一键运行脚本.bat"开头的配置区域
- 应用配置：修改`src/main/resources/application.yml`

## 页面截图说明

系统包含以下页面：
- **登录页** - 紫色渐变背景，简洁的登录表单
- **注册页** - 包含用户名、密码、确认密码、昵称、出生日期、电话、邮箱等字段
- **首页** - 显示当前登录用户信息，快捷入口
- **用户管理页** - 包含筛选条件区、用户表格、分页组件、新增/编辑弹窗
