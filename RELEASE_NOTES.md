## v2.3.4-1 (2026-06-29)

> 上游版本：2.3.4 | Fork 修改版本：-1

### 🆕 全新功能
- 🤖 **自动压缩上下文** — 可设置触发轮次 / Token 数，自动压缩对话历史；目标 Token 和保留消息数复用手动压缩默认值
- 🗜️ 压缩模型独立配置 — 在「默认模型」页为压缩功能指定专用模型
- 💬 **主动消息设置** — 开关 + 可调节间隔（5～1440 分钟）

### 🐱 Fork 专属
- NekoHub 猫猫图标（深色模式适配：白色线条）
- 📷 静音拍照、🎵 音乐控制
- 📍 GPS 定位、附近 POI（高德 API）
- 📅 日历、⏰ 闹钟
- 📱 电池、屏幕时间、应用使用轨迹
- 📋 剪贴板、📩 短信、🔔 通知监听
- 💪 Gadgetbridge 健康数据
- ☁️ Supabase 云端同步
- ⚙️ 权限管理 / 本地工具目录 / 集成配置 / 主动消息

### 🏷️ 品牌 & 发布
- 全面改名 NekoHub（40+ 文件）
- Fork 版关于页：链接指向 Fork 仓库，隐藏 QQ/Discord，添加免责声明
- 更新检查 API → GitHub Releases
- 分享文本 URL 适配（6 语言 + OpenRouter referrer）
- 自述文件中文默认，语言切换只显示其他两种
- 永久签名密钥入库，APK 支持覆盖安装

### 🔧 修复
- 权限页刷新逻辑
- 本地工具列表图标区分（JavaScript vs Supabase）
- 自适应图标 XML 损坏 → 纯栅格图标
- README 自引用语言按钮

---

📦 下载：[最新 APK](https://github.com/SlightNeko/rikkahub/releases/latest)
