# NekoHub v2.3.4-5 改动报告

**发布日期**: 2026-06-30  
**基础版本**: RikkaHub v2.3.4 (上游)  
**当前版本**: `2.3.4-5` (上游 2.3.4 + 第5次修改)  
**versionCode**: `175` (>169)  
**Release**: https://github.com/SlightNeko/rikkahub/releases/tag/v2.3.4-5

---

## 1. 🎨 品牌重塑 — RikkaHub → NekoHub

| 改动 | 文件 |
|------|------|
| 应用显示名改为 NekoHub | `strings.xml` |
| 猫猫图标全密度替换（黑/白双模式） | `mipmap-*/ic_launcher.png` + `mipmap-night-*/` |
| Web 图标 + docs 图标 | `ic_launcher-web.png`, `docs/icon.png`, `docs/icon-dark.png` |
| Watermark URL 更新 | `Export.kt` |

## 2. 🌓 深色模式图标适配

| 改动 | 文件 |
|------|------|
| **APP 启动器图标** — 浅色模式用黑色图标，深色模式用白色图标 | `mipmap-*/ic_launcher.png` (黑) + `mipmap-night-*/ic_launcher.png` (白) |
| **README 图标** — `<picture>` 自适应深色/浅色模式 | `README.md` (+ EN, ZH_TW) |
| **docs 图标双版本** — 白色线条 (dark mode) + 黑色线条 (light mode) | `docs/icon.png` (白), `docs/icon-dark.png` (黑) |

## 3. 🔄 自动上下文压缩

| 改动 | 文件 |
|------|------|
| 设置页面（可调整所有参数） | `SettingAutoCompressPage.kt` |
| 保留消息数可调范围 0-64 | 修复之前范围限制为 0-64 |
| 触发轮数、触发Token、目标Token 均可配置 | `SettingAutoCompressPage.kt` |

## 4. 📡 更新检测修复

| 改动 | 文件 |
|------|------|
| API 从 RikkaHub 旧接口改为 GitHub Releases | `UpdateChecker.kt` |
| **VersionCode 整数比较** — Release body 中 `VersionCode: 175`，客户端直接读整数比较 | `UpdateChecker.kt`, `UpdateCard.kt` |
| Version 解析器修复 — 纯数字 `-N` 后缀视为第4版本号 | `UpdateChecker.kt` |
| 版本号格式 `2.3.4-5` — 上游版本号-修改次数 | `build.gradle.kts` |

## 5. 🔔 主动消息设置

| 改动 | 文件 |
|------|------|
| 主动消息间隔设置页面 | `SettingProactivePage.kt` |

## 6. 🛠️ 工具拆分

| 改动 | 文件 |
|------|------|
| 主动消息/健康数据/Supabase 从 `LocalTools` 移到 `IntegrationTools` | `LocalTools.kt`, `IntegrationTools.kt` |

## 7. 🐛 Bug 修复

| 问题 | 修复 | 文件 |
|------|------|------|
| `toaster.show(duration=...)` 编译错误 | 移除不存在的 duration 参数 | `ChatPage.kt` |
| `stringResource()` 在非Composable调用 | 提取到 LaunchedEffect 外 | `ChatPage.kt` |
| `Duration` 类型错误 | `3000.milliseconds` | `ChatPage.kt` |
| 更新检测版本比较失效 | 切到 GitHub Releases + versionCode 整数比较 | `UpdateChecker.kt` |
| 自动压缩保留消息数范围过窄 | 修复 0-64（与手动弹窗一致） | `SettingAutoCompressPage.kt` |
| 深色模式图标不可见 | 新增 `mipmap-night-*` 白色图标 | 11 个文件 |

## 8. 🏗️ CI/CD

| 改动 | 文件 |
|------|------|
| 签名 keystore 提交到仓库 | `nekohub.keystore` |
| CI 自动签名 + 上传 APK | `.github/workflows/debug-build.yml` |
| CI 自动创建 Release + 写入 VersionCode | `.github/workflows/debug-build.yml` |
| **CI 邮件通知** — 构建完成自动发 Gmail (`wuliaodexunian@gmail.com`) | `.github/workflows/debug-build.yml` |
| SMTP 直达，不依赖 GitHub 通知系统 | Secrets: `MAIL_USERNAME`, `MAIL_PASSWORD` |

## 9. 📝 文档

| 改动 | 文件 |
|------|------|
| README 重组（中文为主） | `README.md`, `README_EN.md`, `README_ZH_TW.md` |
| 图标深色模式自适应 | 三个 README |
| 语言切换器修复 | 三个 README |
| 更新日志移至 GitHub Releases | README → `/releases` |

## 10. 🔌 集成 & 账号

| 改动 | 状态 |
|------|------|
| Bitwarden 登录 + 密码库解锁 | ✅ |
| Google Gmail 连接（himalaya SMTP） | ✅ |
| Gmail 邮件整理（删广告、标记已读） | ✅ |
| GitHub 主邮箱切换为 Gmail | ✅ |
| CI 构建邮件通知 → Gmail | ✅ |

---

## 关于 emoji 彩蛋

`EmojiBurst.kt`（点击关于页面 Logo 出现 emoji 粒子特效）— **这是上游 RikkaHub 作者 `re-ovo` 在 v2.3.4 中加入的** (commit `7b64059e`, 2026-06-28)。不是我们的 fork 加的。
