(ns miktau.query-building)

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

(defn build-get-app-data
  "TESTED"
  [db]
  {:url "/api/get-app-data"
   :params (build-core-query-for-retrieval db)})


(defn build-bulk-operate-on-files
  "TESTED"
  [db action or-else]
  (let [request (build-core-query-for-action db nil)]
    (if (and (contains?  #{:symlinks :default :filebrowser} action) (not (nil? request)))
      {:url "/api/bulk-operate-on-files"
       :params {:action (str (name action)) :request request}}
      or-else)))

(defn build-switch-projects
  "TESTED"
  [db or-else]
  (if (string? (:core-directory db))
    {:url "/api/switch-projects"
     :params {:file-path (:core-directory db)}}
    or-else))

(defn build-update-records
  "TESTED"
  [db or-else]
  (let [request (build-core-query-for-action db nil)
        tags-to-add    (into [] (sort (map (comp str name) (:nodes-temp-tags-to-add db))))
        tags-to-delete (into [] (sort (map (comp str name) (:nodes-temp-tags-to-delete db))))]
    (cond
      (nil? request)
      or-else
      (and (empty? tags-to-add) (empty? tags-to-delete))
      or-else
      :else
      {:url "/api/update-records"
       :params {:tags-to-add       tags-to-add
                :tags-to-delete    tags-to-delete
                :request request}})))

(defn build-check-is-live
  "TESTED"
  []
  {:url  "/api"
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
