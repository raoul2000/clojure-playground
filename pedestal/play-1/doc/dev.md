
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

# Routes in dev


## Display Routes

Use `route/print-routes`to display routes in an easy to read format. In particular this will output the route Id automatically generated when none is provided.

Example:
```clojure
;; define our routes. See that "/echo" has no route ID when "/greet" has one (:greet)
(def routes
  (route/expand-routes
   #{["/greet" :get [coerce-body content-neg-intc respond-hello]  :route-name :greet]
     ["/echo"  :get [coerce-body content-neg-intc echo] ]}))

(route/print-routes routes)
;; output - the route id for "/echo" is :myservice.api/echo
[:get /greet :greet]
[:get /echo :myservice.api/echo]    
```

## Create URL for route

Function `route/url-for-routes` returns the URL for a route given its ID.
Example:
```clojure
((route/url-for-routes routes)  :myservice.api/echo)
"/echo"
```

# Response Test

To test what is the response to a given request :

```clojure
(test/response-for (:io.pedestal.http/service-fn @server) :get "/echo"
                   :headers {"Accept" "application/edn"})
```                   