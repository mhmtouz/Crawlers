import requests
from bs4 import BeautifulSoup
import pymysql.cursors
import random


# con = pymysql.connect(
#     host="",
#     user="",
#     password="",
#     db='',
#     charset='utf8mb4',
#     cursorclass=pymysql.cursors.DictCursor)
# crs = con.cursor()
# sorgu = "INSERT INTO test(name) VALUES (%s)"
# crs.execute(sorgu, ("testicerik",))
# con.commit()

url = "https://onedio.com/haber/bbc-tarafindan-hazirlanan-az-kisinin-bildigi-makale-502787"
response = requests.get(url)
html_contents = response.content
soup = BeautifulSoup(html_contents, "html.parser")
h2s = soup.find_all("h2")
figcaption = soup.find_all("figcaption")
# authors = ["mouz", "admin", "adminouz"]
titles = []
contents = []
makale = {}
for i in range(len(h2s)):
    h2 = h2s[i].get_text()[3:]
    makale[h2] = figcaption[i].get_text()
for v, k in makale.items():
    print(v+" - ", k)
    # sorgu = "INSERT INTO test(title,author,content) VALUES (%s,%s,%s)"
    # crs.execute(sorgu, (v, random.choice(authors), k))
    # con.commit()
print("İşlem Bitti")
# crs.close()
# con.close()
