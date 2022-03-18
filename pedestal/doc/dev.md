
# Interactive development

- from the REPL : starts the server without blocking the REPL
```clojure
clj꞉user꞉> 
(require 'myservice.api)
nil
clj꞉user꞉> 
(myservice.api/start-dev)
``` 
- use `stop-dev` and `restart` in the same way

Note that same effect can be obtained using *Calva* and evaluation of the source code.
