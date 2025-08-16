# SweetAutoResidence

Minecraft 领地自动圈地道具插件

## 简介

你可以通过这个插件，定义多个用于自动圈地的道具。使用道具后，道具会扣除，然后按照配置中的大小进行自动圈地，不会扣除玩家的金币。

你可以添加一定的使用要求，比如要求玩家的已圈地数量为 0，要求玩家需要到达什么等级，要求玩家需要拥有什么权限，等等。

## 兼容性

目前支持使用以下领地插件
+ [Residence](https://www.spigotmc.org/resources/11480/)
+ [Dominion](https://www.minebbs.com/resources/7933/)

更多插件可通过编写附属去兼容，详见 [SweetAutoResidence#checkAdapter](src/main/java/top/mrxiaom/sweet/autores/SweetAutoResidence.java)。

## 开发者

[![jitpack](https://jitpack.io/v/MrXiaoM/SweetAutoResidence.svg)](https://jitpack.io/#MrXiaoM/SweetAutoResidence)
```kotlin
repositories {
    maven("https://jitpack.io")
}
dependencies {
    compileOnly("com.github.MrXiaoM:SweetAutoResidence:$VERSION")
}
```

## 编写领地适配器附属

编写一个类，实现 `top.mrxiaom.sweet.autores.api.IResidenceAdapter`，可参考内置的 [AdapterResidence](https://github.com/MrXiaoM/SweetAutoResidence/blob/main/src/main/java/top/mrxiaom/sweet/autores/impl/residence/AdapterResidence.java)。

这个类必须要有一个满足条件的构造函数：有且仅有一个类型为 `SweetAutoResidence` (本插件主类) 的参数。
```java
package org.example;

import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.IResidenceAdapter;

public class MyAdapter implements IResidenceAdapter {
    public MyAdapter(SweetAutoResidence plugin) {
        // TODO
    }
}
```

然后添加资源文件 `residence-adapter.yml` 到 jar，写入如下内容
```yaml
class: '你实现的类的引用路径，例如 org.example.MyAdapter'
```

将编译后的领地适配器附属 jar 文件放到 `plugins/SweetAutoResidence/libraries/` 目录中，重启服务器即可使用。
