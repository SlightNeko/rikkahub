# NekoHub v2.3.4-5 — 完整改动报告

> Fork 自 [RikkaHub](https://github.com/re-ovo/rikkahub) (v2.3.4)
> 修改次数：5 | versionCode：175 | 构建：#58 ✅

---

## 🔄 品牌重命名：RikkaHub → NekoHub

- **应用名称**：所有界面、字符串、类名从 RikkaHub 替换为 NekoHub
- **包名保持**：`me.rerere.rikkahub`（兼容 OTA 覆盖安装）
- **全新猫猫图标**：原创 NekoHub 猫头图标（深色线条猫脸 + 电路板额头纹路）
- **深色模式全适配**：
  - 启动器图标各密度均为白色剪影（`mipmap-*/ic_launcher.png`）
  - 内嵌矢量图标全白（`small_icon.xml`, `docx.xml`, `ic_lucide_camera.xml`）
  - 关于页图标点击 **🎉 emoji 彩蛋**（50种 emoji 爆发动画）

---

## 🆕 新增 13 个系统级 AI 工具

| 类别 | 工具 |
|------|------|
| 📷 **媒体** | 静音拍照、音乐控制 |
| 📍 **位置** | GPS 定位、附近 POI（高德）、位置追踪 |
| 📅 **日程** | 日历读写、闹钟管理、时间查询 |
| 📱 **设备** | 电池状态、屏幕时间、应用使用轨迹、屏幕事件、剪贴板读写 |
| 💬 **通信** | 短信收发、通知监听、主动消息推送 |
| 💪 **健康** | Gadgetbridge 健康数据集成 |
| ☁️ **同步** | Supabase 云端同步 |
| ⚙️ **设置面板** | 权限管理、本地工具目录、集成配置（高德 API Key、Gadgetbridge DB、Supabase） |

---

## 🔔 更新检测：versionCode 整数比较

**彻底废除字符串比较 / SemVer 语义解析。**

```
手机本地 → BuildConfig.VERSION_CODE = 175
GitHub Release body → "VersionCode: 175"
175 > 169 → 弹出更新卡片 🎉
```

- Release body 由 CI 自动注入 `VersionCode: N`
- `UpdateCard.kt` 用正则 `VersionCode:\s*(\d+)` 从 body 提取整数
- 与 `BuildConfig.VERSION_CODE` 直接比较，回退到第4位组件解析
- 版本号命名：`上游版本号-修改次数`（如 `2.3.4-5`）
- Tag 格式：`v2.3.4-5`

---

## 📦 自动压缩

- 聊天历史过长时自动触发压缩
- 默认参数与手动压缩一致：目标 2000 token，保留最近 32 条
- 可配置范围

---

## 🔑 签名

- 永久 keystore：`app/keystore.jks`（alias `slightNeko`）
- CI 自动签名，覆盖安装无需卸载旧版

---

## 🤖 CI/CD

- GitHub Actions 自动构建 Release APK（arm64-v8a）
- Push master → 构建 → 签名 → 发布 Release
- 构建邮件通知已配好（需设 `MAIL_USERNAME` / `MAIL_PASSWORD` secrets）
- Release body 自动注入 `VersionCode`

---

## 🐛 修复

- `ChatPage.kt`：`stringResource` 在 `LaunchedEffect` 外调用 → 预解析
- `ChatPage.kt`：`duration = 3000` 类型错误 → `.milliseconds`
- `toaster.show()` 多余参数移除
- `LocalToolOption` 密封类遍历 → 显式列表
- 重复字符串资源删除
- CI 子模块 checkout 修复
- Material3 废弃 API 修复

---

## 📝 README

- 默认中文 README，英文/繁体中文作为次要
- README 只做项目自述，改动日志统一放在 GitHub Releases
- 链接到 `/releases` 页面

---

**版本**：2.3.4-5 | **versionCode**：175 | **构建**：GitHub Actions #58
