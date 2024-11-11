# Websocket Demo
# 说明
这是一个 Websocket demo，项目基于 java 17 和 maven。

# 原因
网上现存的 Websocket 示例，大多都是基于低版本 java 实现的。如果直接应用到高版本 java 中，会存在问题，出现某些引用包找不到或报错的情况。
本人在集成Websocket过程中，也被此问题困惑，现将找到的方案做一个记录，也方便有需要的人参考。

# 问题点
在新版本 jdk 中，注意需要引入 jakarta.websocket 包，Websocket Session 需要引用这个包中的 Session。