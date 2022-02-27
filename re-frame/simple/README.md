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

- *pure* function
- receives input: the *coeffect*
- produce and return output: the *effect*
- *coeffect* and *effect* are data

