# 配置服务器

## 初始化本地数据库
``` bash
apt install mysql
apt install python3
pip3 install pymysql
```

通过`mysql -u root -p`进入sql console后配置db
``` sql
create DATABASE LICENSE_DATABASE
exit
```

保证`license_server.py`中关于mysql的配置可以正常连接

## 启动服务
``` base
自动保存log到当前路径的log文件中
nohup ./launch_server.sh &
```

# 配置VPN服务器列表
服务器列表为base64加密后的json文件.
管理人员首先需要将`server.json.en`解密为`server.json`，然后修改`server.json`的内容再加密为`server.json.en`，最后将`server.json.en`复制到网络可访问的路径
***注意 `server.json` 不要放在可下载的路径避免用户或hacker获取到***
## 加密server.json
``` bash
cat server.json | base64 > server.json.en
```

### 解密server.json.en到server.json
``` bash
cat server.json.en | base64 -d > server.json
```

## 通过convert.sh方便地将 xxx.ovpn 转换为json可用的格式
``` bash
./convert.sh xxx.ovpn > ans
# 将ans的内容复制粘贴到server.json
```

# 注册邀请码
``` bash
python3 manager.py 30
```
`30`为允许使用的天数。输出结果应该如下:
``` bash
Try to execute:  CREATE TABLE IF NOT EXISTS `LICENSE_TABLE` (
                            `KEY` CHAR(16) NOT NULL,
                            `DAY` INT NOT NULL);
/home/liaoyuankai/.local/lib/python3.5/site-packages/pymysql/cursors.py:170: Warning: (1050, "Table 'LICENSE_TABLE' already exists")
  result = self._query(query)
Try to execute: INSERT INTO LICENSE_TABLE(`KEY`,`DAY`) VALUES ('178281706894',30)
License:178281706894, day:30
```
最后一行`License`即是邀请码
