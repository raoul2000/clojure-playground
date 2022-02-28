# Re-Frame Notes

## Events

- arrays where the first item is a *key* representing the *event id*
- other items are any data related to the event 

Example:
```clojure
[:buy "2254"]                  ;; buy an item given its Id
[:add-todo "buy some milk"]    ;; add a task to the list of things to do
```

## Event Handler

An Event handler is created by associating an *event id* with a *function*. When the event is fired, the function is invoked. 

About the function:
- it is *pure* 
- it receives 2 arguments 
  - the *coeffect* : a map describing the *world* and any input data required by the function
  - the event to handle
- it produces and return output: the *effect*


This association is can be done in two ways :
- the *coeffect* is limitated to the application state (*app-db*)
```clojure
(re-frame.core/reg-event-db        ;; <-- call this to register a handler
    :set-flag                      ;; this is an event id
   (fn [db [_ new-value]]          ;; this function does the handling
      (assoc db :flag new-value)))
```

- the *coeffect* includes some additional data which are needed by the function to be able to produce effects (e.g. the `local store`)
```clojure
(re-frame.core/reg-event-fx                            ;; notice the -fx
   :load-localstore
   (fn [cofx  _]                                       ;; cofx is a map containing inputs
     (let [defaults (:local-store cofx)]               ;; <--  use it here
       {:db (assoc (:db cofx) :defaults defaults)})))  ;; returns effects map
```


