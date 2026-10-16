# android-lab02 — Android 网络访问与 Git 个人仓库

课程实验二工程：在 Kotlin + Jetpack Compose 项目中完成一次真实网络请求，
对比「禁止 HTTP 明文」与「放行测试域名 HTTP 明文」两种配置下的请求结果，
并使用 Git 将工程提交、推送到个人远程仓库。

## 一、实验目标

1. 定位工程代码、权限文件与运行设备；
2. 区分**网络权限**（`INTERNET`）与**明文网络配置**（Network Security Config）两件不同的事；
3. 在模拟器上发起 HTTPS / HTTP 请求并记录结果差异；
4. 完成两次 Git 提交和一次远程推送。

## 二、运行方式

### 环境要求

| 项目 | 版本 |
| --- | --- |
| JDK | 21（`JAVA_HOME` 指向 JDK 21） |
| Gradle | 8.13（工程自带 wrapper，无需本机安装） |
| Android Gradle Plugin | 8.13.2 |
| Kotlin | 2.0.21 |
| Compile SDK | 36 |
| Min SDK | 24 |

### 构建与安装

```bash
# 1. 配置本机 Android SDK 路径（该文件不入库）
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 2. 构建 debug APK
./gradlew assembleDebug          # Windows: gradlew.bat assembleDebug

# 3. 安装到已连接设备/模拟器
./gradlew installDebug
# 或： adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 复现三次对比测试

1. 保持 `app/src/main/res/xml/network_security_config.xml` 中
   `domain-config` 的 `cleartextTrafficPermitted="false"`，安装运行；
2. 点「HTTPS 请求」→ 预期 `HTTP 200`，响应 JSON 中 `room` 为 `A203`；
3. 点「HTTP 请求」→ 预期失败，错误信息含
   `CLEARTEXT communication to httpbin.org not permitted by network security policy`；
4. 仅把 `domain-config` 改为 `cleartextTrafficPermitted="true"`（`base-config` 保持 `false`），
   重新 `installDebug` 运行，再点「HTTP 请求」→ 预期 `HTTP 200`。

## 三、接口地址

| 用途 | URL |
| --- | --- |
| HTTPS 请求 | `https://httpbin.org/get?room=A203` |
| HTTP 请求（对照） | `http://httpbin.org/get?room=A203` |

`httpbin.org` 会把 URL 查询参数原样回显在响应的 `args` 字段中，
因此把 `room=A203` 改成 `room=A204` 后，响应内容会随之变化，可用于验证参数确实生效。

## 四、关键文件

| 文件 | 作用 |
| --- | --- |
| `app/src/main/AndroidManifest.xml` | 声明 `<uses-permission android:name="android.permission.INTERNET" />`，并挂载 `networkSecurityConfig` |
| `app/src/main/res/xml/network_security_config.xml` | 明文网络策略：`base-config` 禁止，仅对 `httpbin.org` 单独配置 |
| `app/src/main/java/com/example/androidlab02/MainActivity.kt` | Compose 界面与两个请求按钮 |
| `app/src/main/java/com/example/androidlab02/HttpDemo.kt` | 基于系统 `HttpURLConnection` 的 GET 请求，不引入第三方网络库 |

## 五、重要说明

> **HTTP 仅用于教学对比，实际服务优先 HTTPS。**

`INTERNET` 权限只决定「应用能否使用网络」，普通权限安装即授予，无需运行时申请；
是否允许明文 HTTP 是**独立的网络安全配置**，两者互不替代。
生产环境应始终使用 HTTPS，不应为了调试方便在 `base-config` 中全局放行明文。

## 六、安全

工程不包含任何访问令牌、密码或签名密钥；`local.properties`、`*.jks`、`*.keystore`
以及各类构建缓存（`.gradle/`、`.kotlin/`、`**/build/`）均已通过 `.gitignore` 排除。
