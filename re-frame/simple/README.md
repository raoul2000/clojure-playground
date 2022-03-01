# Re-Frame Notes

## Events

- represented by array
- the first array item is **always** a *key* representing the *event id*
- other items are any data related to the event; they are aoptionals

Example:
```clojure
[:clear-cart]                  ;; clear the shopping cart
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
- the *coeffect* is limitated to the application state (*app-db*). Use it to modify onlt the application state.
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

> In fact there is another arity supported by both `reg-event-db` and `reg-event-fx` where the second parameter is an array to *coeffects* required by the function (third argument). 
> 
> See below for more ...

[More about Event Handlers](http://day8.github.io/re-frame/EffectfulHandlers/)

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
    [(inject-cofx :now)]    ;; the 3 arguments signature is used here
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

# Subscriptions

Subscription is the way to react to a value's change. In the example below, each time `:time-color` changes the color-input component is re-rendered :

```clojure
(defn color-input
  []
  [:div.color-input
  {:style {:color @(rf/subscribe [:time-color])}}
   "Nice color"])
```

The data flow through 4 layers:

- **Layer 1** - Ground truth - is the root node, app-db
- **Layer 2** - Extractors - *subscriptions* which extract data directly from app-db, but do no further computation.
- **Layer 3** - Materialised View - *subscriptions* which obtain data from other subscriptions (never app-db directly), and compute derived data from their inputs
- **Layer 4** - View Functions - the leaf nodes which compute hiccup (DOM). They subscribe to values calculated by Layer 2 or Layer 3 nodes.

Actually layers 2 and 3 could be merged into a single one, but maintaining this separation is an **It is an efficiency thing** ([read why](https://day8.github.io/re-frame/subscriptions/#why-layer-2-extractors) if you're curious)

## Layer 2

Creates a layer 2 subscription to extract a simple value from the application state (db):

```clojure
(re-frame.core/reg-sub  ;; a part of the re-frame API
  :id                   ;; usage: (subscribe [:id])
  (fn [db query-v]      ;; `db` is the map out of `app-db`
    (:something db)))   ;; trivial extraction - no computation
```

When called like this :
```clojure
(subscribe [:id "blue" :yeah])
```

... then `query-v` will contain `["blue" :yeah]`.

## Layer 3

Creates a *layer 3* subscription that subscribes to two *Layer 2* subscriptions (`:a` and `:b`). The value returned by this subscription is **derivated** (`calculate-it`) from these 2 subscriptions.

```clojure
(reg-sub 
  :id

  ;; signals function
  (fn [query-v] 
    [(subscribe [:a]) (subscribe [:b 2])])     ;; <-- these inputs are provided to the computation function 

  ;; computation function
  (fn [[a b] query-v]                  ;; input values supplied in a vector
      (calculate-it a b)))
```

Syntactic Sugar :

```clojure
(reg-sub 
  :id

  ;; input signals 
  :<- [:a]        ;; means (subscribe [:a] is an input)
  :<- [:b 2]      ;; means (subscribe [:b 2] is an input)

  ;; computation function
  (fn [[a b] query-v]
       (calculate-it a b)))
```

[More about subscriptions](https://day8.github.io/re-frame/subscriptions/)


