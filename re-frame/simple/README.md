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


This association  can be done in two ways :
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

[More on Event Handlers](http://day8.github.io/re-frame/EffectfulHandlers/)

## Coeffects (input)

To remain *pure function* event handler need to receive all the data they need to do the job via the function arguments.
*Coeffects* is the current state of the world, as data, as presented to an event handler (so they don't have dig into the dirt of the inpure world). 

- When only the application state is needed, *Coeffects* are reduced to the `:db`. Use `reg-event-db` to register the event handler which received the *db* as input
- When other data is needed, extra *Coeffects* need to be defined. Use `reg-event-fx` to register the event handler which receives the full *coeefects map* as input

To create your own *coeffect*:
- **register** it with `reg-cofx`
- **inject** it in the event handler with `inject-cofx`

Regsitering *coeffect* `:now` to provide the current Date to an Event Handler :
```clojure
(reg-cofx               ;; registration function
   :now                 ;; what cofx-id are we registering
   (fn [coeffects _]    ;; second parameter not used in this case
      (assoc coeffects :now (js.Date.))))   ;; add :now key, with value
```

Injecting the `:now` *coeffect* into an event handler :
```clojure
(reg-event-fx
    :add-meeting-date
    [(inject-cofx :now)]    
    (fn [cofx _]
       ... in here I can access cofx's key :now. Its value is the current Date ))
```

[More on Coeffects](http://day8.github.io/re-frame/Coeffects/)


## Effects (output)

As *pure functions* Event Handler are not allowed to modify the oustside world. Instead they return instructions (as data) on how to do it and let other do the dirty job.

Event Handler return one or more *Effects*:
- an event handler registered with `reg-event-db` can produce one single effect dedicated to modify the application state `:db`
- an event handler registered with `reg-event-fx` can produce more than one effect which ciould modify the application state or trigger other actions

Example:
```clojure
(reg-event-fx              ;; -fx registration, not -db registration
  :my-event
  (fn [cofx [_ a]]        ;; 1st argument is coeffects, instead of db
    {:db       (assoc (:db cofx) :flag  a)
     :fx       [[:dispatch [:do-something-else 3]]]})) ;; return effects
```
Event handler registered with `reg-event-fx` return a description of the side-effects required, and that description is a map.


### Builtin Effects

- `:db` : update the application state with the new value
```clojure
(reg-event-fx
  :token 
  (fn [{:keys [db]} event]
    {:db  (assoc db :some-key some-val)}))     ;; <-- new value computed
```
> this is equivalent to returing the new *db* value when the Event handler is registered via `reg-event-db`

For other Effects, use the key `:fx`.

- `:dispatch` : dispatch one event. Expects a single vector.
```clojure
;; dispath one event
{:fx [[:dispatch [:event-id "param1" :param2]]] }
;; dispatch multiple events
{:fx [[:dispatch [:event1 "param1" :param2]]
      [:dispatch [:second]]}
```

[More about builtin effects](https://day8.github.io/re-frame/api-builtin-effects/)

### Create your own Effects


To create your own effect you must:
- define your effect's id (e.g. `:butterfly`)
- register it via `reg-fx`
- add it to the effect map returned by your event handler

Example: create and register your `:butterfly` effect:
```clojure
(reg-fx         ;; <-- registration function
   :butterfly   ;;  <1> effect key
   (fn [value]  ;;  <2> effect handler - requires one argument
      ...do something ditry (not pure) here
      ))
```

Example: the event handler returns our new `:butterfly` effect (something chaotic and not pure will happen):
```clojure
(reg-event-fx              ;; -fx registration, not -db registration
  :my-event
  (fn [cofx [_ a]]        ;; 1st argument is coeffects, instead of db
    {:butterfly "blue"})) ;; return effects
```
[More about Effects](http://day8.github.io/re-frame/Effects/)






