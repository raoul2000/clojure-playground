(ns sfsdb.check
  (:require [babashka.fs :as fs]
            [clojure.string :as s]
            [sfsdb.options :as opts]))

(defn meta-file?
  "Return TRUE if the fs or db *path* has the format of a metadata file path.
   
   *path* must be coercible to String"
  [path]
  (when-let [str-path (str path)]
    (s/ends-with? str-path (str "." (:metadata-extension opts/default)))))

(defn in-db?
  "True if *db-path* describes an object inside the db. The object is not 
   garanteed to exists.
   
   Example:
   ```clojure
   (in-db? \"../item\")
   => false
   (in-db? \"item\")
   => true
   (in-db? \"item/../other-item\")
   => true
   ```
   "
  [^String db-path]
  (and (not (s/starts-with? db-path "/"))
       (not (->> db-path
                 (fs/normalize)
                 (fs/components)
                 (map str)
                 (some #(= ".." %))))))

(defn root-path? [fs-path]
  (and (fs/exists?    fs-path)
       (fs/directory? fs-path)
       (fs/readable?  fs-path)))

(defn validate-root-path [fs-path]
  (if-not (root-path? fs-path)
    (throw (ex-info "Root DB dir path not found"
                    {:path fs-path}))
    true))

(defn validate-db-path [^String db-path]
  (if-not (in-db? db-path)
    (throw (ex-info "Invalid Db Path"
                    {:path db-path}))
    true))

(defn relative-db-path?
  "Returns TRUE when *db-path* is a relative db path.
   
   A relative db path must start with `./` or be equal to `.`"
  [^String db-path]
  (or (= "." db-path)
      (s/starts-with? db-path "./")))

(defn writable-dir? 
  "Returns TRUE when *path* is an absolute path to a writable folder"
  [path]
  (and (not (s/blank? (str path)))
       (fs/absolute? path)
       (fs/directory? path)
       (fs/writable? path)))