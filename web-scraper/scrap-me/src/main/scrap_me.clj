(ns main.scrap-me
  (:require [net.cgrand.enlive-html :as html])
  (:gen-class))


(def base-url "https://www.paris-turf.com")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn page-url [path]
  (str base-url path))

(defn headlines []
  (map html/text (html/select (page-url "/actualites") [:div.tss-1sqgmff-title])))

(defn body []
  (map html/text (html/select (page-url "/actualites") [:p.MuiTypography-root.MuiTypography-body1.tss-1ny00jd-description.mui-163i93q])))



(defn article-href []
  (map #(first (html/attr-values % :href))
       (html/select (fetch-url (page-url "/actualites")) [:div.tss-bsck8-hoverPointer :> :a])))


(comment
  (article-href)
  html/attr-values
  (body)
  (map html/text (html/select (fetch-url base-url) [:div.tss-1sqgmff-title]))

  ;;
  )


(comment
  ;; reading article

  
  (def page (fetch-url (page-url "/actualites/france/mardi-a-la-capelle-double-de-cyril-raimbaud-202260020333")))

  (html/select page
                          
               [:div])

  (html/select
   (fetch-url "https://www.paris-turf.com/actualites/france/nos-bonnes-notes-en-piste-jeudi-03-aout-202263510519"
              #_"https://www.paris-turf.com/actualites/france/nos-bonnes-notes-en-piste-mercredi-02-aout-202258622268")
   #_{[:div.mui-1pp0le7 :div :h1]
      [:div.mui-1pp0le7 :div.mui-5vb4lz [:h4 (html/nth-child 3)]]}

   #_[:div.mui-1pp0le7 :div.qiota_reserve]
   [:div.mui-1opalb8 :img])

  ;;
  )



(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)}))
