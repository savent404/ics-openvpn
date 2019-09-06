from http.server import BaseHTTPRequestHandler, HTTPServer
from os import path
from urllib import parse
from manager import license

class license_requestHandler(BaseHTTPRequestHandler):
    response='{"status"="ok","res"="30"}'
    def checkLicense(self, key):
        print("KEY is %s" % key)
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
        if self.checkLicense(license_key) > 0:
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(self.response.encode('utf-8'))

        else:
            self.send_error(404, "License not found: %s" % license_key)
if __name__ == '__main__':
    server_address = ('', 7021)
    httpd = HTTPServer(server_address, license_requestHandler)
    httpd.serve_forever()
