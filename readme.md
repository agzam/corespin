# Simple prototype API

Simple showcase API to import and retrieve threat analysis data from a JSON file.

Uses Polylith structure.

### To run locally: 

```
clojure -M:dev -m corespin.rest-api.server
```

Server should run on port 3003

```
open localhost:3003
```
You can try uploading example 'indicators.json' provided in the root 

### To run locally in the REPL

```
clojure -M:dev
user=> (go)
```

### CIDER

`cider-jack-in` from the root, Emacs should pick up the values in `.dir-locals.el` 


### Tests

Use `poly` tool, e.g., `poly test`


### Docker build

run `make` and follow the instructions
