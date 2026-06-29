# NekoHub v2.4.0-3 改动报告

**发布日期**: 2026-06-30  
**基础版本**: RikkaHub v2.3.4 (上游), v2.4.0 (fork)  
**当前版本**: `2.4.0-3` (上游 2.4.0 + 第3次修改)  
**Release**: https://github.com/SlightNeko/rikkahub/releases/tag/v2.4.0-3

---

## 1. 品牌重塑 — RikkaHub → NekoHub

| 改动 | 文件 |
|------|------|
| 包名保持 `me.rerere.rikkahub` (兼容覆盖安装) | 不变 |
| 应用显示名改为 NekoHub | `strings.xml` |
| 用户提供的猫猫 SVG → PNG 全密度图标 | `mipmap-*/ic_launcher.png` (hdpi~xxxhdpi) |
| Web 图标 + docs/icon.png | `ic_launcher-web.png`, `docs/icon.png` |
| 删除损坏的 adaptive-icon XML | `mipmap-anydpi-v26/ic_launcher.xml` |
| Watermark URL 更新 | `Export.kt` |

## 2. 🎨 深色模式图标适配

| 改动 | 文件 |
|------|------|
| 通知栏小图标 → 白色剪影 | `small_icon.xml` |
| 文件类型图标 → 白色 | `docx.xml` |
| 相机图标 → 白色 | `ic_lucide_camera.xml` |
| Launcher 图标全部白色线条 | 7 个密度 `mipmap-*/ic_launcher.png` |
| README 图标 `docs/icon.png` 白色 | `docs/icon.png` |

## 3. 🔄 自动上下文压缩

| 改动 | 文件 |
|------|------|
| 新建设置页面 | `SettingAutoCompressPage.kt` (264行) |
| 开关、触发轮数(默认20)、触发Token(默认8000) | `SettingAutoCompressPage.kt` |
| 目标Token(默认2000)、保留消息数(默认32) | `SettingAutoCompressPage.kt` |
| 导航注册 | `SettingPage.kt`, `Screen.kt` |
| 中英文字符串 | `strings.xml`, `strings-zh/strings.xml` |
| 压缩触发逻辑 | `ChatService.kt`, `ChatPage.kt`, `ChatVM.kt` |

## 4. 📡 更新检测修复

| 改动 | 文件 |
|------|------|
| API 改为 GitHub Releases | `UpdateChecker.kt` — `API_URL` |
| **Version 解析器修复** | `UpdateChecker.kt` — `parse()` |
| 纯数字 `-N` 后缀视为第4版本号 | 如 `2.4.0-3` → core=[2,4,0,3] |
| 非数字后缀保持 SemVer 语义 | 如 `1.0.0-alpha` 正常处理 |

## 5. 🔔 主动消息设置

| 改动 | 文件 |
|------|------|
| 主动消息间隔设置页面 | `SettingProactivePage.kt` |

## 6. 🐛 Bug 修复

| 问题 | 修复 | 文件 |
|------|------|------|
| `toaster.show(duration=...)` 编译错误 | 移除不存在的 duration 参数 | `ChatPage.kt:306` |
| `stringResource()` 在非Composable调用 | 提取到 LaunchedEffect 外 | `ChatPage.kt:305` |
| `Duration` 类型错误 | `3000.milliseconds` | `ChatPage.kt:306` |
| 更新检测用旧 API (pre-release) | 切换到 GitHub Releases API | `UpdateChecker.kt` |
| 版本号 `2.3.4-1` < `2.4.0` 无法检测更新 | 改 2.4.0-3 + 修复 Version 解析器 | `UpdateChecker.kt` |

## 7. 🏗️ CI/CD

| 改动 | 文件 |
|------|------|
| 签名 keystore 提交到仓库 | `app/keystore.jks` |
| CI 自动复制 keystore + 签名 | `.github/workflows/build-debug.yml` |
| CI 自动创建 Pre-release + 上传 APK | `.github/workflows/build-debug.yml` |
| google-services.json 占位生成 | `.github/workflows/build-debug.yml` |

## 8. 📝 文档

| 改动 | 文件 |
|------|------|
| README 重组(中文为主) | `README.md`, `README_EN.md`, `README_ZH_TW.md` |
| 语言切换器修复(无自引用) | 三个 README |
| 更新日志移至 GitHub Releases | 链接 `README.md` → `/releases` |
| 版本历史 | `RELEASE.md` |

## 9. 🧹 杂项

| 改动 | 文件 |
|------|------|
| 未使用文件清理 | `README_ZH_CN.md` |
| 语言标签修复 | `values-ja/strings.xml` 等 |
| 包名引用修正 | `Export.kt` |
| URL/链接修正 | `SettingAboutPage.kt` |
| 多语言字符串补全 | `strings.xml`, `values-zh/strings.xml` |

---

## 关于你发现的 emoji 彩蛋

`EmojiBurst.kt` (点击图标/按钮出现 emoji 粒子特效) — **这是上游 RikkaHub 作者 `re-ovo` 在 v2.3.4 中加入的** (commit `7b64059`, 2026-06-28)。不是我们的 fork 加的，所以此前没有报告。

---

## 已安装 Skills 清单

| 类别 | 保留 Skill | 用途 |
|------|-----------|------|
| devops | docker-management | Docker 管理 |
| devops | watchers | RSS/API/GitHub 监控 |
| devops | incident-commander | 事故自愈 |
| devops | hermes-dojo | 自我进化 |
| devops | diagnose/guard/refine/reflect | 诊断/守护/精炼/反思 |
| devops | termux-gateway | Termux 网关保活 |
| devops | termux-python-troubleshooting | Termux Python 修复 |
| memory | plur | 持久学习 |
| security | sherlock | 用户名搜索 |
| mcp | fastmcp/mcporter | MCP 服务器 |
| creative | blender-mcp/drawio/meme/pixel/concept | 创意工具 |
| software | subagent-driven-development | 子代理开发 |
| writing | avoid-ai-writing | 去 AI 味 |

共清理 25 个无用 maestro 子 skill 和临时文件。

---

## 未完成 (网络阻塞)

- **Bitwarden 登录** — `identity.bitwarden.com` 被代理拦截，所有下载/API 请求失败
- **Google 邮箱整理** — 等待 Bitwarden 获取密码
- **CI 邮件通知** — 需要修改 GitHub Actions workflow
