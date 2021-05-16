(ns task-runner.core
  (:require [org.httpkit.client :as http]
            [org.httpkit.sni-client :as sni-client]
            [cheshire.core :as json]
            [clojure.java.io :as io])
  (:gen-class))

(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

(def end-point1 "https://jsonplaceholder.typicode.com/todos/1")

(defn sync-req
  [url]
  (let [{:keys [status headers body error] :as resp} @(http/get url)]
    (if error
      (println "Failed, exception: " error)
      (println "HTTP GET success: " status  body))))

(defn get-todo-by-id
  [id]
  (let [url (str "https://jsonplaceholder.typicode.com/todos/" id)
        {:keys [status headers body error] :as resp} @(http/get url)]
    (if error
      (println "Failed, exception: " error)
      (json/parse-string body))))

(comment
  ((get-todo-by-id 1) "title")
  (sync-req end-point1))


;; =================================================================

(defn get-todos
  []
  (let [url (str "https://jsonplaceholder.typicode.com/todos")
        {:keys [status headers body error] :as resp} @(http/get url)]
    (if error
      (println "Failed, exception: " error)
      (json/parse-string body))))

(defn completed?
  [todo]
  (todo "completed"))

(comment
  (filter completed? (get-todos))
  (let [todos (get-todos)
        count-completed (count (filter completed? todos))]
    count-completed))

;; =================================================================

(defn add-todo
  [todo]
  (let [url "https://jsonplaceholder.typicode.com/posts"
        {:keys [status headers body error] :as resp} @(http/post url {:body (json/encode todo)})]
    (if error
      (println "Failed, exception: " error)
      (json/parse-string body))))

(comment
  (def new-todo-1 {"title" "foo", "body" "bar" , "userId" 1})
  (json/encode new-todo-1)
  (add-todo new-todo-1))

;; =================================================================

(defn download
  [url filename]
  (let [{:keys [status headers body error]} @(http/get url)]
    (if error
      (println "error : " error)
      (when (instance? java.io.InputStream body)
        (with-open [out (io/output-stream filename)]
          (io/copy body out))))))

(defn download-info
  [url filename]
  (let [{:keys [status headers body error]} @(http/get url)]
    (if error
      (println "error : " error)
      (println headers))))

(comment
  
  (download "http://placekitten.com/200/300" "c:/tmp/img.gif")
  (download "https://images.pexels.com/photos/1680140/pexels-photo-1680140.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260" "c:/tmp/img-2.gif")
  (download-info "https://images.pexels.com/photos/1680140/pexels-photo-1680140.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260" "c:/tmp/img-2.gif")
 ;; (download "https://download.virtualbox.org/virtualbox/6.1.22/VirtualBox-6.1.22-144080-Win.exe" "VirtualBox-6.1.22-144080-Win.exe")
  
  
  )

;; =================================================================


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
