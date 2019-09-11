import pymysql # need call `pip install PyMySQL`
import sys
import string
import random
from datetime import datetime

class license:
    def open(self):
        self.db = pymysql.connect('localhost', 'root', 'Root123.', 'LICENSE_DATABASE')
        self.cursor = self.db.cursor()
    def close(self):
        self.db.close()

    def execute(self, cmd, showLog=True):
        if showLog:
            print("Try to execute: %s" % cmd)
        try:
            res = self.cursor.execute(cmd)
            self.db.commit()
        except:
            self.db.rollback()
            print("execute error occurs")
            print(res)
        return res

    def createTableIfNotExists(self):
        self.execute(""" CREATE TABLE IF NOT EXISTS `LICENSE_TABLE` (
                            `KEY` VARCHAR(16) NOT NULL,
                            `DAY` INT NOT NULL,
                            `UUID` VARCHAR(32) NULL DEFAULT NULL,
                            `USED` TIMESTAMP NULL DEFAULT NULL,
                            PRIMARY KEY (`KEY`)
                            );""", showLog=False)
        self.db.commit()
    def insert(self, key, days):
        res,__,__,__ = self.search("KEY", key)

        if res is not None:
            return
        self.execute("INSERT INTO LICENSE_TABLE(`KEY`,`DAY`) VALUES ('%s',%s)" % (key, days))

    def delete(self, key):
        self.execute("DELETE FROM LICENSE_TABLE WHERE `KEY` = %s" % key)

    def search(self, key_name, val):
        sql = "SELECT * FROM LICENSE_TABLE WHERE `%s` = %s" % (key_name, val)
        self.execute(sql)
        results = self.cursor.fetchall()

        key = None
        days = None
        uuid = None
        used = None

        for row in results:
            key = row[0]
            days = row[1]
            uuid = row[2]
            used = row[3]
            print('search result: key=%s, days=%s, uuid=%s, used=%s' % (key, days, uuid, used))
        return key, days, uuid, used

    def update(self, key, key_name, val):
        sql = "UPDATE LICENSE_TABLE SET `%s`=%s WHERE `KEY` = %s" % (key_name, val, key)
        self.execute(sql)

def randomString(stringlen=8):
    return "".join(random.choice(string.digits) for x in range(random.randint(8, 12)))

if __name__ == '__main__':
    l = license()
    l.open()
    l.createTableIfNotExists()
    license = randomString()
    l.insert(license, sys.argv[1])
    l.close()
    print ("License:%s, day:%s" % (license, sys.argv[1]))
