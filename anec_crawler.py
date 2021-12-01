import requests
import json


aneks = {}
for i in range(1, 1143):
    print("Searching for {} anecdote".format(i))
    page = requests.get("https://baneks.ru/{}".format(i))
    _, r = page.text.split("<article>")
    l, _ = r.split("</article>")
    anek = l.split("<p>")[1].split("</p>")[0]
    anek = anek.replace("<br />", "")
    aneks[i] = anek

max_i = max(map(int, aneks.keys())) + 1

for i in range(1000):
    print("Searching for {} anecdote".format(max_i + i))
    page = requests.get("https://baneks.site/random")
    r = page.text.split("<article>")[1]
    l = r.split("</article>")[0]
    anek = l.split("<p>")[1].split("</p>")[0]
    anek = anek.replace("<br/>", "")
    aneks[max_i + i] = anek

words_to_filter = None
with open("words_to_filter") as f:  # Here we need to provide words_to_filter file
    words_to_filter = set(map(lambda x: x[:-1], f.readlines()))

for key in aneks.keys():
    anek = aneks[key]
    for word in words_to_filter:
        anek = anek.replace(word, word[0] + "*" * (len(word) - 1))
    aneks[key] = anek

with open("aneks", "w") as f:
    f.write(json.dumps(aneks))