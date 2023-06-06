(ns sfsdb.health)

;; check DB health state

;; rules
;; level 1 : ERRORS
;; - no orphean metadata file : a metadata file not related to an object
;; - invalid JSON metadata file : metadata file with invalid JSON content
;; - db contains symlinks: only dir and regular files are allowed
;; level 2 : WARNINGS
;; - file with empty content
;; - dir with no content 
;; - deep nested: configurable max depth check

