# AzurLaneAutomationTool
碧蓝航线辅助工具 - 自动捞船

## 使用方法

### 运行前的准备
1. 安装安卓虚拟机，设置分辨率为1280*720，
2. 运行游戏，保证足够的船坞和石油，舰队能没有弹药的境况下以“S”评分通关。
3. 保证游戏画面在主页（[这个页面](https://ws1.sinaimg.cn/mw690/0063VSfxgy1fzz0wmjqlhj30zf0k0e81.jpg)）且没被其他窗口阻挡。

### 运行工具
#### 不折腾会死星人
* 安装jdk1.8，并设置好环境变量
* 安装opencv依赖
* 编译 
* 运行
#### 懒人向
点击进入[release](https://github.com/rainbowtrash2333/AzurLaneAutomationTool/releases)页面，根据自己需求下载版本。

两中任选一个下载并解压。

第一个直接点击`start.bat`就可以运行了。

第二个再命令行下输入`java -jar AzurLane.jar`及运行。


## 软件原理
截取游戏画面，使用`opencv`的模板匹配，然后控制鼠标操作。

## 软件协议
WuLaWuLaWuLaWuLaWuLaWuLaWuLaWuLa

## 已知问题·开发计划·乱七八糟
1. 只能捞4-3之前的，之后的我还没开荒（旗舰大破，emmm丢脸）
2. 会自动捞地图中的问号，但不会去取弹药
3. 如果出现Boss船会优先进攻Boss船（全图扫荡太慢了）
4. 不要带上潜艇去，会有BUG
5. 保障网络畅通，程序不会检查网络
6. 非洲土著求安慰
7. 学业繁忙，没时间肝游戏，等我有时间，用python重构以下，但最近因该不会动了
