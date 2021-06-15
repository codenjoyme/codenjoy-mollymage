# hero client [![codecov](https://codecov.io/gh/dnahurnyi/hero/branch/master/graph/badge.svg)](https://codecov.io/gh/dnahurnyi/hero)
Client for the hero game


Check details at: https://codenjoy.com//codenjoy-contest/resources/help/hero.html

To install this package run:
 > `go get github.com/dnahurnyi/hero`

And import github.com/dnahurnyi/hero to your bot

Use next code snippet to try this client:
```
package main

import (
	"log"

	"github.com/dnahurnyi/hero"
)

func main() {
	browserURL := "https://codenjoy.com//codenjoy-contest/board/player/{player-id}?code={code}&gameName=hero"
	game, c := hero.StartGame(browserURL)

	for {
		select {
		case <-c.Done:
			log.Fatal("It's done")
		case <-c.Read:
			// Make your move
			game.Move(hero.ACT)
			c.Write <- struct{}{}
		}
	}
}

```
