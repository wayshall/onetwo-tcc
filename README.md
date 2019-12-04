# onetwo-tcc
一个简单的tcc事务框架

## 要求
- JDK 1.8+
- Spring 4.0+
- RocketMQ 3.x + 
- Spring Boot 1.4.x, 1.5.x



## maven

**当前snapshot版本：0.5.1-SNAPSHOT**

若使用snapshot版本，请添加snapshotRepository仓储：

```xml
<repository>
     <id>oss</id>
     <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>   
```



其它依赖可以直接参考sample项目的pom.xml配置




## 使用

### 启用注解
要使用tcc功能，首先要使用 @EnableTCC 注解，激活加载tcc相关组件

###  tcc事务注解使用

0、使用@TCCService注解标记服务类为Tcc服务类

```Java
@Service
@TCCService
public class TccService {
}
```



1、标记主事务方法：

```Java

@Service
@TCCService
public class TccService {
    @TCCTransactional(globalized=true)
    public void tccMain() {
    }
}
```

2、标记分支事务方法：

```java

@Service
@TCCService
public class BranchTccService {
    @TCCTransactional(globalized=false, confirmMethod="确认方法名称", cancelMethod="取消方法名称")
    public void tccBranch() {
    }
}
```

3、confirm和cancel方法签名必须与try方法一致，且在同一个类

```Java
	
@Service
@Transactional
@TCCService
public class BranchTccService {
	@TCCTransactional(globalized=false, confirmMethod="confirm", 		cancelMethod="cancel")
    public void try(Request request) {
	}

    public void confirm(Request request) {
	}

    public void cancel(Request request) {
	}
}
```





