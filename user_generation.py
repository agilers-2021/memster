import requests
import time
import base64
import names
import json
import random
import os


URL = 'http://0.0.0.0:8080/api/'
number_of_accounts = 20
pictures_dir = os.getcwd() + '/server/src/main/resources/memes'
pictures = os.listdir(pictures_dir)


def get_random_photo():
    path = random.choice(pictures)
    with open(pictures_dir + '/' + path, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()).decode('utf-8')
        return encoded_string


def get_random_anecdote():
	f = open("aneks", "r")
	aneks = json.loads(f.read())
	f.close()
	return random.choice(list(aneks.items()))[1]


def create_accounts():

	async_list = []

	for i in range(number_of_accounts):
		action_item = requests.post(URL + 'register', json={'username': f'Petya{i}', 'password': '123456', 'display_name': names.get_first_name()})
		async_list.append(action_item)


def add_info():
	for i in range(number_of_accounts):
		x = requests.post(URL + 'authenticate', json={'username': f'Petya{i}', 'password': '123456'})
		token = x.json()['token']

		picture = get_random_photo()
		anecdote = get_random_anecdote()

		y = requests.post(URL + 'settings', headers={"Authorization": "Bearer " + token}, json={'new_photos': [picture], 'anecdote': anecdote})


def matches():
	for i in range(number_of_accounts):
		x = requests.post(URL + 'authenticate', json={'username': f'Petya{i}', 'password': '123456'})
		token = x.json()['token']

		match_req = requests.get(URL + 'match', headers={"Authorization": "Bearer " + token})

		while match_req.status_code == 200:

			sign = match_req.json()['sign']
			vote_req = requests.post(URL + 'vote', headers={"Authorization": "Bearer " + token}, json={'sign': sign, 'action': 'match'})

			match_req = requests.get(URL + 'match', headers={"Authorization": "Bearer " + token})


create_accounts()
add_info()
# matches()
