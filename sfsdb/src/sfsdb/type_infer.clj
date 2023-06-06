(ns sfsdb.type-infer)

;; About assigning a type to DB objects

;; type base tree : 
;;  node
;;  /  \
;; dir file  (no symlink)
;; ........ => user defined types
;; 
;; Each node (dir/file) may be assigned a user defined type via 
;; the 'type' property in its metadata file.
;; Type can have any value. 
;;
;; About type names:
;; - non-empty string
;  - start with a letter
;; - contains alpha/num and separators : '-' '_'
;; - case insensitive
