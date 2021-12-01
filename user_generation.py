import requests
import grequests
import time
import base64
import names


URL = 'http://0.0.0.0:8080/api/'
number_of_accounts = 10


def get_random_photo():
	picture_url = 'https://picsum.photos/200'
	picture_req = requests.get(picture_url)
	picture = base64.b64encode(picture_req.content)
	return picture


def get_random_anecdote():
	return "Сидит Йети на горе"


def create_accounts():

	async_list = []

	for i in range(number_of_accounts):
		action_item = grequests.post(URL + 'register', json={'username': f'Petya{i}', 'password': '123456', 'display_name': names.get_first_name()})
		async_list.append(action_item)

	grequests.map(async_list)


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
matches()
