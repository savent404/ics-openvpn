from http.server import BaseHTTPRequestHandler, HTTPServer
from os import path
from urllib import parse
from manager import license
import time

class license_requestHandler(BaseHTTPRequestHandler):
    def checkLicense(self, key):
        print(localTime() + " get KEY is %s" % key)
        l = license()
        l.open()
        day = l.search(key)
        l.delete(key)
        l.close()
        return day
    def do_GET(self):
        sendReplay = False
        querypath = parse.urlsplit(self.path)
        d = parse.parse_qs(querypath.query)
        license_key = ''.join(d['license'])

        leftDay = self.checkLicense(license_key);
        if leftDay > 0:
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            response = '{"status"="ok", "res"="%d"}' % leftDay
            self.wfile.write(response.encode('utf-8'))

        else:
            self.send_error(404, "License not found: %s" % license_key)
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
