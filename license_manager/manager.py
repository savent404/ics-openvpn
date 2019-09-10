import pymysql # need call `pip install PyMySQL`
import sys
import string
import random

class license:
    def open(self):
        self.db = pymysql.connect('localhost', 'root', 'Root123.', 'LICENSE_DATABASE')
        self.cursor = self.db.cursor()
    def close(self):
        self.db.close()

    def execute(self, cmd):
        print("Try to execute: %s" % cmd)
        try:
            res = self.cursor.execute(cmd)
        except:
            self.db.rollback()
            print("execute error occurs")
            print(res)
        return res

    def createTableIfNotExists(self):
        self.execute(""" CREATE TABLE IF NOT EXISTS `LICENSE_TABLE` (
                            `KEY` CHAR(16) NOT NULL,
                            `DAY` INT NOT NULL);""")
        self.db.commit()
    def insert(self, key, days):
        self.execute("INSERT INTO LICENSE_TABLE(`KEY`,`DAY`) VALUES ('%s',%s)" % (key, days))
        self.db.commit()

    def delete(self, key):
        self.execute("DELETE FROM LICENSE_TABLE WHERE `KEY` = %s" % key)
        self.db.commit()

    def search(self, key):
        sql = "SELECT * FROM LICENSE_TABLE WHERE `KEY` = %s" % key
        self.execute(sql)
        results = self.cursor.fetchall()

        days = -1;
        for row in results:
            key = row[0]
            days = row[1]
            print('search result: key=%s, days=%s' % (key, days))
        return days;

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
