(ns miktau.query-building
  (:require
   [miktau.utils :as utils]
   [clojure.string :as clojure-string]))

(defn build-core-query-for-retrieval
  "TESTED"
  [db]
  {:modified (or (:calendar-selected db) {})
   :sorted   (or (:nodes-sorted db) "")
   :tags     (or (into [] (sort (map str (map name (:cloud-selected db))))) [])})

(defn build-core-query-for-action
  "TESTED"
  [db item-or]
  (cond (contains?  (:nodes-selected db) "*")
        {:modified (or (:calendar-selected db) {})
         :sorted   ""
         :file-paths []
         :tags (into [] (sort (map str (map name (:cloud-selected db)))))}
        (not (empty? (:nodes-selected db)))
        {:modified {}
         :sorted   ""
         :file-paths (or   (into [] (sort (:nodes-selected db))) [])
         :tags []}
        :else item-or))

(defn build-bulk-operate-on-files
  [db action]
  {:url "/api/bulk-operate-on-files"
   :params {}})

(defn build-switch-projects [db]
  {:url "/api/switch-projects"
   :params {}})

;; 
;; type FileActionRequest struct {
;; 	Error   error     `json:"error"`
;; 	Action  string    `json:"action"` // symlinks/default/filebrowser
;; 	Request CoreQuery `json:"request"`
;;                                }
;; /api/switch-projects
;; type SwitchFoldersRequest struct {
;; 	Error    error  `json:"error"`
;; 	FilePath string `json:"file-path"`
;;                                   }
;; /api/update-records
;; type ModifyRecordsRequest struct {
;; 	RecordsAffected int       `json:"records-affected"`
;; 	Error           string    `json:"error"`
;; 	TagsToAdd       []string  `json:"tags-to-add"`
;; 	TagsToDelete    []string  `json:"tags-to-delete"`
;; 	Request         CoreQuery `json:"request"`
;; }
