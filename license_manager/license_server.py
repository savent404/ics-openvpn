from http.server import BaseHTTPRequestHandler, HTTPServer
from os import path
from urllib import parse
from manager import license
import time
from datetime import datetime

class license_requestHandler(BaseHTTPRequestHandler):
    def responseDay(self, day):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        response = '{"status":"ok","res":%d}' % day
        self.wfile.write(response.encode('utf-8'))
        print("access allowed, response:%s" % response)

    def do_GET(self):
        sendReplay = False
        querypath = parse.urlsplit(self.path)
        d = parse.parse_qs(querypath.query)
        method = ''.join(d['method'])
        print ('get url:%s' % querypath.query)
        if method == 'ask':
            uuid = ''.join(d['uuid'])
            print('get ASK method, uuid=%s' % uuid)

            if uuid == "null":
                self.send_error(404, "invalid UUID:%s" % uuid)
                return

            l = license()
            l.open()
            key,day,__,used = l.search('UUID', "'%s'" % uuid)
            if key is None:
                l.close()
                self.send_error(404, "No matched License, UUID:%d" % uuid)
                return
            leftTime = datetime.now() - used
            if leftTime.days < 0:
                l.delete(key)
            l.close()

            if (leftTime.days > 0):
                self.responseDay(leftTime.days)
            elif (leftTime.days == 0):
                self.responseDay(1)
            else:
                self.send_error(404, "no time left")

        elif method == 'register':
            uuid = ''.join(d['uuid'])
            key = ''.join(d['license'])

            l = license()
            l.open()
            res,day,uuid_b,used = l.search('KEY', "'%s'" % key)
            
            if res is None:
                self.send_error(404, "License %s Not found" % key)
            elif uuid_b is None and used is None:
                if uuid != "null":
                    l.update('"%s"' % key, 'UUID', '"%s"' % uuid)
                l.update('"%s"' % key, 'USED', 'NOW()')
                l.close()
                self.responseDay(day)
            elif uuid_b != uuid or used is not None:
                l.close()
                self.send_error(404, "Someone use other's license!")
            else:
                l.close()
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
