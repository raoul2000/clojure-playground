(ns server.controllers.hello
  (:require [ring.util.response :as resp]))


(defn say-hello [req]
  (tap> req)
  (let [name (get-in req [:params :name])]
    (resp/response (if name
                     (str "hello " name)
                     (str "hello stranger")))))

(defn say-bye [req]
  (tap> req)
  (resp/response {:resp "bye bye"
                  :req  (:body req)}))

