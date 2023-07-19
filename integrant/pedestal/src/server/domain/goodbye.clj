(ns server.domain.goodbye)

(defn say-goodbye [polite?]
  (if polite?
    "Goodbye my friend and see you soon ..."
    "ciao"))