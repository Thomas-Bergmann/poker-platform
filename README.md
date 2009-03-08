# Poker Platform #

This poker platform allows to play poker with humans or bots.
You can play as human or can write a bot, which uses the service to get information about the game.

## Poker Service ##

The service:
* stores of all relevant data, like: player,bots,tables,seats and game events
* takes the role of the dealer

All played games will be stored and are reproducable. This includes:
* attended players
* deck (all cards)
* players actions
* payout summary

## Poker App ##

The App allows human to interact with the service.

The App:
* allows to login via previously registered OIDC providers
* can create bot accounts

## Poker Bot ##

An initial bot implementation shows how to interact with the service. The bot interacts with the REST API as well, so implementations are not limited to that example.

# Structure of Project #

## poker ##

* base poker enums - color, images, cards, deck, rank, pot
* rules limit, variant, hand index, rank of hand
* remote api
* table - players infos and actions

## common ##
* basically helper and toolings for REST API

## service ##
* puts all sub projects together to one Spring application.
* contains database setup and migration with Flyway
* contains central CORS

## user ##
* contain storage of users (humans interact with the service)

## oidc ##
* contain storage of OIDC providers
* contains controller for authentication

## player ##
* contain storage and controller for players and bots

## table ##
* storage and controller for table, seats, game
* storage game events
* dealer logic

## bot ##

Initial implementation or template to write a poker bot
* find table and seat
* automatically calls if bot has action

## app ##

The angular app to play poker

app structure
* composites - all pages control data and display
* components for all objects
** bots
** game
** players
** seats
** tables
** oidc
** shell

# flow or how to interact #

* admin add oidc provider via REST
* user login via oidc
* table is automatically create after user login
* user can create bots
* user can assign player and bots to table
* as soon as two players are on table the dealer will start the game
* player can select action - display refresh every 5 seconds

# Development Info #

* service JDK 17
* app typescript with angular 15


# License

Copyright 2009-2023.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
