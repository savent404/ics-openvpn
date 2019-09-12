from http.server import BaseHTTPRequestHandler, HTTPServer
from os import path
from urllib import parse
from manager import license
import time
from datetime import datetime

class license_requestHandler(BaseHTTPRequestHandler):
    def responseDay(self, day):
        """
        返回剩余时间

        day - 剩余天数
        """
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        response = '{"status":"ok","res":%d}' % day
        self.wfile.write(response.encode('utf-8'))
        print("access allowed, response:%s" % response)

    def calculateLeftDays(self, startDate, planDay):
        """
        计算剩余天数
        * 向下取整，不足1天的部分算作0天
        * 返回结果小于等于0即代表License过期
        - startDate 开始使用的时间戳，对应数据库中的`USED`
        - planDay   计划能够使用的天数，对应数据库中的`DAY`
        """
        minusDate = datetime.now() - startDate
        lastDay = minusDate.days
        return planDay - lastDay

    def do_GET(self):
        """
        处理URL请求，有两种方式

        - method=ask & uuid=xxx
          向数据库查询该手机是否有剩余时间
        - method=register & uuid=xxx & license=xxx
          注册一个新的License
        """

        # 解析URL参数
        querypath = parse.urlsplit(self.path)
        d = parse.parse_qs(querypath.query)

        # 输出日志
        print ('Get url:%s\tTime:%s' % (querypath.query, localTime()))

        # 判断请求类型
        method = ''.join(d['method'])
        if method == 'ask':
            """
            根据UUID判断该客户端是否有剩余时间
            - 若UUID非法则返回错误
            - 若数据库中不包含该UUID的数据项则返回错误
            - 若数据项计算后没有剩余时间则返回错误
            """
            # 检查UUID是否合法
            uuid = ''.join(d['uuid'])
            if uuid == "null":
                self.send_error(404, "invalid UUID:%s" % uuid)
                return

            # 查找数据库中含有该UUID的数据项
            l = license()
            l.open()
            key,day,__,used = l.search('UUID', "'%s'" % uuid)
            l.close()

            # 检查是否有匹配数据项
            if key is None:
                self.send_error(404, "No matched License, UUID:%s" % uuid)
                return

            # 计算剩余时间
            if used is not None:
                leftDay = self.calculateLeftDays(used, day)
            else:
                leftDay = 0

            # 检查剩余时间并应答客户端
            if leftDay > 0:
                self.responseDay(leftDay)
            else:
                self.send_error(404, "no time left")

        elif method == 'register':
            """
            检查是否可以注册一个License

            - 首先数据库中需要包含该License
            - 该License不应该被别人注册过
            - (极小概率)若该License是原先这台设备注册过的，需检查是否还有剩余时间
            """
            uuid = ''.join(d['uuid'])
            key = ''.join(d['license'])

            # 查询该License数据项
            l = license()
            l.open()
            res,day,uuid_b,used = l.search('KEY', "'%s'" % key)
            l.close()

            # 检查剩余时间,若没有`USED`数据项则默认为没有剩余时间(0)
            if used is not None:
                leftDay = self.calculateLeftDays(used, day)
            else:
                leftDay = 0

            if res is None:
                # 没有记录过的License
                self.send_error(404, "License %s Not found" % key)
            elif uuid_b is None and used is None:
                # 没有注册过的License，即可以注册
                l = license()
                l.open()
                # NOTE:若手机无法提供UUID,则无法通过ASK方法恢复license
                if uuid != "null":
                    l.update('"%s"' % key, 'UUID', '"%s"' % uuid)
                l.update('"%s"' % key, 'USED', 'NOW()')
                l.close()
                self.responseDay(day)
            elif uuid_b != uuid:
                # 被其他客户端注册过的
                self.send_error(404, "Someone use other's license!")
            elif uuid_b == uuid and leftDay > 0:
                # 被该客户端注册，且还有剩余时间的
                self.responseDay(leftDay)
            else:
                self.send_error(404, "unknow situation, plz contact manager")
        else:
            self.send_error(404, 'Method not found %s' % method)

def localTime():
    return time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(int(round(time.time()*1000))/1000))
if __name__ == '__main__':
    print("")
    print("---------------------------------------")
    print("Started at " + localTime())
    print("---------------------------------------")
    server_address = ('', 7021)
    httpd = HTTPServer(server_address, license_requestHandler)
    httpd.serve_forever()
