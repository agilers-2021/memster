import requests

import grequests
import time
import base64


url = 'http://0.0.0.0:8080/api/'
picture_url = 'https://picsum.photos/200'

async_list = []


for i in range(10*3):
	action_item = grequests.post(url + 'register', json={'username': f'Petya{i}', 'password': '123456', 'display_name': 'Kirpich'})
	async_list.append(action_item)

grequests.map(async_list)


for i in range(10*3):
	x = requests.post(url + 'authenticate', json={'username': f'Petya{i}', 'password': '123456'})
	token = x.json()['token']

	picture_req = requests.get(picture_url)
	picture = base64.b64encode(picture_req.content)


	y = requests.post(url + 'settings', headers={"Authorization": "Bearer " + token}, json={'set_photo': picture, 'anecdote': 'мой новый анедот'})